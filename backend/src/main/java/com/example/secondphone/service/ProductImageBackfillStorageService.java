package com.example.secondphone.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.secondphone.service.AppleProductImageSourceRegistry.ImageSource;

@Service
public class ProductImageBackfillStorageService {
    static final long MAX_BYTES = 5L * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg", "image/png", ".png", "image/webp", ".webp");
    private final Path productDirectory;
    private final HttpClient client;

    @Autowired
    public ProductImageBackfillStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this(PathsHolder.productDirectory(uploadDir), HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER).build());
    }

    ProductImageBackfillStorageService(Path productDirectory, HttpClient client) {
        this.productDirectory = productDirectory.toAbsolutePath().normalize();
        this.client = client;
    }

    public String acquire(String normalizedModelName, String productType, ImageSource source) {
        Path modelDirectory = safeModelDirectory(normalizedModelName);
        try {
            Files.createDirectories(modelDirectory);
            Optional<Path> existing = existingImage(modelDirectory, source == null);
            if (existing.isPresent()) return publicUrl(modelDirectory, existing.get());
            return source == null
                    ? createPlaceholder(modelDirectory, productType, normalizedModelName)
                    : download(modelDirectory, source.previewUrl());
        } catch (IOException exception) {
            throw new ImageAcquisitionException("無法建立或寫入商品圖片目錄");
        }
    }

    public String placeholder(String normalizedModelName, String productType) {
        Path modelDirectory = safeModelDirectory(normalizedModelName);
        try {
            Files.createDirectories(modelDirectory);
            return createPlaceholder(modelDirectory, productType, normalizedModelName);
        } catch (IOException exception) {
            throw new ImageAcquisitionException("無法建立統一 placeholder");
        }
    }

    private String download(Path modelDirectory, String sourceUrl) throws IOException {
        URI uri = URI.create(sourceUrl);
        validateUri(uri);
        HttpResponse<InputStream> response = send(uri, 0);
        byte[] bytes;
        try (InputStream body = response.body()) {
            String contentType = response.headers().firstValue("Content-Type").orElse("").split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
            String extension = EXTENSIONS.get(contentType);
            if (extension == null) throw new ImageAcquisitionException("圖片 Content-Type 不允許：" + contentType);
            long declaredSize = response.headers().firstValueAsLong("Content-Length").orElse(-1);
            if (declaredSize > MAX_BYTES) throw new ImageAcquisitionException("圖片超過 5MB 上限");
            bytes = body.readNBytes((int) MAX_BYTES + 1);
            if (bytes.length > MAX_BYTES) throw new ImageAcquisitionException("圖片超過 5MB 上限");
            if (bytes.length == 0) throw new ImageAcquisitionException("下載的圖片內容為空");

            Path target = modelDirectory.resolve("representative" + extension).normalize();
            verifyWithin(modelDirectory, target);
            Path temporary = Files.createTempFile(modelDirectory, "image-", ".tmp");
            try {
                Files.write(temporary, bytes);
                try {
                    Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException unsupportedAtomicMove) {
                    Files.move(temporary, target);
                }
            } finally {
                Files.deleteIfExists(temporary);
            }
            return publicUrl(modelDirectory, target);
        }
    }

    private HttpResponse<InputStream> send(URI uri, int redirects) throws IOException {
        if (redirects > 5) throw new ImageAcquisitionException("圖片來源重新導向次數過多");
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20))
                .header("User-Agent", "second-phone-platform-image-backfill/1.0")
                .GET().build();
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() >= 300 && response.statusCode() < 400) {
                try (InputStream ignored = response.body()) { }
                String location = response.headers().firstValue("Location")
                        .orElseThrow(() -> new ImageAcquisitionException("圖片重新導向缺少 Location"));
                URI redirected = uri.resolve(location);
                validateUri(redirected);
                return send(redirected, redirects + 1);
            }
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                try (InputStream ignored = response.body()) { }
                throw new ImageAcquisitionException("圖片來源 HTTP " + response.statusCode());
            }
            return response;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ImageAcquisitionException("圖片下載被中斷");
        }
    }

    private String createPlaceholder(Path modelDirectory, String productType, String modelName) throws IOException {
        Path target = modelDirectory.resolve("placeholder.png").normalize();
        verifyWithin(modelDirectory, target);
        if (!Files.exists(target)) {
            BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            try {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setColor(new Color(242, 245, 250));
                graphics.fillRect(0, 0, 800, 600);
                graphics.setColor(new Color(24, 39, 64));
                drawCentered(graphics, productType, new Font("SansSerif", Font.BOLD, 54), 270);
                graphics.setColor(new Color(82, 99, 128));
                drawCentered(graphics, modelName, new Font("SansSerif", Font.PLAIN, 34), 340);
            } finally {
                graphics.dispose();
            }
            ImageIO.write(image, "png", target.toFile());
        }
        return publicUrl(modelDirectory, target);
    }

    private void drawCentered(Graphics2D graphics, String text, Font font, int baseline) {
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        graphics.drawString(text, Math.max(30, (800 - metrics.stringWidth(text)) / 2), baseline);
    }

    private Path safeModelDirectory(String normalizedModelName) {
        String safe = Normalizer.normalize(normalizedModelName, Normalizer.Form.NFKC)
                .replaceAll("[^\\p{L}\\p{N}._ -]", "-")
                .replaceAll("[ .]+", "-")
                .replaceAll("-+", "-")
                .replaceAll("(^-|-$)", "")
                .toLowerCase(Locale.ROOT);
        if (safe.isBlank()) throw new ImageAcquisitionException("標準機型名稱無法安全化");
        if (safe.length() > 80) safe = safe.substring(0, 80);
        Path result = productDirectory.resolve(safe).normalize();
        verifyWithin(productDirectory, result);
        return result;
    }

    private Optional<Path> existingImage(Path modelDirectory, boolean includePlaceholder) throws IOException {
        for (String extension : EXTENSIONS.values()) {
            Path source = modelDirectory.resolve("representative" + extension);
            if (Files.isRegularFile(source)) return Optional.of(source);
        }
        Path placeholder = modelDirectory.resolve("placeholder.png");
        return includePlaceholder && Files.isRegularFile(placeholder) ? Optional.of(placeholder) : Optional.empty();
    }

    private String publicUrl(Path modelDirectory, Path file) {
        return "/uploads/products/" + modelDirectory.getFileName() + "/" + file.getFileName();
    }

    private void validateUri(URI uri) {
        if (!"https".equalsIgnoreCase(uri.getScheme())) throw new ImageAcquisitionException("圖片來源必須使用 HTTPS");
        String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);
        boolean allowed = host.equals("apple.com") || host.endsWith(".apple.com")
                || host.equals("wikimedia.org") || host.endsWith(".wikimedia.org");
        if (!allowed) throw new ImageAcquisitionException("圖片來源網域不在允許清單");
    }

    private void verifyWithin(Path parent, Path target) {
        if (!target.startsWith(parent)) throw new ImageAcquisitionException("圖片路徑不安全");
    }

    public static class ImageAcquisitionException extends RuntimeException {
        public ImageAcquisitionException(String message) { super(message); }
    }

    private static class PathsHolder {
        static Path productDirectory(String uploadDir) {
            return Path.of(uploadDir).toAbsolutePath().normalize().resolve("products");
        }
    }
}

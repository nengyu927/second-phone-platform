package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.secondphone.service.AppleProductImageSourceRegistry.ImageSource;
import com.example.secondphone.service.ProductImageBackfillStorageService.ImageAcquisitionException;

@ExtendWith(MockitoExtension.class)
class ProductImageBackfillStorageServiceTests {
    @TempDir Path directory;
    @Mock HttpClient client;
    @Mock HttpResponse<InputStream> response;

    @Test
    void createsReusableSafePlaceholderWithTypeAndModel() {
        ProductImageBackfillStorageService service = new ProductImageBackfillStorageService(directory, client);

        String first = service.placeholder("iPhone 15 Pro / 測試", "iPhone");
        String second = service.placeholder("iPhone 15 Pro / 測試", "iPhone");

        assertEquals(first, second);
        assertTrue(first.endsWith("/placeholder.png"));
        assertTrue(Files.isRegularFile(directory.resolve("iphone-15-pro-測試/placeholder.png")));
    }

    @Test
    void rejectsSourceOutsideAllowlistBeforeNetworkRequest() {
        ProductImageBackfillStorageService service = new ProductImageBackfillStorageService(directory, client);
        ImageSource source = new ImageSource("OTHER", "https://example.com/image.jpg", "https://example.com");

        ImageAcquisitionException exception = assertThrows(ImageAcquisitionException.class,
                () -> service.acquire("iPhone 15", "iPhone", source));

        assertEquals("圖片來源網域不在允許清單", exception.getMessage());
    }

    @Test
    void rejectsInvalidContentType() throws Exception {
        ProductImageBackfillStorageService service = new ProductImageBackfillStorageService(directory, client);
        stubResponse("text/html", 4, new byte[] {1, 2, 3, 4});
        ImageSource source = new ImageSource("APPLE_OFFICIAL", "https://www.apple.com/image", "https://www.apple.com");

        ImageAcquisitionException exception = assertThrows(ImageAcquisitionException.class,
                () -> service.acquire("iPhone 15", "iPhone", source));

        assertTrue(exception.getMessage().contains("Content-Type"));
    }

    @Test
    void rejectsDeclaredFileLargerThanFiveMegabytes() throws Exception {
        ProductImageBackfillStorageService service = new ProductImageBackfillStorageService(directory, client);
        stubResponse("image/jpeg", ProductImageBackfillStorageService.MAX_BYTES + 1, new byte[] {1});
        ImageSource source = new ImageSource("APPLE_OFFICIAL", "https://www.apple.com/image", "https://www.apple.com");

        ImageAcquisitionException exception = assertThrows(ImageAcquisitionException.class,
                () -> service.acquire("iPhone 15", "iPhone", source));

        assertEquals("圖片超過 5MB 上限", exception.getMessage());
    }

    @SuppressWarnings("unchecked")
    private void stubResponse(String contentType, long contentLength, byte[] body) throws Exception {
        HttpHeaders headers = HttpHeaders.of(
                Map.of("Content-Type", List.of(contentType), "Content-Length", List.of(String.valueOf(contentLength))),
                (name, value) -> true);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);
        when(response.statusCode()).thenReturn(200);
        when(response.headers()).thenReturn(headers);
        when(response.body()).thenReturn(new ByteArrayInputStream(body));
    }
}

package com.example.secondphone.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.secondphone.dto.ProductCsvImportFailure;
import com.example.secondphone.dto.ProductCsvImportResult;
import com.example.secondphone.entity.Brand;
import com.example.secondphone.entity.Category;
import com.example.secondphone.exception.BusinessException;
import com.example.secondphone.service.ProductCsvImportRowService.ImportOutcome;

@Service
public class ProductCsvImportService {
    private static final List<String> HEADERS = List.of("使用程度", "二手等級", "商品名稱", "備註", "價格");

    private final BrandService brands;
    private final CategoryService categories;
    private final ProductCsvImportRowService rowImporter;

    public ProductCsvImportService(BrandService brands, CategoryService categories, ProductCsvImportRowService rowImporter) {
        this.brands = brands;
        this.categories = categories;
        this.rowImporter = rowImporter;
    }

    public ProductCsvImportResult importCsv(MultipartFile file, Long categoryId, Long brandId) {
        if (file == null || file.isEmpty()) throw new BusinessException("請選擇 CSV 檔案");
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
            throw new BusinessException("只允許上傳 CSV 檔案");
        }
        if (categoryId == null) throw new BusinessException("請選擇分類");
        if (brandId == null) throw new BusinessException("請選擇品牌");

        Brand brand = brands.getEntity(brandId);
        Category category = categories.getEntity(categoryId);
        List<CsvRecord> records = parse(readUtf8(file));
        if (records.isEmpty() || !normalized(records.get(0).fields()).equals(HEADERS)) {
            throw new BusinessException("CSV 欄位必須依序為：使用程度、二手等級、商品名稱、備註、價格");
        }

        int total = 0;
        int success = 0;
        int skipped = 0;
        List<ProductCsvImportFailure> failures = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            CsvRecord record = records.get(i);
            if (record.fields().stream().allMatch(String::isBlank)) continue;
            total++;
            try {
                if (record.fields().size() != HEADERS.size()) throw new RowValidationException("欄位數量必須為 5");
                String name = record.fields().get(2).trim();
                if (name.isBlank()) throw new RowValidationException("商品名稱不可空白");
                String priceText = record.fields().get(4).trim();
                if (!priceText.matches("[1-9]\\d*")) throw new RowValidationException("價格必須為正整數");
                BigDecimal price = new BigDecimal(priceText);
                if (price.precision() > 12) throw new RowValidationException("價格超出允許範圍");
                String description = description(record.fields().get(0), record.fields().get(1), record.fields().get(3));
                ImportOutcome outcome = rowImporter.importRow(name, price, description, brand, category);
                if (outcome == ImportOutcome.SKIPPED) skipped++; else success++;
            } catch (RowValidationException exception) {
                failures.add(new ProductCsvImportFailure(record.lineNumber(), exception.getMessage()));
            } catch (RuntimeException exception) {
                failures.add(new ProductCsvImportFailure(record.lineNumber(), "商品儲存失敗"));
            }
        }
        return new ProductCsvImportResult(total, success, failures.size(), skipped, List.copyOf(failures));
    }

    private String readUtf8(MultipartFile file) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(file.getBytes())).toString();
        } catch (CharacterCodingException exception) {
            throw new BusinessException("CSV 檔案必須使用 UTF-8 編碼");
        } catch (IOException exception) {
            throw new BusinessException("無法讀取 CSV 檔案");
        }
    }

    private List<String> normalized(List<String> fields) {
        List<String> result = new ArrayList<>(fields);
        if (!result.isEmpty()) result.set(0, result.get(0).replaceFirst("^\\uFEFF", "").trim());
        for (int i = 1; i < result.size(); i++) result.set(i, result.get(i).trim());
        return result;
    }

    private String description(String usage, String grade, String note) {
        List<String> parts = new ArrayList<>();
        if (!usage.isBlank()) parts.add("使用程度：" + usage.trim());
        if (!grade.isBlank()) parts.add("二手等級：" + grade.trim());
        if (!note.isBlank()) parts.add("備註：" + note.trim());
        return String.join("\n", parts);
    }

    private List<CsvRecord> parse(String csv) {
        List<CsvRecord> records = new ArrayList<>();
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        int line = 1;
        int recordLine = 1;
        for (int i = 0; i < csv.length(); i++) {
            char current = csv.charAt(i);
            if (current == '"') {
                if (quoted && i + 1 < csv.length() && csv.charAt(i + 1) == '"') {
                    field.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (current == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else if ((current == '\r' || current == '\n') && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
                records.add(new CsvRecord(recordLine, List.copyOf(fields)));
                fields.clear();
                if (current == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') i++;
                line++;
                recordLine = line;
            } else {
                field.append(current == '\r' && quoted ? '\n' : current);
                if (current == '\n' || current == '\r') {
                    if (current == '\r' && i + 1 < csv.length() && csv.charAt(i + 1) == '\n') i++;
                    line++;
                }
            }
        }
        if (quoted) throw new BusinessException("CSV 引號格式錯誤");
        if (field.length() > 0 || !fields.isEmpty()) {
            fields.add(field.toString());
            records.add(new CsvRecord(recordLine, List.copyOf(fields)));
        }
        return records;
    }

    private record CsvRecord(int lineNumber, List<String> fields) { }
    private static class RowValidationException extends RuntimeException {
        RowValidationException(String message) { super(message); }
    }
}

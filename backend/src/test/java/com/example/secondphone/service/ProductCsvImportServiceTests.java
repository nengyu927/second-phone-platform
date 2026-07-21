package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.example.secondphone.dto.ProductCsvImportResult;
import com.example.secondphone.entity.Brand;
import com.example.secondphone.entity.Category;
import com.example.secondphone.service.ProductCsvImportRowService.ImportOutcome;

@ExtendWith(MockitoExtension.class)
class ProductCsvImportServiceTests {
    @Mock BrandService brands;
    @Mock CategoryService categories;
    @Mock ProductCsvImportRowService rowImporter;

    private ProductCsvImportService service;

    @BeforeEach
    void setUp() {
        service = new ProductCsvImportService(brands, categories, rowImporter);
        Brand brand = new Brand();
        brand.setName("蘋果");
        Category category = new Category();
        category.setName("手機");
        when(brands.getEntity(1L)).thenReturn(brand);
        when(categories.getEntity(2L)).thenReturn(category);
    }

    @Test
    void importsUtf8ChineseMultilineNotesAndBlankOptionalFieldsWhileReportingBadPrice() {
        String csv = "\uFEFF使用程度,二手等級,商品名稱,備註,價格\r\n"
                + "九成新,A級,蘋果手機,\"第一行\r\n第二行\",12000\r\n"
                + ",,空白欄位,,5000\r\n"
                + "一般,B級,錯誤價格,備註,12.5\r\n"
                + ",,,,\r\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "products.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        when(rowImporter.importRow(anyString(), any(BigDecimal.class), anyString(), any(), any()))
                .thenReturn(ImportOutcome.CREATED);

        ProductCsvImportResult result = service.importCsv(file, 2L, 1L);

        assertEquals(3, result.totalCount());
        assertEquals(2, result.successCount());
        assertEquals(1, result.failedCount());
        assertEquals(0, result.skippedCount());
        assertEquals(5, result.failures().get(0).lineNumber());
        assertEquals("價格必須為正整數", result.failures().get(0).reason());

        ArgumentCaptor<String> names = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> descriptions = ArgumentCaptor.forClass(String.class);
        verify(rowImporter, times(2)).importRow(names.capture(), any(BigDecimal.class), descriptions.capture(), any(), any());
        assertEquals("蘋果手機", names.getAllValues().get(0));
        assertTrue(descriptions.getAllValues().get(0).contains("備註：第一行\n第二行"));
        assertEquals("空白欄位", names.getAllValues().get(1));
        assertEquals("", descriptions.getAllValues().get(1));
    }

    @Test
    void countsDuplicateRowsAsSkipped() {
        String csv = "使用程度,二手等級,商品名稱,備註,價格\n九成新,A級,重複商品,,1000\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "products.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));
        when(rowImporter.importRow(anyString(), any(BigDecimal.class), anyString(), any(), any()))
                .thenReturn(ImportOutcome.SKIPPED);

        ProductCsvImportResult result = service.importCsv(file, 2L, 1L);

        assertEquals(1, result.totalCount());
        assertEquals(0, result.successCount());
        assertEquals(0, result.failedCount());
        assertEquals(1, result.skippedCount());
    }
}

package com.example.secondphone.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.secondphone.entity.Brand;
import com.example.secondphone.entity.Category;
import com.example.secondphone.entity.Product;
import com.example.secondphone.repository.ProductRepository;
import com.example.secondphone.service.ProductCsvImportRowService.ImportOutcome;

@ExtendWith(MockitoExtension.class)
class ProductCsvImportRowServiceTests {
    @Mock ProductRepository products;
    private ProductCsvImportRowService service;

    @BeforeEach
    void setUp() {
        service = new ProductCsvImportRowService(products);
    }

    @Test
    void skipsExistingNameAndPrice() {
        BigDecimal price = new BigDecimal("12000");
        when(products.existsByProductNameIgnoreCaseAndPrice("蘋果手機", price)).thenReturn(true);

        ImportOutcome outcome = service.importRow("蘋果手機", price, "", brand(), category());

        assertEquals(ImportOutcome.SKIPPED, outcome);
        verify(products, never()).saveAndFlush(any());
    }

    @Test
    void createsProductWithRequiredDefaultsAndSelectedRelations() {
        Brand brand = brand();
        Category category = category();
        BigDecimal price = new BigDecimal("12000");

        ImportOutcome outcome = service.importRow("蘋果手機", price, "使用程度：九成新", brand, category);

        assertEquals(ImportOutcome.CREATED, outcome);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(products).saveAndFlush(captor.capture());
        Product product = captor.getValue();
        assertEquals("蘋果手機", product.getProductName());
        assertEquals("蘋果手機", product.getModel());
        assertEquals(price, product.getPrice());
        assertEquals(1, product.getStock());
        assertEquals("ACTIVE", product.getStatus());
        assertSame(brand, product.getBrandEntity());
        assertSame(category, product.getCategory());
    }

    private Brand brand() {
        Brand brand = new Brand();
        brand.setName("蘋果");
        return brand;
    }

    private Category category() {
        Category category = new Category();
        category.setName("手機");
        return category;
    }
}

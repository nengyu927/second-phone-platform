package com.example.secondphone;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.entity.*;
import com.example.secondphone.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:catalog-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa", "spring.datasource.password=",
        "app.jwt.secret=test-only-jwt-secret-at-least-32-characters",
        "app.upload-dir=target/test-uploads"
})
@AutoConfigureMockMvc
@Transactional
class ProductCatalogIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired MemberRepository members;
    @Autowired PasswordEncoder encoder;

    @Test void publicCatalogSupportsBrandCategoryProductAndPagination() throws Exception {
        String token = token(MemberRole.ADMIN, "catalog-admin");
        String brandBody = mvc.perform(post("/api/admin/brands").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Apple\",\"active\":true}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long brandId = mapper.readTree(brandBody).get("id").asLong();
        String categoryBody = mvc.perform(post("/api/admin/categories").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"旗艦手機\",\"active\":true}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long categoryId = mapper.readTree(categoryBody).get("id").asLong();
        String productJson = """
                {"productCode":"SP-TEST-001","productName":"iPhone 15 Pro","brandId":%d,"categoryId":%d,
                "model":"A3101","storageCapacity":"256GB","color":"鈦金屬","conditionLevel":"LIKE_NEW",
                "cost":20000,"price":25900,"stock":3,"reservedStock":1,"status":"ACTIVE","featured":true}
                """.formatted(brandId, categoryId);
        String productBody = mvc.perform(post("/api/products").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(productJson))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.availableStock").value(2))
                .andReturn().getResponse().getContentAsString();
        long productId = mapper.readTree(productBody).get("id").asLong();
        MockMultipartFile image = new MockMultipartFile("file", "phone.png", "image/png", new byte[] { 1, 2, 3, 4 });
        mvc.perform(multipart("/api/admin/products/{id}/images/upload", productId).file(image)
                        .param("primaryImage", "true").header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.startsWith("/uploads/products/")));
        mvc.perform(get("/api/products").param("keyword", "iPhone").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].productCode").value("SP-TEST-001"))
                .andExpect(jsonPath("$.content[0].images[0].primaryImage").value(true));
        mvc.perform(get("/api/brands")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Apple"));
    }

    @Test void inventoryMovementIsTransactionalAndRejectsNegativeStock() throws Exception {
        String token = token(MemberRole.ADMIN, "stock-admin");
        String created = mvc.perform(post("/api/products").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                        {"productName":"庫存測試機","brand":"Test","model":"T1","conditionLevel":"GOOD",
                        "price":5000,"stock":2,"reservedStock":0,"status":"ACTIVE","featured":false}
                        """))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long productId = mapper.readTree(created).get("id").asLong();
        mvc.perform(post("/api/admin/inventory/products/{id}/adjustments", productId).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"OUT\",\"quantity\":1,\"reason\":\"展示出庫\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.afterStock").value(1));
        mvc.perform(post("/api/admin/inventory/products/{id}/adjustments", productId).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"OUT\",\"quantity\":2,\"reason\":\"超量出庫\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/admin/inventory/movements").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test void customerCannotMaintainCatalog() throws Exception {
        String token = token(MemberRole.CUSTOMER, "catalog-customer");
        mvc.perform(post("/api/admin/brands").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Nope\",\"active\":true}"))
                .andExpect(status().isForbidden());
    }

    private String token(MemberRole role, String account) throws Exception {
        Member member = new Member(); member.setAccount(account); member.setEmail(account + "@example.com"); member.setName("測試帳號");
        member.setPassword(encoder.encode("password123")); member.setRole(role); member.setStatus(MemberStatus.ACTIVE); members.save(member);
        String response = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"" + account + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode json = mapper.readTree(response); assertThat(json.get("user").has("password")).isFalse(); return json.get("token").asText();
    }
}

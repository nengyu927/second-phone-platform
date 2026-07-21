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
import org.springframework.transaction.annotation.Transactional;
import com.example.secondphone.entity.*;
import com.example.secondphone.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-test;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "app.jwt.secret=test-only-jwt-secret-at-least-32-characters"
})
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired MemberRepository members;
    @Autowired PasswordEncoder encoder;

    @Test void registerLoginMeAndRoleProtection() throws Exception {
        String register = "{\"username\":\"customer01\",\"email\":\"customer01@example.com\",\"password\":\"password123\",\"name\":\"測試會員\",\"phone\":\"0912345678\",\"role\":\"ADMIN\"}";
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist());
        Member saved = members.findByEmailIgnoreCase("customer01@example.com").orElseThrow();
        assertThat(saved.getPassword()).startsWith("$2");
        assertThat(encoder.matches("password123", saved.getPassword())).isTrue();
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(register))
                .andExpect(status().isConflict());
        String login = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"account\":\"customer01\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.password").doesNotExist()).andReturn().getResponse().getContentAsString();
        JsonNode json = mapper.readTree(login); String token = json.get("token").asText();
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.email").value("customer01@example.com"));
        mvc.perform(put("/api/member/profile").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"更新姓名\",\"phone\":\"0987654321\",\"memberId\":999}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("更新姓名"))
                .andExpect(jsonPath("$.phone").value("0987654321"))
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + token)).andExpect(status().isForbidden());
    }

    @Test void unauthenticatedAndBadPasswordAreRejected() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"account\":\"missing\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void disabledMemberCannotLogin() throws Exception {
        Member member = member("disabled", "disabled@example.com", MemberRole.CUSTOMER, MemberStatus.DISABLED);
        members.save(member);
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"account\":\"disabled\",\"password\":\"password123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test void adminCanAccessDashboard() throws Exception {
        members.save(member("admin-test", "admin-test@example.com", MemberRole.ADMIN, MemberStatus.ACTIVE));
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"account\":\"admin-test\",\"password\":\"password123\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String token = mapper.readTree(body).get("token").asText();
        mvc.perform(get("/api/dashboard/summary").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }

    private Member member(String account, String email, MemberRole role, MemberStatus status) {
        Member member = new Member(); member.setAccount(account); member.setEmail(email);
        member.setPassword(encoder.encode("password123")); member.setName("測試帳號");
        member.setRole(role); member.setStatus(status); return member;
    }
}

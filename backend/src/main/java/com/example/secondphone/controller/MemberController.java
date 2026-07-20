package com.example.secondphone.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.secondphone.entity.Member;
import com.example.secondphone.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member Management", description = "會員資料 CRUD。password 為 write-only，不會出現在 API 回應。")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "查詢或搜尋會員", description = "可依帳號或姓名關鍵字搜尋；未提供關鍵字時回傳全部會員。")
    @ApiResponse(responseCode = "200", description = "查詢成功")
    public ResponseEntity<List<Member>> findAll(
            @Parameter(description = "會員帳號或姓名關鍵字")
            @RequestParam(name = "keyword", required = false) String keyword) {
        return ResponseEntity.ok(memberService.search(keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢單一會員")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "404", description = "會員不存在")
    })
    public ResponseEntity<Member> findById(
            @Parameter(description = "會員 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    @PostMapping
    @Operation(summary = "新增會員")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "新增成功"),
            @ApiResponse(responseCode = "400", description = "輸入資料驗證失敗"),
            @ApiResponse(responseCode = "409", description = "帳號已存在")
    })
    public ResponseEntity<Member> create(@Valid @RequestBody Member member) {
        Member createdMember = memberService.create(member);
        return ResponseEntity.created(URI.create("/api/members/" + createdMember.getId())).body(createdMember);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改會員")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "400", description = "輸入資料驗證失敗"),
            @ApiResponse(responseCode = "404", description = "會員不存在"),
            @ApiResponse(responseCode = "409", description = "帳號已被其他會員使用")
    })
    public ResponseEntity<Member> update(
            @Parameter(description = "會員 ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody Member member) {
        return ResponseEntity.ok(memberService.update(id, member));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "刪除會員")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "刪除成功"),
            @ApiResponse(responseCode = "404", description = "會員不存在")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "會員 ID", example = "1") @PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

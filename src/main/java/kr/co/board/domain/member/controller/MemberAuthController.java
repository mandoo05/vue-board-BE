package kr.co.board.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.board.config.response.ApiResponse;
import kr.co.board.domain.member.dto.MemberAuthRequest;
import kr.co.board.domain.member.service.MemberAuthService;
import kr.co.board.security.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Validated
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "회원 관련 API")
public class MemberAuthController {
    private final MemberAuthService memberAuthService;

    @Operation(summary = "회원가입 API")
    @PostMapping("/signup")
    public ApiResponse<Void> save(@Valid @RequestBody MemberAuthRequest dto) {
        memberAuthService.save(dto);
        return ApiResponse.success();
    }

    @Operation(summary = "jwt 재발급")
    @PostMapping("/refresh")
    public ApiResponse<JwtResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        var data =  memberAuthService.refresh(request, response);
        return ApiResponse.success(data);
    }
}

package kr.co.board.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.board.config.response.ApiResponse;
import kr.co.board.domain.member.dto.MemberAuthRequest;
import kr.co.board.domain.member.service.MemberAuthService;
import kr.co.board.security.JwtResponse;
import kr.co.board.security.auth.MemberDetails;
import kr.co.board.security.config.CookieProvider;
import kr.co.board.security.config.JwtProvider;
import kr.co.board.security.config.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final RefreshTokenService refreshTokenService;
    private final CookieProvider cookieProvider;

    @Operation(summary = "회원가입 API")
    @PostMapping("/signup")
    public ApiResponse<Void> save(@Valid @RequestBody MemberAuthRequest dto) {
        memberAuthService.save(dto);
        return ApiResponse.noContent();
    }

    @Operation(summary = "jwt 재발급 API")
    @PostMapping("/refresh")
    public ApiResponse<JwtResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        var data =  memberAuthService.refresh(request, response);
        return ApiResponse.success(data);
    }

    @Operation(summary = "로그아웃 API")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal MemberDetails member,
            HttpServletResponse response
    ) {
        refreshTokenService.delete(member.getId());

        var deleteCookie = cookieProvider.buildCookie(
                JwtProvider.REFRESH_HEADER_STRING,
                "",
                0
        );
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);

        return ApiResponse.noContent();
    }
}

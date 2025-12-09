package kr.co.board.domain.member.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.board.config.exception.CustomException;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.config.response.ApiResponse;
import kr.co.board.domain.member.dto.MemberAuthRequest;
import kr.co.board.domain.member.infra.entity.Member;
import kr.co.board.domain.member.infra.repository.MemberRepository;
import kr.co.board.security.JwtResponse;
import kr.co.board.security.auth.MemberDetails;
import kr.co.board.security.auth.MemberRole;
import kr.co.board.security.auth.MemberStatus;
import kr.co.board.security.config.CookieProvider;
import kr.co.board.security.config.JwtProvider;
import kr.co.board.security.config.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class MemberAuthService {
    private final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";
    private final String DUPLICATE_USERNAME = "이미 사용중인 계정입니다.";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final CookieProvider cookieProvider;

    @Transactional
    public void save(MemberAuthRequest dto) {
        if (!dto.password().equals(dto.confirmPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (memberRepository.findByUsername(dto.username()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        Member member = Member.builder()
                .username(dto.username())
                .nickname(dto.nickname())
                .password(passwordEncoder.encode(dto.password()))
                .role(MemberRole.ROLE_USER)
                .status(MemberStatus.ACTIVE)
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public JwtResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken =
                cookieProvider.getCookieStringByRequest(request, JwtProvider.REFRESH_HEADER_STRING);

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        MemberDetails memberDetails;
        try {
            memberDetails = jwtProvider.parseToken(refreshToken, jwtProvider.getRefreshKey());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        Member member =
                memberRepository
                        .findById(memberDetails.getId())
                        .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        if (!MemberStatus.ACTIVE.equals(member.getStatus())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        if (!refreshTokenService.validate(member.getId(), refreshToken)) {
            refreshTokenService.delete(member.getId());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        MemberDetails refreshedDetails = new MemberDetails(member);

        String accessToken =
                jwtProvider.createToken(
                        refreshedDetails,
                        jwtProvider.JWT_EXPIRATION_MILLI_TIME,
                        jwtProvider.getJwtKey());

        String newRefreshToken =
                jwtProvider.createToken(
                        refreshedDetails,
                        jwtProvider.REFRESH_EXPIRATION_MILLI_TIME,
                        jwtProvider.getRefreshKey());

        refreshTokenService.save(
                member.getId(),
                newRefreshToken,
                Duration.ofMillis(jwtProvider.REFRESH_EXPIRATION_MILLI_TIME));

        Cookie refreshCookie =
                cookieProvider.buildCookie(
                        JwtProvider.REFRESH_HEADER_STRING,
                        newRefreshToken,
                        jwtProvider.REFRESH_EXPIRATION_MILLI_TIME / 1000);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);

        return JwtResponse.from(accessToken);
    }
}

package kr.co.board.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.config.response.ApiResponse;
import kr.co.board.config.response.ErrorResponse;
import kr.co.board.security.JwtResponse;
import kr.co.board.security.auth.MemberDetails;
import kr.co.board.security.config.CookieProvider;
import kr.co.board.security.config.JwtProvider;
import kr.co.board.security.config.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final ObjectMapper objectMapper;
  private final RefreshTokenService refreshTokenService;
  private final CookieProvider cookieProvider;

  @Override
  public Authentication attemptAuthentication(
          HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String username = obtainUsername(request);
    String password = obtainPassword(request);
    Authentication token = new UsernamePasswordAuthenticationToken(username, password);
    return authenticationManager.authenticate(token);
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult)
      throws IOException {
    MemberDetails member = (MemberDetails) authResult.getPrincipal();

    String accessToken =
        jwtProvider.createToken(
            member, jwtProvider.JWT_EXPIRATION_MILLI_TIME, jwtProvider.getJwtKey());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    JwtResponse jwtResponse = JwtResponse.from(accessToken);
    ApiResponse<JwtResponse> rtn = ApiResponse.success(jwtResponse);

    String refreshToken =
        jwtProvider.createToken(
            member, jwtProvider.REFRESH_EXPIRATION_MILLI_TIME, jwtProvider.getRefreshKey());
    refreshTokenService.save(
        member.getId(),
        refreshToken,
        Duration.ofMillis(jwtProvider.REFRESH_EXPIRATION_MILLI_TIME));
    Cookie refreshTokenCookie =
        cookieProvider.buildCookie(
            JwtProvider.REFRESH_HEADER_STRING,
            refreshToken,
            jwtProvider.REFRESH_EXPIRATION_MILLI_TIME / 1000);

    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);

    objectMapper.writeValue(response.getWriter(), rtn);
    response.setStatus(HttpStatus.OK.value());
  }

  @Override
  protected void unsuccessfulAuthentication(
          HttpServletRequest request,
          HttpServletResponse response,
          AuthenticationException failed
  ) throws IOException, ServletException {

    ErrorCode error = ErrorCode.INVALID_CREDENTIALS;

    ErrorResponse body = ErrorResponse.builder()
            .status(error.getStatus().value())
            .code(error.getCode())
            .message(error.getMessage())
            .errors(null)
            .build();

    response.setStatus(error.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    objectMapper.writeValue(response.getWriter(), body);
  }
}

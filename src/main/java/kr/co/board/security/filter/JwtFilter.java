package kr.co.board.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.config.response.ErrorResponse;
import kr.co.board.security.auth.MemberDetails;
import kr.co.board.security.auth.MemberStatus;
import kr.co.board.security.config.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          HttpServletResponse response,
          FilterChain filterChain)
          throws ServletException, IOException {

    String header = request.getHeader(JwtProvider.JWT_HEADER_STRING);

    // 토큰 헤더 없으면 패스
    if (header == null || header.equals("null") || header.equals("undefined")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.replace(JwtProvider.TOKEN_PREFIX_JWT, "").trim();

    if (token.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      MemberDetails memberDetails = jwtProvider.parseToken(token, jwtProvider.getJwtKey());

      if (memberDetails.getStatus() != MemberStatus.ACTIVE) {
        writeErrorResponse(response, ErrorCode.UNAUTHORIZED);
        return;
      }

      Authentication authentication =
              new UsernamePasswordAuthenticationToken(
                      memberDetails, null, memberDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);

    } catch (ExpiredJwtException e) {
      writeErrorResponse(response, ErrorCode.TOKEN_EXPIRED);
    } catch (MalformedJwtException | UnsupportedJwtException e) {
      writeErrorResponse(response, ErrorCode.INVALID_TOKEN);
    } catch (IllegalArgumentException e) {
      writeErrorResponse(response, ErrorCode.UNAUTHORIZED);
    } catch (Exception e) {
      writeErrorResponse(response, ErrorCode.UNAUTHORIZED);
    }
  }

  private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode)
          throws IOException {

    log.error("[{}] {}", errorCode.getCode(), errorCode.getMessage());

    ErrorResponse body = ErrorResponse.builder()
            .status(errorCode.getStatus().value())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .errors(null)
            .build();

    response.setStatus(errorCode.getStatus().value());
    response.setContentType("application/json;charset=UTF-8");
    objectMapper.writeValue(response.getWriter(), body);
  }
}

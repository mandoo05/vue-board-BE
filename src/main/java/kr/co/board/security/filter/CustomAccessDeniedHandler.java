package kr.co.board.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.config.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json;charset=UTF-8");

    ApiResponse<?> body = ApiResponse.error(
            ErrorCode.FORBIDDEN.getStatus().value(),
            ErrorCode.FORBIDDEN.getCode(),
            ErrorCode.FORBIDDEN.getMessage()
    );
    objectMapper.writeValue(response.getWriter(), body);
  }
}

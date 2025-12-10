package kr.co.board.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.config.response.ErrorResponse;
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
          AccessDeniedException accessDeniedException
  ) throws IOException {

    ErrorCode error = ErrorCode.FORBIDDEN;

    ErrorResponse body = ErrorResponse.builder()
            .status(error.getStatus().value())
            .code(error.getCode())
            .message(error.getMessage())
            .errors(null)
            .build();

    response.setStatus(error.getStatus().value());
    response.setContentType("application/json;charset=UTF-8");

    objectMapper.writeValue(response.getWriter(), body);
  }
}

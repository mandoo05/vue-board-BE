package kr.co.board.config.exception;

import kr.co.board.config.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

        ErrorCode errorCode = ex.getErrorCode();

        log.error("[CustomException] code={}, message={}",
                errorCode.getCode(),
                ex.getMessageToClient()
        );

        ErrorResponse body = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .code(errorCode.getCode())
                .message(ex.getMessageToClient())   // ★ 여기만 바꾸면 됨
                .errors(null)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(body);
    }

    /**
     * Validation 실패 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {

        List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> ErrorResponse.FieldErrorDetail.builder()
                        .field(err.getField())
                        .message(err.getDefaultMessage())
                        .build())
                .toList();

        ErrorResponse body = ErrorResponse.builder()
                .status(400)
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(ErrorCode.VALIDATION_ERROR.getMessage())
                .errors(fieldErrors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 예상하지 못한 서버 오류
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {

        log.error("[Unexpected Exception]", ex);

        ErrorCode e = ErrorCode.INTERNAL_SERVER_ERROR;

        ErrorResponse body = ErrorResponse.builder()
                .status(e.getStatus().value())
                .code(e.getCode())
                .message(e.getMessage())
                .errors(null)
                .build();

        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean anonymous = auth == null || auth instanceof AnonymousAuthenticationToken;

        ErrorCode error = anonymous ? ErrorCode.UNAUTHORIZED : ErrorCode.FORBIDDEN;

        log.warn("[AccessDeniedException] {}", ex.getMessage());

        ErrorResponse body = ErrorResponse.builder()
                .status(error.getStatus().value())
                .code(error.getCode())
                .message(error.getMessage())
                .errors(null)
                .build();

        return ResponseEntity.status(error.getStatus()).body(body);
    }
}


package kr.co.board.config.exception;

import kr.co.board.config.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * CustomException 잡기
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        ErrorCode error = e.getErrorCode();
        log.error("[CustomException] {}", error.getMessage());

        return ResponseEntity
                .status(error.getStatus())
                .body(ApiResponse.error(
                        error.getStatus().value(),
                        error.getCode(),
                        error.getMessage()
                ));
    }

    /**
     * @Valid 유효성 검사 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException e) {

        String message = Optional.ofNullable(e.getBindingResult().getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("유효성 검증에 실패했습니다.");

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(
                        400,
                        ErrorCode.VALIDATION_ERROR.getCode(),
                        message
                ));
    }

    /**
     * 예상 못한 서버 오류
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("[Unexpected Exception]", e);

        ErrorCode error = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity
                .status(error.getStatus())
                .body(ApiResponse.error(
                        error.getStatus().value(),
                        error.getCode(),
                        error.getMessage()
                ));
    }
}

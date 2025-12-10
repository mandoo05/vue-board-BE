package kr.co.board.config.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "API 오류 응답")
public class ErrorResponse {

    @Schema(description = "HTTP 상태값")
    private final int status;

    @Schema(description = "오류 코드")
    private final String code;

    @Schema(description = "오류 메시지")
    private final String message;

    @Schema(description = "필드 오류 목록 (Validation 실패 시)")
    private final List<FieldErrorDetail> errors;

    @Getter
    @Builder
    @Schema(description = "Validation 필드 오류 정보")
    public static class FieldErrorDetail {

        @Schema(description = "필드 이름")
        private final String field;

        @Schema(description = "오류 메시지")
        private final String message;
    }
}

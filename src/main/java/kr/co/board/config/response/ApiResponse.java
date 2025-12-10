package kr.co.board.config.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "API 성공 응답")
public class ApiResponse<T> {

    @Schema(description = "HTTP 상태값 (200, 201, 204 등)")
    private final int status;

    @Schema(description = "성공 코드")
    private final String code;

    @Schema(description = "응답 메시지")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message("요청이 성공했습니다.")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String msg) {
        return ApiResponse.<T>builder()
                .status(200)
                .code("SUCCESS")
                .message(msg)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> successMessage(String msg) {
        return ApiResponse.<Void>builder()
                .status(200)
                .code("SUCCESS")
                .message(msg)
                .data(null)
                .build();
    }

    public static ApiResponse<Void> noContent() {
        return ApiResponse.<Void>builder()
                .status(204)
                .code("SUCCESS")
                .message(null)
                .data(null)
                .build();
    }
}

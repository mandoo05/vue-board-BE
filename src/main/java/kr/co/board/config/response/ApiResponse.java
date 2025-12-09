package kr.co.board.config.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final int status;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(int status, String code, String message, T data) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "SUCCESS", "요청이 성공했습니다.", null);
    }

    public static <T> ApiResponse<T> error(int status, String code, String message) {
        return new ApiResponse<>(status, code, message, null);
    }
}

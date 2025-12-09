package kr.co.board.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400 - 잘못된 요청
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "REQ001", "잘못된 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "REQ002", "유효성 검증 실패"),

    // 401 - 인증 필요
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH003", "토큰이 만료되었습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH004", "유효하지 않은 인증 정보입니다."),

    // 403 - 권한 없음
    FORBIDDEN(HttpStatus.FORBIDDEN, "AUTH100", "접근 권한이 없습니다."),

    // 404 - 리소스 없음
    NOT_FOUND(HttpStatus.NOT_FOUND, "REQ404", "리소스를 찾을 수 없습니다."),

    // 409 - 중복 오류
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "REQ409", "이미 존재하는 리소스입니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "USR409", "이미 존재하는 사용자입니다."),

    // 500 - 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SRV000", "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}

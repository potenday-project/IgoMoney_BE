package igoMoney.BE.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // User 예외
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    EXIST_MEMBER_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),

    // Token 예외
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),

    // login 예외
    LOGIN_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "로그인 요청 오류");



    private final HttpStatus httpStatus;
    private final String detail;
}

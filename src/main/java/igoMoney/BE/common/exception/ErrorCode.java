package igoMoney.BE.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // User 예외
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    EXIST_USER_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
    EXIST_USER_CHALLENGE(HttpStatus.CONFLICT, "이미 등록한 챌린지가 있거나 진행중인 챌린지가 있습니다."),
    USER_NOT_IN_CHALLENGE(HttpStatus.CONFLICT, "참여중인 챌린지가 없습니다."),

    // Token 예외
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    ID_TOKEN_INVALID_1(HttpStatus.UNAUTHORIZED, "ID토큰이 유효하지 않습니다.(토큰 서명 검증 or 구조 문제)"),
    ID_TOKEN_INVALID_2(HttpStatus.UNAUTHORIZED, "ID토큰이 유효하지 않습니다.(토큰 디코딩 문제)"),
    ID_TOKEN_INVALID_3(HttpStatus.UNAUTHORIZED, "ID토큰이 유효하지 않습니다.(서명 검증 결과)"),
    ID_TOKEN_INVALID_4(HttpStatus.UNAUTHORIZED, "ID토큰이 유효하지 않습니다.(서명 검증 결과)"),
    ID_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "ID토큰이 만료되었습니다."),
    AUTH_CODE_INVALID(HttpStatus.UNAUTHORIZED, "Authorization Code가 유효하지 않습니다."),

    // login 예외
    LOGIN_CONNECTION_ERROR(HttpStatus.BAD_REQUEST, "로그인 요청 오류"),
    
    // Challenege 예외
    NOT_FOUND_CHALLENGE(HttpStatus.NOT_FOUND, "해당 챌린지를 찾을 수 없습니다.");



    private final HttpStatus httpStatus;
    private final String detail;
}

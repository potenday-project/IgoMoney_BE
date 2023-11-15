package igoMoney.BE.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    // 일반 에러
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {

        FieldError fieldError = e.getFieldError();

        if (Objects.isNull(fieldError)) {
            return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST);
        }

        log.info(fieldError.toString());
        return ErrorResponse.toResponseEntity(HttpStatus.BAD_REQUEST, fieldError);
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleSizeLimitExceededException(FileSizeLimitExceededException ex) {
        log.info("SizeLimitExceededException :: " + ex.getLocalizedMessage());
        return ErrorResponse.toResponseEntity(new CustomException(ErrorCode.IMAGE_SIZE_EXCEEDED));
    }
}


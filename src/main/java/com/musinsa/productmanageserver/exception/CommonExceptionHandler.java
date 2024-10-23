package com.musinsa.productmanageserver.exception;


import com.musinsa.productmanageserver.common.dto.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(NotFoundResourceException.class)
    protected ResponseEntity<BaseResponse<Object>> handleNotFoundResourceException(
        NotFoundResourceException exception) {

        log.error("NotFoundResourceException: {}", exception.getMessage());

        BaseResponse<Object> response = BaseResponse.builder()
            .resultCode(BaseResponse.FAIL)
            .data("해당 리소스를 찾을 수 없습니다.")
            .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<BaseResponse<Object>> handleIllegalArgumentException(
        IllegalArgumentException exception) {

        log.error("IllegalArgumentException: {}", exception.getMessage());

        BaseResponse<Object> response = BaseResponse.builder()
            .resultCode(BaseResponse.FAIL)
            .data("잘못된 요청 입니다.")
            .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException exception) {

        log.error("MethodArgumentNotValidException: {}", exception.getMessage());

        BaseResponse<Object> response = BaseResponse.builder()
            .resultCode(BaseResponse.FAIL)
            .data("잘못된 요청 입니다.")
            .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<BaseResponse<Object>> handleRuntimeException(RuntimeException exception) {

        log.error("RuntimeException: {}", exception.getMessage());

        BaseResponse<Object> response = BaseResponse.builder()
            .resultCode(BaseResponse.FAIL)
            .data("서버 에러가 발생했습니다.")
            .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<BaseResponse<Object>> handleCommonException(
        MethodArgumentNotValidException exception) {

        log.error("Exception: {}", exception.getMessage());

        BaseResponse<Object> response = BaseResponse.builder()
            .resultCode(BaseResponse.FAIL)
            .data("서버 에러가 발생했습니다.")
            .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

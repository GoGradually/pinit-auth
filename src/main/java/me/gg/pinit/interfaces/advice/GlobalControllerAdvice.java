package me.gg.pinit.interfaces.advice;

import me.gg.pinit.infrastructure.oauth.exception.OIDCException;
import me.gg.pinit.service.exception.ProviderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, ProviderNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(OIDCException.class)
    public ResponseEntity<ErrorResponse> handleOidc(OIDCException ex) {
        log.warn("OIDC 오류 발생", ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        log.error("처리되지 않은 예외", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of("서버 오류가 발생했습니다."));
    }
}

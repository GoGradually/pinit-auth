package me.gg.pinit.infrastructure.oauth.exception;

public class OIDCException extends RuntimeException {
    public OIDCException(String message) {
        super(message);
    }
}

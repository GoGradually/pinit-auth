package me.gg.pinit.infra.oauth.exception;

public class OIDCException extends RuntimeException {
    public OIDCException(String message) {
        super(message);
    }
}

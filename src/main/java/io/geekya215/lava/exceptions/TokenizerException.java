package io.geekya215.lava.exceptions;

public class TokenizerException extends Throwable {
    public TokenizerException(String token) {
        super("invalid token " + token);
    }
}

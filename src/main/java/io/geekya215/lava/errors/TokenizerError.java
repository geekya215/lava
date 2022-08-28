package io.geekya215.lava.errors;

public class TokenizerError extends Throwable {
    public TokenizerError(String token) {
        super("invalid token " + token);
    }
}

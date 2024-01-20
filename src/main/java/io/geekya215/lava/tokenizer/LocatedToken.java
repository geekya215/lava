package io.geekya215.lava.tokenizer;

public record LocatedToken(Token tok, Location loc) {
    @Override
    public String toString() {
        return switch (tok) {
            case Token.EOF _ -> "end of file";
            default -> String.format("%s at line:%d, column:%d", tok, loc.line(), loc.column());
        };
    }
}

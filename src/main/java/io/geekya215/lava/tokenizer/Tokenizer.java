package io.geekya215.lava.tokenizer;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Tokenizer {
    private final String src;
    private int line;
    private int col;

    public Tokenizer(String src) {
        this.src = src;
        this.line = 1;
        this.col = 1;
    }

    public List<LocatedToken> tokenizeWithLocation() {
        List<LocatedToken> tokens = new ArrayList<>();
        Iterator<Character> iter = src.chars().mapToObj(c -> (char) c).iterator();
        Peekable<Character> chars = new Peekable<>(iter);

        while (nextToken(chars) instanceof Option.Some<Token>(Token tok)) {
            LocatedToken locatedToken = new LocatedToken(tok, new Location(line, col));
            tokens.add(locatedToken);

            switch (tok) {
                case Token.SpaceChar(WhiteSpace.NewLine _) -> {line+=1; col=1;}
                case Token.SpaceChar(WhiteSpace.Tab _) -> col += 4;
                case Token.Number(String v) -> col += v.length();
                case Token.Symbol(String v) -> col += v.length();
                case Token.Quote(String v) -> col += (v.length() + 1);
                default -> col += 1;
            }
        }

        return tokens;
    }

    public List<Token> tokenize() {
        return tokenizeWithLocation().stream().map(LocatedToken::tok).collect(Collectors.toList());
    }

    private Option<Token> nextToken(Peekable<Character> chars) {
        return switch (chars.peek()) {
            case Option.Some(Character c) -> switch (c) {
                case ' ' -> consume(chars, new Token.SpaceChar(new WhiteSpace.Space()));
                case '\t' -> consume(chars, new Token.SpaceChar(new WhiteSpace.Tab()));
                case '\n' -> consume(chars, new Token.SpaceChar(new WhiteSpace.NewLine()));
                case '\'' -> {
                    chars.next();
                    String s = peekTakeWhite(chars, ch -> Character.isAlphabetic(ch) || Character.isDigit(ch));
                    if (s.isEmpty()) {
                        throw new TokenizeException(String.format("invalid quote format at line: %d, column: %d", line, col));
                    }
                    yield Option.some(new Token.Quote(s));
                }
                case '(' -> consume(chars, new Token.LeftParen());
                case ')' -> consume(chars, new Token.RightParen());
                default -> {
                    if (Character.isDigit(c)) {
                        String s = peekTakeWhite(chars, Character::isDigit);
                        if (chars.peek() instanceof Option.Some<Character>(Character junk) && Character.isAlphabetic(junk)) {
                            throw new TokenizeException(String.format("invalid number format at line: %d, column: %d", line, col));
                        }
                        yield new Option.Some<>(new Token.Number(s));
                    } else if (Character.isAlphabetic(c)) {
                        String s = peekTakeWhite(chars, ch -> Character.isAlphabetic(ch) || Character.isDigit(ch));
                        yield new Option.Some<>(new Token.Symbol(s));
                    }
                    throw new TokenizeException(String.format("unrecognized char => %c at line: %d, column: %d", c, line, col));
                }
            };
            case Option.None<Character> _ -> Option.none();
        };
    }

    private String peekTakeWhite(Peekable<Character> chars, Predicate<Character> predicate) {
        StringBuilder sb = new StringBuilder();
        while (chars.peek() instanceof Option.Some<Character>(Character c)) {
            if (predicate.test(c)) {
                chars.next();
                sb.append(c);
            } else {
                break;
            }
        }
        return sb.toString();
    }

    private Option<Token> consume(Peekable<Character> chars, Token token) {
        chars.next();
        return Option.some(token);
    }
}
package io.geekya215.lava.tokenizer;


import io.geekya215.lava.common.Option;
import io.geekya215.lava.common.Peekable;
import io.geekya215.lava.exception.TokenizeException;

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
                case Token.SpaceChar(WhiteSpace.NewLine _) -> {
                    line += 1;
                    col = 1;
                }
                case Token.SpaceChar(WhiteSpace.Tab _) -> col += 4;
                case Token.Number(String v) -> col += v.length();
                case Token.Symbol(String v) -> col += v.length();
                case Token.Operator(Operators op) -> {
                    switch (op) {
                        case Operators.LtEq _, Operators.GtEq _, Operators.NotEq _,
                                Operators.LeftShift _, Operators.RightShift _ -> col += 2;
                        default -> col += 1;
                    }
                }
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

                case '\'' -> consume(chars, new Token.Quote());

                case '(' -> consume(chars, new Token.LeftParen());
                case ')' -> consume(chars, new Token.RightParen());

                case '+' -> consume(chars, new Token.Operator(new Operators.Plus()));
                case '-' -> consume(chars, new Token.Operator(new Operators.Minus()));
                case '*' -> consume(chars, new Token.Operator(new Operators.Mul()));
                case '/' -> consume(chars, new Token.Operator(new Operators.Div()));
                case '%' -> consume(chars, new Token.Operator(new Operators.Mod()));

                case '=' -> consume(chars, new Token.Operator(new Operators.Eq()));
                case '>' -> {
                    chars.next();
                    yield switch (chars.peek()) {
                        case Option.Some(Character ch) -> switch (ch) {
                            case '=' -> consume(chars, new Token.Operator(new Operators.GtEq()));
                            case '>' -> consume(chars, new Token.Operator(new Operators.LeftShift()));
                            default -> Option.some(new Token.Operator(new Operators.Gt()));
                        };
                        default -> Option.some(new Token.Operator(new Operators.Gt()));
                    };
                }
                case '<' -> {
                    chars.next();
                    yield switch (chars.peek()) {
                        case Option.Some(Character ch) -> switch (ch) {
                            case '=' -> consume(chars, new Token.Operator(new Operators.LtEq()));
                            case '<' -> consume(chars, new Token.Operator(new Operators.RightShift()));
                            case '>' -> consume(chars, new Token.Operator(new Operators.NotEq()));
                            default -> Option.some(new Token.Operator(new Operators.Lt()));
                        };
                        default -> Option.some(new Token.Operator(new Operators.Lt()));
                    };

                }

                case '_' -> consume(chars, new Token.Underscore());

                default -> {
                    if (Character.isDigit(c)) {
                        String s = peekTakeWhite(chars, Character::isDigit);
                        if (chars.peek() instanceof Option.Some<Character>(
                                Character junk
                        ) && Character.isAlphabetic(junk)) {
                            throw new TokenizeException(String.format("invalid number format at line: %d, column: %d", line, col));
                        }
                        yield new Option.Some<>(new Token.Number(s));
                    } else if (Character.isAlphabetic(c)) {
                        String s = peekTakeWhite(chars, ch -> Character.isAlphabetic(ch) || Character.isDigit(ch));
                        Token tok = switch (s) {
                            // keywords are case-sensitive
                            case "EQ", "eq" -> new Token.Keyword(new Keywords.EQ());
                            case "DEF", "def" -> new Token.Keyword(new Keywords.DEF());
                            case "QUOTE", "quote" -> new Token.Keyword(new Keywords.QUOTE());
                            case "IF", "if" -> new Token.Keyword(new Keywords.IF());
                            case "COND", "cond" -> new Token.Keyword(new Keywords.COND());
                            case "ELSE", "else" -> new Token.Keyword(new Keywords.ELSE());
                            case "CONS", "cons" -> new Token.Keyword(new Keywords.CONS());
                            case "CAR", "car" -> new Token.Keyword(new Keywords.CAR());
                            case "CDR", "cdr" -> new Token.Keyword(new Keywords.CDR());
                            case "LIST", "list" -> new Token.Keyword(new Keywords.LIST());
                            case "FN", "fn" -> new Token.Keyword(new Keywords.FN());
                            case "PROG", "prog" -> new Token.Keyword(new Keywords.PROG());
                            case "EVAL", "eval" -> new Token.Keyword(new Keywords.EVAL());
                            case "MATCH", "match" -> new Token.Keyword(new Keywords.MATCH());
                            case "DEFAULT", "default" -> new Token.Keyword(new Keywords.DEFAULT());
                            default -> new Token.Symbol(s);
                        };
                        yield new Option.Some<>(tok);
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

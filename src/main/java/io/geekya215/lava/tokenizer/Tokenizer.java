package io.geekya215.lava.tokenizer;


import io.geekya215.lava.common.Option;
import io.geekya215.lava.common.Peekable;
import io.geekya215.lava.exception.TokenizeException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Tokenizer {
    private @NotNull final String src;
    private int line;
    private int col;

    public Tokenizer(@NotNull final String src) {
        this.src = src;
        this.line = 1;
        this.col = 1;
    }

    public @NotNull List<@NotNull LocatedToken> tokenizeWithLocation() {
        final List<LocatedToken> tokens = new ArrayList<>();
        final Iterator<Character> iter = src.chars().mapToObj(c -> (char) c).iterator();
        final Peekable<Character> chars = new Peekable<>(iter);

        while (nextToken(chars) instanceof Option.Some<Token>(Token tok)) {
            final LocatedToken locatedToken = new LocatedToken(tok, new Location(line, col));
            tokens.add(locatedToken);

            switch (tok) {
                case Token.SpaceChar(WhiteSpace.NewLine _) -> {
                    line += 1;
                    col = 1;
                }
                // default tab set as 4 space
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
                case Token.Keyword(Keywords keywords) -> col += keywords.toString().length();
                case Token.Annotation annotation -> col += 1 + annotation.toString().length();
                case Token.Comment comment -> col += 1 + comment.s().length();
                case Token.UnquoteSplicing _ -> col += 2;
                default -> col += 1;
            }
        }

        return tokens;
    }

    public @NotNull List<@NotNull Token> tokenize() {
        return tokenizeWithLocation().stream().map(LocatedToken::tok).collect(Collectors.toList());
    }

    private @NotNull Option<Token> nextToken(@NotNull final Peekable<Character> chars) {
        return switch (chars.peek()) {
            case Option.Some(Character c) -> switch (c) {
                case ' ' -> consume(chars, new Token.SpaceChar(new WhiteSpace.Space()));
                case '\t' -> consume(chars, new Token.SpaceChar(new WhiteSpace.Tab()));
                case '\n' -> consume(chars, new Token.SpaceChar(new WhiteSpace.NewLine()));

                case '\'' -> consume(chars, new Token.Quote());
                case '`' -> consume(chars, new Token.QuasiQuote());
                case ',' -> {
                    chars.next();
                    yield switch (chars.peek()) {
                        case Option.Some(Character ch) -> switch (ch) {
                            case '@' -> consume(chars, new Token.UnquoteSplicing());
                            default -> Option.some(new Token.Unquote());
                        };
                        default -> Option.some(new Token.Unquote());
                    };
                }

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
                case '&' -> {
                    chars.next();
                    final String s = peekTakeWhite(chars, Character::isAlphabetic);
                    Token tok = switch (s) {
                        case "whole" -> new Token.Annotation(new Annotations.WHOLE());
                        case "rest" -> new Token.Annotation(new Annotations.REST());
                        default -> throw new TokenizeException(String.format("unknown macro parameter annotation => %s at line: %d, column: %d", s, line, col));
                    };
                    yield new Option.Some<>(tok);
                }

                case ';' -> {
                    chars.next();
                    final String s = peekTakeWhite(chars, ch -> ch != '\n');
                    yield new Option.Some<>(new Token.Comment(s));
                }

                case '_' -> consume(chars, new Token.Underscore());

                default -> {
                    if (Character.isDigit(c)) {
                        final String s = peekTakeWhite(chars, Character::isDigit);
                        if (chars.peek() instanceof Option.Some<Character>(Character junk) && Character.isAlphabetic(junk)) {
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
                            case "LET", "let" -> new Token.Keyword(new Keywords.LET());
                            case "MATCH", "match" -> new Token.Keyword(new Keywords.MATCH());
                            case "DEFAULT", "default" -> new Token.Keyword(new Keywords.DEFAULT());
                            case "MACRO", "macro" -> new Token.Keyword(new Keywords.MACRO());
                            case "EXPAND", "expand" -> new Token.Keyword(new Keywords.EXPAND());
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

    private @NotNull String peekTakeWhite(@NotNull final Peekable<Character> chars,
                                          @NotNull final Predicate<Character> predicate) {
        final StringBuilder sb = new StringBuilder();
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

    private @NotNull  Option<Token> consume(@NotNull final Peekable<Character> chars,
                                            @NotNull final Token token) {
        chars.next();
        return Option.some(token);
    }
}

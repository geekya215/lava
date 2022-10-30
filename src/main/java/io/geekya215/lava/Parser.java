package io.geekya215.lava;

import io.geekya215.lava.adt.Expr;
import io.geekya215.lava.adt.Token;
import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.utils.Ref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Parser {
    public static Expr parse(Ref<List<Token>> tokens) throws ParserException {
        var _tokens = tokens.unwrap();
        if (_tokens.isEmpty()) {
            throw new ParserException("unexpected EOF while reading");
        }

        var head = _tokens.get(0);
        var tail = _tokens.subList(1, _tokens.size());

        return switch (head) {
            case Token.Quote quote -> {
                tokens.update(tail);
                yield new Expr.Quote(parse(tokens));
            }
            case Token.LeftParen leftParen -> {
                tokens.update(tail);
                yield parseList(tokens, new ArrayList<>());
            }
            case Token.RightParen rightParen -> throw new ParserException("unexpected )");
            case Token.Symbol symbol -> {
                tokens.update(tail);
                var sym = symbol.value();
                try {
                    yield new Expr.Number(Integer.parseInt(sym));
                } catch (Exception e) {
                    yield new Expr.Symbol(sym);
                }
            }
        };
    }

    private static Expr.List parseList(Ref<List<Token>> remainTokens, List<Expr> list) throws ParserException {
        var tokens = remainTokens.unwrap();

        if (tokens.isEmpty()) {
            throw new ParserException("unexpected EOF while reading list");
        }

        var head = tokens.get(0);
        var tail = tokens.subList(1, tokens.size());

        return switch (head) {
            case Token.RightParen rightParen -> {
                remainTokens.update(tail);
                Collections.reverse(list);
                yield new Expr.List(list);
            }
            default -> {
                var expr = parse(remainTokens);
                list.add(0, expr);
                yield parseList(remainTokens, list);
            }
        };
    }
}

package io.geekya215.lava;

import io.geekya215.lava.errors.ParserError;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static SExpr parse(List<Token> tokens) throws ParserError {
        if (tokens.size() == 1) {
            var token = tokens.get(0);
            return switch (token) {
                case Token.LeftParenthesis lp -> throw new ParserError("unexpected left parenthesis");
                case Token.RightParenthesis rp -> throw new ParserError("unexpected right parenthesis");
                default -> new SExpr.Atom(token);
            };
        } else {
            var cons = parseList(tokens);
            return new SExpr.Cons(cons);
        }
    }

    private static List<SExpr> parseList(List<Token> tokens) throws ParserError {
        var head = tokens.remove(0);

        if (!(head instanceof Token.LeftParenthesis)) {
            throw new ParserError("expect left parenthesis, but got " + head);
        }

        var cons = new ArrayList<SExpr>();

        while (!tokens.isEmpty()) {
            var token = tokens.remove(0);
            switch (token) {
                case Token.LeftParenthesis lp -> {
                    tokens.add(0, new Token.LeftParenthesis());
                    var sub = parseList(tokens);
                    cons.add(new SExpr.Cons(sub));
                }
                case Token.RightParenthesis rp -> {
                    return cons;
                }
                default -> cons.add(new SExpr.Atom(token));
            }
        }

        throw new ParserError("expect right parenthesis");
    }
}

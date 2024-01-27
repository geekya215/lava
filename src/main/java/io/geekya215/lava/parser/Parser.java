package io.geekya215.lava.parser;

import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.tokenizer.Token;
import io.geekya215.lava.tokenizer.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Parser {
    public static Expr parse(String src) {
        Tokenizer tokenizer = new Tokenizer(src);
        List<Token> tokens = tokenizer.tokenize().stream()
                .filter(t -> !(t instanceof Token.SpaceChar)).collect(Collectors.toList());
        return parseExpr(tokens);
    }

    private static Expr parseExpr(List<Token> tokens) {
        Token head = tokens.removeFirst();
        return switch (head) {
            case Token.LeftParen _ -> parseList(tokens);
            case Token.RightParen _ -> throw new ParserException("'Unexpected )");
            case Token.Quote _ -> new Expr.Quote(parseExpr(tokens));
            default -> new Expr.Atom(head);
        };
    }

    private static Expr parseList(List<Token> tokens) {
        List<Expr> vec = new ArrayList<>();
        while (!(tokens.getFirst() instanceof Token.RightParen)) {
            vec.add(parseExpr(tokens));
            if (tokens.isEmpty()) {
                throw new ParserException("Unexpected EOF");
            }
        }
        tokens.removeFirst();
        return new Expr.Vec(vec);
    }
}

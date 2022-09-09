package io.geekya215.lava;

import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.nodes.ConsNode;
import io.geekya215.lava.nodes.IntegerNode;
import io.geekya215.lava.nodes.NilNode;
import io.geekya215.lava.nodes.SExprNode;
import io.geekya215.lava.utils.PeekingIterator;

import java.util.List;

public final class Parser {
    private final PeekingIterator<Token> tokens;

    public Parser(List<Token> tokens) {
        this.tokens = new PeekingIterator<>(tokens);
    }

    //
    // sexpr -> atom | lparen sexpr-list rparen
    // sexpr-list -> sexpr sexpr-list |
    // atom -> number | operator
    // lparen -> (
    // rparen -> )
    //
    public SExprNode parse() throws ParserException {
        var peek = tokens.peek();
        if (peek.isPresent() && peek.get() instanceof Token.LeftParenthesis) {
            skip(Token.LeftParenthesis.class);
            var cons = parseList();
            skip(Token.RightParenthesis.class);
            return cons;
        } else {
            return parseAtom();
        }
    }

    private SExprNode parseList() throws ParserException {
        var peek = tokens.peek();
        if (peek.isPresent()) {
            return switch (peek.get()) {
                case Token.RightParenthesis rp -> new NilNode();
                default -> {
                    var car = parse();
                    var cdr = parseList();
                    yield new ConsNode(car, cdr);
                }
            };
        } else {
            throw new ParserException("expected token");
        }
    }

    private SExprNode parseAtom() throws ParserException {
        var peek = tokens.peek();
        if (peek.isPresent()) {
            var token = peek.get();
            return switch (token) {
                case Token.Number n -> {
                    tokens.next();
                    yield new IntegerNode(n.number());
                }
                default -> throw new ParserException("unexpected token: " + token);
            };
        } else {
            throw new ParserException("expected atom");
        }
    }

    private void skip(Class<? extends Token> clazz) throws ParserException {
        var peek = tokens.peek();
        if (peek.isPresent() && peek.get().getClass() == clazz) {
            tokens.next();
        } else {
            throw new ParserException("expected " + clazz.getSimpleName());
        }
    }
}

package io.geekya215.lava.expr;

import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.nodes.*;

public sealed interface Expr permits
    RefExpr, BoolExpr, IntegerExpr, ConsExpr, NilExpr,
    PlusExpr, MinusExpr, MulExpr, DivExpr,
    CarExpr, CdrExpr, QuoteExpr {
    static Expr from(SExprNode sexpr) throws ParserException {
        return switch (sexpr) {
            case SymbolNode symbolNode -> new RefExpr(symbolNode.val());
            case NilNode nilNode -> new NilExpr();
            case TrueNode trueNode -> new BoolExpr(true);
            case FalseNode falseNode -> new BoolExpr(false);
            case IntegerNode integerNode -> new IntegerExpr(integerNode.val());
            case ConsNode consNode -> {
                var nodes = consNode.toList();
                var size = nodes.size();
                if (size < 2) {
                    throw new ParserException("unexpected S-expression: " + sexpr);
                } else {
                    var head = nodes.get(0);
                    if (head instanceof SymbolNode symbolNode) {
                        if (size == 2) {
                            var arg = nodes.get(1);
                            yield switch (symbolNode.val()) {
                                case "car" -> new CarExpr(from(arg));
                                case "cdr" -> new CdrExpr(from(arg));
                                case "quote" -> new QuoteExpr(from(arg));
                                default -> throw new ParserException("unexpected S-expression: " + sexpr);
                            };
                        }
                        if (size == 3) {
                            var car = nodes.get(1);
                            var cdr = nodes.get(2);
                            yield switch (symbolNode.val()) {
                                case "cons" -> new ConsExpr(from(car), from(cdr));
                                case "+" -> new PlusExpr(from(car), from(cdr));
                                case "-" -> new MinusExpr(from(car), from(cdr));
                                case "*" -> new MulExpr(from(car), from(cdr));
                                case "/" -> new DivExpr(from(car), from(cdr));
                                default -> throw new ParserException("unexpected S-expression: " + sexpr);
                            };
                        } else {
                            throw new ParserException("unexpected S-expression: " + sexpr);
                        }
                    } else {
                        throw new ParserException("unexpected S-expression: " + sexpr);
                    }
                }
            }
        };
    }
}

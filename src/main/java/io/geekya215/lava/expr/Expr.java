package io.geekya215.lava.expr;

import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.nodes.*;

import java.util.ArrayList;

public sealed interface Expr permits
    RefExpr, LambdaExpr, AppExpr,
    BoolExpr, IntegerExpr, NilExpr,
    PlusExpr, MinusExpr, MulExpr, DivExpr,
    ConsExpr, CarExpr, CdrExpr, QuoteExpr {
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
                                default -> buildApplication(consNode);
                            };
                        }
                        else if (size == 3) {
                            var car = nodes.get(1);
                            var cdr = nodes.get(2);
                            yield switch (symbolNode.val()) {
                                case "cons" -> new ConsExpr(from(car), from(cdr));
                                case "+" -> new PlusExpr(from(car), from(cdr));
                                case "-" -> new MinusExpr(from(car), from(cdr));
                                case "*" -> new MulExpr(from(car), from(cdr));
                                case "/" -> new DivExpr(from(car), from(cdr));
                                case "lambda" -> {
                                    var params = new ArrayList<String>();
                                    for (var p : car.toList()) {
                                        if (p instanceof SymbolNode sym) {
                                            params.add(sym.val());
                                        } else {
                                            throw new ParserException("invalid parameter: " + p);
                                        }
                                    }
                                    yield new LambdaExpr(params, from(cdr));
                                }
                                default -> buildApplication(consNode);
                            };
                        } else {
                            yield buildApplication(consNode);
                        }
                    } else {
                        throw new ParserException("unexpected S-expression: " + sexpr);
                    }
                }
            }
        };
    }

    static AppExpr buildApplication(ConsNode consNode) throws ParserException {
        var fun = from(consNode.car());
        var args = new ArrayList<Expr>();
        for (var arg : consNode.cdr().toList()) {
            args.add(from(arg));
        }
        return new AppExpr(fun, args);
    }
}

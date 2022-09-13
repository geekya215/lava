package io.geekya215.lava.expr;

import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.nodes.*;

import java.util.ArrayList;

public sealed interface Expr permits
    RefExpr, LambdaExpr, AppExpr, DefineExpr,
    BoolExpr, IfExpr,
    EqExpr, LtExpr, LtEqExpr, GtExpr, GtEqExpr,
    NotExpr, AndExpr, OrExpr, XorExpr,
    IntegerExpr, NilExpr,
    PlusExpr, MinusExpr, MulExpr, DivExpr,
    ConsExpr, CarExpr, CdrExpr, QuoteExpr, SymbolExpr {
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
                var head = nodes.get(0);
                if (head instanceof SymbolNode symbolNode) {
                    if (size == 2) {
                        var arg = nodes.get(1);
                        yield switch (symbolNode.val()) {
                            case "car" -> new CarExpr(from(arg));
                            case "cdr" -> new CdrExpr(from(arg));
                            case "quote" -> new QuoteExpr(buildQuote(arg));
                            case "not" -> new NotExpr(from(arg));
                            default -> buildApplication(consNode);
                        };
                    } else if (size == 3) {
                        var car = nodes.get(1);
                        var cdr = nodes.get(2);
                        yield switch (symbolNode.val()) {
                            case "cons" -> new ConsExpr(from(car), from(cdr));
                            case "+" -> new PlusExpr(from(car), from(cdr));
                            case "-" -> new MinusExpr(from(car), from(cdr));
                            case "*" -> new MulExpr(from(car), from(cdr));
                            case "/" -> new DivExpr(from(car), from(cdr));
                            case "=" -> new EqExpr(from(car), from(cdr));
                            case "<" -> new LtExpr(from(car), from(cdr));
                            case ">" -> new GtExpr(from(car), from(cdr));
                            case "<=" -> new LtEqExpr(from(car), from(cdr));
                            case ">=" -> new GtEqExpr(from(car), from(cdr));
                            case "and" -> new AndExpr(from(car), from(cdr));
                            case "or" -> new OrExpr(from(car), from(cdr));
                            case "xor" -> new XorExpr(from(car), from(cdr));
                            case "define" -> {
                                if (car instanceof SymbolNode id) {
                                    yield new DefineExpr(id.val(), from(cdr));
                                } else {
                                    throw new ParserException("invalid define expr");
                                }
                            }
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
                    } else if (size == 4) {
                        yield switch (symbolNode.val()) {
                            case "if" -> {
                                var cond = nodes.get(1);
                                var trueBranch = nodes.get(2);
                                var falseBranch = nodes.get(3);
                                yield new IfExpr(from(cond), from(trueBranch), from(falseBranch));
                            }
                            default -> buildApplication(consNode);
                        };
                    } else {
                        yield buildApplication(consNode);
                    }
                } else if (from(head) instanceof LambdaExpr lambdaExpr) {
                    yield buildApplication(consNode);
                } else {
                    throw new ParserException("unexpected S-expression: " + sexpr);
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

    static Expr buildQuote(SExprNode arg) throws ParserException {
        return switch (arg) {
            case ConsNode consNode -> {
                var car = consNode.car();
                var cdr = consNode.cdr();
                yield new ConsExpr(buildQuote(car), buildQuote(cdr));
            }
            case SymbolNode symbolNode -> new SymbolExpr(symbolNode.val());
            case IntegerNode integerNode -> new IntegerExpr(integerNode.val());
            case TrueNode trueNode -> new BoolExpr(true);
            case FalseNode falseNode -> new BoolExpr(false);
            case NilNode nilNode -> new NilExpr();
        };
    }
}

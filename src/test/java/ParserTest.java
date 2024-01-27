import io.geekya215.lava.parser.Expr;
import io.geekya215.lava.parser.Parser;
import io.geekya215.lava.tokenizer.Keywords;
import io.geekya215.lava.tokenizer.Operators;
import io.geekya215.lava.tokenizer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    void parseNumber() {
        Expr actual = Parser.parse("123");
        Expr excepted = new Expr.Atom(new Token.Number("123"));
        assertEquals(excepted, actual);
    }

    @Test
    void parseParenthesis() {
        Expr actual = Parser.parse("()");
        Expr excepted = new Expr.Vec(List.of());
        assertEquals(excepted, actual);
    }

    @Test
    void parseQuote() {
        Expr actual = Parser.parse("(list 'a 'b 'c)");
        Expr excepted = new Expr.Vec(List.of(
                new Expr.Atom(new Token.Keyword(new Keywords.LIST())),
                new Expr.Quote(new Expr.Atom(new Token.Symbol("a"))),
                new Expr.Quote(new Expr.Atom(new Token.Symbol("b"))),
                new Expr.Quote(new Expr.Atom(new Token.Symbol("c")))
        ));
        assertEquals(excepted, actual);
    }

    @Test
    void parseFib() {
        Expr actual = Parser.parse("(def fib (fn (n) (if (<= n 1) (n) (+ (fib (- n 1)) (fib (- n 2))))))");
        Expr excepted = new Expr.Vec(List.of(
                new Expr.Atom(new Token.Keyword(new Keywords.DEF())),
                new Expr.Atom(new Token.Symbol("fib")),
                new Expr.Vec(List.of(
                        new Expr.Atom(new Token.Keyword(new Keywords.FN())),
                        new Expr.Vec(List.of(
                                new Expr.Atom(new Token.Symbol("n"))
                        )),
                        new Expr.Vec(List.of(
                                new Expr.Atom(new Token.Keyword(new Keywords.IF())),
                                new Expr.Vec(List.of(
                                        new Expr.Atom(new Token.Operator(new Operators.LtEq())),
                                        new Expr.Atom(new Token.Symbol("n")),
                                        new Expr.Atom(new Token.Number("1")))),
                                new Expr.Vec(List.of(
                                        new Expr.Atom(new Token.Symbol("n")))),
                                new Expr.Vec(List.of(
                                        new Expr.Atom(new Token.Operator(new Operators.Plus())),
                                        new Expr.Vec(List.of(
                                                new Expr.Atom(new Token.Symbol("fib")),
                                                new Expr.Vec(List.of(
                                                        new Expr.Atom(new Token.Operator(new Operators.Minus())),
                                                        new Expr.Atom(new Token.Symbol("n")),
                                                        new Expr.Atom(new Token.Number("1"))))
                                        )),
                                        new Expr.Vec(List.of(
                                                new Expr.Atom(new Token.Symbol("fib")),
                                                new Expr.Vec(List.of(
                                                        new Expr.Atom(new Token.Operator(new Operators.Minus())),
                                                        new Expr.Atom(new Token.Symbol("n")),
                                                        new Expr.Atom(new Token.Number("2"))))
                                        ))
                                ))
                        ))
                ))
        ));
        assertEquals(excepted, actual);
    }
}

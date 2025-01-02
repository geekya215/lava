import io.geekya215.lava.interpreter.Interpreter;
import io.geekya215.lava.parser.Expr;
import io.geekya215.lava.parser.Parser;
import io.geekya215.lava.tokenizer.Keywords;
import io.geekya215.lava.tokenizer.Operators;
import io.geekya215.lava.tokenizer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    Expr getExpr(String input) {
        return Parser.parse(input);
    }

    @Test
    void getNumberOne() {
        var expr = getExpr("1");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("1"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getQuoteA() {
        var expr = getExpr("'a");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Symbol("a"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getQuoteListOfABC() {
        var expr = getExpr("'(a b c)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(
                List.of(
                        new Expr.Atom(new Token.Symbol("a")),
                        new Expr.Atom(new Token.Symbol("b")),
                        new Expr.Atom(new Token.Symbol("c"))));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OnePlusTwoEqualThree() {
        var expr = getExpr("(+ 1 2)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("3"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneMinusTwoEqualNegativeOne() {
        var expr = getExpr("(- 1 2)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("-1"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneMultiplyTwoEqualTwo() {
        var expr = getExpr("(* 1 2)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("2"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoDivideOneEqualTwo() {
        var expr = getExpr("(/ 2 1)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("2"));
        assertEquals(expectedResult, actualResult);
    }

    //
    @Test
    void OnePlusTwoMultiplyThreeEqualSeven() {
        var expr = getExpr("(+ 1 (* 2 3))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("7"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneLessThanTwo() {
        var expr = getExpr("(< 1 2)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.TRUE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneLessThanOrEqualTwo() {
        var expr = getExpr("(<= 1 2)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.TRUE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoGreaterThanOne() {
        var expr = getExpr("(> 2 1)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.TRUE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoGreaterThanOrEqualOne() {
        var expr = getExpr("(>= 2 1)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.TRUE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneEqualOne() {
        var expr = getExpr("(= 1 1)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.TRUE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneNotEqualTwo() {
        var expr = getExpr("(= 1 2)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.FALSE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void SymbolAEqualSymbolA() {
        var expr = getExpr("(eq 'a 'a)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.TRUE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ListOfABCNotEqualListOfAB() {
        var expr = getExpr("(eq '(a b c) '(a b))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = Interpreter.BuiltinValue.FALSE;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CarListOfABCEqualA() {
        var expr = getExpr("(car '(a b c))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Symbol("a"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CarEmptyListEqualEmptyList() {
        var expr = getExpr("(car ())");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(List.of());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CdrListOfABCEqualListOfBC() {
        var expr = getExpr("(cdr '(a b c))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(
                List.of(new Expr.Atom(new Token.Symbol("b")),
                        new Expr.Atom(new Token.Symbol("c"))));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CdrEmptyListEqualEmptyList() {
        var expr = getExpr("(cdr ())");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(List.of());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ConsAWithListOfBCEqualListABC() {
        var expr = getExpr("(cons 'a '(b c))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(
                List.of(new Expr.Atom(new Token.Symbol("a")),
                        new Expr.Atom(new Token.Symbol("b")),
                        new Expr.Atom(new Token.Symbol("c"))));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getSymbolAIfOneLessThanTwo() {
        var expr = getExpr("(if (< 1 2) 'a 'b)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Symbol("a"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getSymbolBIfOneNotLessThanTwo() {
        var expr = getExpr("(if (> 1 2) 'a 'b)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Symbol("b"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getWordByNumber() {
        var expr = getExpr("""
                    (prog
                        (def a 3)
                        (cond
                            ((= a 1) 'one)
                            ((= a 2) 'two)
                            (else    'others)))
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Symbol("others"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineAEqualOneAndAccess() {
        var expr = getExpr("(prog (def a 1) a)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("1"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineAEqualListOfABCAndAccess() {
        var expr = getExpr("(prog (def a '(a b c)) a)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(
                List.of(new Expr.Atom(new Token.Symbol("a")),
                        new Expr.Atom(new Token.Symbol("b")),
                        new Expr.Atom(new Token.Symbol("c"))));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ListWithSymbolASymbolBSymbolCEqualListOfABC() {
        var expr = getExpr("(list 'a 'b 'c)");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(
                List.of(new Expr.Atom(new Token.Symbol("a")),
                        new Expr.Atom(new Token.Symbol("b")),
                        new Expr.Atom(new Token.Symbol("c"))));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineLambdaWithZeroParametersAndReturnOne() {
        var expr = getExpr("(prog (def one (fn () 1)) (one))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("1"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ImmediatelyInvokedFunctionExpression() {
        var expr = getExpr("((fn () 1))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("1"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineRecursiveFunction() {
        var expr = getExpr("""
                (prog
                    (def fib (fn (n)
                                 (if (<= n 1)
                                     n
                                 (+ (fib (- n 1)) (fib (- n 2))))))
                    (fib 10))
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("55"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineHighOrderFunction() {
        var expr = getExpr("""
                (prog
                    (def repeat (fn (f)
                                    (fn (x) (f (f x)))))
                    (def double (fn (x) (* 2 x)))
                    (def quadruple (repeat double))
                    (quadruple 2))
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("8"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineYCombinatorAndImmediatelyInvoked() {
        var expr = getExpr("""
                (((fn (b) ((fn (f) (b (fn (x) ((f f) x)))) (fn (f) (b (fn (x) ((f f) x))))))
                      (fn (fact) (fn (n) (if (= 0 n) 1 (* n (fact (- n 1)))))))
                                      5)
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("120"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineMarcoAndCall() {
        var expr = getExpr("""
                (prog
                    (def defun (macro (head body) (list 'def (car head) (list 'fn (cdr head) body))))
                    (defun (plus a b) (+ a b))
                    (plus 1 2))
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("3"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineMarcoWithQuasiQuoteAndCall() {
        var expr = getExpr("""
                (prog
                    (def defun (macro (head body) `(def ,(car head) (fn ,(cdr head) ,body))))
                    (defun (plus a b) (+ a b))
                    (plus 1 2))
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("3"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineMarcoWithAnnotation() {
        var expr = getExpr("""
                (prog
                    (def test (macro (&whole w a b &rest r) `(,w ,a ,b ,r)))
                    (expand (test 1 2 3 4))
                )
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(List.of(
                new Expr.Vec(List.of(
                        new Expr.Atom(new Token.Symbol("TEST")),
                        new Expr.Atom(new Token.Number("1")),
                        new Expr.Atom(new Token.Number("2")),
                        new Expr.Atom(new Token.Number("3")),
                        new Expr.Atom(new Token.Number("4"))
                )),
                new Expr.Atom(new Token.Number("1")),
                new Expr.Atom(new Token.Number("2")),
                new Expr.Vec(List.of(
                        new Expr.Atom(new Token.Number("3")),
                        new Expr.Atom(new Token.Number("4"))
                ))
        ));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void quasiQuoteWithSplicing() {
        var expr = getExpr("""
                (prog
                    (def numbers '(1 2 3))
                    `(+ a ,@numbers (,@numbers))
                )
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(List.of(
                new Expr.Atom(new Token.Operator(new Operators.Plus())),
                new Expr.Atom(new Token.Symbol("a")),
                new Expr.Atom(new Token.Number("1")),
                new Expr.Atom(new Token.Number("2")),
                new Expr.Atom(new Token.Number("3")),
                new Expr.Vec(List.of(
                        new Expr.Atom(new Token.Number("1")),
                        new Expr.Atom(new Token.Number("2")),
                        new Expr.Atom(new Token.Number("3"))
                ))
        ));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineMacroAndExpand() {
        var expr = getExpr("""
                (prog
                    (def plus (macro (a b c) `(+ ,a ,b c)))
                    (expand (plus (+ 1 2) ((fn (x) (* x x)) 2) 4))
                )
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Vec(List.of(
                new Expr.Atom(new Token.Operator(new Operators.Plus())),
                new Expr.Vec(List.of(
                        new Expr.Atom(new Token.Operator(new Operators.Plus())),
                        new Expr.Atom(new Token.Number("1")),
                        new Expr.Atom(new Token.Number("2"))
                )),
                new Expr.Vec(List.of(
                        new Expr.Vec(List.of(
                                new Expr.Atom(new Token.Keyword(new Keywords.FN())),
                                new Expr.Vec(List.of(
                                        new Expr.Atom(new Token.Symbol("x"))
                                )),
                                new Expr.Vec(List.of(
                                        new Expr.Atom(new Token.Operator(new Operators.Mul())),
                                        new Expr.Atom(new Token.Symbol("x")),
                                        new Expr.Atom(new Token.Symbol("x"))
                                ))
                        )),
                        new Expr.Atom(new Token.Number("2"))
                )),
                new Expr.Atom(new Token.Symbol("c"))

        ));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void patternMatchTest() {
        var expr = getExpr("""
                (prog
                    (def a '(1 2 3 4))
                    (match a (
                        (a 1)
                        ((1 2) 3)
                        ((1 2 3) (cons 0 a))
                        (default 11)
                    ))
                )
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("11"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void evalQuoteListPlusOneTwo() {
        var expr = getExpr("(eval '(+ 1 2))");
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("3"));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void useLetBindVarsAndEval() {
        var expr = getExpr("""
                (prog
                    (def a 1)
                    (def b 2)
                    (def c 3)
                    (let ((a 4) (b 5) (c 6))
                         (+ a b c))
                )
                """);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new Expr.Atom(new Token.Number("15"));
        assertEquals(expectedResult, actualResult);
    }
}

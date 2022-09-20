import io.geekya215.lava.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    public Env env;

    @BeforeEach
    void initialEnv() {
        env = Interpreter.initialStandardEnv();
    }

    Expr getExpr(String input) {
        var tokens = Tokenizer.tokenize(input);
        return Parser.parse(new Ref<>(tokens));
    }

    @Test
    void getNumberOne() {
        var expr = getExpr("1");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getQuoteA() {
        var expr = getExpr("'a");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("a");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getQuoteListOfABC() {
        var expr = getExpr("'(a b c)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(
            List.of(new Expr.Symbol("a"), new Expr.Symbol("b"), new Expr.Symbol("c")));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OnePlusTwoEqualThree() {
        var expr = getExpr("(+ 1 2)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(3);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneMinusTwoEqualNegativeOne() {
        var expr = getExpr("(- 1 2)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(-1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneMultiplyTwoEqualTwo() {
        var expr = getExpr("(* 1 2)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(2);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoDivideOneEqualTwo() {
        var expr = getExpr("(/ 2 1)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(2);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OnePlusTwoMultiplyThreeEqualSeven() {
        var expr = getExpr("(+ 1 (* 2 3))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(7);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneLessThanTwo() {
        var expr = getExpr("(< 1 2)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#t");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneLessThanOrEqualTwo() {
        var expr = getExpr("(<= 1 2)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#t");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoGreaterThanOne() {
        var expr = getExpr("(> 2 1)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#t");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoGreaterThanOrEqualOne() {
        var expr = getExpr("(>= 2 1)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#t");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneEqualOne() {
        var expr = getExpr("(= 1 1)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#t");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneNotEqualTwo() {
        var expr = getExpr("(= 1 2)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#f");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void SymbolAEqualSymbolA() {
        var expr = getExpr("(= 'a 'a)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#t");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ListOfABCNotEqualListOfAB() {
        var expr = getExpr("(= '(a b c) '(a b))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("#f");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CarListOfABCEqualA() {
        var expr = getExpr("(car '(a b c))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("a");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CarEmptyListEqualEmptyList() {
        var expr = getExpr("(car ())");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(List.of());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CdrListOfABCEqualListOfBC() {
        var expr = getExpr("(cdr '(a b c))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(
            List.of(new Expr.Symbol("b"), new Expr.Symbol("c")));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void CdrEmptyListEqualEmptyList() {
        var expr = getExpr("(cdr ())");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(List.of());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ConsAWithListOfBCEqualListABC() {
        var expr = getExpr("(cons 'a '(b c))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(
            List.of(new Expr.Symbol("a"), new Expr.Symbol("b"), new Expr.Symbol("c")));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getSymbolAIfOneLessThanTwo() {
        var expr = getExpr("(if (< 1 2) 'a 'b)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("a");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getSymbolBIfOneNotLessThanTwo() {
        var expr = getExpr("(if (> 1 2) 'a 'b)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Symbol("b");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineAEqualOneAndAccess() {
        var expr = getExpr("(begin (define a 1) a)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineAEqualListOfABCAndAccess() {
        var expr = getExpr("(begin (define a '(a b c)) a)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(
            List.of(new Expr.Symbol("a"), new Expr.Symbol("b"), new Expr.Symbol("c")));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ListWithSymbolASymbolBSymbolCEqualListOfABC() {
        var expr = getExpr("(list 'a 'b 'c)");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.List(
            List.of(new Expr.Symbol("a"), new Expr.Symbol("b"), new Expr.Symbol("c")));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineLambdaWithZeroParametersAndReturnOne() {
        var expr = getExpr("(begin (define one (lambda () 1)) (one))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ImmediatelyInvokedFunctionExpression() {
        var expr = getExpr("((lambda () 1))");
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineRecursiveFunction() {
        var expr = getExpr("""
            (begin
                (define fib (lambda (n)
                                    (if (<= n 1)
                                        n
                                        (+ (fib (- n 1)) (fib (- n 2))))))
                (fib 10))
            """);
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(55);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineHighOrderFunction() {
        var expr = getExpr("""
            (begin
                (define repeat (lambda (f)
                                       (lambda (x) (f (f x)))))
                (define double (lambda (x) (* 2 x)))
                (define quadruple (repeat double))
                (quadruple 2))
            """);
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(8);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void defineMarcoAndExpand() {
        var expr = getExpr("""
            (begin
                (defmarco defun (name args body) (define name (lambda args body)))
                (defun 'plus '(a b) '(+ a b))
                (plus 1 2))
            """);
        var actualResult = Interpreter.eval(expr, env);
        var expectedResult = new Expr.Number(3);
        assertEquals(expectedResult, actualResult);
    }
}

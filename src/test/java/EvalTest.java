import exception.EmptyException;
import exception.EvalException;
import exception.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class EvalTest {
    @Test
    void testArithmetic() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(+ 1 2)", new Env()),
            new Type.Integer(3)
        );
        Assertions.assertEquals(
            Eval.eval("(- 1 2)", new Env()),
            new Type.Integer(-1)
        );
        Assertions.assertEquals(
            Eval.eval("(* 1 2)", new Env()),
            new Type.Integer(2)
        );
        Assertions.assertEquals(
            Eval.eval("(/ 1 2)", new Env()),
            new Type.Integer(0)
        );
        Assertions.assertEquals(
            Eval.eval("(neg 1)", new Env()),
            new Type.Integer(-1)
        );
        Assertions.assertEquals(
            Eval.eval("(neg -1)", new Env()),
            new Type.Integer(1)
        );
    }

    @Test
    void testCompare() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(> 3 2)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(< 3 2)", new Env()),
            new Type.Bool(false)
        );
        Assertions.assertEquals(
            Eval.eval("(<= 3 2)", new Env()),
            new Type.Bool(false)
        );
        Assertions.assertEquals(
            Eval.eval("(>= 3 2)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(<= 2 2)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(>= 3 3)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(= 3 2)", new Env()),
            new Type.Bool(false)
        );
        Assertions.assertEquals(
            Eval.eval("(!= 3 2)", new Env()),
            new Type.Bool(true)
        );
    }

    @Test
    void testLogic() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(and (= 1 2) (< 2 3))", new Env()),
            new Type.Bool(false)
        );
        Assertions.assertEquals(
            Eval.eval("(or (= 1 2) (< 2 3))", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(not (= 1 2))", new Env()),
            new Type.Bool(true)
        );
    }

    @Test
    void testNull() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(null? ())", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(null? (1 2 3))", new Env()),
            new Type.Bool(false)
        );
    }

    @Test
    void testCar() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(car ())", new Env()),
            new Type.Nil()
        );
        Assertions.assertEquals(
            Eval.eval("(car (1 2 3))", new Env()),
            new Type.Integer(1)
        );
    }

    @Test
    void testCdr() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(cdr ())", new Env()),
            new Type.Nil()
        );
        Assertions.assertEquals(
            Eval.eval("(cdr (1 2 3))", new Env()),
            new Type.List(new ArrayList<>() {{
                add(new Type.Integer(2));
                add(new Type.Integer(3));
            }})
        );
    }

    @Test
    void testQuote() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(quote a)", new Env()),
            new Type.Symbol("a")
        );
        Assertions.assertEquals(
            Eval.eval("(quote (+ a b 1))", new Env()),
            new Type.List(new ArrayList<>() {{
                add(new Type.Symbol("+"));
                add(new Type.Symbol("a"));
                add(new Type.Symbol("b"));
                add(new Type.Integer(1));
            }})
        );
        Assertions.assertEquals(
            Eval.eval("(quote ((a (b c)) d))", new Env()),
            new Type.List(new ArrayList<>() {{
                add(new Type.List(new ArrayList<>() {{
                    add(new Type.Symbol("a"));
                    add(new Type.List(new ArrayList<>() {{
                        add(new Type.Symbol("b"));
                        add(new Type.Symbol("c"));
                    }}));
                }}));
                add(new Type.Symbol("d"));
            }})
        );
    }

    @Test
    void testAtom() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(atom 1)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(atom (quote a))", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(atom ())", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(atom (lambda () ()))", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("(atom (1 2 3))", new Env()),
            new Type.Bool(false)
        );
        Assertions.assertEquals(
            Eval.eval("(atom (quote (a b 1 2 3)))", new Env()),
            new Type.Bool(false)
        );
    }

    @Test
    void testLambda() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("((lambda (n) (+ n 1)) 1)", new Env()),
            new Type.Integer(2)
        );
        Assertions.assertEquals(
            Eval.eval("((lambda (n) (= n 1)) 1)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("((lambda (n) (= n 1)) 1)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("((lambda (n) (atom n)) 1)", new Env()),
            new Type.Bool(true)
        );
        Assertions.assertEquals(
            Eval.eval("((lambda (n) (atom n)) (quote (1 2 3)))", new Env()),
            new Type.Bool(false)
        );
        Assertions.assertEquals(
            Eval.eval("((lambda (a b x) (+ (* a (* x x)) (* b x))) 4 2 3)", new Env()),
            new Type.Integer(42)
        );
    }

    @Test
    void testFunc() throws EmptyException, EvalException, ParseException {
        var program = """
            (
                (define double (lambda (n) (* 2 n)))
                (double 2)
            )
            """;
        Assertions.assertEquals(
            Eval.eval(program, new Env()),
            new Type.List(new ArrayList<>() {{
                add(new Type.Integer(4));
            }})
        );
    }

    @Test
    void testFib() throws EmptyException, EvalException, ParseException {
        var program = """
            (
                (define fib (lambda (n)
                                (if (< n 2)
                                    1
                                    (+ (fib (- n 1)) (fib (- n 2))))))
                (fib 10)
            )
            """;
        Assertions.assertEquals(
            Eval.eval(program, new Env()),
            new Type.List(new ArrayList<>() {{
                add(new Type.Integer(89));
            }})
        );
    }

    @Test
    void testMultiFunc() throws EmptyException, EvalException, ParseException {
        var program = """
                (
                    (define pi 314)
                    (define r 10)
                    (define sqr (lambda (r) (* r r)))
                    (define area (lambda (r) (* pi (sqr r))))
                    (area r)
                )
            """;
        Assertions.assertEquals(
            Eval.eval(program, new Env()),
            new Type.List(new ArrayList<>() {{
                add(new Type.Integer(31400));
            }})
        );
    }
}

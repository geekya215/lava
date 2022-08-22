import exception.EmptyException;
import exception.EvalException;
import exception.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class EvalTest {
    @Test
    void testAdd() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(+ 1 2)", new Env()),
            new Type.Integer(3)
        );
    }

    @Test
    void testCompare() throws EmptyException, EvalException, ParseException {
        Assertions.assertEquals(
            Eval.eval("(> 3 2)", new Env()),
            new Type.Bool(true)
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

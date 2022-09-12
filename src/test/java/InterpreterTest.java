import io.geekya215.lava.Interpreter;
import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.exception.TokenizerException;
import io.geekya215.lava.expr.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterTest {
    @Test
    void getNumber() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("1");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new IntegerExpr(1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OnePlusTwoEqualThree() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(+ 1 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new IntegerExpr(3);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneMinusTwoEqualNegativeOne() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(- 1 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new IntegerExpr(-1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneMultiplyTwoEqualTwo() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(* 1 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new IntegerExpr(2);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneModDivideEqualZero() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(/ 1 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new IntegerExpr(0);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OnePlusTwoMultiplyThreeEqualSeven() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(+ 1 (* 2 3))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new IntegerExpr(7);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneEqualOne() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(= 1 1)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TrueEqualTrue() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(= #t #t)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneLessThanTwo() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(< 1 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoGreaterThanOne() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(> 2 1)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TwoLessThanOrEqualTwo() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(<= 2 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void OneLessThanOrEqualTwo() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(<= 1 2)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void ThreeGreaterThanOrEqualThree() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(>= 3 3)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void FourGreaterThanOrEqualThree() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(>= 4 3)");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void NotTrueEqualFalse() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(not (= 1 1))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(false);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TrueAndFalseEqualFalse() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(and (= 1 1) (= 1 2))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(false);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TrueOrFalseEqualTrue() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(or (= 1 1) (= 1 2))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void TrueXorFalseEqualTrue() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(xor (= 1 1) (= 1 2))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCar() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(car (quote (#t #f)))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(true);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCdr() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(cdr (quote (#t #f)))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new ConsExpr(new BoolExpr(false), new NilExpr());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCdrCar() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(car (cdr (quote (#t #f))))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new BoolExpr(false);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCarNil() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(car (quote ()))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new NilExpr();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCdrNil() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(cdr (quote ()))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new NilExpr();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCons() throws TokenizerException, ParserException, EvalException {
        var tokens = Tokenizer.tokenize("(cons (+ 1 2) (cdr (quote (#t #f))))");
        var node = new Parser(tokens).parse();
        var expr = Expr.from(node);
        var actualResult = Interpreter.eval(expr);
        var expectedResult = new ConsExpr(new IntegerExpr(3), new ConsExpr(new BoolExpr(false), new NilExpr()));
        assertEquals(expectedResult, actualResult);
    }
}

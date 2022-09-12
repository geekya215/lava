import io.geekya215.lava.Interpreter;
import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.exception.TokenizerException;
import io.geekya215.lava.expr.Expr;
import io.geekya215.lava.expr.IntegerExpr;
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
}

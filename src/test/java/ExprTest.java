import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.exceptions.TokenizerException;
import io.geekya215.lava.expr.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprTest {
    @Test
    void getRef() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("a");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new RefExpr("a");
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getAtomQuote() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(quote a)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new QuoteExpr(new RefExpr("a"));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getConsQuote() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(quote (cons 1 #t))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new QuoteExpr(new ConsExpr(new IntegerExpr(1), new BoolExpr(true)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getInteger() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("1");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new IntegerExpr(1);
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getBoolean() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(cons #t #f)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new ConsExpr(new BoolExpr(true), new BoolExpr(false));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getCar() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(car (cons #t #f))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new CarExpr(new ConsExpr(new BoolExpr(true), new BoolExpr(false)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getCdr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(cdr (cons #t #f))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new CdrExpr(new ConsExpr(new BoolExpr(true), new BoolExpr(false)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getNil() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("nil");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new NilExpr();
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getPlusExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(+ 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new PlusExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getMinusExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(- 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new MinusExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getMulExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(* 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new MulExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getDivExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(/ 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new DivExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getLambda() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(lambda (a b) (+ a b))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new LambdaExpr(List.of("a", "b"), new PlusExpr(new RefExpr("a"), new RefExpr("b")));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getTwoParamsApplication() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(plus 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new AppExpr(new RefExpr("plus"), List.of(new IntegerExpr(1), new IntegerExpr(2)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getThreeParamsApplication() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(sum 1 2 3)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new AppExpr(new RefExpr("sum"), List.of(new IntegerExpr(1), new IntegerExpr(2), new IntegerExpr(3)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getMoreThanThreeParamsApplication() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(max 1 2 3 4 5)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new AppExpr(new RefExpr("max"),
            List.of(new IntegerExpr(1), new IntegerExpr(2), new IntegerExpr(3), new IntegerExpr(4), new IntegerExpr(5)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getEqExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(= 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new EqExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getLtExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(< 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new LtExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getGtExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(> 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new GtExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getLtEqExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(<= 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new LtEqExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getGtEqExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(>= 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new GtEqExpr(new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getIfExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(if (< 1 2) 1 2)");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new IfExpr(new LtExpr(new IntegerExpr(1), new IntegerExpr(2)), new IntegerExpr(1), new IntegerExpr(2));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getNotExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(not (< 1 2))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new NotExpr(new LtExpr(new IntegerExpr(1), new IntegerExpr(2)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getAndExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(and (< 1 2) (> 1 2))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new AndExpr(
            new LtExpr(new IntegerExpr(1), new IntegerExpr(2)),
            new GtExpr(new IntegerExpr(1), new IntegerExpr(2)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getOrExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(or (< 1 2) (> 1 2))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new OrExpr(
            new LtExpr(new IntegerExpr(1), new IntegerExpr(2)),
            new GtExpr(new IntegerExpr(1), new IntegerExpr(2)));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getXorExpr() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(xor (< 1 2) (> 1 2))");
        var node = new Parser(tokens).parse();
        var actualExpr = Expr.from(node);
        var expectedExpr = new XorExpr(
            new LtExpr(new IntegerExpr(1), new IntegerExpr(2)),
            new GtExpr(new IntegerExpr(1), new IntegerExpr(2)));
        assertEquals(expectedExpr, actualExpr);
    }
}

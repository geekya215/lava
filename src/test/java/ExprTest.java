import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.exceptions.TokenizerException;
import io.geekya215.lava.expr.*;
import org.junit.jupiter.api.Test;

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
}
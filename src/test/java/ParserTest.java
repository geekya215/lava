import io.geekya215.lava.Parser;
import io.geekya215.lava.SExpr;
import io.geekya215.lava.Token;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.errors.ParserError;
import io.geekya215.lava.errors.TokenizerError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
    @Test
    void getAtom() throws TokenizerError, ParserError {
        var tokens = Tokenizer.tokenize("1");
        var actualAtom = Parser.parse(tokens);
        var expectedAtom = new SExpr.Atom(new Token.Number(1));
        assertEquals(actualAtom, expectedAtom);
    }

    @Test
    void getCons() throws TokenizerError, ParserError {
        var tokens = Tokenizer.tokenize("(1 2)");
        var actualCons = Parser.parse(tokens);
        var expectedCons = new SExpr.Cons(
            new ArrayList<>() {{
                add(new SExpr.Atom(new Token.Number(1)));
                add(new SExpr.Atom(new Token.Number(2)));
            }}
        );
        assertEquals(actualCons, expectedCons);
    }

    @Test
    void getNestCons() throws TokenizerError, ParserError {
        var tokens = Tokenizer.tokenize("((1 2) 3)");
        var actualCons = Parser.parse(tokens);
        var expectedCons = new SExpr.Cons(
            new ArrayList<>() {{
                add(new SExpr.Cons(new ArrayList<>() {{
                    add(new SExpr.Atom(new Token.Number(1)));
                    add(new SExpr.Atom(new Token.Number(2)));
                }}));
                add(new SExpr.Atom(new Token.Number(3)));
            }}
        );
        assertEquals(actualCons, expectedCons);
    }

    @Test
    void unmatchedAtomLeftParenthesis() throws TokenizerError {
        var tokens = Tokenizer.tokenize("(");
        var actualError = assertThrows(ParserError.class, () -> Parser.parse(tokens));
        var actualMessage = actualError.getMessage();
        var expectedMessage = "unexpected left parenthesis";
        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    void unmatchedAtomRightParenthesis() throws TokenizerError {
        var tokens = Tokenizer.tokenize(")");
        var actualError = assertThrows(ParserError.class, () -> Parser.parse(tokens));
        var actualMessage = actualError.getMessage();
        var expectedMessage = "unexpected right parenthesis";
        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    void unmatchedConsLeftParenthesis() throws TokenizerError {
        var tokens = Tokenizer.tokenize("((");
        var actualError = assertThrows(ParserError.class, () -> Parser.parse(tokens));
        var actualMessage = actualError.getMessage();
        var expectedMessage = "expect right parenthesis";
        assertEquals(actualMessage, expectedMessage);
    }

    @Test
    void unmatchedConsRightParenthesis() throws TokenizerError {
        var tokens = Tokenizer.tokenize("1))");
        var actualError = assertThrows(ParserError.class, () -> Parser.parse(tokens));
        var actualMessage = actualError.getMessage();
        var expectedMessage = "expect left parenthesis, but got 1";
        assertEquals(actualMessage, expectedMessage);
    }
}

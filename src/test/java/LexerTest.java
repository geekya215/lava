import io.geekya215.lava.Lexer;
import io.geekya215.lava.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {

    List<Token> tokenize(String input) {
        return Lexer.tokenize(Lexer.preprocess(input));
    }

    @Test
    void getParenthesis() {
        var actualTokens = tokenize("(())");
        var expectedTokens = List.of(
            new Token.LeftParen(),
            new Token.LeftParen(),
            new Token.RightParen(),
            new Token.RightParen());
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void getQuote() {
        var actualTokens = tokenize("'abc");
        var expectedTokens = List.of(
            new Token.Quote(),
            new Token.Symbol("abc"));
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void getSymbol() {
        var actualTokens = tokenize("a b c");
        var expectedTokens = List.of(
            new Token.Symbol("a"),
            new Token.Symbol("b"),
            new Token.Symbol("c"));
        assertEquals(expectedTokens, actualTokens);
    }
}

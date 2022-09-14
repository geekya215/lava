import io.geekya215.lava.Token;
import io.geekya215.lava.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {
    @Test
    void getParenthesis() {
        var actualTokens = Tokenizer.tokenize("(())");
        var expectedTokens = List.of(
            new Token.LeftParen(),
            new Token.LeftParen(),
            new Token.RightParen(),
            new Token.RightParen());
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void getQuote() {
        var actualTokens = Tokenizer.tokenize("'abc");
        var expectedTokens = List.of(
            new Token.Quote(),
            new Token.Symbol("abc"));
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void getSymbol() {
        var actualTokens = Tokenizer.tokenize("a b c");
        var expectedTokens = List.of(
            new Token.Symbol("a"),
            new Token.Symbol("b"),
            new Token.Symbol("c"));
        assertEquals(expectedTokens, actualTokens);
    }
}

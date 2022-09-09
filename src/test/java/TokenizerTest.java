import io.geekya215.lava.Token;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exceptions.TokenizerException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {
    @Test
    void getArithmeticToken() throws TokenizerException {
        var input = "+ - * / mod neg";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.Plus());
            add(new Token.Minus());
            add(new Token.Mul());
            add(new Token.Div());
            add(new Token.Mod());
            add(new Token.Neg());
        }};
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void getBooleanLiterally() throws TokenizerException {
        var input = "#t #f";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.True());
            add(new Token.False());
        }};
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void getIntegerToken() throws TokenizerException {
        var input = "123 321 1";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.Number(123));
            add(new Token.Number(321));
            add(new Token.Number(1));
        }};
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void withParenthesis() throws TokenizerException {
        var input = "(+ 123 - 321 1)";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.LeftParenthesis());
            add(new Token.Plus());
            add(new Token.Number(123));
            add(new Token.Minus());
            add(new Token.Number(321));
            add(new Token.Number(1));
            add(new Token.RightParenthesis());
        }};
        assertEquals(expectedTokens, actualTokens);
    }

    @Test
    void withNestParenthesis() throws TokenizerException {
        var input = "(* (+ 123 4 ) (neg 321))";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.LeftParenthesis());
            add(new Token.Mul());
            add(new Token.LeftParenthesis());
            add(new Token.Plus());
            add(new Token.Number(123));
            add(new Token.Number(4));
            add(new Token.RightParenthesis());
            add(new Token.LeftParenthesis());
            add(new Token.Neg());
            add(new Token.Number(321));
            add(new Token.RightParenthesis());
            add(new Token.RightParenthesis());
        }};
        assertEquals(expectedTokens,actualTokens );
    }

    @Test
    void withNilLiterally() throws TokenizerException {
        var input = "(nil 1 nil (+ 1 2))";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.LeftParenthesis());
            add(new Token.Nil());
            add(new Token.Number(1));
            add(new Token.Nil());
            add(new Token.LeftParenthesis());
            add(new Token.Plus());
            add(new Token.Number(1));
            add(new Token.Number(2));
            add(new Token.RightParenthesis());
            add(new Token.RightParenthesis());
        }};
        assertEquals(expectedTokens, actualTokens);
    }
}

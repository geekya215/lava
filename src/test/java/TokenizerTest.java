import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.Token;
import io.geekya215.lava.errors.TokenizerError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {
    @Test
    void getArithmeticToken() throws TokenizerError {
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
        assertEquals(actualTokens, expectedTokens);
    }

    @Test
    void getIntegerToken() throws TokenizerError {
        var input = "123 321 1";
        var actualTokens = Tokenizer.tokenize(input);
        var expectedTokens = new ArrayList<>() {{
            add(new Token.Number(123));
            add(new Token.Number(321));
            add(new Token.Number(1));
        }};
        assertEquals(actualTokens, expectedTokens);
    }

    @Test
    void withParenthesis() throws TokenizerError {
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
        assertEquals(actualTokens, expectedTokens);
    }

    @Test
    void withNestParenthesis() throws TokenizerError {
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
        assertEquals(actualTokens, expectedTokens);
    }

    @Test
    void withNilLiterally() throws TokenizerError {
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
        assertEquals(actualTokens, expectedTokens);
    }
}

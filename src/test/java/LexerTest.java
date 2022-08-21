import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class LexerTest {
    @Test
    void testNormal() {
        Assertions.assertEquals(
            Lexer.tokenize("(+ 1 2)"),
            new ArrayList<>() {{
                add(new Token.LeftParenthesis());
                add(new Token.Symbol("+"));
                add(new Token.Integer(1));
                add(new Token.Integer(2));
                add(new Token.RightParenthesis());
            }}
        );
    }

    @Test
    void testNest() {
        Assertions.assertEquals(
            Lexer.tokenize("(* (+ 2 3) (- 3 1))"),
            new ArrayList<>() {{
                add(new Token.LeftParenthesis());
                add(new Token.Symbol("*"));
                add(new Token.LeftParenthesis());
                add(new Token.Symbol("+"));
                add(new Token.Integer(2));
                add(new Token.Integer(3));
                add(new Token.RightParenthesis());
                add(new Token.LeftParenthesis());
                add(new Token.Symbol("-"));
                add(new Token.Integer(3));
                add(new Token.Integer(1));
                add(new Token.RightParenthesis());
                add(new Token.RightParenthesis());
            }}
        );
    }
}

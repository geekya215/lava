import io.geekya215.lava.tokenizer.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TokenizerTest {
    @Test
    void tokenizeNumber() {
        List<Token> actual = new Tokenizer("123").tokenize();
        List<Token> expected = List.of(new Token.Number("123"));
        assertEquals(expected, actual);
    }

    @Test
    void tokenizeParenthesis() {
        List<Token> actual = new Tokenizer("()").tokenize();
        List<Token> expected = List.of(new Token.LeftParen(), new Token.RightParen());
        assertEquals(expected, actual);
    }

    @Test
    void tokenizeSpaceChar() {
        List<Token> actual = new Tokenizer("""
                ( \t
                \t)""").tokenize();
        List<Token> expected = List.of(
                new Token.LeftParen(),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.SpaceChar(new WhiteSpace.Tab()),
                new Token.SpaceChar(new WhiteSpace.NewLine()),
                new Token.SpaceChar(new WhiteSpace.Tab()),
                new Token.RightParen());
        assertEquals(expected, actual);
    }

    @Test
    void tokenizeQuote() {
        List<Token> actual = new Tokenizer("(list 'a 'b 'c)").tokenize();
        List<Token> expected = List.of(
                new Token.LeftParen(),
                new Token.Keyword(new Keywords.LIST()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Quote(),
                new Token.Symbol("a"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Quote(),
                new Token.Symbol("b"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Quote(),
                new Token.Symbol("c"),
                new Token.RightParen());
        assertEquals(expected, actual);
    }

    @Test
    void tokenizeFib() {
        List<Token> actual = new Tokenizer("(def fib (fn (n) (if (<= n 1) (n) (+ (fib (- n 1)) (fib (- n 2))))))")
                .tokenize();
        List<Token> expected = List.of(
                new Token.LeftParen(),
                new Token.Keyword(new Keywords.DEF()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Symbol("fib"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Keyword(new Keywords.FN()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Symbol("n"),
                new Token.RightParen(),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Keyword(new Keywords.IF()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Operator(new Operators.LtEq()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Symbol("n"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Number("1"),
                new Token.RightParen(),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Symbol("n"),
                new Token.RightParen(),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Operator(new Operators.Plus()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Symbol("fib"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Operator(new Operators.Minus()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Symbol("n"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Number("1"),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Symbol("fib"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.LeftParen(),
                new Token.Operator(new Operators.Minus()),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Symbol("n"),
                new Token.SpaceChar(new WhiteSpace.Space()),
                new Token.Number("2"),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.RightParen(),
                new Token.RightParen()
        );
        assertEquals(expected, actual);
    }
}

import io.geekya215.lava.*;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    List<Token> tokenize(String input) {
        return Lexer.tokenize(Lexer.preprocess(input));
    }
    @Test
    void getNumber() {
        var tokens = tokenize("1");
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.Number(1);
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getSymbol() {
        var tokens = tokenize("abc");
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.Symbol("abc");
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getQuote() {
        var tokens = tokenize("'abc");
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.Quote(new Expr.Symbol("abc"));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getList() {
        var tokens = tokenize("(a b 1)");
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.List(List.of(new Expr.Symbol("a"), new Expr.Symbol("b"), new Expr.Number(1)));
        assertEquals(expectedExpr, actualExpr);
    }
}

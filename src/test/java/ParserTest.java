import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.adt.Expr;
import io.geekya215.lava.utils.Ref;
import io.geekya215.lava.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    void getNumber() {
        var tokens = Tokenizer.tokenize(Utils.preprocessInput("1"));
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.Number(1);
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getSymbol() {
        var tokens = Tokenizer.tokenize(Utils.preprocessInput("abc"));
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.Symbol("abc");
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getQuote() {
        var tokens = Tokenizer.tokenize(Utils.preprocessInput("'abc"));
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.Quote(new Expr.Symbol("abc"));
        assertEquals(expectedExpr, actualExpr);
    }

    @Test
    void getList() {
        var tokens = Tokenizer.tokenize(Utils.preprocessInput("(a b 1)"));
        var actualExpr = Parser.parse(new Ref<>(tokens));
        var expectedExpr = new Expr.List(List.of(new Expr.Symbol("a"), new Expr.Symbol("b"), new Expr.Number(1)));
        assertEquals(expectedExpr, actualExpr);
    }
}

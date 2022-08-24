import exception.EmptyException;
import exception.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class ParserTest {
    @Test
    void testNormal() throws EmptyException, ParseException {
        var tokens = Lexer.tokenize("(+ 1 2)");
        Assertions.assertEquals(
            Parser.parse(tokens),
            new Type.List(new ArrayList<>() {{
                add(new Type.BinaryOperator("+"));
                add(new Type.Integer(1));
                add(new Type.Integer(2));
            }})
        );
    }

    @Test
    void testNest() throws EmptyException, ParseException {
        var tokens = Lexer.tokenize("(+ (* 2 3) 4)");
        Assertions.assertEquals(
            Parser.parse(tokens),
            new Type.List(new ArrayList<>() {{
                add(new Type.BinaryOperator("+"));
                add(new Type.List(new ArrayList<>() {{
                    add(new Type.BinaryOperator("*"));
                    add(new Type.Integer(2));
                    add(new Type.Integer(3));
                }}));
                add(new Type.Integer(4));
            }})
        );
    }

    @Test
    void testMultiLine() throws EmptyException, ParseException {
        var tokens = Lexer.tokenize("""
            (
                (define r 10)
                (define pi 314)
                (* pi (* r r))
            )
            """);
        Assertions.assertEquals(
            Parser.parse(tokens),
            new Type.List(new ArrayList<>() {{
                add(new Type.List(new ArrayList<>() {{
                    add(new Type.Keyword("define"));
                    add(new Type.Symbol("r"));
                    add(new Type.Integer(10));
                }}));
                add(new Type.List(new ArrayList<>() {{
                    add(new Type.Keyword("define"));
                    add(new Type.Symbol("pi"));
                    add(new Type.Integer(314));
                }}));
                add(new Type.List(new ArrayList<>() {{
                    add(new Type.BinaryOperator("*"));
                    add(new Type.Symbol("pi"));
                    add(new Type.List(new ArrayList<>() {{
                        add(new Type.BinaryOperator("*"));
                        add(new Type.Symbol("r"));
                        add(new Type.Symbol("r"));
                    }}));
                }}));
            }})
        );
    }
}

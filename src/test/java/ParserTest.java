import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.exceptions.TokenizerException;
import io.geekya215.lava.nodes.ConsNode;
import io.geekya215.lava.nodes.IntegerNode;
import io.geekya215.lava.nodes.NilNode;
import io.geekya215.lava.nodes.SymbolNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    void getNumber() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("1");
        var actualAtom = new Parser(tokens).parse();
        var expectedAtom = new IntegerNode(1);
        assertEquals(expectedAtom, actualAtom);
    }

    @Test
    void getOperators() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(+ - * / neg mod)");
        var actualAtom = new Parser(tokens).parse();
        var expectedAtom = new ConsNode(new SymbolNode("+"),
            new ConsNode(new SymbolNode("-"),
                new ConsNode(new SymbolNode("*"),
                    new ConsNode(new SymbolNode("/"),
                        new ConsNode(new SymbolNode("neg"),
                            new ConsNode(new SymbolNode("mod"), new NilNode()))))));
        assertEquals(expectedAtom, actualAtom);
    }

    @Test
    void getCons() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(1 2)");
        var actualCons = new Parser(tokens).parse();
        var expectedCons = new ConsNode(new IntegerNode(1), new ConsNode(new IntegerNode(2), new NilNode()));
        assertEquals(expectedCons, actualCons);
    }

    @Test
    void getNestCons() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("( 1 ( 2 3))");
        var actualCons = new Parser(tokens).parse();
        var expectedCons = new ConsNode(new IntegerNode(1),
            new ConsNode(new ConsNode(new IntegerNode(2),
                new ConsNode(new IntegerNode(3), new NilNode())), new NilNode()));
        assertEquals(expectedCons, actualCons);
    }
}

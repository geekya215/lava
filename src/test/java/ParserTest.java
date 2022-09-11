import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.exceptions.ParserException;
import io.geekya215.lava.exceptions.TokenizerException;
import io.geekya215.lava.nodes.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    @Test
    void getNumber() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("1");
        var actualNumber = new Parser(tokens).parse();
        var expectedNumber = new IntegerNode(1);
        assertEquals(expectedNumber, actualNumber);
    }

    @Test
    void getOperators() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(+ - * / neg mod)");
        var actualOperators = new Parser(tokens).parse();
        var expectedOperators = new ConsNode(new SymbolNode("+"),
            new ConsNode(new SymbolNode("-"),
                new ConsNode(new SymbolNode("*"),
                    new ConsNode(new SymbolNode("/"),
                        new ConsNode(new SymbolNode("neg"),
                            new ConsNode(new SymbolNode("mod"), new NilNode()))))));
        assertEquals(expectedOperators, actualOperators);
    }

    @Test
    void getBooleanLiterally() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("(#t #f)");
        var actualBoolean = new Parser(tokens).parse();
        var expectedBoolean = new ConsNode(new TrueNode(), new ConsNode(new FalseNode(), new NilNode()));
        assertEquals(expectedBoolean, actualBoolean);
    }

    @Test
    void getNilByEmptyList() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("()");
        var actualCons = new Parser(tokens).parse();
        var expectedCons = new NilNode();
        assertEquals(expectedCons, actualCons);
    }

    @Test
    void getNilByLiterally() throws TokenizerException, ParserException {
        var tokens = Tokenizer.tokenize("nil");
        var actualCons = new Parser(tokens).parse();
        var expectedCons = new NilNode();
        assertEquals(expectedCons, actualCons);
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

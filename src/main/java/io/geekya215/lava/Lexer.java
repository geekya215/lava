package io.geekya215.lava;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Lexer {
    public static String preprocess(String input) {
        return (input + "\n")
            // split special char with whitespace
            .replaceAll("\\(", " ( ")
            .replaceAll("\\)", " ) ")
            .replaceAll("'", " ' ")
            // skip comments
            .replaceAll(";;[^\\n\\r]*?(?:[\\n\\r])", "");
    }

    public static List<Token> tokenize(String input) {
        var tokens = new ArrayList<Token>();
        var tokenizer = new StringTokenizer(input);

        while (tokenizer.hasMoreTokens()) {
            var lex = tokenizer.nextToken();
            var token = switch (lex) {
                case "(" -> new Token.LeftParen();
                case ")" -> new Token.RightParen();
                case "'" -> new Token.Quote();
                default -> new Token.Symbol(lex);
            };
            tokens.add(token);
        }

        return tokens;
    }
}
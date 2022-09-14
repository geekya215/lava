package io.geekya215.lava;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Tokenizer {
    public static List<Token> tokenize(String input) {
        var text = input
            .replaceAll("\\(", " ( ")
            .replaceAll("\\)", " ) ")
            .replaceAll("'", " ' ");
        var tokens = new ArrayList<Token>();
        var tokenizer = new StringTokenizer(text);

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

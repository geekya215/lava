package io.geekya215.lava;

import io.geekya215.lava.errors.TokenizerError;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Tokenizer {
    public static List<Token> tokenize(String input) throws TokenizerError {
        var tokens = new ArrayList<Token>();
        var text = input.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
        var tokenizer = new StringTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            var token = tokenizer.nextToken();
            switch (token) {
                case "nil" -> tokens.add(new Token.Nil());
                case "(" -> tokens.add(new Token.LeftParenthesis());
                case ")" -> tokens.add(new Token.RightParenthesis());
                case "+" -> tokens.add(new Token.Plus());
                case "-" -> tokens.add(new Token.Minus());
                case "*" -> tokens.add(new Token.Mul());
                case "/" -> tokens.add(new Token.Div());
                case "mod" -> tokens.add(new Token.Mod());
                case "neg" -> tokens.add(new Token.Neg());
                default -> {
                    try {
                        var num = Integer.parseInt(token);
                        tokens.add(new Token.Number(num));
                    } catch (Exception e) {
                        throw new TokenizerError(token);
                    }
                }
            }
        }
        return tokens;
    }
}
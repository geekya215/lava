import java.util.ArrayList;
import java.util.StringTokenizer;

public class Lexer {
    public static ArrayList<Token> tokenize(String input) {
        var input2 = input.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ");
        var tokens = new ArrayList<Token>();
        var tokenizer = new StringTokenizer(input2);
        while (tokenizer.hasMoreTokens()) {
            var word = tokenizer.nextToken();
            switch (word) {
                case "(" -> tokens.add(new Token.LeftParenthesis());
                case ")" -> tokens.add(new Token.RightParenthesis());
                default -> {
                    try {
                        var num = Integer.parseInt(word);
                        tokens.add(new Token.Integer(num));
                    } catch (Exception e) {
                        tokens.add(new Token.Symbol(word));
                    }
                }
            }
        }
        return tokens;
    }
}

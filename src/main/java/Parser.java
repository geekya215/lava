import exception.EmptyException;
import exception.ParseException;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    public static Type parse(List<Token> tokens) throws ParseException, EmptyException {
        if (tokens.isEmpty()) {
            throw new EmptyException();
        }

        var t = tokens.remove(0);
        if (!(t instanceof Token.LeftParenthesis)) {
            throw new ParseException("Except left parenthesis, but got " + t);
        }

        var res = new ArrayList<Type>();

        while (!tokens.isEmpty()) {
            var token = tokens.remove(0);
            switch (token) {
                case Token.Integer i -> res.add(new Type.Integer(i.val()));
                case Token.Symbol sym -> {
                    var s = sym.val();
                    switch (s) {
                        case "neg", "not" -> res.add(new Type.UnaryOperator(s));
                        case "+", "-", "*", "/",
                            "<", ">", "=", "!=", "<=", ">=",
                            "and", "or" -> res.add(new Type.BinaryOperator(s));
                        case "define", "lambda", "if",
                            "null?", "car", "cdr", "quote" -> res.add(new Type.Keyword(s));
                        default -> res.add(new Type.Symbol(s));
                    }
                }
                case Token.LeftParenthesis l -> {
                    tokens.add(0, new Token.LeftParenthesis());
                    Type sub = parse(tokens);
                    res.add(sub);
                }
                case Token.RightParenthesis r -> {
                    return new Type.List(res);
                }
            }

        }

        throw new ParseException("Except right parenthesis");
    }
}

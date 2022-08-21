public sealed interface Token
    permits Token.Integer, Token.LeftParenthesis, Token.RightParenthesis, Token.Symbol {

    record Integer(java.lang.Integer val) implements Token {
    }

    record Symbol(String val) implements Token {
    }

    record LeftParenthesis() implements Token {
    }

    record RightParenthesis() implements Token {
    }
}

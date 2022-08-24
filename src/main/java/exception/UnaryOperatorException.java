package exception;

public class UnaryOperatorException extends EvalException {
    public UnaryOperatorException(String message) {
        super(message);
    }

    public UnaryOperatorException(String uop, Class expected, Class found) {
        super(String.format("invalid operand type for `%s` operator:\n\texpected `%s`, found `%s`",
            uop, expected.getSimpleName(), found.getSimpleName()));
    }
}

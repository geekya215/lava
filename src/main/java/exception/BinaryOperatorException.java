package exception;

public class BinaryOperatorException extends EvalException {
    public BinaryOperatorException(String message) {
        super(message);
    }

    public BinaryOperatorException(String bop, Class lExpected, Class rExpected, Class lFound, Class rFound) {
        super(String.format(
            "invalid types for `%s` operator:\n\texpected (+ `%s` `%s`), found (+ `%s` `%s`)",
            bop, lExpected.getSimpleName(), rExpected.getSimpleName(), lFound.getSimpleName(), rFound.getSimpleName()));
    }
}

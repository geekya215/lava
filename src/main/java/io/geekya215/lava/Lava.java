package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Lava {
    public static final String PROMPT = "lava> ";
    public static final String INDICATOR = "=> ";

    public static void main(String[] args) {
        var standardEnv = Interpreter.initialStandardEnv();
        var reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Hello Lava");

        for (; ; ) {
            System.out.print(PROMPT);
            try {
                var input = reader.readLine();
                if (Objects.equals(input, "exit")) {
                    break;
                } else {
                    var tokens = Tokenizer.tokenize(input);
                    var expr = Parser.parse(new Ref<>(tokens));
                    var result = Interpreter.eval(expr, standardEnv);
                    System.out.print(INDICATOR);
                    System.out.println(result);
                }
            } catch (ParserException | EvalException e) {
                System.out.println("error: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("\ncrashed cause: " + e.getMessage());
                System.exit(1);
            } catch (StackOverflowError error) {
                System.err.println("\ncrashed cause: call stack overflow");
                System.exit(1);
            }
        }
    }
}

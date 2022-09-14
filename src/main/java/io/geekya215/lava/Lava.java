package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.exception.TokenizerException;
import io.geekya215.lava.expr.Expr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Scanner;

public class Lava {
    public static final String PROMPT = "lava> ";

    public static void main(String[] args) throws IOException {
        var sc = new Scanner(System.in);
        var env = new Env();
        System.out.println("Hello Lava!");
        boolean exit = false;
        var reader = new BufferedReader(new InputStreamReader(System.in));
        while (!exit) {
            System.out.print(PROMPT);
            String s = reader.readLine();
            if (Objects.equals(s, "exit")) {
                exit = true;
            } else {
                try {
                    var tokens = Tokenizer.tokenize(s);
                    var node = new Parser(tokens).parse();
                    var expr = Expr.from(node);
                    var actualResult = Interpreter.eval(expr, env);
                    System.out.print("=> ");
                    System.out.println(actualResult);
                } catch (ParserException | TokenizerException | EvalException e) {
                    System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }
    }
}

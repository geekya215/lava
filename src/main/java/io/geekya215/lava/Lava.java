package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.FileNameCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Lava {
    public static void main(String[] args) throws IOException {
        var standardEnv = Interpreter.initialStandardEnv();
        var initialEnv = Env.extend(standardEnv);

        String filePath = null;
        Expr res = null;

        var terminal = TerminalBuilder.builder()
            .jansi(true)
            .jna(false)
            .build();
        var lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new ArgumentCompleter(new StringsCompleter("load"), new FileNameCompleter(), NullCompleter.INSTANCE))
            .build();

        // loop
        while (true) {
            try {
                var input = lineReader.readLine(Constants.PROMPT);
                if (input.isBlank()) {
                    continue;
                } else if (Objects.equals(input, "exit")) {
                    System.out.println("\nBye.");
                    System.exit(0);
                } else if (Objects.equals(input, "reload")) {
                    if (filePath == null) {
                        System.out.println("error: please load file first");
                        continue;
                    }
                    res = loadFile(filePath, initialEnv);
                } else if (input.startsWith("load")) {
                    filePath = input.substring(4).stripLeading().stripTrailing();
                    res = loadFile(filePath, initialEnv);
                } else {
                    res = eval(input, initialEnv);
                }

                print(res);

            } catch (UserInterruptException e) {
                System.out.println("\nctrl-c interrupt");
                System.exit(1);
            } catch (ParserException | EvalException e) {
                System.out.println("error: " + e.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println("error: file not exist");
            } catch (StackOverflowError error) {
                System.err.println("\ncrashed cause: call stack overflow");
                System.exit(1);
            }
        }
    }

    public static Expr eval(String input, Env env) {
        var tokens = Tokenizer.tokenize(input);
        var expr = Parser.parse(new Ref<>(tokens));
        return Interpreter.eval(expr, env);
    }

    public static Expr loadFile(String path, Env env) throws FileNotFoundException {
        try (var sc = new Scanner(new File(path))) {
            Expr res = null;
            while (sc.hasNext()) {
                var line = sc.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                res = eval(line, env);
            }
            return res;
        }
    }

    public static void print(Expr expr) {
        System.out.printf("%s%s\n", Constants.INDICATOR, expr);
    }
}
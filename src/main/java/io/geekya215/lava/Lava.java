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
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Lava {
    public static void main(String[] args) throws IOException {
        var standardEnv = Interpreter.initialStandardEnv();
        var initialEnv = Env.extend(standardEnv);
        String filePath = null;
        var terminal = TerminalBuilder.builder()
            .jansi(true)
            .jna(false)
            .build();
        var lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(new ArgumentCompleter(new StringsCompleter("load"), new FileNameCompleter(), NullCompleter.INSTANCE))
            .build();

        while (true) {
            try {
                var input = lineReader.readLine(Constants.PROMPT);
                if (Objects.equals(input, "exit")) {
                    break;
                } else if (Objects.equals(input, "reload")) {
                    if (filePath == null) {
                        System.out.println("please load file first");
                        continue;
                    }
                    try (var sc = new Scanner(new File(filePath))) {
                        Expr result = null;
                        while (sc.hasNext()) {
                            var line = sc.nextLine();
                            if (line.isBlank()) {
                                continue;
                            }
                            var tokens = Tokenizer.tokenize(line);
                            var expr = Parser.parse(new Ref<>(tokens));
                            result = Interpreter.eval(expr, initialEnv);
                        }
                        System.out.println(result);
                    }
                } else if (input.startsWith("load")) {
                    filePath = input.substring(4).stripLeading().stripTrailing();
                    try (var sc = new Scanner(new File(filePath))) {
                        Expr result = null;
                        while (sc.hasNext()) {
                            var line = sc.nextLine();
                            if (line.isBlank()) {
                                continue;
                            }
                            var tokens = Tokenizer.tokenize(line);
                            var expr = Parser.parse(new Ref<>(tokens));
                            result = Interpreter.eval(expr, initialEnv);
                        }
                        System.out.println(result);
                    } catch (Exception e) {
                        System.out.println("file not exist");
                    }
                } else {
                    var tokens = Tokenizer.tokenize(input);
                    var expr = Parser.parse(new Ref<>(tokens));
                    var result = Interpreter.eval(expr, initialEnv);
                    System.out.print(Constants.INDICATOR);
                    System.out.println(result);
                }
            } catch (UserInterruptException e) {
                System.out.println("\nBye.");
                break;
            } catch (ParserException | EvalException e) {
                System.out.println("error: " + e.getMessage());
            } catch (StackOverflowError error) {
                System.err.println("\ncrashed cause: call stack overflow");
                System.exit(1);
            }
        }
    }
}
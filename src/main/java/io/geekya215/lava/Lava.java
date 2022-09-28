package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.Objects;

public class Lava {
    public static void main(String[] args) throws IOException {
        var standardEnv = Interpreter.initialStandardEnv();
        var initialEnv = Env.extend(standardEnv);
        var terminal = TerminalBuilder.builder()
            .jansi(true)
            .jna(false)
            .build();
        var lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build();

        while (true) {
            try {
                var input = lineReader.readLine(Constants.PROMPT);
                if (Objects.equals(input, "exit")) {
                    break;
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
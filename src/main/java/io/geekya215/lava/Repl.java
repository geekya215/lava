package io.geekya215.lava;

import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import org.jline.reader.UserInterruptException;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

public class Repl {
    private final IO io;
    private final String prompt;

    private final ReplContext ctx;

    private final Env env = Env.extend(Interpreter.initialStandardEnv());

    public Repl(IO io, String prompt, ReplContext ctx) {
        this.io = io;
        this.prompt = prompt;
        this.ctx = ctx;
    }

    private String hintMessage() {
        return "Welcome to Lava REPL!";
    }

    public void start() {
        io.println(hintMessage());
        loop();
    }

    private void loop() {
        Expr result = null;
        while (true) {
            try {
                var x = io.readLine(prompt);
                // skip blank input
                if (x.isBlank()) {
                    continue;
                }

                var s = x.stripLeading().stripTrailing();
                if (s.equalsIgnoreCase(Command.EXIT)) {
                    io.println("Bye\n");
                    System.exit(0);
                } else if (s.equalsIgnoreCase(Command.RELOAD)) {
                    result = evalFromFile();
                } else if (s.startsWith(Command.LOAD)) {
                    var path = s.substring(4);
                    ctx.setFileLoadPath(Path.of(path));
                    result = evalFromFile();
                } else {
                    // preprocess input to ignore comments
                    var input = Utils.preprocessInput(x);
                    result = eval(input, env);
                }
                io.println(result);
            } catch (UserInterruptException e) {
                io.println("\nctrl-c interrupt");
                System.exit(1);
            } catch (ParserException | EvalException e) {
                io.println("error: " + e.getMessage());
            } catch (FileNotFoundException e) {
                io.println("error: file not exist");
            } catch (StackOverflowError error) {
                io.error("\ncrashed cause: call stack overflow");
                System.exit(1);
            } catch (Exception e) {
                io.error("unknown errors");
                System.exit(1);
            }
        }
    }

    private Expr evalFromFile() throws FileNotFoundException {
        try (var sc = new Scanner(ctx.getFileLoadPath().toFile())) {
            Expr result = null;
            while (sc.hasNext()) {
                var x = sc.nextLine();
                var input = Utils.preprocessInput(x);
                if (input.isBlank()) {
                    continue;
                }
                result = eval(input, env);
            }
            return result;
        }
    }

    private Expr eval(String input, Env env) {
        var tokens = Tokenizer.tokenize(input);
        var expr = Parser.parse(new Ref<>(tokens));
        return Interpreter.eval(expr, env);
    }
}

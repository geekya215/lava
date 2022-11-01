package io.geekya215.lava.repl;

import io.geekya215.lava.Command;
import io.geekya215.lava.Parser;
import io.geekya215.lava.Tokenizer;
import io.geekya215.lava.adt.Expr;
import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.interpreter.Interpreter;
import io.geekya215.lava.utils.Ref;
import io.geekya215.lava.utils.Utils;
import org.jline.reader.UserInterruptException;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

public abstract class Repl {
    protected final IO io;
    protected final String prompt;
    private final ReplContext ctx;

    public Repl(IO io, String prompt, ReplContext ctx) {
        this.io = io;
        this.prompt = prompt;
        this.ctx = ctx;
    }

    protected abstract String hintMessage();

    protected abstract String readLine(String prompt);

    public void start() {
        io.println(hintMessage());
        loop();
    }

    private void loop() {
        Expr result = null;
        while (true) {
            try {
                var x = readLine(prompt + "> ");
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
                    var path = s.substring(4).stripLeading().stripTrailing();
                    ctx.setFileLoadPath(Path.of(path));
                    result = evalFromFile();
                } else {
                    // preprocess input to ignore comments
                    var input = Utils.preprocessInput(x);
                    if (input.isBlank()) {
                        continue;
                    }
                    result = eval(input);
                }
                io.println(result);
            } catch (UserInterruptException e) {
                io.println("\nuser interrupt");
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
                result = eval(input);
            }
            return result;
        }
    }

    private Expr eval(String input) {
        var tokens = Tokenizer.tokenize(input);
        var expr = Parser.parse(new Ref<>(tokens));
        return Interpreter.eval(expr);
    }
}

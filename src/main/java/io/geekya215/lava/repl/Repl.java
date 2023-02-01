package io.geekya215.lava.repl;

import io.geekya215.lava.Expr;
import io.geekya215.lava.Lexer;
import io.geekya215.lava.Parser;
import io.geekya215.lava.Ref;
import io.geekya215.lava.exception.EvalException;
import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.interpreter.Interpreter;
import org.jline.reader.UserInterruptException;

public abstract class Repl {
    protected Context context;

    public Repl(Context context) {
        this.context = context;
    }

    protected abstract String banner();

    protected abstract String readLine(String prompt);

    protected abstract void println(String s);

    protected abstract void error(String s);

    public void start() {
        println(banner());
        loop();
    }

    public void loop() {
        while (true) {
            try {
                var x = readLine(context.getPrompt() + "> ");

                // skip empty input
                var line = Lexer.preprocess(x);
                if (line.isBlank()) {
                    continue;
                }

                println(context.getIndicator() + " " + eval(line));
            } catch (UserInterruptException e) {
                println("\nuser interrupt");
                System.exit(1);
            } catch (ParserException | EvalException e) {
                println("error: " + e.getMessage());
            } catch (StackOverflowError error) {
                error("\ncrashed cause: call stack overflow");
                System.exit(1);
            } catch (Exception e) {
                error("unknown errors");
                System.exit(1);
            }
        }
    }

    private Expr eval(String line) {
        var lex = Lexer.tokenize(line);
        var expr = Parser.parse(Ref.of(lex));
        return Interpreter.eval(expr);
    }
}

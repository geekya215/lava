package io.geekya215.lava;


import io.geekya215.lava.exception.ParserException;
import io.geekya215.lava.repl.*;

public class Main {
    public static void main(String[] args) throws ParserException {
        Repl repl;
        Context ctx = new Context("lava", "=>");
        try {
            repl = new JLineRepl(ctx);
        } catch (Exception e) {
            repl = new PlainRepl(ctx, IO.STDIO);
        }
        repl.start();
    }
}
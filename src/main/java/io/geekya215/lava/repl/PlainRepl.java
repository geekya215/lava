package io.geekya215.lava.repl;

public class PlainRepl extends Repl {
    public PlainRepl(IO io, String prompt, ReplContext ctx) {
        super(io, prompt, ctx);
    }

    @Override
    protected String hintMessage() {
        return "Welcome to Lava REPL, you are using the plain REPL. Some features may not be available.";
    }

    @Override
    protected String readLine(String prompt) {
        return io.readLine(prompt);
    }
}

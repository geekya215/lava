package io.geekya215.lava.repl;

public class PlainRepl extends Repl {
    private final IO io;

    public PlainRepl(Context context, IO io) {
        super(context);
        this.io = io;
    }

    @Override
    protected String banner() {
        return "Welcome to Lava REPL, you are using the plain REPL. Some features may not be available.";
    }

    @Override
    protected String readLine(String prompt) {
        return io.readLine(prompt);
    }

    @Override
    protected void println(String s) {
        io.out().println(s);
    }

    @Override
    protected void error(String s) {
        io.err().println(s);
    }
}

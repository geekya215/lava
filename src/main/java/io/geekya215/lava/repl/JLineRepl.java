package io.geekya215.lava.repl;

import org.jline.reader.LineReader;

public class JLineRepl extends Repl {
    private final LineReader lineReader;

    public JLineRepl(IO io, String prompt, ReplContext ctx, LineReader lineReader) {
        super(io, prompt, ctx);
        this.lineReader = lineReader;
    }

    @Override
    protected String hintMessage() {
        return "Welcome to Lava REPL!";
    }

    @Override
    protected String readLine(String prompt) {
        return lineReader.readLine(prompt);
    }
}

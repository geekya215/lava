package io.geekya215.lava.repl;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class JLineRepl extends Repl {
    private final Terminal terminal;
    private final LineReader lineReader;

    public JLineRepl(Context context) throws IOException {
        super(context);
        terminal = TerminalBuilder.builder()
                .jansi(true)
                .jna(false)
                .build();
        lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .history(new DefaultHistory())
                .build();
    }

    @Override
    protected String banner() {
        return "Welcome to Lava REPL!";
    }

    @Override
    protected String readLine(String prompt) {
        return lineReader.readLine(prompt);
    }

    @Override
    protected void println(String s) {
        terminal.writer().println(s);
        terminal.flush();
    }

    @Override
    protected void error(String s) {
        terminal.writer().println(s);
    }
}

package io.geekya215.lava;

import io.geekya215.lava.repl.*;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.FileNameCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.TerminalBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Repl repl = null;
        var io = new IO(new Scanner(new BufferedInputStream(System.in)), new PrintWriter(System.out), new PrintWriter(System.err));
        var prompt = "lava";
        var ctx = new ReplContext(Path.of(""));
        try {
            var terminal = TerminalBuilder.builder()
                    .jansi(true)
                    .jna(false)
                    .build();
            var lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new ArgumentCompleter(new StringsCompleter("load"), new FileNameCompleter(), NullCompleter.INSTANCE))
                    .build();
            repl = new JLineRepl(io, prompt, ctx, lineReader);
        } catch (IOException e) {
            repl = new PlainRepl(io, prompt, ctx);
        }
        repl.start();
    }
}

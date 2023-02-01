package io.geekya215.lava.repl;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

public record IO(
    Scanner scanner,
    PrintWriter out,
    PrintWriter err
) {
    public static final IO STDIO = new IO(new InputStreamReader(System.in), new PrintWriter(System.out), new PrintWriter(System.err));

    public IO(Readable input, Writer out, Writer err) {
        this(new Scanner(input), new PrintWriter(out), new PrintWriter(err));
    }

    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        return scanner.nextLine();
    }
}

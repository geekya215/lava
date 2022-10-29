package io.geekya215.lava;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.Scanner;

public class IO {
    private final Scanner scanner;
    private final PrintWriter out;
    private final PrintWriter err;

    public IO(Scanner scanner, PrintWriter out, PrintWriter err) {
        this.scanner = scanner;
        this.out = out;
        this.err = err;
    }

    public String readLine(String prompt) {
        print(prompt + "> ");
        return scanner.nextLine();
    }

    public void print(Object x) {
        out.print(x);
        out.flush();
    }

    public void println(Object x) {
        out.println(x);
        out.flush();
    }

    public void error(String e) {
        err.println(e);
        err.flush();
    }
}

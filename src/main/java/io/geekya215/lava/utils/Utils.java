package io.geekya215.lava.utils;

public final class Utils {
    private Utils() {
    }

    public static String preprocessInput(String input) {
        return (input + "\n")
            .replaceAll("\\(", " ( ")
            .replaceAll("\\)", " ) ")
            .replaceAll("'", " ' ")
            .replaceAll(";;[^\\n\\r]*?(?:[\\n\\r])", "");
    }
}

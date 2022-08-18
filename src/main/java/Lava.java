import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public class Lava {
    private static final String PROMPT = "lava> ";

    public static String READ(String s) {
        return s;
    }

    public static void PRINT(String s) {
        System.out.println(s);
    }

    public static void repl(String s) {
        PRINT(READ(s));
    }

    public static void main(String[] args) throws IOException {
        boolean exit = false;
        var reader = new BufferedReader(new InputStreamReader(System.in));
        while (!exit) {
            System.out.print(PROMPT);
            String s = reader.readLine();
            if (Objects.equals(s, "exit")) {
                exit = true;
            } else {
                repl(s);
            }
        }
    }
}

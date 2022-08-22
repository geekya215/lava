import java.util.HashMap;
import java.util.Optional;

public class Env {
    private final Optional<Env> parent;
    private final HashMap<String, Type> vars;

    public Env() {
        this.parent = Optional.empty();
        this.vars = new HashMap<>();
    }

    public Env(Optional<Env> parent, HashMap<String, Type> vars) {
        this.parent = parent;
        this.vars = vars;
    }

    public static Env extend(Env parent) {
        return new Env(
            Optional.of(parent),
            new HashMap<>()
        );
    }

    public Optional<Type> get(String name) {
        Type type = vars.get(name);
        if (type == null) {
            if (parent.isPresent()) {
                return parent.get().get(name);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.of(type);
        }
    }

    public void set(String name, Type val) {
        vars.put(name, val);
    }
}

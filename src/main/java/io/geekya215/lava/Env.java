package io.geekya215.lava;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Env(
    Optional<Env> parent,
    Map<String, Expr> variables
) {
    public static Env extend(Env env) {
        return new Env(Optional.of(env), new HashMap<>());
    }

    public Optional<Expr> get(String key) {
        Expr expr = variables.get(key);
        if (expr == null) {
            return parent.flatMap(p -> p.get(key));
        } else {
            return Optional.of(expr);
        }
    }

    public void set(String key, Expr expr) {
        variables.put(key, expr);
    }
}

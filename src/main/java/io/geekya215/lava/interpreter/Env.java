package io.geekya215.lava.interpreter;

import io.geekya215.lava.common.Option;
import io.geekya215.lava.parser.Expr;

import java.util.HashMap;
import java.util.Map;

public record Env(Option<Env> parent, Map<String, Expr> variables) {
    public static Env extend(Env env) {
        return new Env(Option.some(env), new HashMap<>());
    }

    public Option<Expr> get(String key) {
        Expr expr = variables.get(key);
        if (expr == null) {
            return Option.bind(parent, p -> p.get(key));
        } else {
           return Option.some(expr);
        }
    }

    public void set(String key, Expr expr) {
        variables.put(key, expr);
    }
}

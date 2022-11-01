package io.geekya215.lava;

import io.geekya215.lava.adt.Expr;

import java.util.HashMap;
import java.util.Optional;

public class Env {
    private final Optional<Env> parent;
    private final HashMap<String, Expr> vars;

    public Env() {
        this.parent = Optional.empty();
        this.vars = new HashMap<>();
    }

    public Env(Optional<Env> parent, HashMap<String, Expr> vars) {
        this.parent = parent;
        this.vars = vars;
    }

    public static Env extend(Env parent) {
        return new Env(
                Optional.of(parent),
                new HashMap<>()
        );
    }

    public Optional<Expr> get(String name) {
        Expr expr = vars.get(name);
        if (expr == null) {
            if (parent.isPresent()) {
                return parent.get().get(name);
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.of(expr);
        }
    }

    public void set(String name, Expr expr) {
        vars.put(name, expr);
    }
}

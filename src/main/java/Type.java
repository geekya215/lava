public sealed interface Type
    permits Type.Bool, Type.Integer, Type.Lambda, Type.List, Type.Symbol, Type.Unit {

    record Unit() implements Type {
    }

    record Integer(java.lang.Integer val) implements Type {
    }

    record Bool(Boolean val) implements Type {
    }

    record Symbol(String val) implements Type {
    }

    record List(java.util.List<Type> val) implements Type {
    }

    record Lambda(java.util.List<String> params, java.util.List<Type> body) implements Type {}
}

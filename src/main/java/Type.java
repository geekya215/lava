import java.util.function.Function;

public sealed interface Type
    permits Type.Bool, Type.Integer, Type.Lambda, Type.List, Type.Symbol {

    record Integer(java.lang.Integer val) implements Type {
    }

    record Bool(Boolean val) implements Type {
    }

    record Symbol(String val) implements Type {
    }

    record List(java.util.List<Type> val) implements Type {
    }

    record Lambda(Function<Type, Type> val) implements Type {}
}

import java.util.Iterator;

public sealed interface Type
    permits Type.Bool, Type.Integer, Type.Lambda, Type.List, Type.Symbol, Type.Unit {

    record Unit() implements Type {
        @Override
        public String toString() {
            return "Unit";
        }
    }

    record Integer(java.lang.Integer val) implements Type {
        @Override
        public String toString() {
            return val.toString();
        }
    }

    record Bool(Boolean val) implements Type {
        @Override
        public String toString() {
            return val ? "#t" : "#f";
        }
    }

    record Symbol(String val) implements Type {
        @Override
        public String toString() {
            return val;
        }
    }

    record List(java.util.List<Type> val) implements Type {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            Iterator<Type> iterator = val.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append(" ");
                }
            }
            sb.append(")");
            return sb.toString();
        }
    }

    record Lambda(java.util.List<String> params, java.util.List<Type> body) implements Type {
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Lambda(");
            Iterator<String> param = params.iterator();
            while (param.hasNext()) {
                sb.append(param.next());
                if (param.hasNext()) {
                    sb.append(" ");
                }
            }
            sb.append(") ");

            Iterator<Type> iterator = body.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append(" ");
                }
            }

            return sb.toString();
        }
    }
}

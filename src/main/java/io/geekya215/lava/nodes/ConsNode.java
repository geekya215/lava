package io.geekya215.lava.nodes;

import java.util.List;

public record ConsNode(SExprNode car, SExprNode cdr) implements SExprNode {
    @Override
    public List<SExprNode> toList() {
        var list = cdr.toList();
        list.add(0, car);
        return list;
    }

    @Override
    public Boolean isList() {
        return true;
    }
}

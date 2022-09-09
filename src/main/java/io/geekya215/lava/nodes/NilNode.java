package io.geekya215.lava.nodes;

import java.util.List;

public record NilNode() implements SExprNode {
    @Override
    public List<SExprNode> toList() {
        return List.of();
    }

    @Override
    public Boolean isList() {
        return true;
    }
}

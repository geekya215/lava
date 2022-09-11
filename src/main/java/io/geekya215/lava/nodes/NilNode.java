package io.geekya215.lava.nodes;

import java.util.ArrayList;
import java.util.List;

public record NilNode() implements SExprNode {
    @Override
    public List<SExprNode> toList() {
        return new ArrayList<>();
    }

    @Override
    public Boolean isList() {
        return true;
    }
}

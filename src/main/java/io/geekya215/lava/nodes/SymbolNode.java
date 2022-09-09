package io.geekya215.lava.nodes;

import io.geekya215.lava.exceptions.InconvertibleException;

import java.util.List;

public record SymbolNode(String val) implements SExprNode {
    @Override
    public List<SExprNode> toList() {
        throw new InconvertibleException();
    }

    @Override
    public Boolean isList() {
        return false;
    }
}

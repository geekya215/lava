package io.geekya215.lava.nodes;


import java.util.List;

public sealed interface SExprNode permits IntegerNode, SymbolNode, TrueNode, FalseNode, ConsNode, NilNode {
    List<SExprNode> toList();

    Boolean isList();
}

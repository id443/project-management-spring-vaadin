package com.PMVaadin.PMVaadin.ProjectStructure;

import java.util.*;

public class SimpleTreeItem<V> implements TreeItem<V> {

    private V value;
    private List<TreeItem<V>> children = new LinkedList<>();
    private TreeItem<V> parent;

    public SimpleTreeItem() {

    }

    public SimpleTreeItem(V value) {
        this.value = value;
    }

    @Override
    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public void setParent(TreeItem<V> parent) {
        this.parent = parent;
    }

    @Override
    public TreeItem<V> getParent() {
        return parent;
    }

    @Override
    public List<TreeItem<V>> getChildren() {
        return children;
    }

}

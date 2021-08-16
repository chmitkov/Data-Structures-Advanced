package main;

import java.util.ArrayList;
import java.util.List;

public class MyNode<T> {

    private T value;
    private MyNode<T> parent;
    private List<MyNode<T>> children;

    public MyNode() {
    }

    public MyNode(T value) {
        this.value = value;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public List<MyNode<T>> getChildren() {
        return children;
    }

    public void setChildren(List<MyNode<T>> children) {
        this.children = children;
    }

    public void addChild(MyNode<T> tMyNode) {
        this.children.add(tMyNode);
    }

    public MyNode<T> getParent() {
        return parent;
    }

    public void setParent(MyNode<T> parent) {
        this.parent = parent;
    }
}

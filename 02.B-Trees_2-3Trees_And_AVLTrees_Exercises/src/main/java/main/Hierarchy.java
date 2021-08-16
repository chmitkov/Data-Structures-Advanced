package main;

import java.util.*;
import java.util.stream.Collectors;

public class Hierarchy<T> implements IHierarchy<T> {

    private MyNode<T> root;
    private Map<T, MyNode<T>> allNodes;

    public Hierarchy(T value) {
        this.allNodes = new HashMap<>();
        MyNode<T> newNode = new MyNode<>(value);
        this.root = newNode;
        allNodes.put(value, newNode);
    }

    @Override
    public int getCount() {
        return this.allNodes.size();
    }

    @Override
    public void add(T element, T child) {
        MyNode<T> parent = allNodes.get(element);

        if (element == null || contains(child) || parent == null) {
            throw new IllegalArgumentException();
        }

        MyNode<T> toBeAdded = new MyNode<>(child);
        toBeAdded.setParent(parent);
        parent.getChildren().add(toBeAdded);

        allNodes.put(child, toBeAdded);

    }

    @Override
    public void remove(T element) {
        MyNode<T> toRemove = allNodes.get(element);

        if (!contains(element) || toRemove == null) {
            throw new IllegalArgumentException();
        }

        if (toRemove.getParent() == null) {
            throw new IllegalStateException();
        }

        MyNode<T> searchedNode = getNodeByElement(element);

        if (searchedNode == null) {
            throw new IllegalArgumentException();
        }

        MyNode<T> parent = searchedNode.getParent();
        parent.getChildren().remove(searchedNode);

        searchedNode
                .getChildren()
                .forEach(tMyNode -> {
                    tMyNode.setParent(parent);
                    parent.addChild(tMyNode);
                });

        allNodes.remove(searchedNode.getValue());
    }

    private MyNode<T> getNodeByElement(T element) {
        for (T key : allNodes.keySet()) {
            if (key.equals(element)) {
                return allNodes.get(element);
            }
        }
        return null;
    }

    @Override
    public Iterable<T> getChildren(T element) {
        return getNodeByElement(element)
                .getChildren()
                .stream()
                .map(MyNode::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public T getParent(T element) {
        MyNode<T> tMyNode = allNodes.get(element);

        if (tMyNode == null) {
            throw new IllegalArgumentException();
        }

        return tMyNode.getParent() == null ? null : tMyNode.getParent().getValue();
    }

    @Override
    public boolean contains(T element) {

        for (T key : allNodes.keySet()) {
            if (key.equals(element)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterable<T> getCommonElements(IHierarchy<T> other) {
        List<T> result = new ArrayList<>();

        allNodes
                .forEach((t, tMyNode) -> {
                    if (other.contains(t)) {
                        result.add(t);
                    }
                });


        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Deque<MyNode<T>> deque = new ArrayDeque<>(
                    Collections.singletonList(root)
            );

            @Override
            public boolean hasNext() {
                return deque.size() > 0;
            }

            @Override
            public T next() {
                MyNode<T> nextElement = deque.poll();
                deque.addAll(nextElement.getChildren());

                return nextElement.getValue();
            }
        };
    }

    private void iterateWithBFS(MyNode<T> root, Queue<MyNode<T>> queue) {
        queue.offer(root);

        if (root.getChildren().isEmpty()) {
            return;
        }

        root.getChildren().forEach(tMyNode -> iterateWithBFS(tMyNode, queue));
    }
}

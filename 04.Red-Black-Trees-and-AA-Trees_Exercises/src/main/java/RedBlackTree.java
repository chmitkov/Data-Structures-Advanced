import java.util.ArrayDeque;
import java.util.Deque;

public class RedBlackTree<Key extends Comparable<Key>, Value> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root;


    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private boolean color;
        private int size;

        public Node(Key key, Value val, boolean color, int size) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.size = size;
        }
    }

    public RedBlackTree() {
    }

    private boolean isRed(Node x) {
        if (x == null) {
            return false;
        }

        return x.color;
    }

    private int size(Node x) {
        if (x == null) {
            return 0;
        }

        return x.size;
    }


    public int size() {
        return size(root);
    }


    public boolean isEmpty() {
        return root == null;
    }

    public Value get(Key key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }

        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) {
            return null;
        }

        int cmp = x.key.compareTo(key);

        if (cmp > 0) {
            return get(x.left, key);
        } else if (cmp < 0) {
            return get(x.right, key);
        } else {
            return x.val;
        }
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }

    public void put(Key key, Value val) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }

        root = put(root, key, val);
        root.color = BLACK;
    }

    private Node put(Node h, Key key, Value val) {
        if (h == null) {
            return new Node(key, val, RED, 1);
        }

        int cmp = key.compareTo(h.key);

        if (cmp < 0) {
            h.left = put(h.left, key, val);
        } else if (cmp > 0) {
            h.right = put(h.right, key, val);
        } else {
            h.val = val;
        }

        return balance(h);
    }

    public void deleteMin() {
        if (isEmpty()) {
            throw new IllegalStateException("Tree is empty");
        }

        if (root.left == null) {
            if (root.right == null) {
                root = null;
                return;
            }

            root = rotateLeft(root);
            root.color = BLACK;

            root.left = null;
            root = balance(root);

            return;
        }

        root = deleteMin(root);
    }

    private Node deleteMin(Node h) {
        if (h.left == null) {
            return null;
        }

        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);

        return balance(h);
    }

    public void deleteMax() {
        if (isEmpty()) {
            throw new IllegalStateException("Tree is empty");
        }

        if (root.right == null) {
            if (root.left == null) {
                root = null;
                return;
            }

            root = rotateRight(root);
            root.color = BLACK;

            root.right = null;
            root = balance(root);

            return;
        }

        root = deleteMax(root);
    }

    private Node deleteMax(Node h) {
        if (isRed(h.left)) {
            h = rotateRight(h);
        }

        if (h.right == null) {
            return null;
        }


        if (!isRed(h.right) && !isRed(h.right.right)) {
            h = moveRedRight(h);
        }

        h.right = deleteMax(h.right);

        return balance(h);
    }

    public void delete(Key key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if (isEmpty()) {
            throw new IllegalStateException("Tree is empty");
        }

        root = delete(root, key);

        if (!isEmpty()) {
            root.color = BLACK;
        }
    }

    private Node delete(Node h, Key key) {

        if (h == null) {
            return null;
        }

        int cmp = key.compareTo(h.key);

        if (cmp < 0) {
            if (!isRed(h.left) && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = delete(h.left, key);
        } else {
            if (isRed(h.left)) {
                h = moveRedRight(h);
            }

            if (cmp == 0 && h.right == null) {
                return null;
            }

            if (!isRed(h.right) && !isRed(h.right.right)) {
                h = moveRedRight(h);
            }

            if (cmp == 0) {
                Node min = min(h.right);
                h.key = min.key;
                h.val = min.val;
                h.right = deleteMin(h.right);
            } else {
                h.right = delete(h.right, key);
            }
        }

        return balance(h);
    }

    private Node rotateRight(Node h) {
        Node temp = h.left;
        h.left = temp.right;
        temp.right = h;
        temp.color = h.color;
        temp.right.color = RED;
        temp.size = h.size;
        h.size = size(h.left) + size(h.right) + 1;
        return temp;
    }

    private Node rotateLeft(Node h) {
        Node temp = h.right;
        h.right = temp.left;
        temp.left = h;
        temp.color = h.color;
        temp.left.color = RED;
        temp.size = h.size;
        h.size = size(h.left) + 1 + size(h.right);
        return temp;
    }

    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }


    private Node moveRedLeft(Node h) {
        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }

        return h;
    }

    private Node moveRedRight(Node h) {
        flipColors(h);

        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }

        return h;
    }

    private Node balance(Node h) {
        if (!isRed(h.left) && isRed(h.right)) {
            h = rotateLeft(h);
        }

        if (isRed(h.left) && isRed(h.left.left)) {
            h = rotateRight(h);
        }

        if (isRed(h.left) && isRed(h.right)) {
            flipColors(h);
        }

        h.size = size(h.left) + size(h.right) + 1;

        return h;
    }

    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) {
            return -1;
        }

        return Math.max(height(x.left), height(x.right)) + 1;
    }

    public Key min() {
        Node min = min(root);

        return min == null ? null : min.key;
    }

    private Node min(Node x) {
        if (x == null) {
            return null;
        }

        while (x.left != null) {
            x = x.left;
        }

        return x;
    }

    public Key max() {
        Node max = max(root);

        return max == null ? null : max.key;
    }

    private Node max(Node x) {
        if (x == null) {
            return null;
        }

        while (x.right != null) {
            x = x.right;
        }

        return x;
    }

    public Key floor(Key key) {
        Node floor = floor(root, key);

        return floor == null ? null : floor.key;
    }

    private Node floor(Node x, Key key) {
        if (x == null) {
            return null;
        }

        int cmp = key.compareTo(x.key);

        if (cmp == 0) {
            return x;
        }

        if (cmp < 0) {
            return floor(x.left, key);
        }

        Node node = floor(x.right, key);

        return node != null ? node : x;
    }

    public Key ceiling(Key key) {
        Node ceiling = ceiling(root, key);

        return ceiling == null ? null : ceiling.key;
    }

    private Node ceiling(Node x, Key key) {
        if(x == null){
            return null;
        }

        int cmp = key.compareTo(x.key);

        if(cmp == 0){
            return x;
        }

        if(cmp > 0){
            return ceiling(x.right, key);
        }

        Node node = ceiling(x.left, key);

        return node != null ? node : x;
    }

    public Key select(int rank) {
        if (isEmpty()) {
            throw new IllegalStateException();
        }

        return select(root, rank);
    }


    private Key select(Node x, int rank) {
        if (x == null) {
            return null;
        }

        int size = size(x.left);

        if (size > rank) {
            return select(x.left, rank);
        } else if (size == rank) {
            return x.key;
        } else {
            return select(x.right, rank - size - 1);
        }
    }

    public int rank(Key key) {
        if (isEmpty()) {
            throw new IllegalStateException();
        }

        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        return rank(key, root);
    }

    private int rank(Key key, Node x) {
        if (x == null) {
            return 0;
        }

        int cmp = key.compareTo(x.key);

        if (cmp == 0) {
            return size(x.left);
        } else if (cmp < 0) {
            return rank(key, x.left);
        } else {
            return size(x.left) + 1 + rank(key, x.right);
        }
    }

    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        Deque<Key> deque = new ArrayDeque<>();

        keys(root, deque, lo, hi);
        return deque;
    }


    private void keys(Node x, Deque<Key> queue, Key lo, Key hi) {
        if (x == null) {
            return;
        }

        int cmp1 = lo.compareTo(x.key);
        int cmp2 = hi.compareTo(x.key);

        if (cmp1 < 0) {
            keys(x.left, queue, lo, hi);
        }
        if (cmp1 <= 0 && cmp2 >= 0) {
            queue.offer(x.key);
        }
        if (cmp2 > 0) {
            keys(x.right, queue, lo, hi);
        }
    }

    public int size(Key lo, Key hi) {
        return 0;
    }

    private boolean check() {
        return false;
    }

    //ToDo
    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since order is strict
    private boolean isBST() {
        return false;
    }

    //ToDo
    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    private boolean isBST(Node x, Key min, Key max) {
        return false;
    }

    //ToDo
    // are the size fields correct?
    private boolean isSizeConsistent() {
        return false;
    }

    //ToDo
    private boolean isSizeConsistent(Node x) {
        return false;
    }

    //ToDo
    // check that ranks are consistent
    private boolean isRankConsistent() {
        return false;
    }

    //ToDo
    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean isTwoThree() {
        return false;
    }

    //ToDo
    private boolean isTwoThree(Node x) {
        return false;
    }

    //ToDo
    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() {
        return false;
    }

    //ToDo
    // does every path from the root to a leaf have the given number of black links?
    private boolean isBalanced(Node x, int black) {
        return false;
    }
}
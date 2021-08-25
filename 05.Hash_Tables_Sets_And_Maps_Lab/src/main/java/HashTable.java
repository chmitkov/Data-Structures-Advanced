import java.util.*;

public class HashTable<K, V> implements Iterable<KeyValue<K, V>> {

    private static final int INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.8D;


    private LinkedList<KeyValue<K, V>>[] slots;
    private int count;
    private int capacity;

    public HashTable() {
        this.slots = new LinkedList[INITIAL_CAPACITY];
        this.capacity = INITIAL_CAPACITY;
    }

    public HashTable(int capacity) {
        this.slots = new LinkedList[capacity];
        this.capacity = capacity;
    }

    public void add(K key, V value) {
        int index = findSlotNumber(key);

        growIfNeeded();


        if (slots[index] == null) {
            slots[index] = new LinkedList<>();
        }

        //If already exists
        for (KeyValue<K, V> keyValue : slots[index]) {
            if (keyValue.getKey().equals(key)) {
                throw new IllegalArgumentException();
            }
        }

        KeyValue<K, V> element = new KeyValue<>(key, value);
        slots[index].add(element);
        count++;
    }

    private int findSlotNumber(K key) {
        return Math.abs(key.hashCode()) % this.slots.length;
    }

    private void growIfNeeded() {
        if (((double) this.count + 1) / this.capacity > LOAD_FACTOR) {
            grow();
        }
    }

//    private void grow() {
//        HashTable<K, V> hashTable = new HashTable<>(capacity * 2);
//        for (LinkedList<KeyValue<K, V>> kvp : slots) {
//            if (kvp != null) {
//                kvp.forEach(kvKeyValue -> hashTable.add(kvKeyValue.getKey(), kvKeyValue.getValue()));
//            }
//        }
//
//        this.slots = hashTable.slots;
//        capacity *= 2;
//    }

    private void grow() {
        capacity *= 2;
        LinkedList<KeyValue<K, V>>[] newSlots = new LinkedList[capacity];

        for (LinkedList<KeyValue<K, V>> slot : slots) {
            if (slot != null) {
                slot.forEach(kvKeyValue -> {
                    int index = findSlotNumber(kvKeyValue.getKey());
                    if (newSlots[index] == null) {
                        newSlots[index] = new LinkedList<>();
                    }
                    newSlots[index].add(kvKeyValue);
                });
            }
        }

        this.slots = newSlots;
    }

    public int size() {
        return this.count;
    }

    public int capacity() {
        return this.capacity;
    }

    public boolean addOrReplace(K key, V value) {
        KeyValue<K, V> element = find(key);
        if (element == null) {
            add(key, value);
            return true;
        } else {
            return false;
        }
    }

    public V get(K key) {
        KeyValue<K, V> element = find(key);
        if (element == null) {
            throw new IllegalArgumentException();
        }
        return find(key).getValue();
    }

    public KeyValue<K, V> find(K key) {
        for (LinkedList<KeyValue<K, V>> slot : slots) {
            if (slot != null) {
                for (KeyValue<K, V> kvKeyValue : slot) {
                    if (kvKeyValue.getKey().equals(key)) {
                        return kvKeyValue;
                    }
                }
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        for (LinkedList<KeyValue<K, V>> slot : slots) {
            if (slot != null) {
                for (KeyValue<K, V> kvKeyValue : slot) {
                    if (kvKeyValue.getKey().equals(key)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean remove(K key) {
        int index = findSlotNumber(key);

        if (slots[index] == null) {
            return false;
        }

        KeyValue<K, V> kvp = null;

        for (KeyValue<K, V> slot : slots[index]) {
            if (slot.getKey().equals(key)) {
                kvp = slot;
                break;
            }
        }
        if (kvp != null) {
            slots[index].remove();
            this.count--;
            return true;
        }

        return false;
    }

    public void clear() {
        this.slots = null;
        this.count = 0;
    }

    public Iterable<K> keys() {
        throw new UnsupportedOperationException();
    }

    public Iterable<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<KeyValue<K, V>> iterator() {
        return new HashIterator();
    }

    private class HashIterator implements Iterator<KeyValue<K, V>> {

        Deque<KeyValue<K, V>> elements;

        HashIterator() {
            this.elements = new ArrayDeque<>();
            for (LinkedList<KeyValue<K, V>> slot : slots) {
                if (slot != null) {
                    elements.addAll(slot);
                }
            }
        }


        @Override
        public boolean hasNext() {
            return !elements.isEmpty();
        }

        @Override
        public KeyValue<K, V> next() {
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            return elements.poll();
        }
    }
}

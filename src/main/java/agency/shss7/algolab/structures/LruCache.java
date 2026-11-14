package agency.shss7.algolab.structures;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Deterministic O(1) Least Recently Used (LRU) cache backed by a hash map and
 * an intrusive doubly-linked list of sentinel-bounded nodes.
 * Not thread-safe; external synchronisation is required for concurrent access.
 */
public class LruCache<K, V> {

    private static final class Node<K, V> {
        final K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node<K, V>> index;
    private final Node<K, V> head;
    private final Node<K, V> tail;

    public LruCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Cache capacity must be positive");
        }
        this.capacity = capacity;
        this.index = new HashMap<>(capacity * 2);
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    public Optional<V> get(K key) {
        Node<K, V> node = index.get(key);
        if (node == null) {
            return Optional.empty();
        }
        moveToFront(node);
        return Optional.of(node.value);
    }

    public void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value must not be null");
        }
        Node<K, V> existing = index.get(key);
        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        if (index.size() == capacity) {
            Node<K, V> evicted = tail.prev;
            detach(evicted);
            index.remove(evicted.key);
        }

        Node<K, V> fresh = new Node<>(key, value);
        attachAfterHead(fresh);
        index.put(key, fresh);
    }

    public boolean containsKey(K key) {
        return key != null && index.containsKey(key);
    }

    public boolean remove(K key) {
        Node<K, V> existing = key != null ? index.remove(key) : null;
        if (existing == null) {
            return false;
        }
        detach(existing);
        return true;
    }

    public int size() {
        return index.size();
    }

    public int capacity() {
        return capacity;
    }

    private void moveToFront(Node<K, V> node) {
        detach(node);
        attachAfterHead(node);
    }

    private void detach(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void attachAfterHead(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
}

package datastructures;

/**
 * CUSTOM HASH TABLE — built from scratch, no java.util.*
 *
 * Strategy : Separate chaining — each bucket holds a singly-linked
 *            list of Entry nodes, so collisions are resolved by
 *            chaining entries in the same bucket.
 *
 * Generic   : CustomHashTable<K, V> works for any key/value types.
 *             For StudentRegistry, K = String (studentId), V = Student.
 *
 * Operations and their time complexities:
 *   put()     — O(1) average, O(n) worst (all keys in same bucket)
 *   get()     — O(1) average
 *   remove()  — O(1) average
 *   contains()— O(1) average
 *   size()    — O(1)
 *   getAll()  — O(n)
 */
public class CustomHashTable<K, V> {
    //  Inner node class — forms the chain in each bucket
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;   // pointer to next entry in the chain

        Entry(K key, V value) {
            this.key   = key;
            this.value = value;
            this.next  = null;
        }
    }

    //  Table internals
    private Entry<K, V>[] buckets;   // array of chain heads
    private int           capacity;  // number of buckets
    private int           size;      // total number of key-value pairs
    private static final double LOAD_FACTOR = 0.75;
    //  Constructors
    @SuppressWarnings("unchecked")
    public CustomHashTable(int initialCapacity) {
        this.capacity = initialCapacity;
        this.buckets  = new Entry[capacity];
        this.size     = 0;
    }
    /** Default capacity of 16 buckets. */
    public CustomHashTable() {
        this(16);
    }
    //  Hash function
    /**
     * Maps a key to a bucket index.
     * Uses Java's built-in hashCode() on the key, then takes the
     * absolute value modulo capacity so the index is always valid.
     *
     *   index = |key.hashCode()| % capacity
     *
     * The Math.abs guard handles Integer.MIN_VALUE edge case.
     */
    private int hash(K key) {
        int h = key.hashCode();
        // protect against Integer.MIN_VALUE whose abs is still negative
        if (h == Integer.MIN_VALUE) h = 0;
        return Math.abs(h) % capacity;
    }
    //  Core operations
    /**
     * Inserts or updates a key-value pair.
     * If the key already exists, its value is overwritten.
     * Triggers a resize when load factor is exceeded.
     */
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");

        // Resize before inserting if load factor exceeded
        if ((double) size / capacity >= LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        Entry<K, V> head = buckets[index];

        // Walk the chain — update value if key exists
        Entry<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;   // update existing
                return;
            }
            current = current.next;
        }

        // Key not found — prepend a new entry to the chain (O(1))
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next   = head;
        buckets[index]  = newEntry;
        size++;
    }

    /**
     * Returns the value associated with the key, or null if not found.
     */
    public V get(K key) {
        if (key == null) return null;

        int index = hash(key);
        Entry<K, V> current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) return current.value;
            current = current.next;
        }
        return null;   // not found
    }

    /**
     * Removes the entry with the given key.
     * Returns true if removed, false if key did not exist.
     */
    public boolean remove(K key) {
        if (key == null) return false;

        int index = hash(key);
        Entry<K, V> current = buckets[index];
        Entry<K, V> prev    = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    buckets[index] = current.next;  // remove head of chain
                } else {
                    prev.next = current.next;        // bypass node
                }
                size--;
                return true;
            }
            prev    = current;
            current = current.next;
        }
        return false;   // key not found
    }

    /**
     * Returns true if the key exists in the table.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the total number of entries in the table.
     */
    public int size() { return size; }

    /**
     * Returns true if the table is empty.
     */
    public boolean isEmpty() { return size == 0; }
    //  Retrieval helpers
    /**
     * Returns all values in the table as a plain Object array.
     * Used by StudentRegistry to iterate over every student.
     */
    @SuppressWarnings("unchecked")
    public V[] getAllValues() {
        Object[] result = new Object[size];
        int idx = 0;

        for (int i = 0; i < capacity; i++) {
            Entry<K, V> current = buckets[i];
            while (current != null) {
                result[idx++] = current.value;
                current = current.next;
            }
        }
        return (V[]) result;
    }

    /**
     * Returns all keys in the table as a plain Object array.
     */
    @SuppressWarnings("unchecked")
    public K[] getAllKeys() {
        Object[] result = new Object[size];
        int idx = 0;

        for (int i = 0; i < capacity; i++) {
            Entry<K, V> current = buckets[i];
            while (current != null) {
                result[idx++] = current.key;
                current = current.next;
            }
        }
        return (K[]) result;
    }

    //  Resize (rehash)

    /**
     * Doubles the capacity and rehashes all existing entries.
     * Called automatically when load factor exceeds 0.75.
     * This keeps average bucket length short, maintaining O(1) ops.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int oldCapacity   = capacity;
        Entry<K, V>[] old = buckets;

        capacity = capacity * 2;
        buckets  = new Entry[capacity];
        size     = 0;   // will be recounted during re-insertion

        for (int i = 0; i < oldCapacity; i++) {
            Entry<K, V> current = old[i];
            while (current != null) {
                put(current.key, current.value);   // rehash into new buckets
                current = current.next;
            }
        }
    }


    //  Debug
    /** Prints internal bucket structure — useful during testing. */
    public void printInternalState() {
        System.out.println("\n  [HashTable Debug — capacity=" + capacity + ", size=" + size + "]");
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                System.out.print("  Bucket[" + i + "]: ");
                Entry<K, V> cur = buckets[i];
                while (cur != null) {
                    System.out.print("[" + cur.key + "] -> ");
                    cur = cur.next;
                }
                System.out.println("null");
            }
        }
    }
}

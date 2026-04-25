package datastructures;

/**
 * CUSTOM ARRAY LIST — built from scratch, no java.util.*
 *
 * ─────────────────────────────────────────────────────────────
 *  Why an ArrayList for Event Bookings?
 * ─────────────────────────────────────────────────────────────
 *  Events are created, listed, searched, and occasionally
 *  removed. The dominant operations are:
 *    - add()     — O(1) amortised  → new events added quickly
 *    - get(i)    — O(1)            → direct index access for display
 *    - search    — O(n)            → scan by eventId or name
 *    - remove    — O(n)            → shift elements after removal
 *
 *  A resizable array is ideal here because:
 *    - We rarely delete events (mostly add + read)
 *    - Random access by index is O(1) — great for listing
 *    - Dynamic resizing means we don't need to know the
 *      total number of events upfront
 *
 * ─────────────────────────────────────────────────────────────
 *  How It Works — Resizable Array
 * ─────────────────────────────────────────────────────────────
 *  Internally uses a plain Object[] array.
 *  Starts at DEFAULT_CAPACITY (10).
 *  When full, doubles capacity and copies all elements.
 *
 *  add(item):
 *    Place item at index [size], increment size.
 *    If size == capacity → resize first.   O(1) amortised.
 *
 *  get(index):
 *    Direct array access — heap[index].    O(1).
 *
 *  remove(index):
 *    Shift all elements after index left by one.
 *    Decrement size. Clear last slot.      O(n).
 *
 *  contains / indexOf:
 *    Linear scan comparing each element.  O(n).
 *
 * ─────────────────────────────────────────────────────────────
 *  Time Complexities
 * ─────────────────────────────────────────────────────────────
 *    add(item)         — O(1) amortised
 *    get(index)        — O(1)
 *    set(index, item)  — O(1)
 *    remove(index)     — O(n)
 *    removeItem(item)  — O(n)
 *    contains(item)    — O(n)
 *    indexOf(item)     — O(n)
 *    size / isEmpty    — O(1)
 *    toArray           — O(n)
 *
 * Generic: CustomArrayList<T>
 * For EventBooking, T = Event.
 */
public class CustomArrayList<T> {

    // ─────────────────────────────────────────────
    //  Internal storage
    // ─────────────────────────────────────────────
    private Object[] data;
    private int      size;
    private int      capacity;

    private static final int DEFAULT_CAPACITY = 10;

    // ─────────────────────────────────────────────
    //  Constructors
    // ─────────────────────────────────────────────
    public CustomArrayList(int initialCapacity) {
        this.capacity = initialCapacity;
        this.data     = new Object[capacity];
        this.size     = 0;
    }

    public CustomArrayList() {
        this(DEFAULT_CAPACITY);
    }

    // ═════════════════════════════════════════════
    //  CORE OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Appends item to the end of the list.
     * Resizes automatically if the array is full.
     * O(1) amortised.
     */
    public void add(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot add null.");
        if (size == capacity) resize();
        data[size++] = item;
    }

    /**
     * Inserts item at a specific index.
     * Shifts all elements from index onwards one position right.
     * O(n).
     */
    public void add(int index, T item) {
        checkIndexForAdd(index);
        if (size == capacity) resize();
        // Shift right from the end down to index
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = item;
        size++;
    }

    /**
     * Returns the element at index.
     * O(1).
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    /**
     * Replaces the element at index with item.
     * Returns the old element.
     * O(1).
     */
    @SuppressWarnings("unchecked")
    public T set(int index, T item) {
        checkIndex(index);
        T old      = (T) data[index];
        data[index] = item;
        return old;
    }

    /**
     * Removes and returns the element at index.
     * Shifts all subsequent elements left.
     * O(n).
     */
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndex(index);
        T removed = (T) data[index];
        // Shift left
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[--size] = null;   // clear last slot
        return removed;
    }

    /**
     * Removes the first occurrence of item using equals().
     * Returns true if found and removed, false otherwise.
     * O(n).
     */
    public boolean removeItem(T item) {
        int index = indexOf(item);
        if (index == -1) return false;
        remove(index);
        return true;
    }

    // ═════════════════════════════════════════════
    //  SEARCH
    // ═════════════════════════════════════════════

    /**
     * Returns the index of the first occurrence of item,
     * or -1 if not found. Uses equals(). O(n).
     */
    @SuppressWarnings("unchecked")
    public int indexOf(T item) {
        for (int i = 0; i < size; i++) {
            if (((T) data[i]).equals(item)) return i;
        }
        return -1;
    }

    /**
     * Returns true if the list contains item. O(n).
     */
    public boolean contains(T item) {
        return indexOf(item) != -1;
    }

    // ═════════════════════════════════════════════
    //  SIZE / STATE
    // ═════════════════════════════════════════════

    public int     size()     { return size;      }
    public boolean isEmpty()  { return size == 0; }

    /**
     * Removes all elements from the list.
     */
    public void clear() {
        data     = new Object[DEFAULT_CAPACITY];
        capacity = DEFAULT_CAPACITY;
        size     = 0;
    }

    /**
     * Returns all elements as a typed array. O(n).
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(data, 0, result, 0, size);
        return (T[]) result;
    }

    // ═════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═════════════════════════════════════════════

    /**
     * Doubles the internal array capacity.
     * Copies all existing elements into the new array.
     */
    private void resize() {
        capacity *= 2;
        Object[] newData = new Object[capacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    /** Validates that index is within [0, size). */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for size " + size);
        }
    }

    /** Validates that index is within [0, size] for insertions. */
    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for size " + size);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
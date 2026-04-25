package datastructures;

/**
 * CUSTOM MIN-HEAP — built from scratch, no java.util.*
 *
 * ─────────────────────────────────────────────────────────────
 *  Why a Min-Heap for the Help Desk?
 * ─────────────────────────────────────────────────────────────
 *  A help desk must always process the MOST URGENT ticket first,
 *  not the oldest. A min-heap guarantees the ticket with the
 *  lowest priority number (highest urgency) is always at the root,
 *  so it can be accessed in O(1) and removed in O(log n).
 *
 *  A regular queue would serve tickets in arrival order (FIFO),
 *  which means a Critical ticket submitted after a Low ticket
 *  would have to wait — unacceptable for a campus help desk.
 *
 * ─────────────────────────────────────────────────────────────
 *  How a Min-Heap Works
 * ─────────────────────────────────────────────────────────────
 *  A binary heap is a COMPLETE BINARY TREE stored in an array.
 *  "Complete" means every level is full except possibly the last,
 *  which is filled left to right.
 *
 *  The HEAP PROPERTY (min-heap variant):
 *    Every parent node is SMALLER THAN OR EQUAL TO its children.
 *    This means the minimum element is always at index 0 (the root).
 *
 *  Array representation — for a node at index i:
 *    Parent      : (i - 1) / 2
 *    Left child  : 2 * i + 1
 *    Right child : 2 * i + 2
 *
 *  Visual example with priorities [1, 3, 2, 4, 5, 6]:
 *
 *              1          ← root (minimum, index 0)
 *            /   \
 *           3     2
 *          / \   /
 *         4   5 6
 *
 *  Stored in array: [1, 3, 2, 4, 5, 6]
 *
 * ─────────────────────────────────────────────────────────────
 *  Core Operations
 * ─────────────────────────────────────────────────────────────
 *  insert(item)      — Add item at the end, then BUBBLE UP
 *                      until heap property is restored.  O(log n)
 *
 *  extractMin()      — Remove the root (minimum), move the last
 *                      element to the root, then SIFT DOWN
 *                      until heap property is restored.  O(log n)
 *
 *  peekMin()         — Return root without removing it.  O(1)
 *
 *  Bubble Up   (heapifyUp):
 *    New element at end → swap with parent while smaller than parent
 *    Stops when parent is smaller or element reaches root.
 *
 *  Sift Down   (heapifyDown):
 *    Root removed → last element placed at root →
 *    swap with the SMALLER of its two children while larger than children
 *    Stops when smaller than both children or reaches a leaf.
 *
 * ─────────────────────────────────────────────────────────────
 *  Time Complexities
 * ─────────────────────────────────────────────────────────────
 *    insert      — O(log n)
 *    extractMin  — O(log n)
 *    peekMin     — O(1)
 *    contains    — O(n)
 *    size/isEmpty— O(1)
 *    toArray     — O(n)
 *
 * Generic: CustomMinHeap<T extends Comparable<T>>
 * For HelpDesk, T = Ticket (which implements Comparable<Ticket>).
 */
public class CustomMinHeap<T extends Comparable<T>> {

    // ─────────────────────────────────────────────
    //  Internal array storage
    // ─────────────────────────────────────────────
    private Object[] heap;     // stores heap elements
    private int      size;     // current number of elements
    private int      capacity; // current array capacity

    private static final int DEFAULT_CAPACITY = 20;

    // ─────────────────────────────────────────────
    //  Constructors
    // ─────────────────────────────────────────────
    public CustomMinHeap(int capacity) {
        this.capacity = capacity;
        this.heap     = new Object[capacity];
        this.size     = 0;
    }

    public CustomMinHeap() {
        this(DEFAULT_CAPACITY);
    }

    // ═════════════════════════════════════════════
    //  CORE OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Inserts a new element into the heap.
     *
     * Steps:
     *   1. Place item at the next available slot (end of array)
     *   2. Bubble it UP until heap property is satisfied
     *
     * Bubbling up: if the new element is smaller than its parent,
     * swap them. Repeat until no swap needed or root reached.
     */
    public void insert(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot insert null.");
        if (size == capacity) resize();

        heap[size] = item;   // place at end
        size++;
        heapifyUp(size - 1); // restore heap property upward
    }

    /**
     * Removes and returns the MINIMUM element (root).
     *
     * Steps:
     *   1. Save the root (minimum)
     *   2. Move the last element to the root position
     *   3. Shrink size
     *   4. Sift the new root DOWN until heap property is satisfied
     *
     * Sifting down: if the root is larger than either child,
     * swap with the SMALLER child. Repeat until no swap needed
     * or a leaf is reached.
     */
    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) throw new RuntimeException("Heap is empty — nothing to extract.");

        T min = (T) heap[0];       // save the minimum (root)
        heap[0] = heap[size - 1];  // move last element to root
        heap[size - 1] = null;     // clear last slot (help GC)
        size--;
        heapifyDown(0);            // restore heap property downward
        return min;
    }

    /**
     * Returns the minimum element WITHOUT removing it.
     */
    @SuppressWarnings("unchecked")
    public T peekMin() {
        if (isEmpty()) throw new RuntimeException("Heap is empty — nothing to peek.");
        return (T) heap[0];
    }

    // ═════════════════════════════════════════════
    //  HEAP PROPERTY RESTORATION
    // ═════════════════════════════════════════════

    /**
     * BUBBLE UP — restores heap property after insert.
     *
     * Starting at index i, compare with parent at (i-1)/2.
     * If current < parent, swap and continue upward.
     * Stop when current >= parent or root is reached.
     */
    private void heapifyUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;

            if (compare(i, parent) < 0) {
                swap(i, parent);  // current is smaller than parent → swap
                i = parent;       // move up
            } else {
                break;            // heap property satisfied
            }
        }
    }

    /**
     * SIFT DOWN — restores heap property after extractMin.
     *
     * Starting at index i, find the smaller of the two children.
     * If current > smaller child, swap and continue downward.
     * Stop when current <= both children or a leaf is reached.
     */
    private void heapifyDown(int i) {
        while (true) {
            int left     = 2 * i + 1;   // left child index
            int right    = 2 * i + 2;   // right child index
            int smallest = i;            // assume current is smallest

            // Check if left child exists and is smaller
            if (left < size && compare(left, smallest) < 0) {
                smallest = left;
            }

            // Check if right child exists and is smaller than current smallest
            if (right < size && compare(right, smallest) < 0) {
                smallest = right;
            }

            if (smallest != i) {
                swap(i, smallest);  // swap with the smaller child
                i = smallest;       // move down
            } else {
                break;              // heap property satisfied
            }
        }
    }

    // ═════════════════════════════════════════════
    //  SEARCH & UTILITY
    // ═════════════════════════════════════════════

    /**
     * Returns true if the heap is empty.
     */
    public boolean isEmpty() { return size == 0; }

    /**
     * Returns the number of elements in the heap.
     */
    public int size() { return size; }

    /**
     * Returns true if the heap contains the given item.
     * O(n) — must scan all elements since heap is not sorted.
     */
    @SuppressWarnings("unchecked")
    public boolean contains(T item) {
        for (int i = 0; i < size; i++) {
            if (((T) heap[i]).compareTo(item) == 0) return true;
        }
        return false;
    }

    /**
     * Removes a specific item from the heap by value.
     * Finds it, replaces with last element, then re-heapifies.
     * O(n) to find + O(log n) to re-heapify.
     */
    @SuppressWarnings("unchecked")
    public boolean remove(T item) {
        // Find the item
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (((T) heap[i]).compareTo(item) == 0) {
                index = i;
                break;
            }
        }
        if (index == -1) return false;

        // Replace with last element
        heap[index]     = heap[size - 1];
        heap[size - 1]  = null;
        size--;

        // Restore heap property both ways
        if (index < size) {
            heapifyUp(index);
            heapifyDown(index);
        }
        return true;
    }

    /**
     * Returns all elements as an array (NOT in sorted order —
     * only the root is guaranteed to be the minimum).
     * Does NOT modify the heap.
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        Object[] result = new Object[size];
        System.arraycopy(heap, 0, result, 0, size);
        return (T[]) result;
    }

    /**
     * Returns elements in SORTED order (ascending priority).
     * Destructive — empties the heap. Call on a copy if needed.
     */
    @SuppressWarnings("unchecked")
    public T[] toSortedArray() {
        int origSize = size;
        T[] extracted = (T[]) new Comparable[origSize];

        // Extract all elements in priority order
        for (int i = 0; i < origSize; i++) {
            extracted[i] = extractMin();
        }

        // Re-insert all to restore the heap
        for (T item : extracted) insert(item);

        return extracted;
    }

    /**
     * Removes all elements from the heap.
     */
    public void clear() {
        heap = new Object[capacity];
        size = 0;
    }

    // ═════════════════════════════════════════════
    //  DISPLAY
    // ═════════════════════════════════════════════

    /**
     * Prints the heap array as stored (not sorted).
     * Shows the tree structure with parent/child relationships.
     */
    @SuppressWarnings("unchecked")
    public void printHeap() {
        if (isEmpty()) {
            System.out.println("  [MinHeap: empty]");
            return;
        }
        System.out.println("  [MinHeap — size=" + size + "  root(min)=" + heap[0] + "]");
        System.out.print("  Array: [ ");
        for (int i = 0; i < size; i++) {
            System.out.print(heap[i]);
            if (i < size - 1) System.out.print(" | ");
        }
        System.out.println(" ]");
    }

    // ═════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═════════════════════════════════════════════

    /**
     * Compares elements at two indices using their natural ordering.
     * Returns negative if heap[a] < heap[b], 0 if equal, positive if greater.
     */
    @SuppressWarnings("unchecked")
    private int compare(int a, int b) {
        return ((T) heap[a]).compareTo((T) heap[b]);
    }

    /** Swaps elements at indices a and b. */
    private void swap(int a, int b) {
        Object tmp = heap[a];
        heap[a]    = heap[b];
        heap[b]    = tmp;
    }

    /** Doubles the heap array capacity when full. */
    private void resize() {
        capacity  *= 2;
        Object[] newHeap = new Object[capacity];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }
}
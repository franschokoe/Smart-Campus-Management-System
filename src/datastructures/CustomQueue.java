package datastructures;

/**
 * CUSTOM QUEUE — built from scratch, no java.util.*
 *
 * ─────────────────────────────────────────────────────────────
 *  Why a Queue for Hostel Allocation?
 * ─────────────────────────────────────────────────────────────
 *  When students apply for a hostel room and none are available,
 *  they join a WAITING LIST. The fairest policy is
 *  First-Come, First-Served (FCFS):
 *    - First student to apply → first to get a room when one opens
 *
 *  A Queue enforces this naturally:
 *    enqueue() — student joins the back of the waiting list
 *    dequeue() — first student in line gets the next free room
 *    peek()    — see who is next without removing them
 *
 * ─────────────────────────────────────────────────────────────
 *  Implementation — Circular Array Queue
 * ─────────────────────────────────────────────────────────────
 *  Uses a fixed-size array with two pointers:
 *    front — index of the element to dequeue next
 *    rear  — index where the next enqueue will go
 *
 *  "Circular" means when rear or front reach the end of the
 *  array they wrap around to index 0, reusing space freed by
 *  previous dequeues — no wasted memory, no shifting.
 *
 *  Visual example (capacity = 5):
 *    After enqueue A,B,C:   [A][B][C][ ][ ]   front=0 rear=3
 *    After dequeue (A out): [ ][B][C][ ][ ]   front=1 rear=3
 *    After enqueue D,E:     [ ][B][C][D][E]   front=1 rear=0  (wrapped)
 *
 * ─────────────────────────────────────────────────────────────
 *  Time Complexities
 * ─────────────────────────────────────────────────────────────
 *    enqueue  — O(1)
 *    dequeue  — O(1)
 *    peek     — O(1)
 *    isEmpty  — O(1)
 *    isFull   — O(1)
 *    size     — O(1)
 *    display  — O(n)
 *
 * Generic: CustomQueue<T> works for any type T.
 * For HostelAllocation, T = String (studentId waiting for a room).
 */
public class CustomQueue<T> {

    // ─────────────────────────────────────────────
    //  Internal state
    // ─────────────────────────────────────────────
    private Object[] data;    // underlying circular array
    private int      front;   // index of the front element
    private int      rear;    // index where next element goes
    private int      size;    // current number of elements
    private int      capacity;

    private static final int DEFAULT_CAPACITY = 50;

    // ─────────────────────────────────────────────
    //  Constructors
    // ─────────────────────────────────────────────
    public CustomQueue(int capacity) {
        this.capacity = capacity;
        this.data     = new Object[capacity];
        this.front    = 0;
        this.rear     = 0;
        this.size     = 0;
    }

    public CustomQueue() {
        this(DEFAULT_CAPACITY);
    }

    // ═════════════════════════════════════════════
    //  CORE OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Adds an element to the BACK of the queue.
     * If the queue is full, it automatically doubles capacity (resize).
     *
     * After enqueue, rear advances circularly:
     *   rear = (rear + 1) % capacity
     */
    public void enqueue(T item) {
        if (item == null) throw new IllegalArgumentException("Cannot enqueue null.");
        if (isFull()) resize();

        data[rear] = item;
        rear       = (rear + 1) % capacity;   // circular wrap
        size++;
    }

    /**
     * Removes and returns the element at the FRONT of the queue.
     * Throws an exception if the queue is empty.
     *
     * After dequeue, front advances circularly:
     *   front = (front + 1) % capacity
     */
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (isEmpty()) throw new RuntimeException("Queue is empty — nothing to dequeue.");

        T item  = (T) data[front];
        data[front] = null;                    // help GC
        front   = (front + 1) % capacity;     // circular wrap
        size--;
        return item;
    }

    /**
     * Returns the front element WITHOUT removing it.
     * Throws an exception if the queue is empty.
     */
    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new RuntimeException("Queue is empty — nothing to peek.");
        return (T) data[front];
    }

    /**
     * Returns true if the queue has no elements.
     */
    public boolean isEmpty() { return size == 0; }

    /**
     * Returns true if the queue has reached capacity.
     * (Triggers resize on next enqueue, so callers rarely need this.)
     */
    public boolean isFull() { return size == capacity; }

    /**
     * Returns the number of elements currently in the queue.
     */
    public int size() { return size; }

    /**
     * Returns the capacity of the underlying array.
     */
    public int capacity() { return capacity; }

    /**
     * Removes all elements from the queue.
     */
    public void clear() {
        data  = new Object[capacity];
        front = 0;
        rear  = 0;
        size  = 0;
    }

    /**
     * Returns true if the queue contains the given item.
     * O(n) — scans the circular array.
     */
    public boolean contains(T item) {
        for (int i = 0; i < size; i++) {
            int idx = (front + i) % capacity;
            if (data[idx] != null && data[idx].equals(item)) return true;
        }
        return false;
    }

    /**
     * Returns the position of the item in the queue (1 = front).
     * Returns -1 if not found.
     */
    public int positionOf(T item) {
        for (int i = 0; i < size; i++) {
            int idx = (front + i) % capacity;
            if (data[idx] != null && data[idx].equals(item)) return i + 1;
        }
        return -1;
    }

    /**
     * Returns all elements as an array (front to back order).
     * Does NOT modify the queue.
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = data[(front + i) % capacity];
        }
        return (T[]) result;
    }

    // ═════════════════════════════════════════════
    //  DISPLAY
    // ═════════════════════════════════════════════

    /**
     * Prints the queue from front to back.
     * Used by HostelAllocation to show the waiting list.
     */
    public void display(String label) {
        System.out.println("\n  [Queue: " + label + " | size=" + size + " | capacity=" + capacity + "]");
        if (isEmpty()) {
            System.out.println("  (empty)");
            return;
        }
        System.out.print("  FRONT → ");
        for (int i = 0; i < size; i++) {
            System.out.print("[" + data[(front + i) % capacity] + "]");
            if (i < size - 1) System.out.print(" → ");
        }
        System.out.println(" ← BACK");
    }

    // ═════════════════════════════════════════════
    //  RESIZE
    // ═════════════════════════════════════════════

    /**
     * Doubles capacity when the queue is full.
     * Copies elements in logical order (front → back),
     * then resets front = 0, rear = old size.
     */
    private void resize() {
        int      newCapacity = capacity * 2;
        Object[] newData     = new Object[newCapacity];

        // Copy in logical order — linearises the circular layout
        for (int i = 0; i < size; i++) {
            newData[i] = data[(front + i) % capacity];
        }

        data     = newData;
        front    = 0;
        rear     = size;
        capacity = newCapacity;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "Queue[]";
        StringBuilder sb = new StringBuilder("Queue[");
        for (int i = 0; i < size; i++) {
            sb.append(data[(front + i) % capacity]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
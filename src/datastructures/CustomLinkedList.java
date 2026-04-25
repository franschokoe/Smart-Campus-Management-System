package datastructures;

/**
 * CUSTOM DOUBLY LINKED LIST — built from scratch, no java.util.*
 *
 * ─────────────────────────────────────────────────────────────
 *  Why a Linked List for the Library System?
 * ─────────────────────────────────────────────────────────────
 *  A library catalogue grows and shrinks dynamically:
 *    - New books are added constantly
 *    - Books get removed when lost or decommissioned
 *    - No fixed size is known upfront
 *
 *  A linked list handles arbitrary insertions and deletions
 *  WITHOUT shifting elements the way an array would.
 *  Each node lives independently in memory — adding or removing
 *  a book is just a pointer update, not a mass copy.
 *
 *  DOUBLY linked (prev + next pointers) so we can:
 *    - Traverse FORWARD  (head → tail) for full catalogue listing
 *    - Traverse BACKWARD (tail → head) for reverse listing
 *    - Delete a node in O(1) once we have a reference to it
 *      (no need to find the previous node like in singly linked)
 *
 * ─────────────────────────────────────────────────────────────
 *  Structure
 * ─────────────────────────────────────────────────────────────
 *
 *   null ← [HEAD] ⇄ [Node] ⇄ [Node] ⇄ ... ⇄ [TAIL] → null
 *
 *  Each Node holds:
 *    data  — the stored element (type T)
 *    next  — pointer to the next node
 *    prev  — pointer to the previous node
 *
 *  The list tracks:
 *    head  — first node (null if empty)
 *    tail  — last node  (null if empty)
 *    size  — number of nodes
 *
 * ─────────────────────────────────────────────────────────────
 *  Time Complexities
 * ─────────────────────────────────────────────────────────────
 *    addFirst()       — O(1)   prepend to head
 *    addLast()        — O(1)   append to tail
 *    removeFirst()    — O(1)   remove head
 *    removeLast()     — O(1)   remove tail
 *    removeItem()     — O(n)   find then O(1) unlink
 *    get(index)       — O(n)   walk from head
 *    contains()       — O(n)   linear scan
 *    size / isEmpty   — O(1)
 *    toArray()        — O(n)
 *
 * Generic: CustomLinkedList<T>
 * For LibrarySystem, T = Book.
 */
public class CustomLinkedList<T> {

    // ─────────────────────────────────────────────
    //  Inner Node class
    // ─────────────────────────────────────────────
    public static class Node<T> {
        public T       data;
        public Node<T> next;
        public Node<T> prev;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    // ─────────────────────────────────────────────
    //  List state
    // ─────────────────────────────────────────────
    private Node<T> head;   // first node
    private Node<T> tail;   // last node
    private int     size;

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public CustomLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // ═════════════════════════════════════════════
    //  ADD OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Prepends item to the FRONT of the list.  O(1)
     *
     * Before:  null ← [old head] ⇄ ...
     * After:   null ← [newNode] ⇄ [old head] ⇄ ...
     */
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;   // new node points forward to old head
            head.prev    = newNode; // old head points back to new node
            head         = newNode; // head pointer moves to new node
        }
        size++;
    }

    /**
     * Appends item to the END of the list.  O(1)
     *
     * Before:  ... ⇄ [old tail] → null
     * After:   ... ⇄ [old tail] ⇄ [newNode] → null
     */
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;    // new node points back to old tail
            tail.next    = newNode; // old tail points forward to new node
            tail         = newNode; // tail pointer moves to new node
        }
        size++;
    }

    /**
     * Inserts item at a specific index.  O(n)
     * Index 0 = addFirst; index size = addLast.
     */
    public void add(int index, T item) {
        checkIndexForAdd(index);
        if (index == 0)    { addFirst(item); return; }
        if (index == size) { addLast(item);  return; }

        Node<T> current = getNode(index);   // node currently AT index
        Node<T> newNode = new Node<>(item);

        // Wire new node between current.prev and current
        newNode.prev         = current.prev;
        newNode.next         = current;
        current.prev.next    = newNode;
        current.prev         = newNode;
        size++;
    }

    /**
     * Convenience — appends to the end (mirrors ArrayList API).
     */
    public void add(T item) {
        addLast(item);
    }

    // ═════════════════════════════════════════════
    //  REMOVE OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Removes and returns the FIRST element.  O(1)
     */
    public T removeFirst() {
        if (isEmpty()) throw new RuntimeException("List is empty.");
        T data = head.data;

        if (size == 1) {
            head = tail = null;
        } else {
            head      = head.next;  // advance head
            head.prev = null;       // cut back-pointer
        }
        size--;
        return data;
    }

    /**
     * Removes and returns the LAST element.  O(1)
     */
    public T removeLast() {
        if (isEmpty()) throw new RuntimeException("List is empty.");
        T data = tail.data;

        if (size == 1) {
            head = tail = null;
        } else {
            tail      = tail.prev;  // retreat tail
            tail.next = null;       // cut forward-pointer
        }
        size--;
        return data;
    }

    /**
     * Removes the element at the given index.  O(n)
     */
    public T remove(int index) {
        checkIndex(index);
        if (index == 0)        return removeFirst();
        if (index == size - 1) return removeLast();

        Node<T> node = getNode(index);
        unlinkNode(node);
        return node.data;
    }

    /**
     * Removes the first occurrence of item using equals().
     * Returns true if found and removed, false otherwise.  O(n)
     */
    public boolean removeItem(T item) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(item)) {
                if (current == head)  { removeFirst(); return true; }
                if (current == tail)  { removeLast();  return true; }
                unlinkNode(current);
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // ═════════════════════════════════════════════
    //  ACCESS OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Returns the element at index without removing it.  O(n)
     */
    public T get(int index) {
        checkIndex(index);
        return getNode(index).data;
    }

    /**
     * Returns the first element without removing it.  O(1)
     */
    public T getFirst() {
        if (isEmpty()) throw new RuntimeException("List is empty.");
        return head.data;
    }

    /**
     * Returns the last element without removing it.  O(1)
     */
    public T getLast() {
        if (isEmpty()) throw new RuntimeException("List is empty.");
        return tail.data;
    }

    /**
     * Replaces the element at index with item.
     * Returns the old element.  O(n)
     */
    public T set(int index, T item) {
        checkIndex(index);
        Node<T> node = getNode(index);
        T old        = node.data;
        node.data    = item;
        return old;
    }

    // ═════════════════════════════════════════════
    //  SEARCH OPERATIONS
    // ═════════════════════════════════════════════

    /**
     * Returns true if the list contains item.  O(n)
     */
    public boolean contains(T item) {
        return indexOf(item) != -1;
    }

    /**
     * Returns the index of the first occurrence of item,
     * or -1 if not found.  O(n)
     */
    public int indexOf(T item) {
        Node<T> current = head;
        int     index   = 0;
        while (current != null) {
            if (current.data.equals(item)) return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * Returns the head node — used by LibrarySystem to traverse
     * the list manually for custom search logic (e.g. search by ISBN).
     */
    public Node<T> getHead() { return head; }

    /**
     * Returns the tail node — used for reverse traversal.
     */
    public Node<T> getTail() { return tail; }

    // ═════════════════════════════════════════════
    //  SIZE / STATE
    // ═════════════════════════════════════════════

    public int     size()    { return size;      }
    public boolean isEmpty() { return size == 0; }

    /**
     * Removes all nodes from the list.
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Returns all elements as an array (head → tail order).  O(n)
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        Object[]  result  = new Object[size];
        Node<T>   current = head;
        int       i       = 0;
        while (current != null) {
            result[i++] = current.data;
            current     = current.next;
        }
        return (T[]) result;
    }

    /**
     * Returns all elements in REVERSE order (tail → head).  O(n)
     */
    @SuppressWarnings("unchecked")
    public T[] toReverseArray() {
        Object[]  result  = new Object[size];
        Node<T>   current = tail;
        int       i       = 0;
        while (current != null) {
            result[i++] = current.data;
            current     = current.prev;
        }
        return (T[]) result;
    }

    // ═════════════════════════════════════════════
    //  DISPLAY
    // ═════════════════════════════════════════════

    /**
     * Prints the list from head to tail.
     * Useful for debugging during development.
     */
    public void printList() {
        if (isEmpty()) {
            System.out.println("  [LinkedList: empty]");
            return;
        }
        System.out.print("  HEAD ⇄ ");
        Node<T> current = head;
        while (current != null) {
            System.out.print("[" + current.data + "]");
            if (current.next != null) System.out.print(" ⇄ ");
            current = current.next;
        }
        System.out.println(" ⇄ TAIL");
        System.out.println("  Size: " + size);
    }

    // ═════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═════════════════════════════════════════════

    /**
     * Walks to the node at the given index.
     * Optimised: starts from HEAD if index < size/2,
     *            starts from TAIL if index >= size/2.
     * This halves traversal time for tail-side accesses.
     */
    private Node<T> getNode(int index) {
        Node<T> current;
        if (index < size / 2) {
            // Traverse from head
            current = head;
            for (int i = 0; i < index; i++) current = current.next;
        } else {
            // Traverse from tail
            current = tail;
            for (int i = size - 1; i > index; i--) current = current.prev;
        }
        return current;
    }

    /**
     * Unlinks a middle node (not head or tail) from the list.
     * Updates its neighbours' pointers and decrements size.
     *
     * Before:  ... ⇄ [prev] ⇄ [node] ⇄ [next] ⇄ ...
     * After:   ... ⇄ [prev] ⇄ [next] ⇄ ...
     */
    private void unlinkNode(Node<T> node) {
        node.prev.next = node.next;   // prev skips over node
        node.next.prev = node.prev;   // next points back to prev
        node.next      = null;        // help garbage collection
        node.prev      = null;
        size--;
    }

    /** Validates index is within [0, size). */
    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for size " + size);
    }

    /** Validates index is within [0, size] for insertions. */
    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for size " + size);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HEAD ⇄ ");
        Node<T> current = head;
        while (current != null) {
            sb.append("[").append(current.data).append("]");
            if (current.next != null) sb.append(" ⇄ ");
            current = current.next;
        }
        return sb.append(" ⇄ TAIL").toString();
    }
}
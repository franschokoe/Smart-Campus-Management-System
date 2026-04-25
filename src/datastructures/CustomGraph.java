package datastructures;

public class CustomGraph {

    private static class AdjNode {
        int     vertexIndex;
        AdjNode next;
        AdjNode(int vi) { this.vertexIndex = vi; }
    }

    private static class Vertex {
        String  label;
        AdjNode adjHead;
        Vertex(String label) { this.label = label; }
    }
    //  State

    private Vertex[] vertices;
    private int      vertexCount;
    private int      edgeCount;
    private int      capacity;

    public CustomGraph() {
        capacity    = 20;
        vertices    = new Vertex[capacity];
        vertexCount = 0;
        edgeCount   = 0;
    }

    //  VERTEX OPERATIONS

    /** Adds vertex. Returns its index. No-op if label already exists. */
    public int addVertex(String label) {
        int existing = indexOf(label);
        if (existing != -1) return existing;
        if (vertexCount == capacity) resize();
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] == null) {
                vertices[i] = new Vertex(label);
                vertexCount++;
                return i;
            }
        }
        return -1;
    }

    /** Removes vertex and all edges connected to it. */
    public boolean removeVertex(String label) {
        int idx = indexOf(label);
        if (idx == -1) return false;
        AdjNode cur = vertices[idx].adjHead;
        while (cur != null) { edgeCount--; cur = cur.next; }
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null && i != idx) removeEdgeByIndices(i, idx);
        }
        vertices[idx] = null;
        vertexCount--;
        return true;
    }

    public boolean hasVertex(String label) { return indexOf(label) != -1; }
    public int vertexCount()               { return vertexCount;          }
    public int edgeCount()                 { return edgeCount;            }

    //  EDGE OPERATIONS
    public boolean addEdge(String from, String to) {
        int fi = indexOf(from);
        int ti = indexOf(to);
        if (fi == -1 || ti == -1)  return false;
        if (hasEdge(from, to))     return false;
        AdjNode node           = new AdjNode(ti);
        node.next              = vertices[fi].adjHead;
        vertices[fi].adjHead   = node;
        edgeCount++;
        return true;
    }

    public boolean removeEdge(String from, String to) {
        int fi = indexOf(from);
        int ti = indexOf(to);
        if (fi == -1 || ti == -1) return false;
        return removeEdgeByIndices(fi, ti);
    }

    public boolean hasEdge(String from, String to) {
        int fi = indexOf(from);
        int ti = indexOf(to);
        if (fi == -1 || ti == -1) return false;
        AdjNode cur = vertices[fi].adjHead;
        while (cur != null) {
            if (cur.vertexIndex == ti) return true;
            cur = cur.next;
        }
        return false;
    }

    //  GRAPH QUERIE
    /** All vertices that have a direct edge pointing TO courseCode. */
    public String[] getDirectPrerequisites(String courseCode) {
        int targetIdx = indexOf(courseCode);
        if (targetIdx == -1) return new String[0];
        int count = 0;
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null) {
                AdjNode cur = vertices[i].adjHead;
                while (cur != null) {
                    if (cur.vertexIndex == targetIdx) { count++; break; }
                    cur = cur.next;
                }
            }
        }
        String[] result = new String[count];
        int ri = 0;
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null) {
                AdjNode cur = vertices[i].adjHead;
                while (cur != null) {
                    if (cur.vertexIndex == targetIdx) { result[ri++] = vertices[i].label; break; }
                    cur = cur.next;
                }
            }
        }
        return result;
    }

    /**
     * ALL prerequisites (direct + transitive) found via reverse BFS.
     * e.g. getAllPrerequisites("CS301") might return ["CS201","CS101"]
     */
    public String[] getAllPrerequisites(String courseCode) {
        int targetIdx = indexOf(courseCode);
        if (targetIdx == -1) return new String[0];
        boolean[] seen    = new boolean[capacity];
        boolean[] isPrereq = new boolean[capacity];
        int[]     bfsQ    = new int[capacity];
        int       head = 0, tail = 0, found = 0;
        bfsQ[tail++]     = targetIdx;
        seen[targetIdx]  = true;
        while (head < tail) {
            int current = bfsQ[head++];
            for (int i = 0; i < capacity; i++) {
                if (vertices[i] != null && !seen[i]) {
                    AdjNode cur = vertices[i].adjHead;
                    while (cur != null) {
                        if (cur.vertexIndex == current) {
                            seen[i] = true;
                            isPrereq[i] = true;
                            bfsQ[tail++] = i;
                            found++;
                            break;
                        }
                        cur = cur.next;
                    }
                }
            }
        }
        String[] result = new String[found];
        int ri = 0;
        for (int i = 0; i < capacity; i++) {
            if (isPrereq[i]) result[ri++] = vertices[i].label;
        }
        return result;
    }

    /** All courses reachable forward from courseCode (forward BFS). */
    public String[] getCoursesUnlocked(String courseCode) {
        int startIdx = indexOf(courseCode);
        if (startIdx == -1) return new String[0];
        boolean[] visited = new boolean[capacity];
        int[]     bfsQ    = new int[capacity];
        int       head = 0, tail = 0, found = 0;
        bfsQ[tail++]       = startIdx;
        visited[startIdx]  = true;
        while (head < tail) {
            int current = bfsQ[head++];
            AdjNode cur = vertices[current].adjHead;
            while (cur != null) {
                int nb = cur.vertexIndex;
                if (!visited[nb]) {
                    visited[nb]  = true;
                    bfsQ[tail++] = nb;
                    if (nb != startIdx) found++;
                }
                cur = cur.next;
            }
        }
        String[] result = new String[found];
        int ri = 0;
        for (int i = 0; i < capacity; i++) {
            if (visited[i] && i != startIdx) result[ri++] = vertices[i].label;
        }
        return result;
    }

    /** All vertex labels in the graph. */
    public String[] getAllVertices() {
        String[] result = new String[vertexCount];
        int ri = 0;
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null) result[ri++] = vertices[i].label;
        }
        return result;
    }

    //  CYCLE DETECTION  (DFS three-colour)

    public boolean hasCycle() {
        int[] colour = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null && colour[i] == 0) {
                if (dfsCycle(i, colour)) return true;
            }
        }
        return false;
    }

    /** Check if adding edge from->to would create a cycle. */
    public boolean wouldCreateCycle(String from, String to) {
        addEdge(from, to);
        boolean cycle = hasCycle();
        removeEdge(from, to);
        return cycle;
    }

    private boolean dfsCycle(int v, int[] colour) {
        colour[v] = 1;
        AdjNode cur = vertices[v].adjHead;
        while (cur != null) {
            int nb = cur.vertexIndex;
            if (vertices[nb] != null) {
                if (colour[nb] == 1)                   return true;
                if (colour[nb] == 0 && dfsCycle(nb, colour)) return true;
            }
            cur = cur.next;
        }
        colour[v] = 2;
        return false;
    }

    //  TOPOLOGICAL SORT  (Kahn's BFS algorithm)
    /**
     * Returns a valid study order that respects all prerequisites.
     * Returns null if a cycle exists (no valid order possible).
     *
     * Kahn's Algorithm:
     *   1. Compute in-degree of every vertex
     *   2. Enqueue vertices with in-degree 0 (no prerequisites)
     *   3. Dequeue, add to result, reduce neighbours' in-degree
     *   4. Enqueue any neighbour whose in-degree drops to 0
     */
    public String[] topologicalSort() {
        if (hasCycle()) return null;
        int[] inDegree = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null) {
                AdjNode cur = vertices[i].adjHead;
                while (cur != null) { inDegree[cur.vertexIndex]++; cur = cur.next; }
            }
        }
        int[] queue = new int[capacity];
        int   head  = 0, tail = 0;
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null && inDegree[i] == 0) queue[tail++] = i;
        }
        String[] result = new String[vertexCount];
        int ri = 0;
        while (head < tail) {
            int v = queue[head++];
            if (vertices[v] != null) result[ri++] = vertices[v].label;
            AdjNode cur = vertices[v].adjHead;
            while (cur != null) {
                inDegree[cur.vertexIndex]--;
                if (inDegree[cur.vertexIndex] == 0) queue[tail++] = cur.vertexIndex;
                cur = cur.next;
            }
        }
        return result;
    }

    //  DISPLAY
    /** Prints the full adjacency list. */
    public void printGraph() {
        System.out.println("\n  [Prerequisite Graph — Adjacency List]");
        System.out.println("  " + "-".repeat(48));
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null) {
                System.out.print("  " + vertices[i].label + "  ──►  ");
                AdjNode cur = vertices[i].adjHead;
                if (cur == null) System.out.print("(no outgoing edges)");
                while (cur != null) {
                    System.out.print(vertices[cur.vertexIndex].label);
                    if (cur.next != null) System.out.print("  ──►  ");
                    cur = cur.next;
                }
                System.out.println();
            }
        }
        System.out.println("  " + "-".repeat(48));
        System.out.printf("  Vertices: %d   Edges: %d%n", vertexCount, edgeCount);
    }

    /** Prints prerequisite chain for one course. */
    public void printPrerequisiteChain(String courseCode) {
        String[] prereqs = getAllPrerequisites(courseCode);
        String[] unlocks = getCoursesUnlocked(courseCode);
        System.out.println("\n  Course  : " + courseCode);
        System.out.print("  Requires: ");
        if (prereqs.length == 0) System.out.print("None (entry-level)");
        else for (String p : prereqs) System.out.print(p + "  ");
        System.out.println();
        System.out.print("  Unlocks : ");
        if (unlocks.length == 0) System.out.print("Nothing (terminal course)");
        else for (String u : unlocks) System.out.print(u + "  ");
        System.out.println();
    }

    //  PRIVATE HELPERS
    private int indexOf(String label) {
        for (int i = 0; i < capacity; i++) {
            if (vertices[i] != null && vertices[i].label.equals(label)) return i;
        }
        return -1;
    }

    private boolean removeEdgeByIndices(int fi, int ti) {
        AdjNode cur = vertices[fi].adjHead, prev = null;
        while (cur != null) {
            if (cur.vertexIndex == ti) {
                if (prev == null) vertices[fi].adjHead = cur.next;
                else              prev.next = cur.next;
                edgeCount--;
                return true;
            }
            prev = cur; cur = cur.next;
        }
        return false;
    }

    private void resize() {
        Vertex[] newArr = new Vertex[capacity * 2];
        System.arraycopy(vertices, 0, newArr, 0, capacity);
        vertices = newArr;
        capacity *= 2;
    }
}

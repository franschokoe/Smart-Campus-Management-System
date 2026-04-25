package models;

/**
 * Represents a help desk ticket in the Smart Campus system.
 *
 * A Ticket is the data object stored inside the CustomMinHeap.
 * The heap orders tickets by priority so the most urgent ones
 * are always processed first.
 *
 * Priority levels (lower number = higher urgency):
 *   1 — Critical  (system down, safety issue)
 *   2 — High      (blocking academic work)
 *   3 — Medium    (inconvenient but workable)
 *   4 — Low       (general inquiry / cosmetic)
 *
 * The Comparable interface is implemented so the MinHeap can
 * compare two tickets and decide which one has higher priority.
 */
public class Ticket implements Comparable<Ticket> {

    // ─────────────────────────────────────────────
    //  Fields
    // ─────────────────────────────────────────────
    private String ticketId;       // unique ID,   e.g. "TKT001"
    private String studentId;      // who raised it
    private String studentName;    // name of the student
    private String category;       // e.g. "IT Support", "Finance", "Housing"
    private String description;    // what the problem is
    private int    priority;       // 1 (Critical) → 4 (Low)
    private String status;         // Open | In Progress | Resolved
    private String dateSubmitted;  // simple string date, e.g. "2025-08-01"
    private String dateResolved;   // null until resolved
    private String resolutionNote; // admin notes when closing ticket

    // ─────────────────────────────────────────────
    //  Static counter for auto-generating ticket IDs
    // ─────────────────────────────────────────────
    private static int counter = 1;

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public Ticket(String studentId,
                  String studentName,
                  String category,
                  String description,
                  int    priority,
                  String dateSubmitted) {
        this.ticketId      = String.format("TKT%03d", counter++);
        this.studentId     = studentId;
        this.studentName   = studentName;
        this.category      = category;
        this.description   = description;
        this.priority      = priority;
        this.status        = "Open";
        this.dateSubmitted = dateSubmitted;
        this.dateResolved  = null;
        this.resolutionNote = null;
    }

    // ─────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────
    public String getTicketId()       { return ticketId;       }
    public String getStudentId()      { return studentId;      }
    public String getStudentName()    { return studentName;    }
    public String getCategory()       { return category;       }
    public String getDescription()    { return description;    }
    public int    getPriority()       { return priority;       }
    public String getStatus()         { return status;         }
    public String getDateSubmitted()  { return dateSubmitted;  }
    public String getDateResolved()   { return dateResolved;   }
    public String getResolutionNote() { return resolutionNote; }

    /** Human-readable priority label. */
    public String getPriorityLabel() {
        return switch (priority) {
            case 1  -> "CRITICAL";
            case 2  -> "HIGH";
            case 3  -> "MEDIUM";
            case 4  -> "LOW";
            default -> "UNKNOWN";
        };
    }

    // ─────────────────────────────────────────────
    //  Setters / state transitions
    // ─────────────────────────────────────────────
    public void setStatus(String status)             { this.status         = status;         }
    public void setDateResolved(String date)         { this.dateResolved   = date;           }
    public void setResolutionNote(String note)       { this.resolutionNote = note;           }
    public void setPriority(int priority)            { this.priority       = priority;       }

    /** Marks this ticket as resolved. */
    public void resolve(String resolutionNote, String dateResolved) {
        this.status         = "Resolved";
        this.resolutionNote = resolutionNote;
        this.dateResolved   = dateResolved;
    }

    /** Marks this ticket as In Progress. */
    public void startProcessing() {
        this.status = "In Progress";
    }

    public boolean isResolved()   { return "Resolved".equals(status);    }
    public boolean isOpen()       { return "Open".equals(status);        }
    public boolean isInProgress() { return "In Progress".equals(status); }

    // ─────────────────────────────────────────────
    //  Comparable — used by the MinHeap to compare
    // ─────────────────────────────────────────────

    /**
     * Lower priority number = higher urgency = comes FIRST in the heap.
     * compareTo returns negative if THIS ticket is more urgent.
     *
     * Example:
     *   Critical (1).compareTo(Low (4)) → 1 - 4 = -3  (Critical comes first)
     *   Low (4).compareTo(Critical (1)) → 4 - 1 = +3  (Critical still first)
     */
    @Override
    public int compareTo(Ticket other) {
        return Integer.compare(this.priority, other.priority);
    }

    // ─────────────────────────────────────────────
    //  Display
    // ─────────────────────────────────────────────

    /** Full formatted ticket card. */
    public void display() {
        String thin = "-".repeat(52);
        System.out.println("\n" + thin);
        System.out.println("  TICKET DETAILS");
        System.out.println(thin);
        System.out.printf("  %-20s: %s%n",  "Ticket ID",      ticketId);
        System.out.printf("  %-20s: %s (%s)%n", "Student",    studentName, studentId);
        System.out.printf("  %-20s: %s%n",  "Category",       category);
        System.out.printf("  %-20s: %s%n",  "Description",    description);
        System.out.printf("  %-20s: [%d] %s%n", "Priority",   priority, getPriorityLabel());
        System.out.printf("  %-20s: %s%n",  "Status",         status);
        System.out.printf("  %-20s: %s%n",  "Date Submitted", dateSubmitted);
        if (isResolved()) {
            System.out.printf("  %-20s: %s%n", "Date Resolved",  dateResolved);
            System.out.printf("  %-20s: %s%n", "Resolution",     resolutionNote);
        }
        System.out.println(thin);
    }

    /** One-line row for ticket listing tables. */
    public String toTableRow() {
        return String.format("  %-8s %-10s %-14s %-16s [%d] %-10s %s",
                ticketId, studentId, category,
                truncate(description, 16), priority,
                getPriorityLabel(), status);
    }

    /** Truncates a string to maxLen characters with "..." if needed. */
    private String truncate(String s, int maxLen) {
        if (s == null)          return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }

    @Override
    public String toString() {
        return ticketId + " [" + getPriorityLabel() + "] " + category + " — " + status;
    }
}
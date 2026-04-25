package models;

/**
 * Represents a campus event in the Smart Campus system.
 *
 * An Event is the data object stored inside the CustomArrayList.
 * Each event tracks its own attendee list as a fixed-size array
 * of student IDs — capacity is set when the event is created.
 *
 * Fields:
 *   eventId      — unique ID,        e.g. "EVT001"
 *   eventName    — full name,        e.g. "Annual Tech Expo"
 *   category     — type of event,    e.g. "Academic", "Social", "Sports"
 *   venue        — location,         e.g. "Main Hall"
 *   date         — event date,       e.g. "2025-09-15"
 *   time         — event time,       e.g. "14:00"
 *   capacity     — max attendees
 *   organiser    — name of organiser
 *   description  — short blurb
 *   attendees[]  — array of studentIds who have booked
 *   attendeeCount— how many have booked so far
 *   status       — Open | Full | Cancelled
 */
public class Event {

    // ─────────────────────────────────────────────
    //  Fields
    // ─────────────────────────────────────────────
    private String   eventId;
    private String   eventName;
    private String   category;
    private String   venue;
    private String   date;
    private String   time;
    private int      capacity;
    private String   organiser;
    private String   description;
    private String[] attendees;      // array of studentIds — fixed to capacity
    private int      attendeeCount;
    private String   status;         // Open | Full | Cancelled

    // ─────────────────────────────────────────────
    //  Static counter for auto-generating event IDs
    // ─────────────────────────────────────────────
    private static int counter = 1;

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public Event(String eventName,
                 String category,
                 String venue,
                 String date,
                 String time,
                 int    capacity,
                 String organiser,
                 String description) {
        this.eventId       = String.format("EVT%03d", counter++);
        this.eventName     = eventName;
        this.category      = category;
        this.venue         = venue;
        this.date          = date;
        this.time          = time;
        this.capacity      = capacity;
        this.organiser     = organiser;
        this.description   = description;
        this.attendees     = new String[capacity];
        this.attendeeCount = 0;
        this.status        = "Open";
    }


    //  Getters

    public String   getEventId()       { return eventId;                      }
    public String   getEventName()     { return eventName;                    }
    public String   getCategory()      { return category;                     }
    public String   getVenue()         { return venue;                        }
    public String   getDate()          { return date;                         }
    public String   getTime()          { return time;                         }
    public int      getCapacity()      { return capacity;                     }
    public String   getOrganiser()     { return organiser;                    }
    public String   getDescription()   { return description;                  }
    public int      getAttendeeCount() { return attendeeCount;                }
    public String   getStatus()        { return status;                       }
    public int      getSlotsLeft()     { return capacity - attendeeCount;     }
    public boolean  isFull()           { return attendeeCount >= capacity;    }
    public boolean  isCancelled()      { return "Cancelled".equals(status);  }
    public boolean  isOpen()           { return "Open".equals(status);       }

    // ─────────────────────────────────────────────
    //  Setters
    // ─────────────────────────────────────────────
    public void setEventName(String v)   { this.eventName   = v; }
    public void setVenue(String v)       { this.venue       = v; }
    public void setDate(String v)        { this.date        = v; }
    public void setTime(String v)        { this.time        = v; }
    public void setOrganiser(String v)   { this.organiser   = v; }
    public void setDescription(String v) { this.description = v; }
    public void setStatus(String v)      { this.status      = v; }

    // ─────────────────────────────────────────────
    //  Booking operations
    // ─────────────────────────────────────────────

    /**
     * Books a spot for a student.
     * Returns true if successful, false if event is full,
     * cancelled, or the student is already booked.
     */
    public boolean book(String studentId) {
        if (isFull() || isCancelled())   return false;
        if (hasBooked(studentId))        return false;
        attendees[attendeeCount++] = studentId;
        if (isFull()) status = "Full";
        return true;
    }

    /**
     * Cancels a student's booking.
     * Shifts remaining attendees left to fill the gap.
     * Returns true if the student was found and removed.
     */
    public boolean cancelBooking(String studentId) {
        for (int i = 0; i < attendeeCount; i++) {
            if (attendees[i].equals(studentId)) {
                // Shift left
                for (int j = i; j < attendeeCount - 1; j++) {
                    attendees[j] = attendees[j + 1];
                }
                attendees[--attendeeCount] = null;
                // Re-open if it was marked full
                if ("Full".equals(status)) status = "Open";
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the student has already booked this event.
     */
    public boolean hasBooked(String studentId) {
        for (int i = 0; i < attendeeCount; i++) {
            if (attendees[i].equals(studentId)) return true;
        }
        return false;
    }

    /**
     * Returns a copy of the attendee array (only filled slots).
     */
    public String[] getAttendees() {
        String[] copy = new String[attendeeCount];
        System.arraycopy(attendees, 0, copy, 0, attendeeCount);
        return copy;
    }

    /** Cancels the entire event. */
    public void cancel() { this.status = "Cancelled"; }

    // ─────────────────────────────────────────────
    //  Display
    // ─────────────────────────────────────────────

    /** Full formatted event card. */
    public void display() {
        String thin = "-".repeat(52);
        System.out.println("\n" + thin);
        System.out.println("  EVENT DETAILS");
        System.out.println(thin);
        System.out.printf("  %-20s: %s%n",  "Event ID",    eventId);
        System.out.printf("  %-20s: %s%n",  "Event Name",  eventName);
        System.out.printf("  %-20s: %s%n",  "Category",    category);
        System.out.printf("  %-20s: %s%n",  "Venue",       venue);
        System.out.printf("  %-20s: %s  at  %s%n", "Date & Time", date, time);
        System.out.printf("  %-20s: %s%n",  "Organiser",   organiser);
        System.out.printf("  %-20s: %d / %d  (%d slots left)%n",
                "Bookings", attendeeCount, capacity, getSlotsLeft());
        System.out.printf("  %-20s: %s%n",  "Status",      status);
        System.out.printf("  %-20s: %s%n",  "Description", description);
        System.out.println(thin);
    }

    /** One-line row used in event listing tables. */
    public String toTableRow() {
        return String.format("  %-8s %-28s %-12s %-12s %-5s %d/%d  %s",
                eventId, eventName, category, venue,
                date, attendeeCount, capacity, status);
    }

    @Override
    public String toString() {
        return eventId + " — " + eventName + " [" + status + "]";
    }
}
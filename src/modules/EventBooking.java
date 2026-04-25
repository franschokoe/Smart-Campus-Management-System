package modules;

import datastructures.CustomArrayList;
import datastructures.CustomHashTable;
import models.Event;

import java.util.Scanner;

/**
 * EVENT BOOKING MODULE
 *
 * Manages all campus events and student bookings.
 *
 * ─────────────────────────────────────────────────────────────
 *  Data Structures Used
 * ─────────────────────────────────────────────────────────────
 *
 *  CustomArrayList<Event>  (eventList)
 *    Stores all Event objects in insertion order.
 *    Used for listing, iterating, and displaying events.
 *    add()  O(1) amortised — new events added fast.
 *    get(i) O(1)           — direct index access for display.
 *    Why ArrayList? Events are mostly added and read, rarely
 *    deleted. A resizable array is the most natural fit.
 *
 *  CustomHashTable<String, Event>  (eventIndex)
 *    Key   : eventId  (e.g. "EVT001")
 *    Value : Event object
 *    Why   : O(1) average lookup when a student books or cancels
 *            by event ID — much faster than scanning the list.
 *
 *  CustomHashTable<String, CustomArrayList<String>>  (studentBookings)
 *    Key   : studentId
 *    Value : list of eventIds the student has booked
 *    Why   : O(1) lookup of "what events has this student booked?"
 *            without scanning every event's attendee list.
 *
 * ─────────────────────────────────────────────────────────────
 *  Public API  (called by Main.java)
 * ─────────────────────────────────────────────────────────────
 *  Admin:
 *    createEvent(scanner)          — add a new event
 *    cancelEvent(scanner)          — cancel an entire event
 *    updateEvent(scanner)          — edit event details
 *    displayAllEvents()            — list every event
 *    displayEventAttendees(scanner)— show who booked an event
 *    displayStatistics()           — event and booking summary
 *
 *  Student:
 *    bookEvent(scanner)            — book a spot at an event
 *    cancelMyBooking(scanner)      — cancel their own booking
 *    myBookings(scanner)           — list events they booked
 *    displayAllEvents()            — view upcoming events
 */
public class EventBooking {

    // ─────────────────────────────────────────────
    //  Storage
    // ─────────────────────────────────────────────
    private final CustomArrayList<Event>                              eventList;
    private final CustomHashTable<String, Event>                      eventIndex;
    private final CustomHashTable<String, CustomArrayList<String>>    studentBookings;

    private static final String BORDER      = "=".repeat(60);
    private static final String THIN_BORDER = "-".repeat(60);

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public EventBooking() {
        eventList       = new CustomArrayList<>();
        eventIndex      = new CustomHashTable<>(16);
        studentBookings = new CustomHashTable<>(16);
        seedDemoData();
    }

    // ─────────────────────────────────────────────
    //  Demo data
    // ─────────────────────────────────────────────
    private void seedDemoData() {
        addEventInternal(new Event("Annual Tech Expo",          "Academic",  "Main Hall",        "2025-09-15", "09:00", 200, "Dr. Sithole",  "Showcasing student tech projects and innovations."));
        addEventInternal(new Event("Welcome Back Braai",        "Social",    "Campus Grounds",   "2025-09-20", "12:00", 300, "SRC",          "Annual welcome braai for all returning students."));
        addEventInternal(new Event("Career Fair 2025",          "Academic",  "Sports Centre",    "2025-09-25", "08:00", 500, "Career Office", "Meet top employers from across South Africa."));
        addEventInternal(new Event("Inter-Res Football",        "Sports",    "Campus Stadium",   "2025-10-02", "14:00", 800, "Sports Dept",  "Annual inter-residence football tournament."));
        addEventInternal(new Event("Mental Health Awareness",   "Wellness",  "Lecture Hall B3",  "2025-10-05", "10:00", 100, "Counselling",  "Panel discussion on student mental wellbeing."));
        addEventInternal(new Event("Java Programming Workshop", "Academic",  "Computer Lab 2",   "2025-10-10", "13:00", 40,  "CS Department","Hands-on Java workshop for first-year students."));
        addEventInternal(new Event("Cultural Night",            "Social",    "Great Hall",       "2025-10-18", "18:00", 400, "SRC",          "Celebrating the diversity of campus cultures."));
        addEventInternal(new Event("Graduation Ceremony",       "Academic",  "Auditorium",       "2025-11-28", "09:00", 600, "Registrar",    "Graduation ceremony for Class of 2025."));

        // Seed some bookings
        bookInternal("EVT001", "S001");
        bookInternal("EVT001", "S002");
        bookInternal("EVT002", "S001");
        bookInternal("EVT003", "S003");
        bookInternal("EVT006", "S005");
    }

    /** Internal — adds an event to both list and index. */
    private void addEventInternal(Event event) {
        eventList.add(event);
        eventIndex.put(event.getEventId(), event);
    }

    /** Internal — books a student into an event and records it. */
    private boolean bookInternal(String eventId, String studentId) {
        Event event = eventIndex.get(eventId);
        if (event == null || !event.book(studentId)) return false;
        // Add to student's booking list
        CustomArrayList<String> bookings = studentBookings.get(studentId);
        if (bookings == null) {
            bookings = new CustomArrayList<>();
            studentBookings.put(studentId, bookings);
        }
        bookings.add(eventId);
        return true;
    }

    // ═════════════════════════════════════════════
    //  CREATE EVENT  (admin)
    // ═════════════════════════════════════════════
    public void createEvent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  CREATE NEW EVENT");
        System.out.println(THIN_BORDER);

        String name  = prompt(scanner, "Event Name");
        String cat   = promptCategory(scanner);
        String venue = prompt(scanner, "Venue");
        String date  = prompt(scanner, "Date (YYYY-MM-DD)");
        String time  = prompt(scanner, "Time (HH:MM)");
        int    cap   = readInt(scanner, "Capacity (max attendees)", 1, 2000);
        String org   = prompt(scanner, "Organiser");
        String desc  = prompt(scanner, "Short Description");

        Event event = new Event(name, cat, venue, date, time, cap, org, desc);
        addEventInternal(event);

        printSuccess("Event '" + name + "' created with ID: " + event.getEventId());
        event.display();
    }

    // ═════════════════════════════════════════════
    //  CANCEL EVENT  (admin)
    // ═════════════════════════════════════════════
    public void cancelEvent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  CANCEL EVENT");
        System.out.println(THIN_BORDER);

        String eventId = prompt(scanner, "Enter Event ID to cancel").toUpperCase();
        Event  event   = eventIndex.get(eventId);

        if (event == null)        { printError("Event '" + eventId + "' not found."); return; }
        if (event.isCancelled())  { printError("Event '" + eventId + "' is already cancelled."); return; }

        event.display();
        System.out.println("  ⚠  This will cancel bookings for "
                + event.getAttendeeCount() + " student(s).");

        String confirm = prompt(scanner, "Confirm cancellation? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            event.cancel();
            printSuccess("Event '" + event.getEventName() + "' has been cancelled.");
            System.out.println("  Affected students: " + event.getAttendeeCount());
        } else {
            System.out.println("  Cancellation aborted.");
        }
    }

    // ═════════════════════════════════════════════
    //  UPDATE EVENT  (admin)
    // ═════════════════════════════════════════════
    public void updateEvent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  UPDATE EVENT");
        System.out.println(THIN_BORDER);

        String eventId = prompt(scanner, "Enter Event ID to update").toUpperCase();
        Event  event   = eventIndex.get(eventId);

        if (event == null)       { printError("Event '" + eventId + "' not found."); return; }
        if (event.isCancelled()) { printError("Cannot update a cancelled event."); return; }

        event.display();
        System.out.println("  What would you like to update?");
        System.out.println("  1. Event Name");
        System.out.println("  2. Venue");
        System.out.println("  3. Date");
        System.out.println("  4. Time");
        System.out.println("  5. Organiser");
        System.out.println("  6. Description");
        System.out.println(THIN_BORDER);

        switch (prompt(scanner, "Enter choice")) {
            case "1" -> { event.setEventName(prompt(scanner, "New event name"));   printSuccess("Name updated.");        }
            case "2" -> { event.setVenue(prompt(scanner, "New venue"));            printSuccess("Venue updated.");       }
            case "3" -> { event.setDate(prompt(scanner, "New date (YYYY-MM-DD)")); printSuccess("Date updated.");        }
            case "4" -> { event.setTime(prompt(scanner, "New time (HH:MM)"));      printSuccess("Time updated.");        }
            case "5" -> { event.setOrganiser(prompt(scanner, "New organiser"));    printSuccess("Organiser updated.");   }
            case "6" -> { event.setDescription(prompt(scanner, "New description")); printSuccess("Description updated."); }
            default  ->   printError("Invalid choice.");
        }
    }

    // ═════════════════════════════════════════════
    //  DISPLAY ALL EVENTS  (both roles)
    // ═════════════════════════════════════════════
    public void displayAllEvents() {
        System.out.println("\n" + BORDER);
        System.out.println("  ALL EVENTS  (" + eventList.size() + " total)");
        System.out.println(BORDER);

        if (eventList.isEmpty()) {
            System.out.println("  No events scheduled.");
            System.out.println(BORDER);
            return;
        }

        printEventTableHeader();
        System.out.println(THIN_BORDER);

        for (int i = 0; i < eventList.size(); i++) {
            System.out.println(eventList.get(i).toTableRow());
        }

        System.out.println(BORDER);
        System.out.println("  Total events: " + eventList.size());
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY EVENT ATTENDEES  (admin)
    // ═════════════════════════════════════════════
    public void displayEventAttendees(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  VIEW EVENT ATTENDEES");
        System.out.println(THIN_BORDER);

        String eventId = prompt(scanner, "Enter Event ID").toUpperCase();
        Event  event   = eventIndex.get(eventId);

        if (event == null) { printError("Event '" + eventId + "' not found."); return; }

        event.display();

        String[] attendees = event.getAttendees();
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ATTENDEES  (" + attendees.length + " booked)");
        System.out.println(THIN_BORDER);

        if (attendees.length == 0) {
            System.out.println("  No students have booked this event yet.");
        } else {
            for (int i = 0; i < attendees.length; i++) {
                System.out.printf("  %3d. %s%n", (i + 1), attendees[i]);
            }
        }

        System.out.println(THIN_BORDER);
        System.out.printf("  Booked: %d   Slots left: %d   Capacity: %d%n",
                event.getAttendeeCount(), event.getSlotsLeft(), event.getCapacity());
        System.out.println(THIN_BORDER);
    }

    // ═════════════════════════════════════════════
    //  BOOK EVENT  (student)
    // ═════════════════════════════════════════════
    public void bookEvent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  BOOK AN EVENT");
        System.out.println(THIN_BORDER);

        displayAllEvents();

        String eventId   = prompt(scanner, "Enter Event ID to book").toUpperCase();
        Event  event     = eventIndex.get(eventId);

        if (event == null)        { printError("Event '" + eventId + "' not found."); return; }
        if (event.isCancelled())  { printError("This event has been cancelled."); return; }
        if (event.isFull())       { printError("This event is fully booked. No slots available."); return; }

        String studentId = prompt(scanner, "Your Student ID").toUpperCase();

        if (event.hasBooked(studentId)) {
            printError("You have already booked this event.");
            return;
        }

        event.display();
        String confirm = prompt(scanner, "Confirm booking? (yes/no)");

        if (confirm.equalsIgnoreCase("yes")) {
            bookInternal(eventId, studentId);
            printSuccess("You have successfully booked: " + event.getEventName());
            System.out.printf("  Date  : %s  at  %s%n", event.getDate(), event.getTime());
            System.out.printf("  Venue : %s%n", event.getVenue());
            System.out.printf("  Slots remaining: %d%n", event.getSlotsLeft());
        } else {
            System.out.println("  Booking cancelled.");
        }
    }

    // ═════════════════════════════════════════════
    //  CANCEL MY BOOKING  (student)
    // ═════════════════════════════════════════════
    public void cancelMyBooking(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  CANCEL MY BOOKING");
        System.out.println(THIN_BORDER);

        String studentId = prompt(scanner, "Your Student ID").toUpperCase();
        String eventId   = prompt(scanner, "Event ID to cancel").toUpperCase();

        Event event = eventIndex.get(eventId);
        if (event == null) { printError("Event '" + eventId + "' not found."); return; }

        if (!event.hasBooked(studentId)) {
            printError("You have not booked this event.");
            return;
        }

        event.display();
        String confirm = prompt(scanner, "Confirm cancellation of your booking? (yes/no)");

        if (confirm.equalsIgnoreCase("yes")) {
            // Remove from event attendees
            event.cancelBooking(studentId);

            // Remove from student's personal booking list
            CustomArrayList<String> bookings = studentBookings.get(studentId);
            if (bookings != null) bookings.removeItem(eventId);

            printSuccess("Your booking for '" + event.getEventName() + "' has been cancelled.");
        } else {
            System.out.println("  Cancellation aborted.");
        }
    }

    // ═════════════════════════════════════════════
    //  MY BOOKINGS  (student)
    // ═════════════════════════════════════════════
    public void myBookings(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  MY EVENT BOOKINGS");
        System.out.println(THIN_BORDER);

        String studentId = prompt(scanner, "Your Student ID").toUpperCase();

        CustomArrayList<String> bookings = studentBookings.get(studentId);

        if (bookings == null || bookings.isEmpty()) {
            System.out.println("  You have no event bookings.");
            return;
        }

        System.out.println("\n" + THIN_BORDER);
        System.out.printf("  Bookings for Student: %s%n", studentId);
        System.out.println(THIN_BORDER);
        printEventTableHeader();
        System.out.println(THIN_BORDER);

        for (int i = 0; i < bookings.size(); i++) {
            Event event = eventIndex.get(bookings.get(i));
            if (event != null) System.out.println(event.toTableRow());
        }

        System.out.println(THIN_BORDER);
        System.out.println("  Total bookings: " + bookings.size());
        System.out.println(THIN_BORDER);
    }

    // ═════════════════════════════════════════════
    //  SEARCH EVENTS  (both)
    // ═════════════════════════════════════════════
    public void searchEvents(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  SEARCH EVENTS");
        System.out.println(THIN_BORDER);
        System.out.println("  1. Search by Event ID");
        System.out.println("  2. Search by Name / Keyword");
        System.out.println("  3. Filter by Category");
        System.out.println(THIN_BORDER);

        switch (prompt(scanner, "Enter choice")) {
            case "1" -> {
                String id    = prompt(scanner, "Enter Event ID").toUpperCase();
                Event  event = eventIndex.get(id);
                if (event == null) printError("Event '" + id + "' not found.");
                else               event.display();
            }
            case "2" -> {
                String query = prompt(scanner, "Enter keyword").toLowerCase();
                System.out.println("\n" + THIN_BORDER);
                printEventTableHeader();
                System.out.println(THIN_BORDER);
                boolean found = false;
                for (int i = 0; i < eventList.size(); i++) {
                    Event e = eventList.get(i);
                    if (e.getEventName().toLowerCase().contains(query)
                            || e.getDescription().toLowerCase().contains(query)
                            || e.getVenue().toLowerCase().contains(query)) {
                        System.out.println(e.toTableRow());
                        found = true;
                    }
                }
                if (!found) printError("No events found matching '" + query + "'.");
                else System.out.println(THIN_BORDER);
            }
            case "3" -> {
                String cat = promptCategory(scanner);
                System.out.println("\n" + THIN_BORDER);
                System.out.println("  CATEGORY: " + cat.toUpperCase());
                System.out.println(THIN_BORDER);
                printEventTableHeader();
                System.out.println(THIN_BORDER);
                boolean found = false;
                for (int i = 0; i < eventList.size(); i++) {
                    Event e = eventList.get(i);
                    if (e.getCategory().equals(cat)) {
                        System.out.println(e.toTableRow());
                        found = true;
                    }
                }
                if (!found) printError("No " + cat + " events found.");
                else System.out.println(THIN_BORDER);
            }
            default -> printError("Invalid choice.");
        }
    }

    // ═════════════════════════════════════════════
    //  STATISTICS  (admin)
    // ═════════════════════════════════════════════
    public void displayStatistics() {
        System.out.println("\n" + BORDER);
        System.out.println("  EVENT BOOKING STATISTICS");
        System.out.println(BORDER);

        int open = 0, full = 0, cancelled = 0, totalBookings = 0;
        int academic = 0, social = 0, sports = 0, wellness = 0, other = 0;

        for (int i = 0; i < eventList.size(); i++) {
            Event e = eventList.get(i);
            totalBookings += e.getAttendeeCount();

            switch (e.getStatus()) {
                case "Open"      -> open++;
                case "Full"      -> full++;
                case "Cancelled" -> cancelled++;
            }
            switch (e.getCategory()) {
                case "Academic"  -> academic++;
                case "Social"    -> social++;
                case "Sports"    -> sports++;
                case "Wellness"  -> wellness++;
                default          -> other++;
            }
        }

        System.out.println("  BY STATUS:");
        System.out.printf("  %-25s: %d%n", "Open",         open);
        System.out.printf("  %-25s: %d%n", "Fully Booked", full);
        System.out.printf("  %-25s: %d%n", "Cancelled",    cancelled);
        System.out.printf("  %-25s: %d%n", "Total Events", eventList.size());
        System.out.println(THIN_BORDER);
        System.out.println("  BY CATEGORY:");
        System.out.printf("  %-25s: %d%n", "Academic",  academic);
        System.out.printf("  %-25s: %d%n", "Social",    social);
        System.out.printf("  %-25s: %d%n", "Sports",    sports);
        System.out.printf("  %-25s: %d%n", "Wellness",  wellness);
        System.out.printf("  %-25s: %d%n", "Other",     other);
        System.out.println(THIN_BORDER);
        System.out.printf("  %-25s: %d%n", "Total Bookings", totalBookings);
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═════════════════════════════════════════════

    private void printEventTableHeader() {
        System.out.printf("  %-8s %-28s %-12s %-12s %-11s %-7s %s%n",
                "ID", "Name", "Category", "Venue", "Date", "Booked", "Status");
    }

    private String promptCategory(Scanner scanner) {
        System.out.println("  Category:");
        System.out.println("  1. Academic   2. Social   3. Sports   4. Wellness   5. Other");
        return switch (prompt(scanner, "Enter choice")) {
            case "1" -> "Academic";
            case "2" -> "Social";
            case "3" -> "Sports";
            case "4" -> "Wellness";
            default  -> "Other";
        };
    }

    private int readInt(Scanner scanner, String label, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(prompt(scanner, label + " (" + min + "-" + max + ")"));
                if (v >= min && v <= max) return v;
                printError("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    private String prompt(Scanner scanner, String label) {
        System.out.print("  " + label + ": ");
        return scanner.nextLine().trim();
    }

    private void printSuccess(String msg) { System.out.println("\n  ✔ " + msg); }
    private void printError(String msg)   { System.out.println("\n  ✘ ERROR: " + msg); }
}
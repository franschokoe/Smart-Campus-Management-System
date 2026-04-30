package modules;

import datastructures.CustomMinHeap;
import datastructures.CustomHashTable;
import models.Ticket;

import java.util.Scanner;

public class HelpDesk {

    //  Storage
    private final CustomMinHeap<Ticket>          pendingHeap;    // open tickets by priority
    private final CustomHashTable<String, Ticket> resolvedTable; // resolved tickets
    private final CustomHashTable<String, Ticket> allTickets;    // master index

    private static final String BORDER      = "=".repeat(56);
    private static final String THIN_BORDER = "-".repeat(56);


    //  Constructor
    public HelpDesk() {
        pendingHeap   = new CustomMinHeap<>(20);
        resolvedTable = new CustomHashTable<>(16);
        allTickets    = new CustomHashTable<>(32);
        seedDemoData();
    }


    //  Demo data
    private void seedDemoData() {
        submitInternal(new Ticket(
                "202232392",
                "SIRAKALALA",
                "IT Support",
                "Cannot login to student portal",
                1,
                "2026-04-25"
        ));
    }

    /** Internal helper — adds ticket to both heap and master index. */
    private void submitInternal(Ticket ticket) {
        pendingHeap.insert(ticket);
        allTickets.put(ticket.getTicketId(), ticket);
    }

    //  SUBMIT TICKET  (student)
    public void submitTicket(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  SUBMIT HELP DESK TICKET");
        System.out.println(THIN_BORDER);

        String studentId   = prompt(scanner, "Your Student Number").toUpperCase();
        String studentName = prompt(scanner, "Your Full Name");

        // Category selection
        System.out.println("\n  Select Category:");
        System.out.println("  1. IT Support");
        System.out.println("  2. Finance");
        System.out.println("  3. Academic");
        System.out.println("  4. Housing");
        System.out.println("  5. Library");
        System.out.println("  6. Other");
        System.out.println(THIN_BORDER);

        String category = switch (prompt(scanner, "Enter choice")) {
            case "1" -> "IT Support";
            case "2" -> "Finance";
            case "3" -> "Academic";
            case "4" -> "Housing";
            case "5" -> "Library";
            default  -> "Other";
        };

        String description = prompt(scanner, "Describe your issue");

        // Priority selection
        System.out.println("\n  Select Priority:");
        System.out.println("  1. CRITICAL — System down / safety issue");
        System.out.println("  2. HIGH     — Blocking my academic work");
        System.out.println("  3. MEDIUM   — Inconvenient but manageable");
        System.out.println("  4. LOW      — General inquiry / minor issue");
        System.out.println(THIN_BORDER);

        int priority = 3; // default to medium
        String pChoice = prompt(scanner, "Enter priority (1-4)");
        try {
            int p = Integer.parseInt(pChoice);
            if (p >= 1 && p <= 4) priority = p;
            else printError("Invalid priority. Defaulting to MEDIUM (3).");
        } catch (NumberFormatException e) {
            printError("Invalid input. Defaulting to MEDIUM (3).");
        }

        Ticket ticket = new Ticket(studentId, studentName, category,
                description, priority, getTodayDate());
        submitInternal(ticket);

        printSuccess("Ticket submitted successfully!");
        System.out.println("  Your Ticket ID : " + ticket.getTicketId());
        System.out.println("  Priority       : [" + priority + "] " + ticket.getPriorityLabel());
        System.out.println("  Keep your Ticket ID to track your request.");
        ticket.display();
    }

    //  PROCESS NEXT TICKET  (admin)
    public void processNextTicket() {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  PROCESS NEXT TICKET");
        System.out.println(THIN_BORDER);

        if (pendingHeap.isEmpty()) {
            System.out.println("  No pending tickets. All clear!");
            return;
        }

        // Peek to show which ticket will be processed
        Ticket next = pendingHeap.peekMin();
        System.out.println("  Next ticket to process (highest priority):");
        next.display();

        System.out.println("  Remaining pending tickets: "
                + (pendingHeap.size() - 1) + " after this one.");

        // Mark as In Progress — it stays in the heap until resolved
        next.startProcessing();
        printSuccess("Ticket " + next.getTicketId() + " is now IN PROGRESS.");
        System.out.println("  Use 'Resolve Ticket' to close it when done.");
    }

    //  RESOLVE TICKET  (admin)
    public void resolveTicket(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  RESOLVE TICKET");
        System.out.println(THIN_BORDER);

        if (pendingHeap.isEmpty()) {
            System.out.println("  No pending tickets to resolve.");
            return;
        }

        // Show what's at the top
        Ticket top = pendingHeap.peekMin();
        System.out.println("  Current highest-priority ticket:");
        top.display();

        String ticketId = prompt(scanner, "Enter Ticket ID to resolve (or press Enter for '" + top.getTicketId() + "')");
        if (ticketId.isEmpty()) ticketId = top.getTicketId();
        ticketId = ticketId.toUpperCase();

        // Find the ticket in allTickets
        Ticket ticket = allTickets.get(ticketId);
        if (ticket == null) {
            printError("Ticket '" + ticketId + "' not found.");
            return;
        }
        if (ticket.isResolved()) {
            printError("Ticket '" + ticketId + "' is already resolved.");
            return;
        }

        String note = prompt(scanner, "Enter resolution note");
        ticket.resolve(note, getTodayDate());

        // Remove from heap, add to resolved table
        pendingHeap.remove(ticket);
        resolvedTable.put(ticket.getTicketId(), ticket);

        printSuccess("Ticket '" + ticketId + "' has been resolved.");
        ticket.display();
        System.out.println("  Pending tickets remaining: " + pendingHeap.size());
    }

    //  ESCALATE TICKET  (admin)
    public void escalateTicket(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ESCALATE TICKET PRIORITY");
        System.out.println(THIN_BORDER);

        String ticketId = prompt(scanner, "Enter Ticket ID to escalate").toUpperCase();
        Ticket ticket   = allTickets.get(ticketId);

        if (ticket == null)       { printError("Ticket '" + ticketId + "' not found.");       return; }
        if (ticket.isResolved())  { printError("Cannot escalate a resolved ticket.");          return; }
        if (ticket.getPriority() == 1) { printError("Ticket is already CRITICAL priority."); return; }

        int oldPriority = ticket.getPriority();

        // Remove from heap, change priority, re-insert so heap reorders
        pendingHeap.remove(ticket);
        ticket.setPriority(oldPriority - 1);
        pendingHeap.insert(ticket);

        printSuccess("Ticket '" + ticketId + "' escalated from ["
                + oldPriority + "] " + priorityLabel(oldPriority)
                + " → [" + ticket.getPriority() + "] " + ticket.getPriorityLabel());
    }

    //  DISPLAY PENDING TICKETS  (admin)
    public void displayPendingTickets() {
        System.out.println("\n" + BORDER);
        System.out.println("  PENDING TICKETS  (" + pendingHeap.size() + " total)");
        System.out.println("  Sorted by priority — CRITICAL tickets first");
        System.out.println(BORDER);

        if (pendingHeap.isEmpty()) {
            System.out.println("  No pending tickets. Help desk is clear!");
            System.out.println(BORDER);
            return;
        }

        printTicketTableHeader();
        System.out.println(THIN_BORDER);

        // toSortedArray extracts in priority order
        Object[] sorted = pendingHeap.toSortedArray();
        for (Object obj : sorted) {
            Ticket t = (Ticket) obj;
            System.out.println(t.toTableRow());
        }

        System.out.println(BORDER);
        System.out.println("  Total pending: " + pendingHeap.size());
        System.out.println(BORDER);
    }

    //  DISPLAY RESOLVED TICKETS  (admin)
    public void displayResolvedTickets() {
        System.out.println("\n" + BORDER);
        System.out.println("  RESOLVED TICKETS  (" + resolvedTable.size() + " total)");
        System.out.println(BORDER);

        if (resolvedTable.size() == 0) {
            System.out.println("  No tickets have been resolved yet.");
            System.out.println(BORDER);
            return;
        }

        printTicketTableHeader();
        System.out.println(THIN_BORDER);

        Object[] resolved = resolvedTable.getAllValues();
        for (Object obj : resolved) {
            Ticket t = (Ticket) obj;
            System.out.println(t.toTableRow());
        }

        System.out.println(BORDER);
        System.out.println("  Total resolved: " + resolvedTable.size());
        System.out.println(BORDER);
    }


    //  DISPLAY ALL TICKETS  (admin)
    public void displayAllTickets() {
        int total = allTickets.size();
        System.out.println("\n" + BORDER);
        System.out.println("  ALL TICKETS  (" + total + " total)");
        System.out.println(BORDER);

        if (total == 0) {
            System.out.println("  No tickets in the system.");
            System.out.println(BORDER);
            return;
        }

        printTicketTableHeader();
        System.out.println(THIN_BORDER);

        Object[] tickets = allTickets.getAllValues();
        for (Object obj : tickets) {
            System.out.println(((Ticket) obj).toTableRow());
        }

        System.out.println(BORDER);
        System.out.printf("  Pending: %d   Resolved: %d   Total: %d%n",
                pendingHeap.size(), resolvedTable.size(), total);
        System.out.println(BORDER);
    }

    //  CHECK TICKET STATUS  (student)

    public void checkTicketStatus(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  CHECK TICKET STATUS");
        System.out.println(THIN_BORDER);

        String ticketId = prompt(scanner, "Enter your Ticket ID (e.g. TKT001)").toUpperCase();
        Ticket ticket   = allTickets.get(ticketId);

        if (ticket == null) {
            printError("Ticket '" + ticketId + "' not found. Check your ticket ID.");
            return;
        }

        ticket.display();

        // Friendly status message
        switch (ticket.getStatus()) {
            case "Open"        -> System.out.println("  ℹ Your ticket is queued and will be processed by priority.");
            case "In Progress" -> System.out.println("  ℹ A staff member is currently working on your ticket.");
            case "Resolved"    -> System.out.println("  ✔ Your ticket has been resolved. See the resolution note above.");
        }
    }

    //  STATISTICS  (admin)
    public void displayStatistics() {
        System.out.println("\n" + BORDER);
        System.out.println("  HELP DESK STATISTICS");
        System.out.println(BORDER);

        Object[] tickets = allTickets.getAllValues();
        int critical = 0, high = 0, medium = 0, low = 0;
        int open = 0, inProgress = 0, resolved = 0;
        int itSupport = 0, finance = 0, academic = 0, housing = 0, library = 0, other = 0;

        for (Object obj : tickets) {
            Ticket t = (Ticket) obj;

            switch (t.getPriority()) {
                case 1 -> critical++;
                case 2 -> high++;
                case 3 -> medium++;
                case 4 -> low++;
            }
            switch (t.getStatus()) {
                case "Open"        -> open++;
                case "In Progress" -> inProgress++;
                case "Resolved"    -> resolved++;
            }
            switch (t.getCategory()) {
                case "IT Support" -> itSupport++;
                case "Finance"    -> finance++;
                case "Academic"   -> academic++;
                case "Housing"    -> housing++;
                case "Library"    -> library++;
                default           -> other++;
            }
        }

        System.out.println("  BY STATUS:");
        System.out.printf("  %-25s: %d%n", "Open",        open);
        System.out.printf("  %-25s: %d%n", "In Progress", inProgress);
        System.out.printf("  %-25s: %d%n", "Resolved",    resolved);
        System.out.printf("  %-25s: %d%n", "TOTAL",       tickets.length);
        System.out.println(THIN_BORDER);
        System.out.println("  BY PRIORITY:");
        System.out.printf("  %-25s: %d%n", "[1] CRITICAL", critical);
        System.out.printf("  %-25s: %d%n", "[2] HIGH",     high);
        System.out.printf("  %-25s: %d%n", "[3] MEDIUM",   medium);
        System.out.printf("  %-25s: %d%n", "[4] LOW",      low);
        System.out.println(THIN_BORDER);
        System.out.println("  BY CATEGORY:");
        System.out.printf("  %-25s: %d%n", "IT Support",  itSupport);
        System.out.printf("  %-25s: %d%n", "Finance",     finance);
        System.out.printf("  %-25s: %d%n", "Academic",    academic);
        System.out.printf("  %-25s: %d%n", "Housing",     housing);
        System.out.printf("  %-25s: %d%n", "Library",     library);
        System.out.printf("  %-25s: %d%n", "Other",       other);
        System.out.println(BORDER);

        if (!pendingHeap.isEmpty()) {
            Ticket top = pendingHeap.peekMin();
            System.out.println("  NEXT TO PROCESS: " + top.getTicketId()
                    + " [" + top.getPriorityLabel() + "] — " + top.getCategory());
            System.out.println(BORDER);
        }
    }

    //  PRIVATE HELPERS
    private void printTicketTableHeader() {
        System.out.printf("  %-8s %-10s %-14s %-16s %-12s %-10s %s%n",
                "ID", "Student", "Category", "Description", "Priority", "Level", "Status");
    }

    private String priorityLabel(int p) {
        return switch (p) {
            case 1 -> "CRITICAL";
            case 2 -> "HIGH";
            case 3 -> "MEDIUM";
            case 4 -> "LOW";
            default -> "UNKNOWN";
        };
    }

    /** Returns a simple date string for demo purposes. */
    private String getTodayDate() {
        return java.time.LocalDate.now().toString();
    }

    private String prompt(Scanner scanner, String label) {
        System.out.print("  " + label + ": ");
        return scanner.nextLine().trim();
    }

    private void printSuccess(String msg) { System.out.println("\n  ✔ " + msg); }
    private void printError(String msg)   { System.out.println("\n  ✘ ERROR: " + msg); }
}
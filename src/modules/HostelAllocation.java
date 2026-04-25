package modules;

import datastructures.CustomHashTable;
import datastructures.CustomQueue;
import models.Room;

import java.util.Scanner;

/**
 * HOSTEL ALLOCATION MODULE
 *
 * Manages campus room inventory and student room assignments.
 *
 * ─────────────────────────────────────────────────────────────
 *  Data Structures Used
 * ─────────────────────────────────────────────────────────────
 *  CustomHashTable<String, Room>
 *    — Stores all Room objects keyed by roomNumber (e.g. "A101")
 *    — O(1) average lookup, insert, delete
 *    — Used to: find a specific room, check if it is occupied,
 *               update occupancy, display all rooms
 *
 *  CustomQueue<String>  (waitingQueue)
 *    — Stores studentIds of students waiting for a room
 *    — FIFO — first to apply is first to receive a room
 *    — When a room becomes available, we dequeue the next student
 *    — O(1) enqueue and dequeue
 *
 *  CustomHashTable<String, String>  (studentRoomMap)
 *    — Maps studentId → roomNumber for quick "which room am I in?" lookup
 *    — O(1) lookup — used for student-facing allocation check
 *
 * ─────────────────────────────────────────────────────────────
 *  Public API
 * ─────────────────────────────────────────────────────────────
 *  Admin:
 *    addRoom(scanner)          — add a new room to inventory
 *    removeRoom(scanner)       — remove a room (must be vacant)
 *    allocateRoom(scanner)     — manually assign a room to a student
 *    deallocateRoom(scanner)   — free a room (auto-offers to next in queue)
 *    updateRoom(scanner)       — edit room details
 *    displayAllRooms()         — list every room with status
 *    displayAvailableRooms()   — list only vacant rooms
 *    displayOccupiedRooms()    — list only occupied rooms
 *    displayWaitingQueue()     — show the full waiting list
 *    displayStatistics()       — occupancy stats summary
 *
 *  Student:
 *    applyForHostel(scanner)   — join waiting queue or get a room
 *    checkMyAllocation(scanner)— see which room they are assigned to
 *    checkAvailability()       — see how many rooms are free
 *    cancelApplication(scanner)— remove themselves from the queue
 */
public class HostelAllocation {

    // ─────────────────────────────────────────────
    //  Storage
    // ─────────────────────────────────────────────
    private final CustomHashTable<String, Room>   rooms;          // roomNumber → Room
    private final CustomQueue<String>             waitingQueue;   // studentIds waiting
    private final CustomHashTable<String, String> studentRoomMap; // studentId → roomNumber

    private static final String BORDER      = "=".repeat(56);
    private static final String THIN_BORDER = "-".repeat(56);

    // ─────────────────────────────────────────────
    //  Constructor — seeds demo rooms
    // ─────────────────────────────────────────────
    public HostelAllocation() {
        rooms          = new CustomHashTable<>(32);
        waitingQueue   = new CustomQueue<>(20);
        studentRoomMap = new CustomHashTable<>(16);
        seedDemoData();
    }

    private void seedDemoData() {
        // ── Block A — Single rooms ──────────────────────────────
//        addRoomInternal(new Room("A101", "Block A", "Single", 1, 2500.00, "Wi-Fi, Desk, Wardrobe"));
//        addRoomInternal(new Room("A102", "Block A", "Single", 1, 2500.00, "Wi-Fi, Desk, Wardrobe"));
//        addRoomInternal(new Room("A103", "Block A", "Single", 1, 2500.00, "Wi-Fi, Desk, Wardrobe"));
//        addRoomInternal(new Room("A201", "Block A", "Single", 2, 2700.00, "Wi-Fi, Desk, Wardrobe, En-suite"));
//        addRoomInternal(new Room("A202", "Block A", "Single", 2, 2700.00, "Wi-Fi, Desk, Wardrobe, En-suite"));

        // ── Block B — Double rooms ──────────────────────────────
//        addRoomInternal(new Room("B101", "Block B", "Double", 1, 1800.00, "Wi-Fi, Shared Bathroom"));
//        addRoomInternal(new Room("B102", "Block B", "Double", 1, 1800.00, "Wi-Fi, Shared Bathroom"));
//        addRoomInternal(new Room("B201", "Block B", "Double", 2, 2000.00, "Wi-Fi, Air-Con, Shared Bathroom"));
//        addRoomInternal(new Room("B202", "Block B", "Double", 2, 2000.00, "Wi-Fi, Air-Con, Shared Bathroom"));

        // ── Block C — Triple rooms ──────────────────────────────
//        addRoomInternal(new Room("C101", "Block C", "Triple", 1, 1200.00, "Wi-Fi, Shared Bathroom, Common Room"));
//        addRoomInternal(new Room("C102", "Block C", "Triple", 1, 1200.00, "Wi-Fi, Shared Bathroom, Common Room"));
//        addRoomInternal(new Room("C201", "Block C", "Triple", 2, 1400.00, "Wi-Fi, Air-Con, Shared Bathroom"));

        // ── Pre-allocate some rooms to seed students ─────────────
//        allocateInternal("A101", "S001", "Thabo Nkosi");
//        allocateInternal("B101", "S002", "Lerato Dlamini");
//        allocateInternal("A201", "S003", "Sipho Mokoena");

        // ── Seed waiting queue ───────────────────────────────────
        waitingQueue.enqueue("S004");
        waitingQueue.enqueue("S005");
    }

    // ─────────────────────────────────────────────
    //  Internal helpers
    // ─────────────────────────────────────────────
    private void addRoomInternal(Room r) {
        rooms.put(r.getRoomNumber(), r);
    }

    private boolean allocateInternal(String roomNumber, String studentId, String studentName) {
        Room r = rooms.get(roomNumber);
        if (r == null || r.isOccupied()) return false;
        r.assign(studentId, studentName);
        studentRoomMap.put(studentId, roomNumber);
        return true;
    }

    // ═════════════════════════════════════════════
    //  ADD ROOM  (admin)
    // ═════════════════════════════════════════════
    public void addRoom(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADD NEW ROOM");
        System.out.println(THIN_BORDER);

        String number = prompt(scanner, "Room Number (e.g. D101)").toUpperCase();
        if (rooms.containsKey(number)) {
            printError("Room '" + number + "' already exists.");
            return;
        }

        String block  = prompt(scanner, "Block name (e.g. Block D)");
        String type   = readRoomType(scanner);
        int    floor  = readInt(scanner, "Floor number", 0, 10);
        double fee    = readDouble(scanner, "Monthly fee (R)", 500, 10000);
        String amen   = prompt(scanner, "Amenities (comma-separated, e.g. Wi-Fi, Desk)");

        Room r = new Room(number, block, type, floor, fee, amen);
        addRoomInternal(r);
        printSuccess("Room '" + number + "' added to inventory.");
        r.display();

        // If students are waiting, prompt admin to allocate immediately
        if (!waitingQueue.isEmpty()) {
            System.out.println("\n  ℹ  " + waitingQueue.size()
                    + " student(s) are waiting. Use 'Allocate Room' to assign this room.");
        }
    }

    // ═════════════════════════════════════════════
    //  REMOVE ROOM  (admin)
    // ═════════════════════════════════════════════
    public void removeRoom(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  REMOVE ROOM");
        System.out.println(THIN_BORDER);

        String number = prompt(scanner, "Enter Room Number to remove").toUpperCase();
        Room r = rooms.get(number);

        if (r == null)       { printError("Room '" + number + "' not found.");                  return; }
        if (r.isOccupied())  { printError("Room '" + number + "' is occupied. Deallocate first."); return; }

        r.display();
        String confirm = prompt(scanner, "Confirm removal? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            rooms.remove(number);
            printSuccess("Room '" + number + "' removed from inventory.");
        } else {
            System.out.println("  Removal cancelled.");
        }
    }

    // ═════════════════════════════════════════════
    //  ALLOCATE ROOM  (admin)
    // ═════════════════════════════════════════════
    public void allocateRoom(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ALLOCATE ROOM TO STUDENT");
        System.out.println(THIN_BORDER);

        // Show available rooms first
        displayAvailableRooms();

        if (countAvailableRooms() == 0) {
            printError("No rooms are currently available.");
            return;
        }

        System.out.println("\n  Allocate:");
        System.out.println("  1. Allocate to student from WAITING QUEUE (FIFO — recommended)");
        System.out.println("  2. Allocate to a specific student manually");
        System.out.println(THIN_BORDER);

        String choice = prompt(scanner, "Enter choice");

        if (choice.equals("1")) {
            allocateFromQueue(scanner);
        } else if (choice.equals("2")) {
            allocateManually(scanner);
        } else {
            printError("Invalid choice.");
        }
    }

    // Dequeue the next waiting student and assign a room
    private void allocateFromQueue(Scanner scanner) {
        if (waitingQueue.isEmpty()) {
            printError("Waiting queue is empty. No students are waiting.");
            return;
        }

        String nextStudentId = waitingQueue.peek();
        System.out.println("\n  Next student in queue: " + nextStudentId);

        String roomNumber = prompt(scanner, "Enter Room Number to assign").toUpperCase();
        Room r = rooms.get(roomNumber);

        if (r == null)      { printError("Room '" + roomNumber + "' not found.");      return; }
        if (r.isOccupied()) { printError("Room '" + roomNumber + "' is occupied.");    return; }

        String studentName = prompt(scanner, "Enter student's full name");

        // Dequeue the student and assign
        waitingQueue.dequeue();
        boolean success = allocateInternal(roomNumber, nextStudentId, studentName);

        if (success) {
            printSuccess("Room " + roomNumber + " allocated to " + nextStudentId
                    + " (" + studentName + ").");
            System.out.println("  Students still waiting: " + waitingQueue.size());
        } else {
            printError("Allocation failed. Please try again.");
            // Put student back at front — re-enqueue is not ideal but safe
        }
    }

    // Allocate to any specific student (bypasses queue — admin discretion)
    private void allocateManually(Scanner scanner) {
        String studentId   = prompt(scanner, "Enter Student ID").toUpperCase();
        String studentName = prompt(scanner, "Enter student's full name");
        String roomNumber  = prompt(scanner, "Enter Room Number to assign").toUpperCase();

        if (studentRoomMap.containsKey(studentId)) {
            String existing = studentRoomMap.get(studentId);
            printError("Student " + studentId + " is already allocated to room " + existing + ".");
            return;
        }

        Room r = rooms.get(roomNumber);
        if (r == null)      { printError("Room '" + roomNumber + "' not found.");   return; }
        if (r.isOccupied()) { printError("Room '" + roomNumber + "' is occupied."); return; }

        // Remove from waiting queue if they were in it
        if (waitingQueue.contains(studentId)) {
            System.out.println("  ℹ  " + studentId + " was in the waiting queue — removing them.");
            removeFromQueue(studentId);
        }

        boolean success = allocateInternal(roomNumber, studentId, studentName);
        if (success) {
            printSuccess("Room " + roomNumber + " manually allocated to "
                    + studentId + " (" + studentName + ").");
        } else {
            printError("Allocation failed.");
        }
    }

    // ═════════════════════════════════════════════
    //  DEALLOCATE ROOM  (admin)
    // ═════════════════════════════════════════════
    public void deallocateRoom(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  DEALLOCATE ROOM");
        System.out.println(THIN_BORDER);

        String number = prompt(scanner, "Enter Room Number to free up").toUpperCase();
        Room r = rooms.get(number);

        if (r == null)       { printError("Room '" + number + "' not found.");        return; }
        if (!r.isOccupied()) { printError("Room '" + number + "' is already vacant."); return; }

        r.display();
        String confirm = prompt(scanner, "Confirm deallocation? (yes/no)");
        if (!confirm.equalsIgnoreCase("yes")) { System.out.println("  Deallocation cancelled."); return; }

        String prevOccupant = r.getOccupantId();
        studentRoomMap.remove(prevOccupant);
        r.vacate();
        printSuccess("Room " + number + " is now vacant. Student " + prevOccupant + " has been removed.");

        // Auto-offer the freed room to the next student in the waiting queue
        if (!waitingQueue.isEmpty()) {
            System.out.println("\n  ℹ  Next student in waiting queue: " + waitingQueue.peek());
            String offer = prompt(scanner, "Assign this room to next student in queue? (yes/no)");
            if (offer.equalsIgnoreCase("yes")) {
                String nextId   = waitingQueue.dequeue();
                String nextName = prompt(scanner, "Enter " + nextId + "'s full name");
                allocateInternal(number, nextId, nextName);
                printSuccess("Room " + number + " assigned to " + nextId + " (" + nextName + ").");
            }
        }
    }

    // ═════════════════════════════════════════════
    //  UPDATE ROOM  (admin)
    // ═════════════════════════════════════════════
    public void updateRoom(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  UPDATE ROOM");
        System.out.println(THIN_BORDER);

        String number = prompt(scanner, "Enter Room Number to update").toUpperCase();
        Room r = rooms.get(number);
        if (r == null) { printError("Room '" + number + "' not found."); return; }

        r.display();
        System.out.println("\n  What would you like to update?");
        System.out.println("  1. Monthly Fee");
        System.out.println("  2. Amenities");
        System.out.println("  3. Room Type");
        System.out.println(THIN_BORDER);

        String choice = prompt(scanner, "Enter choice");
        switch (choice) {
            case "1" -> { r.setMonthlyFee(readDouble(scanner,"New monthly fee (R)",500,10000)); printSuccess("Fee updated."); }
            case "2" -> { r.setAmenities(prompt(scanner,"New amenities (comma-separated)")); printSuccess("Amenities updated."); }
            case "3" -> { r.setRoomType(readRoomType(scanner)); printSuccess("Room type updated."); }
            default  -> printError("Invalid choice.");
        }
    }

    // ═════════════════════════════════════════════
    //  DISPLAY ALL ROOMS  (admin)
    // ═════════════════════════════════════════════
    public void displayAllRooms() {
        Object[] allRooms = rooms.getAllValues();
        if (allRooms.length == 0) { printError("No rooms in inventory."); return; }

        System.out.println("\n" + BORDER);
        System.out.println("  ALL HOSTEL ROOMS  (" + allRooms.length + " total)");
        System.out.println(BORDER);
        printRoomTableHeader();

        for (Object obj : allRooms) {
            Room r = (Room) obj;
            System.out.println(r.toTableRow());
        }
        System.out.println(BORDER);
        System.out.println("  Available: " + countAvailableRooms()
                + "   Occupied: " + countOccupiedRooms()
                + "   Waiting:  " + waitingQueue.size());
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY AVAILABLE ROOMS  (both)
    // ═════════════════════════════════════════════
    public void displayAvailableRooms() {
        Object[] allRooms = rooms.getAllValues();
        int count = countAvailableRooms();

        System.out.println("\n" + BORDER);
        System.out.println("  AVAILABLE ROOMS  (" + count + " vacant)");
        System.out.println(BORDER);

        if (count == 0) {
            System.out.println("  No rooms currently available.");
            System.out.println("  Students in waiting queue: " + waitingQueue.size());
        } else {
            printRoomTableHeader();
            for (Object obj : allRooms) {
                Room r = (Room) obj;
                if (!r.isOccupied()) System.out.println(r.toTableRow());
            }
        }
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY OCCUPIED ROOMS  (admin)
    // ═════════════════════════════════════════════
    public void displayOccupiedRooms() {
        Object[] allRooms = rooms.getAllValues();
        int count = countOccupiedRooms();

        System.out.println("\n" + BORDER);
        System.out.println("  OCCUPIED ROOMS  (" + count + " occupied)");
        System.out.println(BORDER);

        if (count == 0) {
            System.out.println("  No rooms are currently occupied.");
        } else {
            printRoomTableHeader();
            for (Object obj : allRooms) {
                Room r = (Room) obj;
                if (r.isOccupied()) System.out.println(r.toTableRow());
            }
        }
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY WAITING QUEUE  (admin)
    // ═════════════════════════════════════════════
    public void displayWaitingQueue() {
        System.out.println("\n" + BORDER);
        System.out.println("  HOSTEL WAITING QUEUE");
        System.out.println("  Students are served in FIFO order (first applied = first allocated)");
        System.out.println(BORDER);

        if (waitingQueue.isEmpty()) {
            System.out.println("  Waiting queue is empty — no students pending allocation.");
        } else {
            Object[] waiting = waitingQueue.toArray();
            System.out.println("  Position  Student ID");
            System.out.println(THIN_BORDER);
            for (int i = 0; i < waiting.length; i++) {
                String marker = (i == 0) ? "  ← NEXT" : "";
                System.out.printf("  %-10d%s%s%n", (i + 1), waiting[i], marker);
            }
        }
        System.out.println(BORDER);
        System.out.println("  Total waiting: " + waitingQueue.size()
                + "   Available rooms: " + countAvailableRooms());
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY STATISTICS  (admin)
    // ═════════════════════════════════════════════
    public void displayStatistics() {
        Object[] allRooms = rooms.getAllValues();
        int total     = allRooms.length;
        int occupied  = countOccupiedRooms();
        int available = countAvailableRooms();
        int waiting   = waitingQueue.size();

        int singles = 0, doubles = 0, triples = 0;
        double totalRevenue = 0;

        for (Object obj : allRooms) {
            Room r = (Room) obj;
            if (r.isOccupied()) totalRevenue += r.getMonthlyFee();
            switch (r.getRoomType()) {
                case "Single" -> singles++;
                case "Double" -> doubles++;
                case "Triple" -> triples++;
            }
        }

        double occupancyRate = total > 0 ? (double) occupied / total * 100 : 0;

        System.out.println("\n" + BORDER);
        System.out.println("  HOSTEL ALLOCATION STATISTICS");
        System.out.println(THIN_BORDER);
        System.out.printf("  %-28s: %d%n",      "Total Rooms",       total);
        System.out.printf("  %-28s: %d%n",      "Occupied",          occupied);
        System.out.printf("  %-28s: %d%n",      "Available",         available);
        System.out.printf("  %-28s: %.1f%%%n",  "Occupancy Rate",    occupancyRate);
        System.out.printf("  %-28s: %d%n",      "Students Waiting",  waiting);
        System.out.println(THIN_BORDER);
        System.out.printf("  %-28s: %d%n",      "Single Rooms",      singles);
        System.out.printf("  %-28s: %d%n",      "Double Rooms",      doubles);
        System.out.printf("  %-28s: %d%n",      "Triple Rooms",      triples);
        System.out.println(THIN_BORDER);
        System.out.printf("  %-28s: R %.2f / month%n", "Monthly Revenue", totalRevenue);
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  APPLY FOR HOSTEL  (student)
    // ═════════════════════════════════════════════
    public void applyForHostel(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  APPLY FOR HOSTEL ROOM");
        System.out.println(THIN_BORDER);

        String studentId = prompt(scanner, "Enter your Student ID").toUpperCase();

        // Already allocated?
        if (studentRoomMap.containsKey(studentId)) {
            String roomNum = studentRoomMap.get(studentId);
            printError("You are already allocated to room " + roomNum + ".");
            rooms.get(roomNum).display();
            return;
        }

        // Already in queue?
        if (waitingQueue.contains(studentId)) {
            int pos = waitingQueue.positionOf(studentId);
            printError("You are already in the waiting queue at position " + pos + ".");
            return;
        }

        // Filter rooms by preference
        System.out.println("\n  Room type preference:");
        System.out.println("  1. Single  (R2500–R2700/month)");
        System.out.println("  2. Double  (R1800–R2000/month)");
        System.out.println("  3. Triple  (R1200–R1400/month)");
        System.out.println("  4. Any available");
        String typePref = prompt(scanner, "Enter preference");

        String preferredType = switch (typePref) {
            case "1" -> "Single";
            case "2" -> "Double";
            case "3" -> "Triple";
            default  -> "Any";
        };

        // Try to find a matching available room
        Room matchedRoom = findAvailableRoom(preferredType);

        if (matchedRoom != null) {
            System.out.println("\n  A matching room is available!");
            matchedRoom.display();
            String confirm = prompt(scanner, "Accept this room? (yes/no)");
            if (confirm.equalsIgnoreCase("yes")) {
                String name = prompt(scanner, "Enter your full name");
                allocateInternal(matchedRoom.getRoomNumber(), studentId, name);
                printSuccess("Room " + matchedRoom.getRoomNumber()
                        + " has been allocated to you (" + studentId + ").");
                printSuccess("Monthly fee: R" + String.format("%.2f", matchedRoom.getMonthlyFee()));
            } else {
                System.out.println("  Application cancelled.");
            }
        } else {
            // No room available — join waiting queue
            System.out.println("\n  No " + (preferredType.equals("Any") ? "" : preferredType + " ")
                    + "rooms are currently available.");
            String join = prompt(scanner, "Join the waiting queue? (yes/no)");
            if (join.equalsIgnoreCase("yes")) {
                waitingQueue.enqueue(studentId);
                int position = waitingQueue.positionOf(studentId);
                printSuccess("You have been added to the waiting queue.");
                System.out.println("  Your position: " + position + " of " + waitingQueue.size());
                System.out.println("  You will be notified when a room becomes available.");
            } else {
                System.out.println("  Application cancelled.");
            }
        }
    }

    // ═════════════════════════════════════════════
    //  CHECK MY ALLOCATION  (student)
    // ═════════════════════════════════════════════
    public void checkMyAllocation(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  MY HOSTEL ALLOCATION");
        System.out.println(THIN_BORDER);

        String studentId = prompt(scanner, "Enter your Student ID").toUpperCase();

        // Allocated to a room?
        if (studentRoomMap.containsKey(studentId)) {
            String roomNum = studentRoomMap.get(studentId);
            Room r = rooms.get(roomNum);
            if (r != null) {
                printSuccess("You are allocated to room " + roomNum + ".");
                r.display();
            }
            return;
        }

        // In the waiting queue?
        if (waitingQueue.contains(studentId)) {
            int pos   = waitingQueue.positionOf(studentId);
            int total = waitingQueue.size();
            printSuccess("You are on the waiting list.");
            System.out.println("  Your position : " + pos + " of " + total);
            System.out.println("  Available rooms: " + countAvailableRooms());
            System.out.println("  You will be allocated when a room becomes free.");
            return;
        }

        // Not found in either
        printError("No hostel application found for student ID " + studentId + ".");
        System.out.println("  Use 'Apply for Hostel' to submit an application.");
    }

    // ═════════════════════════════════════════════
    //  CHECK AVAILABILITY  (student)
    // ═════════════════════════════════════════════
    public void checkAvailability() {
        int available = countAvailableRooms();
        int waiting   = waitingQueue.size();

        System.out.println("\n" + BORDER);
        System.out.println("  HOSTEL AVAILABILITY");
        System.out.println(THIN_BORDER);
        System.out.printf("  %-28s: %d%n", "Available Rooms",  available);
        System.out.printf("  %-28s: %d%n", "Students Waiting", waiting);
        System.out.println(THIN_BORDER);

        if (available > 0) {
            System.out.println("  Rooms are available! Apply now.");
            displayAvailableRooms();
        } else {
            System.out.println("  No rooms available. Join the waiting queue to be");
            System.out.println("  automatically allocated when one opens up.");
        }
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  CANCEL APPLICATION  (student)
    // ═════════════════════════════════════════════
    public void cancelApplication(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  CANCEL HOSTEL APPLICATION");
        System.out.println(THIN_BORDER);

        String studentId = prompt(scanner, "Enter your Student ID").toUpperCase();

        if (!waitingQueue.contains(studentId)) {
            printError("Student " + studentId + " is not in the waiting queue.");
            return;
        }

        String confirm = prompt(scanner, "Confirm cancellation of your hostel application? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            removeFromQueue(studentId);
            printSuccess("Your hostel application has been cancelled.");
        } else {
            System.out.println("  Cancellation aborted.");
        }
    }

    // ═════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═════════════════════════════════════════════

    /** Counts rooms that are currently not occupied. */
    private int countAvailableRooms() {
        Object[] allRooms = rooms.getAllValues();
        int count = 0;
        for (Object obj : allRooms) {
            if (!((Room) obj).isOccupied()) count++;
        }
        return count;
    }

    /** Counts rooms that are currently occupied. */
    private int countOccupiedRooms() {
        return rooms.size() - countAvailableRooms();
    }

    /**
     * Finds the first available room matching the given type.
     * If type is "Any", returns the first available room found.
     */
    private Room findAvailableRoom(String type) {
        Object[] allRooms = rooms.getAllValues();
        for (Object obj : allRooms) {
            Room r = (Room) obj;
            if (!r.isOccupied()) {
                if (type.equals("Any") || r.getRoomType().equals(type)) return r;
            }
        }
        return null;
    }

    /**
     * Removes a specific studentId from the waiting queue.
     * Since CustomQueue has no direct remove-by-value, we drain it
     * and re-enqueue everything except the target student.
     * O(n) — acceptable since waiting queues are small.
     */
    private void removeFromQueue(String studentId) {
        Object[] all = waitingQueue.toArray();
        waitingQueue.clear();
        for (Object obj : all) {
            String id = (String) obj;
            if (!id.equals(studentId)) waitingQueue.enqueue(id);
        }
    }

    /** Prints the column header for the room listing table. */
    private void printRoomTableHeader() {
        System.out.printf("  %-6s %-10s %-8s %-6s %-12s %-10s %s%n",
                "Room", "Block", "Type", "Floor", "Fee/Month", "Status", "Occupant");
        System.out.println(THIN_BORDER);
    }

    /** Reads and validates a room type choice. */
    private String readRoomType(Scanner scanner) {
        while (true) {
            System.out.println("  Room Type:  1. Single   2. Double   3. Triple");
            String c = prompt(scanner, "Enter choice");
            switch (c) {
                case "1": return "Single";
                case "2": return "Double";
                case "3": return "Triple";
                default : printError("Please enter 1, 2, or 3.");
            }
        }
    }

    private String prompt(Scanner scanner, String label) {
        System.out.print("  " + label + ": ");
        return scanner.nextLine().trim();
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

    private double readDouble(Scanner scanner, String label, double min, double max) {
        while (true) {
            try {
                double v = Double.parseDouble(prompt(scanner, label));
                if (v >= min && v <= max) return v;
                printError("Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    private void printSuccess(String msg) { System.out.println("\n  ✔ " + msg); }
    private void printError(String msg)   { System.out.println("\n  ✘ ERROR: " + msg); }
}

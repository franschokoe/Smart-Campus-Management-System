
import modules.StudentRegistry;
import modules.CourseRegistration;
import modules.LibrarySystem;
import modules.HostelAllocation;
import modules.HelpDesk;
import modules.EventBooking;

import java.util.Scanner;

public class Menu {
    //  Constants variable
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String BORDER         = "=".repeat(50);
    private static final String THIN_BORDER    = "-".repeat(50);


    //  Shared scanner
    private static final Scanner scanner = new Scanner(System.in);

    //to be continiued

    private static final StudentRegistry   studentRegistry   = new StudentRegistry();
    private static final CourseRegistration courseRegistration = new CourseRegistration();
    private static final LibrarySystem     librarySystem     = new LibrarySystem();
    private static final HostelAllocation  hostelAllocation  = new HostelAllocation();
    private static final HelpDesk          helpDesk          = new HelpDesk();
    private static final EventBooking      eventBooking      = new EventBooking();

    public static void main(String[] args) {
        printWelcomeBanner();

        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> handleAdminLogin();
                case 2 -> handleStudentLogin();
                case 3 -> {
                    running = false;
                    printGoodbye();
                }
                default -> printError("Invalid option. Choose 1, 2, or 3.");
            }
        }

        scanner.close();
    }

    //welcome banner
    private static void printWelcomeBanner() {
        System.out.println("\n" + BORDER);
        System.out.println("      SMART CAMPUS MANAGEMENT SYSTEM");
        System.out.println("         University of Limpopo");
        System.out.println(BORDER);
        System.out.println("  Managing Students | Courses | Library");
        System.out.println("  Hostels | Help Desk | Events");
        System.out.println(BORDER + "\n");
    }
    //Bye
    private static void printGoodbye() {
        System.out.println("\n" + BORDER);
        System.out.println("  Thank you for using Smart Campus System.");
        System.out.println("  Goodbye!");
        System.out.println(BORDER + "\n");
    }

    //  MAIN MENU
    private static void printMainMenu() {
        System.out.println("\n" + BORDER);
        System.out.println("              MAIN MENU");
        System.out.println(THIN_BORDER);
        System.out.println("  1. Login as Administrator");
        System.out.println("  2. Login as Student");
        System.out.println("  3. Exit");
        System.out.println(BORDER);
    }

    //  ADMIN LOGIN & PASSWORD CHECKINGS
    private static void handleAdminLogin() {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADMINISTRATOR LOGIN");
        System.out.println(THIN_BORDER);
        System.out.print("  Enter admin password: ");
        String password = scanner.nextLine().trim();

        if (password.equals(ADMIN_PASSWORD)) {
            printSuccess("Access granted. Welcome Administrator!");
            //Call admin menu to run
            adminMenu();
        } else {
            printError("Incorrect password. Access denied.");
        }
    }

    //  ADMIN MENU
    private static void adminMenu() {

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n" + BORDER);
            System.out.println("           ADMINISTRATOR MENU");
            System.out.println(THIN_BORDER);
            System.out.println("  1. Student Records");
            System.out.println("  2. Course Registration");
            System.out.println("  3. Library Management");
            System.out.println("  4. Hostel Allocation");
            System.out.println("  5. Help Desk");
            System.out.println("  6. Event Bookings");
            System.out.println("  7. Logout");
            System.out.println(BORDER);

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> studentRecordsMenu(true);
                case 2 -> courseRegistrationMenu(true);
                case 3 -> libraryMenu(true);
                case 4 -> hostelMenu(true);
                case 5 -> helpDeskMenu(true);
                case 6 -> eventBookingMenu(true);
                case 7 -> {
                    loggedIn = false;
                    printSuccess("Logged out successfully.");
                }
                default -> printError("Invalid option. Choose between 1 and 7.");
            }
        }
    }

    //  STUDENT LOGIN
    private static void handleStudentLogin() {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  STUDENT LOGIN");
        System.out.println(THIN_BORDER);
        System.out.print("  Enter your Student Number: ");
        String studentId = scanner.nextLine().trim();

        if (studentRegistry.studentExists(studentId)) {
            printSuccess("Welcome, " + studentRegistry.getStudentName(studentId) + "!");
            studentMenu(studentId);
        } else {
            printError("Student ID '" + studentId + "' not found. Please contact admin.");
        }
    }

    //  STUDENT MENU
    private static void studentMenu(String studentId) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n" + BORDER);
            System.out.println("              STUDENT MENU");
            System.out.println("              Student No.: " + studentId);
            System.out.println(THIN_BORDER);
            System.out.println("  1. View My Profile");
            System.out.println("  2. Course Registration");
            System.out.println("  3. Library Services");
            System.out.println("  4. Hostel Information");
            System.out.println("  5. Submit Help Desk Ticket");
            System.out.println("  6. Event Bookings");
            System.out.println("  7. Logout");
            System.out.println(BORDER);

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> studentRegistry.displayStudent(studentId);
                case 2 -> courseRegistrationMenu(false);
                case 3 -> libraryMenu(false);
                case 4 -> hostelMenu(false);
                case 5 -> helpDeskMenu(false);
                case 6 -> eventBookingMenu(false);
                case 7 -> {
                    loggedIn = false;
                    printSuccess("Logged out successfully.");
                }
                default -> printError("Invalid option. Choose between 1 and 7.");
            }
        }
    }

    //  MODULE MENUS
    //  STUDENT RECORDS -- Admin only
    private static void studentRecordsMenu(boolean isAdmin) {
        boolean active = true;

        while (active) {
            System.out.println("\n" + BORDER);
            System.out.println("           STUDENT RECORDS");
            System.out.println(THIN_BORDER);

            if (isAdmin) {
                System.out.println("  1. Add Student");
                System.out.println("  2. Search Student");
                System.out.println("  3. Delete Student");
                System.out.println("  4. Display All Students");
                System.out.println("  5. Back");
            }

            System.out.println(BORDER);
            int choice = readInt("Enter choice: ");

            if (isAdmin) {
                switch (choice) {
                    case 1 -> studentRegistry.addStudent(scanner);
                    case 2 -> studentRegistry.searchStudent(scanner);
                    case 3 -> studentRegistry.deleteStudent(scanner);
                    case 4 -> studentRegistry.displayAllStudents();
                    case 5 -> active = false;
                    default -> printError("Invalid option.");
                }
            } else {
                active = false;
            }
        }
    }

    //  COURSE REGISTRATION
    private static void courseRegistrationMenu(boolean isAdmin) {
        boolean active = true;

        while (active) {
            System.out.println("\n" + BORDER);
            System.out.println("         COURSE REGISTRATION");
            System.out.println(THIN_BORDER);

            if (isAdmin) {
                System.out.println("  1. Add Course");
                System.out.println("  2. Remove Course");
                System.out.println("  3. Add Prerequisite Link");
                System.out.println("  4. Display All Courses");
                System.out.println("  5. Show Course Prerequisites");
                System.out.println("  6. Back");
            } else {
                System.out.println("  1. View Available Courses");
                System.out.println("  2. View Course Prerequisites");
                System.out.println("  3. Register for a Course");
                System.out.println("  4. Back");
            }

            System.out.println(BORDER);
            int choice = readInt("Enter choice: ");

            //Commented some CourseRgistration
            if (isAdmin) {

                switch (choice) {
                    case 1 -> courseRegistration.addCourse(scanner);
                    case 2 -> courseRegistration.removeCourse(scanner);
                    case 3 -> courseRegistration.addPrerequisite(scanner);
                    case 4 -> courseRegistration.displayAllCourses();
                    case 5 -> courseRegistration.showPrerequisites(scanner);
                    case 6 -> active = false;
                    default -> printError("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1 -> courseRegistration.displayAllCourses();
                    case 2 -> courseRegistration.showPrerequisites(scanner);
                    case 3 -> courseRegistration.registerForCourse(scanner);
                    case 4 -> active = false;
                    default -> printError("Invalid option.");
                }
            }
        }
    }

    //  LIBRARY MANAGEMENT
    private static void libraryMenu(boolean isAdmin) {
        boolean active = true;

        while (active) {
            System.out.println("\n" + BORDER);
            System.out.println("          LIBRARY MANAGEMENT");
            System.out.println(THIN_BORDER);

            if (isAdmin) {
                //Admin Adding a book
                System.out.println("  1. Add Book");
                System.out.println("  2. Remove Book");
                System.out.println("  3. Search Book");
                System.out.println("  4. Display All Books");
                System.out.println("  5. View Borrowed Books");
                System.out.println("  6. Back");
            } else {
                //Student seArching the book but not allowed to add or delete the book
                System.out.println("  1. Search Book");
                System.out.println("  2. Borrow Book");
                System.out.println("  3. Return Book");
                System.out.println("  4. Display All Books");
                System.out.println("  5. Back");
            }

            System.out.println(BORDER);
            int choice = readInt("Enter choice: ");

            if (isAdmin) {
                switch (choice) {
                    case 1 -> librarySystem.addBook(scanner);
                    case 2 -> librarySystem.removeBook(scanner);
                    case 3 -> librarySystem.searchBook(scanner);
                    case 4 -> librarySystem.displayAllBooks();
                    case 5 -> librarySystem.displayBorrowedBooks();
                    case 6 -> active = false;
                    default -> printError("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1 -> librarySystem.searchBook(scanner);
                    case 2 -> librarySystem.borrowBook(scanner);
                    case 3 -> librarySystem.returnBook(scanner);
                    case 4 -> librarySystem.displayAllBooks();
                    case 5 -> active = false;
                    default -> printError("Invalid option.");
                }
            }
        }
    }

    //  HOSTEL ALLOCATION
    private static void hostelMenu(boolean isAdmin) {
        boolean active = true;

        while (active) {
            System.out.println("\n" + BORDER);
            System.out.println("          HOSTEL ALLOCATION");
            System.out.println(THIN_BORDER);

            if (isAdmin) {
                //admin prevalegise
                System.out.println("  1. Add Room");
                System.out.println("  2. Allocate Room to Student");
                System.out.println("  3. Deallocate Room");
                System.out.println("  4. View All Rooms");
                System.out.println("  5. View Waiting Queue");
                System.out.println("  6. Back");
            } else {
                //Sudent access only
                System.out.println("  1. Check Room Availability");
                System.out.println("  2. Apply for Hostel");
                System.out.println("  3. Check My Allocation");
                System.out.println("  4. Back");
            }

            System.out.println(BORDER);
            int choice = readInt("Enter choice: ");

            if (isAdmin) {
                switch (choice) {
                    case 1 -> hostelAllocation.addRoom(scanner);
                    case 2 -> hostelAllocation.allocateRoom(scanner);
                    case 3 -> hostelAllocation.deallocateRoom(scanner);
                    case 4 -> hostelAllocation.displayAllRooms();
                    case 5 -> hostelAllocation.displayWaitingQueue();
                    case 6 -> active = false;
                    default -> printError("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1 -> hostelAllocation.checkAvailability();
                    case 2 -> hostelAllocation.applyForHostel(scanner);
                    case 3 -> hostelAllocation.checkMyAllocation(scanner);
                    case 4 -> active = false;
                    default -> printError("Invalid option.");
                }
            }
        }
    }

    //  HELP DESK
    private static void helpDeskMenu(boolean isAdmin) {
        boolean active = true;

        while (active) {
            System.out.println("\n" + BORDER);
            System.out.println("              HELP DESK");
            System.out.println(THIN_BORDER);

            if (isAdmin) {
                System.out.println("  1. Process Next Ticket");
                System.out.println("  2. View All Pending Tickets");
                System.out.println("  3. Resolve Ticket");
                System.out.println("  4. View Resolved Tickets");
                System.out.println("  5. Back");
            } else {
                System.out.println("  1. Submit a Ticket");
                System.out.println("  2. Check My Ticket Status");
                System.out.println("  3. Back");
            }

            System.out.println(BORDER);
            int choice = readInt("Enter choice: ");
            //Un commenting after implementing the helpDesk
            if (isAdmin) {
                switch (choice) {
                    case 1 -> helpDesk.processNextTicket();
                    case 2 -> helpDesk.displayPendingTickets();
                    case 3 -> helpDesk.resolveTicket(scanner);
                    case 4 -> helpDesk.displayResolvedTickets();
                    case 5 -> active = false;
                    default -> printError("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1 -> helpDesk.submitTicket(scanner);
                    case 2 -> helpDesk.checkTicketStatus(scanner);
                    case 3 -> active = false;
                    default -> printError("Invalid option.");
                }
            }
        }
    }

    //  EVENT BOOKINGS
    private static void eventBookingMenu(boolean isAdmin) {
        boolean active = true;

        while (active) {
            System.out.println("\n" + BORDER);
            System.out.println("           EVENT BOOKINGS");
            System.out.println(THIN_BORDER);

            if (isAdmin) {
                System.out.println("  1. Create Event");
                System.out.println("  2. Cancel Event");
                System.out.println("  3. View All Events");
                System.out.println("  4. View Event Attendees");
                System.out.println("  5. Back");
            } else {
                System.out.println("  1. View Upcoming Events");
                System.out.println("  2. Book an Event");
                System.out.println("  3. Cancel My Booking");
                System.out.println("  4. My Bookings");
                System.out.println("  5. Back");
            }

            System.out.println(BORDER);
            int choice = readInt("Enter choice: ");

            if (isAdmin) {
                switch (choice) {
                    case 1 -> eventBooking.createEvent(scanner);
                    case 2 -> eventBooking.cancelEvent(scanner);
                    case 3 -> eventBooking.displayAllEvents();
                    case 4 -> eventBooking.displayEventAttendees(scanner);
                    case 5 -> active = false;
                    default -> printError("Invalid option.");
                }
            } else {
                switch (choice) {
                    case 1 -> eventBooking.displayAllEvents();
                    case 2 -> eventBooking.bookEvent(scanner);
                    case 3 -> eventBooking.cancelMyBooking(scanner);
                    case 4 -> eventBooking.myBookings(scanner);
                    case 5 -> active = false;
                    default -> printError("Invalid option.");
                }
            }
        }
    }

    //  UTILITY METHODS
    /**
     * Safely reads an integer from the console.
     * Handles non-numeric input gracefully without crashing.
     */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            try {
                String line = scanner.nextLine().trim();
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    /** Prints a green-style success message. */
    private static void printSuccess(String message) {
        System.out.println("\n  Success: " + message);
    }

    /** Prints a red-style error message. */
    private static void printError(String message) {
        System.out.println("\n  Error: " + message);
    }
}
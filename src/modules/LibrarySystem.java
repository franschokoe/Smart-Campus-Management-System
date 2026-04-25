package modules;

import datastructures.CustomLinkedList;
import datastructures.CustomHashTable;
import models.Book;

import java.util.Scanner;

/**
 * LIBRARY SYSTEM MODULE
 *
 * Manages the campus library catalogue, borrowing, and returns.
 *
 * ─────────────────────────────────────────────────────────────
 *  Data Structures Used
 * ─────────────────────────────────────────────────────────────
 *
 *  CustomLinkedList<Book>  (catalogue)
 *    Stores every Book in insertion order.
 *    Used for full catalogue traversal, listing, and search.
 *    addLast()   O(1) — new books appended to the end.
 *    removeItem()O(n) — find and unlink a book node.
 *    Why LinkedList? The catalogue grows and shrinks dynamically.
 *    Books are added and removed without shifting — each removal
 *    is just a pointer update once the node is found.
 *
 *  CustomHashTable<String, Book>  (isbnIndex)
 *    Key   : isbn
 *    Value : Book object
 *    Why   : O(1) average lookup when a student borrows or
 *            returns a book by ISBN — faster than scanning
 *            the entire linked list every time.
 *
 *  CustomHashTable<String, String>  (borrowerIndex)
 *    Key   : studentId
 *    Value : isbn of the book they currently have
 *    Why   : O(1) lookup of "which book does student X have?"
 *            Used to validate returns and display borrow records.
 *
 * ─────────────────────────────────────────────────────────────
 *  Public API  (called by Main.java)
 * ─────────────────────────────────────────────────────────────
 *  Admin:
 *    addBook(scanner)          — add a new book to catalogue
 *    removeBook(scanner)       — remove a book permanently
 *    updateBook(scanner)       — edit book details
 *    displayAllBooks()         — list entire catalogue
 *    displayBorrowedBooks()    — list only borrowed books
 *    displayAvailableBooks()   — list only available books
 *    displayStatistics()       — catalogue summary
 *    markBookLost(scanner)     — mark a book as lost
 *
 *  Student:
 *    searchBook(scanner)       — search by ISBN, title, or author
 *    borrowBook(scanner)       — borrow a book
 *    returnBook(scanner)       — return a borrowed book
 *    displayAllBooks()         — browse full catalogue
 */
public class LibrarySystem {

    // ─────────────────────────────────────────────
    //  Storage
    // ─────────────────────────────────────────────
    private final CustomLinkedList<Book>          catalogue;
    private final CustomHashTable<String, Book>   isbnIndex;
    private final CustomHashTable<String, String> borrowerIndex; // studentId → isbn

    private static final String BORDER      = "=".repeat(60);
    private static final String THIN_BORDER = "-".repeat(60);

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public LibrarySystem() {
        catalogue     = new CustomLinkedList<>();
        isbnIndex     = new CustomHashTable<>(32);
        borrowerIndex = new CustomHashTable<>(32);
        seedDemoData();
    }

    // ─────────────────────────────────────────────
    //  Demo data
    // ─────────────────────────────────────────────
    private void seedDemoData() {
        addBookInternal(new Book("978-0-13-468599-1", "Clean Code",                        "Robert C. Martin",  "Computer Science", "Prentice Hall",    2008, 3));
        addBookInternal(new Book("978-0-13-235088-4", "The Pragmatic Programmer",          "David Thomas",      "Computer Science", "Addison-Wesley",   2019, 2));
        addBookInternal(new Book("978-0-596-51774-8", "JavaScript: The Good Parts",        "Douglas Crockford", "Computer Science", "O'Reilly Media",   2008, 2));
        addBookInternal(new Book("978-0-13-110362-7", "The C Programming Language",        "Brian Kernighan",   "Computer Science", "Prentice Hall",    1988, 2));
        addBookInternal(new Book("978-0-13-468599-2", "Introduction to Algorithms",        "Thomas Cormen",     "Computer Science", "MIT Press",        2009, 4));
        addBookInternal(new Book("978-0-13-149505-0", "Design Patterns",                   "Gang of Four",      "Computer Science", "Addison-Wesley",   1994, 2));
        addBookInternal(new Book("978-0-06-112008-4", "To Kill a Mockingbird",             "Harper Lee",        "Fiction",          "HarperCollins",    1960, 3));
        addBookInternal(new Book("978-0-7432-7356-5", "1984",                              "George Orwell",     "Fiction",          "Signet Classic",   1949, 3));
        addBookInternal(new Book("978-0-06-093546-9", "Thinking, Fast and Slow",           "Daniel Kahneman",   "Psychology",       "Farrar Straus",    2011, 2));
        addBookInternal(new Book("978-1-59327-584-6", "The Linux Command Line",            "William Shotts",    "Computer Science", "No Starch Press",  2019, 2));
        addBookInternal(new Book("978-0-13-235088-5", "Database System Concepts",          "Silberschatz",      "Computer Science", "McGraw-Hill",      2019, 3));
        addBookInternal(new Book("978-0-321-12521-7", "Domain-Driven Design",              "Eric Evans",        "Computer Science", "Addison-Wesley",   2003, 1));

        // Pre-borrow some books to demo the system
        borrowInternal("978-0-13-468599-1", "S001", "Thabo Nkosi",    "2025-08-01", "2025-08-15");
        borrowInternal("978-0-13-235088-4", "S002", "Lerato Dlamini", "2025-08-03", "2025-08-17");
        borrowInternal("978-0-596-51774-8", "S003", "Sipho Mokoena",  "2025-08-05", "2025-08-19");
    }

    /** Internal — adds a book to both the linked list and ISBN index. */
    private void addBookInternal(Book book) {
        catalogue.addLast(book);
        isbnIndex.put(book.getIsbn(), book);
    }

    /** Internal — borrows a book and updates the borrower index. */
    private boolean borrowInternal(String isbn, String studentId,
                                   String studentName, String dateBorrowed, String dateDue) {
        Book book = isbnIndex.get(isbn);
        if (book == null || !book.isAvailable()) return false;
        book.borrow(studentId, studentName, dateBorrowed, dateDue);
        borrowerIndex.put(studentId, isbn);
        return true;
    }

    // ═════════════════════════════════════════════
    //  ADD BOOK  (admin)
    // ═════════════════════════════════════════════
    public void addBook(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADD NEW BOOK");
        System.out.println(THIN_BORDER);

        String isbn = prompt(scanner, "ISBN");
        if (isbnIndex.containsKey(isbn)) {
            printError("A book with ISBN '" + isbn + "' already exists.");
            return;
        }

        String title     = prompt(scanner, "Title");
        String author    = prompt(scanner, "Author");
        String genre     = promptGenre(scanner);
        String publisher = prompt(scanner, "Publisher");
        int    year      = readInt(scanner, "Year Published", 1000, 2025);
        int    copies    = readInt(scanner, "Number of Copies", 1, 50);

        Book book = new Book(isbn, title, author, genre, publisher, year, copies);
        addBookInternal(book);

        printSuccess("Book '" + title + "' added to the catalogue.");
        book.display();
    }

    // ═════════════════════════════════════════════
    //  REMOVE BOOK  (admin)
    // ═════════════════════════════════════════════
    public void removeBook(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  REMOVE BOOK");
        System.out.println(THIN_BORDER);

        String isbn = prompt(scanner, "Enter ISBN of book to remove");
        Book   book = isbnIndex.get(isbn);

        if (book == null) { printError("No book found with ISBN '" + isbn + "'."); return; }

        book.display();

        if (book.isBorrowed()) {
            printError("Cannot remove a book that is currently borrowed.");
            System.out.println("  Wait for it to be returned first.");
            return;
        }

        String confirm = prompt(scanner, "Confirm removal? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            catalogue.removeItem(book);
            isbnIndex.remove(isbn);
            printSuccess("Book '" + book.getTitle() + "' removed from the catalogue.");
        } else {
            System.out.println("  Removal cancelled.");
        }
    }

    // ═════════════════════════════════════════════
    //  UPDATE BOOK  (admin)
    // ═════════════════════════════════════════════
    public void updateBook(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  UPDATE BOOK DETAILS");
        System.out.println(THIN_BORDER);

        String isbn = prompt(scanner, "Enter ISBN of book to update");
        Book   book = isbnIndex.get(isbn);
        if (book == null) { printError("No book found with ISBN '" + isbn + "'."); return; }

        book.display();
        System.out.println("  What would you like to update?");
        System.out.println("  1. Title");
        System.out.println("  2. Author");
        System.out.println("  3. Genre");
        System.out.println("  4. Publisher");
        System.out.println("  5. Year Published");
        System.out.println("  6. Total Copies");
        System.out.println(THIN_BORDER);

        switch (prompt(scanner, "Enter choice")) {
            case "1" -> { book.setTitle(prompt(scanner, "New title"));               printSuccess("Title updated.");         }
            case "2" -> { book.setAuthor(prompt(scanner, "New author"));             printSuccess("Author updated.");        }
            case "3" -> { book.setGenre(promptGenre(scanner));                       printSuccess("Genre updated.");         }
            case "4" -> { book.setPublisher(prompt(scanner, "New publisher"));       printSuccess("Publisher updated.");     }
            case "5" -> { book.setYearPublished(readInt(scanner,"New year",1000,2025)); printSuccess("Year updated.");      }
            case "6" -> { book.setTotalCopies(readInt(scanner,"New total copies",1,50)); printSuccess("Copies updated.");   }
            default  ->   printError("Invalid choice.");
        }
    }

    // ═════════════════════════════════════════════
    //  SEARCH BOOK  (both roles)
    // ═════════════════════════════════════════════
    public void searchBook(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  SEARCH BOOK");
        System.out.println(THIN_BORDER);
        System.out.println("  1. Search by ISBN");
        System.out.println("  2. Search by Title");
        System.out.println("  3. Search by Author");
        System.out.println("  4. Search by Genre");
        System.out.println(THIN_BORDER);

        switch (prompt(scanner, "Enter choice")) {
            case "1" -> searchByIsbn(scanner);
            case "2" -> searchByKeyword(scanner, "title");
            case "3" -> searchByKeyword(scanner, "author");
            case "4" -> searchByKeyword(scanner, "genre");
            default  -> printError("Invalid choice.");
        }
    }

    private void searchByIsbn(Scanner scanner) {
        String isbn = prompt(scanner, "Enter ISBN");
        Book   book = isbnIndex.get(isbn);
        if (book == null) printError("No book found with ISBN '" + isbn + "'.");
        else              book.display();
    }

    private void searchByKeyword(Scanner scanner, String field) {
        String query = prompt(scanner, "Enter " + field + " keyword").toLowerCase();

        System.out.println("\n" + THIN_BORDER);
        System.out.printf("  Search results for %s: \"%s\"%n", field, query);
        System.out.println(THIN_BORDER);
        printBookTableHeader();
        System.out.println(THIN_BORDER);

        // Traverse the linked list from head to tail
        CustomLinkedList.Node<Book> current = catalogue.getHead();
        boolean found = false;

        while (current != null) {
            Book book = current.data;
            boolean match = switch (field) {
                case "title"  -> book.getTitle().toLowerCase().contains(query);
                case "author" -> book.getAuthor().toLowerCase().contains(query);
                case "genre"  -> book.getGenre().toLowerCase().contains(query);
                default       -> false;
            };
            if (match) {
                System.out.println(book.toTableRow());
                found = true;
            }
            current = current.next;
        }

        if (!found) printError("No books found matching '" + query + "'.");
        else        System.out.println(THIN_BORDER);
    }

    // ═════════════════════════════════════════════
    //  BORROW BOOK  (student)
    // ═════════════════════════════════════════════
    public void borrowBook(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  BORROW A BOOK");
        System.out.println(THIN_BORDER);

        String studentId   = prompt(scanner, "Your Student ID").toUpperCase();
        String studentName = prompt(scanner, "Your Full Name");

        // Check if student already has a book borrowed
        if (borrowerIndex.containsKey(studentId)) {
            String existingIsbn = borrowerIndex.get(studentId);
            Book   existing     = isbnIndex.get(existingIsbn);
            printError("You already have a borrowed book:");
            if (existing != null) {
                System.out.println("  ISBN  : " + existingIsbn);
                System.out.println("  Title : " + existing.getTitle());
                System.out.println("  Due   : " + existing.getDateDue());
            }
            System.out.println("  Please return it before borrowing another.");
            return;
        }

        String isbn = prompt(scanner, "Enter ISBN of book to borrow");
        Book   book = isbnIndex.get(isbn);

        if (book == null)         { printError("No book found with ISBN '" + isbn + "'."); return; }
        if (!book.isAvailable())  { printError("Sorry, '" + book.getTitle() + "' has no available copies."); return; }

        book.display();

        String dateBorrowed = getTodayDate();
        String dateDue      = getDueDate();

        String confirm = prompt(scanner, "Confirm borrow? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            borrowInternal(isbn, studentId, studentName, dateBorrowed, dateDue);
            printSuccess("You have borrowed: " + book.getTitle());
            System.out.println("  Borrowed on : " + dateBorrowed);
            System.out.println("  Due date    : " + dateDue);
            System.out.println("  Please return the book by the due date.");
        } else {
            System.out.println("  Borrow cancelled.");
        }
    }

    // ═════════════════════════════════════════════
    //  RETURN BOOK  (student)
    // ═════════════════════════════════════════════
    public void returnBook(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  RETURN A BOOK");
        System.out.println(THIN_BORDER);

        String studentId = prompt(scanner, "Your Student ID").toUpperCase();

        if (!borrowerIndex.containsKey(studentId)) {
            printError("No borrow record found for Student ID '" + studentId + "'.");
            System.out.println("  You have no books currently borrowed.");
            return;
        }

        String isbn = borrowerIndex.get(studentId);
        Book   book = isbnIndex.get(isbn);

        if (book == null) { printError("Book record not found. Contact library staff."); return; }

        book.display();

        String confirm = prompt(scanner, "Confirm return of '" + book.getTitle() + "'? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            book.returnBook();
            borrowerIndex.remove(studentId);
            printSuccess("'" + book.getTitle() + "' has been returned successfully.");
            System.out.println("  Thank you! Available copies: " + book.getAvailableCopies());
        } else {
            System.out.println("  Return cancelled.");
        }
    }

    // ═════════════════════════════════════════════
    //  DISPLAY ALL BOOKS  (both roles)
    // ═════════════════════════════════════════════
    public void displayAllBooks() {
        System.out.println("\n" + BORDER);
        System.out.println("  LIBRARY CATALOGUE  (" + catalogue.size() + " books)");
        System.out.println(BORDER);

        if (catalogue.isEmpty()) {
            System.out.println("  The catalogue is empty.");
            System.out.println(BORDER);
            return;
        }

        printBookTableHeader();
        System.out.println(THIN_BORDER);

        // Walk the linked list from head to tail
        CustomLinkedList.Node<Book> current = catalogue.getHead();
        while (current != null) {
            System.out.println(current.data.toTableRow());
            current = current.next;
        }

        System.out.println(BORDER);
        System.out.println("  Total books in catalogue: " + catalogue.size());
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY BORROWED BOOKS  (admin)
    // ═════════════════════════════════════════════
    public void displayBorrowedBooks() {
        System.out.println("\n" + BORDER);
        System.out.println("  CURRENTLY BORROWED BOOKS");
        System.out.println(BORDER);

        int count = 0;
        System.out.printf("  %-20s %-30s %-12s %-12s %s%n",
                "ISBN", "Title", "Borrowed By", "Date Borrowed", "Due Date");
        System.out.println(THIN_BORDER);

        // Traverse the linked list looking for borrowed books
        CustomLinkedList.Node<Book> current = catalogue.getHead();
        while (current != null) {
            Book book = current.data;
            if (book.getBorrowedBy() != null) {
                System.out.printf("  %-20s %-30s %-12s %-12s %s%n",
                        book.getIsbn(),
                        truncate(book.getTitle(), 28),
                        book.getBorrowedBy(),
                        book.getDateBorrowed(),
                        book.getDateDue());
                count++;
            }
            current = current.next;
        }

        if (count == 0) System.out.println("  No books are currently borrowed.");
        System.out.println(BORDER);
        System.out.println("  Total borrowed: " + count);
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  DISPLAY AVAILABLE BOOKS  (both)
    // ═════════════════════════════════════════════
    public void displayAvailableBooks() {
        System.out.println("\n" + BORDER);
        System.out.println("  AVAILABLE BOOKS");
        System.out.println(BORDER);

        printBookTableHeader();
        System.out.println(THIN_BORDER);

        int count = 0;
        CustomLinkedList.Node<Book> current = catalogue.getHead();
        while (current != null) {
            Book book = current.data;
            if (book.isAvailable()) {
                System.out.println(book.toTableRow());
                count++;
            }
            current = current.next;
        }

        if (count == 0) System.out.println("  No books currently available.");
        System.out.println(BORDER);
        System.out.println("  Available: " + count + " of " + catalogue.size());
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  MARK BOOK AS LOST  (admin)
    // ═════════════════════════════════════════════
    public void markBookLost(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  MARK BOOK AS LOST");
        System.out.println(THIN_BORDER);

        String isbn = prompt(scanner, "Enter ISBN of lost book");
        Book   book = isbnIndex.get(isbn);

        if (book == null) { printError("No book found with ISBN '" + isbn + "'."); return; }

        book.display();
        String confirm = prompt(scanner, "Confirm marking as lost? (yes/no)");

        if (confirm.equalsIgnoreCase("yes")) {
            String studentId = book.getBorrowedBy();
            book.markAsLost();

            // Remove from borrower index if it was on loan
            if (studentId != null) borrowerIndex.remove(studentId);

            // Remove from catalogue entirely if no copies remain
            if (book.getTotalCopies() == 0) {
                catalogue.removeItem(book);
                isbnIndex.remove(isbn);
                printSuccess("All copies lost. Book removed from the catalogue.");
            } else {
                printSuccess("Book marked as lost. Remaining copies: " + book.getTotalCopies());
            }
        } else {
            System.out.println("  Cancelled.");
        }
    }

    // ═════════════════════════════════════════════
    //  STATISTICS  (admin)
    // ═════════════════════════════════════════════
    public void displayStatistics() {
        System.out.println("\n" + BORDER);
        System.out.println("  LIBRARY STATISTICS");
        System.out.println(BORDER);

        int totalBooks = catalogue.size();
        int available = 0, borrowed = 0, lost = 0;
        int csBooks = 0, fiction = 0, psych = 0, other = 0;
        int totalCopies = 0, totalAvailCopies = 0;

        CustomLinkedList.Node<Book> current = catalogue.getHead();
        while (current != null) {
            Book book = current.data;
            totalCopies      += book.getTotalCopies();
            totalAvailCopies += book.getAvailableCopies();

            if (book.isAvailable())       available++;
            else if (book.isBorrowed())   borrowed++;
            else if (book.isLost())       lost++;

            switch (book.getGenre()) {
                case "Computer Science" -> csBooks++;
                case "Fiction"          -> fiction++;
                case "Psychology"       -> psych++;
                default                 -> other++;
            }
            current = current.next;
        }

        System.out.println("  CATALOGUE:");
        System.out.printf("  %-30s: %d%n", "Total Unique Titles",  totalBooks);
        System.out.printf("  %-30s: %d%n", "Total Physical Copies", totalCopies);
        System.out.printf("  %-30s: %d%n", "Available Copies",      totalAvailCopies);
        System.out.printf("  %-30s: %d%n", "Copies On Loan",        totalCopies - totalAvailCopies);
        System.out.println(THIN_BORDER);
        System.out.println("  BY STATUS:");
        System.out.printf("  %-30s: %d%n", "Available Titles",  available);
        System.out.printf("  %-30s: %d%n", "Fully Borrowed",    borrowed);
        System.out.printf("  %-30s: %d%n", "Lost",              lost);
        System.out.println(THIN_BORDER);
        System.out.println("  BY GENRE:");
        System.out.printf("  %-30s: %d%n", "Computer Science",  csBooks);
        System.out.printf("  %-30s: %d%n", "Fiction",           fiction);
        System.out.printf("  %-30s: %d%n", "Psychology",        psych);
        System.out.printf("  %-30s: %d%n", "Other",             other);
        System.out.println(BORDER);
    }

    // ═════════════════════════════════════════════
    //  PRIVATE HELPERS
    // ═════════════════════════════════════════════

    private void printBookTableHeader() {
        System.out.printf("  %-20s %-30s %-22s %-14s %s%n",
                "ISBN", "Title", "Author", "Genre", "Copies  Status");
    }

    private String promptGenre(Scanner scanner) {
        System.out.println("  Genre:");
        System.out.println("  1. Computer Science   2. Fiction   3. Psychology");
        System.out.println("  4. Mathematics        5. Engineering   6. Other");
        return switch (prompt(scanner, "Enter choice")) {
            case "1" -> "Computer Science";
            case "2" -> "Fiction";
            case "3" -> "Psychology";
            case "4" -> "Mathematics";
            case "5" -> "Engineering";
            default  -> "Other";
        };
    }

    private String getTodayDate() {
        return java.time.LocalDate.now().toString();
    }

    private String getDueDate() {
        return java.time.LocalDate.now().plusDays(14).toString();
    }

    private String truncate(String s, int max) {
        if (s == null || s.length() <= max) return s;
        return s.substring(0, max - 3) + "...";
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
package modules;

import datastructures.CustomHashTable;
import models.Student;

import java.util.Scanner;

/**
 * STUDENT REGISTRY MODULE
 *
 * Manages all student data using a CustomHashTable.
 *
 * Data structure choice — Hash Table:
 *   Students are looked up constantly by their student ID.
 *   A hash table gives O(1) average time for insert, lookup,
 *   and delete — far better than a list's O(n) search.
 *
 * Key  : studentId  (String)
 * Value: Student    (object)
 *
 * Public API (called by Main.java):
 *   addStudent(scanner)         — admin: register a new student
 *   searchStudent(scanner)      — admin: find by ID or name
 *   deleteStudent(scanner)      — admin: remove a student
 *   updateStudent(scanner)      — admin: edit student details
 *   displayAllStudents()        — admin: list every student
 *   displayStudent(studentId)   — student: view own profile
 *   studentExists(studentId)    — Main: validate login
 *   getStudentName(studentId)   — Main: fetch name for greeting
 */
public class StudentRegistry {

    //  Storage — custom hash table
    private final CustomHashTable<String, Student> table;

    private static final String BORDER      = "=".repeat(50);
    private static final String THIN_BORDER = "-".repeat(50);
    //  Constructor — seeds a few demo
    public StudentRegistry() {
        table = new CustomHashTable<>(16);
        seedDemoData();
    }
    // HARD CODED RECORDS OF STUDENTS
    // TO BE REMOVED OR COMMENTED ILL HEAR YOU GROUPMATES
    private void seedDemoData() {
        table.put("S001", new Student("S001", "Thabo",   "Nkosi",    "thabo@campus.ac.za",   "0712345678", "BSc Computer Science",  2, 3.5, "Active"));
        table.put("S002", new Student("S002", "Lerato",  "Dlamini",  "lerato@campus.ac.za",  "0823456789", "BCom Accounting",       1, 3.8, "Active"));
        table.put("S003", new Student("S003", "Sipho",   "Mokoena",  "sipho@campus.ac.za",   "0634567890", "BEng Civil",            3, 3.2, "Active"));
        table.put("S004", new Student("S004", "Nomsa",   "Zulu",     "nomsa@campus.ac.za",   "0745678901", "BA Psychology",         4, 2.9, "Active"));
        table.put("S005", new Student("S005", "Kagiso",  "Sithole",  "kagiso@campus.ac.za",  "0856789012", "BSc Computer Science",  1, 3.6, "Active"));
    }
    //  ADD STUDENT
    public void addStudent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADD NEW STUDENT");
        System.out.println(THIN_BORDER);

        String id = prompt(scanner, "Student Number: ").toUpperCase();

        if (table.containsKey(id)) {
            printError("Student Number '" + id + "' already exists.");
            return;
        }

        String firstName = prompt(scanner, "First Name");
        String lastName  = prompt(scanner, "Last Name");
        String email     = prompt(scanner, "Email Address");
        String phone     = prompt(scanner, "Phone Number");
        String programme = prompt(scanner, "Programme (e.g. BSc Computer Science)");

        int year = 0;
        while (year < 1 || year > 4) {
            try {
                year = Integer.parseInt(prompt(scanner, "Year of Study (1-4)"));
                if (year < 1 || year > 4) printError("Year must be between 1 and 4.");
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }

        double gpa = -1;
        while (gpa < 0.0 || gpa > 4.0) {
            try {
                gpa = Double.parseDouble(prompt(scanner, "Average (0.0 - 4.0)"));
                if (gpa < 0.0 || gpa > 4.0) printError("Average must be between 0.0 and 4.0.");
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }

        Student student = new Student(id, firstName, lastName, email,
                                      phone, programme, year, gpa, "Active");
        table.put(id, student);

        printSuccess("Student '" + student.getFullName() + "' registered with Student number: " + id);
        student.display();
    }

    //  SEARCH STUDENT
    public void searchStudent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  SEARCH STUDENT");
        System.out.println(THIN_BORDER);
        System.out.println("  1. Search by Student Number");
        System.out.println("  2. Search by Name");
        System.out.println(THIN_BORDER);

        String choice = prompt(scanner, "Enter choice");

        switch (choice.trim()) {
            case "1" -> searchById(scanner);
            case "2" -> searchByName(scanner);
            default  -> printError("Invalid choice.");
        }
    }

    private void searchById(Scanner scanner) {
        String id = prompt(scanner, "Enter Student Number").toUpperCase();
        Student s = table.get(id);

        if (s == null) {
            printError("No student found with student number '" + id + "'.");
        } else {
            s.display();
        }
    }

    private void searchByName(Scanner scanner) {
        String query = prompt(scanner, "Enter name (or part of name)").toLowerCase();

        Object[] values = table.getAllValues();
        boolean  found  = false;

        System.out.println("\n" + THIN_BORDER);
        System.out.printf("  %-10s %-22s %-30s %-8s %s%n",
                "Student No.", "Full Name", "Email", "Status", "Year");
        System.out.println(THIN_BORDER);

        for (Object obj : values) {
            Student s = (Student) obj;
            if (s.getFullName().toLowerCase().contains(query) ||
                s.getFirstName().toLowerCase().contains(query) ||
                s.getLastName().toLowerCase().contains(query)) {
                System.out.println(s.toTableRow());
                found = true;
            }
        }

        if (!found) {
            printError("No students found matching '" + query + "'.");
        } else {
            System.out.println(THIN_BORDER);
        }
    }
    //  DELETE STUDENT
    public void deleteStudent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  DELETE STUDENT");
        System.out.println(THIN_BORDER);

        String id = prompt(scanner, "Enter Student Number to delete").toUpperCase();
        Student s = table.get(id);

        if (s == null) {
            printError("Student No. '" + id + "' not found.");
            return;
        }

        s.display();
        String confirm = prompt(scanner, "Confirm delete? This cannot be undone. (yes/no)").toLowerCase();

        if (confirm.equalsIgnoreCase("yes")) {
            table.remove(id);
            printSuccess("Student '" + s.getFullName() + "' (" + id + ") has been removed.");
        } else {
            System.out.println("  Deletion cancelled.");
        }
    }

    //  UPDATE STUDENT
    public void updateStudent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  UPDATE STUDENT");
        System.out.println(THIN_BORDER);

        String id = prompt(scanner, "Enter Student Number to update").toUpperCase();
        Student s = table.get(id);

        if (s == null) {
            printError("Student Number '" + id + "' not found.");
            return;
        }

        s.display();

        System.out.println("\n  What would you like to update?");
        System.out.println("  1. Email");
        System.out.println("  2. Phone Number");
        System.out.println("  3. Programme");
        System.out.println("  4. Year of Study");
        System.out.println("  5. Average");
        System.out.println("  6. Status (Active / Suspended / Graduated)");
        System.out.println(THIN_BORDER);

        String choice = prompt(scanner, "Enter choice");

        switch (choice.trim()) {
            case "1" -> {
                s.setEmail(prompt(scanner, "New email"));
                printSuccess("Email updated.");
            }
            case "2" -> {
                s.setPhoneNumber(prompt(scanner, "New phone number"));
                printSuccess("Phone number updated.");
            }
            case "3" -> {
                s.setProgramme(prompt(scanner, "New programme"));
                printSuccess("Programme updated.");
            }
            case "4" -> {
                try {
                    int year = Integer.parseInt(prompt(scanner, "New year of study (1-4)"));
                    if (year >= 1 && year <= 4) {
                        s.setYearOfStudy(year);
                        printSuccess("Year of study updated.");
                    } else {
                        printError("Year must be between 1 and 4.");
                    }
                } catch (NumberFormatException e) {
                    printError("Invalid number entered.");
                }
            }
            case "5" -> {
                try {
                    double gpa = Double.parseDouble(prompt(scanner, "New Average (0.0 - 4.0)"));
                    if (gpa >= 0.0 && gpa <= 4.0) {
                        s.setGpa(gpa);
                        printSuccess("Average updated.");
                    } else {
                        printError("Average must be between 0.0 and 4.0.");
                    }
                } catch (NumberFormatException e) {
                    printError("Invalid number entered.");
                }
            }
            case "6" -> {
                String status = prompt(scanner, "New status (Active / Suspended / Graduated)");
                if (status.equals("Active") || status.equals("Suspended") || status.equals("Graduated")) {
                    s.setStatus(status);
                    printSuccess("Status updated.");
                } else {
                    printError("Status must be 'Active', 'Suspended', or 'Graduated'.");
                }
            }
            default -> printError("Invalid choice.");
        }
    }

    //  DISPLAY ALL STUDENTS
    public void displayAllStudents() {
        if (table.isEmpty()) {
            printError("No students registered yet.");
            return;
        }

        Object[] values = table.getAllValues();

        System.out.println("\n" + BORDER);
        System.out.println("  ALL REGISTERED STUDENTS  (" + table.size() + " total)");
        System.out.println(BORDER);
        System.out.printf("  %-10s %-22s %-30s %-12s %s%n",
                "Student No.", "Full Name", "Email", "Status", "Year");
        System.out.println(THIN_BORDER);

        for (Object obj : values) {
            Student s = (Student) obj;
            System.out.println(s.toTableRow());
        }

        System.out.println(BORDER);
        System.out.println("  Total students: " + table.size());
        System.out.println(BORDER);
    }

    //  DISPLAY SINGLE STUDENT (student-facing)

    public void displayStudent(String studentId) {
        Student s = table.get(studentId);
        if (s != null) {
            s.display();
        } else {
            printError("Student profile not found.");
        }
    }

    //  STATISTICS (admin)
    public void displayStatistics() {
        if (table.isEmpty()) {
            printError("No student data available.");
            return;
        }

        Object[] values  = table.getAllValues();
        int    active    = 0, suspended = 0, graduated = 0;
        double totalGpa  = 0;
        int    year1 = 0, year2 = 0, year3 = 0, year4 = 0;

        for (Object obj : values) {
            Student s = (Student) obj;
            totalGpa += s.getGpa();

            switch (s.getStatus()) {
                case "Active"     -> active++;
                case "Suspended"  -> suspended++;
                case "Graduated"  -> graduated++;
            }
            switch (s.getYearOfStudy()) {
                case 1 -> year1++;
                case 2 -> year2++;
                case 3 -> year3++;
                case 4 -> year4++;
            }
        }

        double avgGpa = totalGpa / table.size();

        System.out.println("\n" + BORDER);
        System.out.println("  STUDENT STATISTICS");
        System.out.println(THIN_BORDER);
        System.out.printf("  %-25s: %d%n",  "Total Students",    table.size());
        System.out.printf("  %-25s: %d%n",  "Active",            active);
        System.out.printf("  %-25s: %d%n",  "Suspended",         suspended);
        System.out.printf("  %-25s: %d%n",  "Graduated",         graduated);
        System.out.println(THIN_BORDER);
        System.out.printf("  %-25s: %.2f%n","Average GPA",       avgGpa);
        System.out.println(THIN_BORDER);
        System.out.printf("  %-25s: %d%n",  "Year 1",            year1);
        System.out.printf("  %-25s: %d%n",  "Year 2",            year2);
        System.out.printf("  %-25s: %d%n",  "Year 3",            year3);
        System.out.printf("  %-25s: %d%n",  "Year 4",            year4);
        System.out.println(BORDER);
    }

    //  HELPERS USED BY Main.java

    /** Returns true if a student with this ID exists — used for login validation. */
    public boolean studentExists(String studentId) {
        return table.containsKey(studentId.toUpperCase());
    }

    /** Returns the full name of the student — used for welcome message. */
    public String getStudentName(String studentId) {
        Student s = table.get(studentId.toUpperCase());
        return (s != null) ? s.getFullName() : "Unknown";
    }

    //  Private utility methods
    private String prompt(Scanner scanner, String label) {
        System.out.print("  " + label + ": ");
        return scanner.nextLine().trim();
    }

    private void printSuccess(String msg) {
        System.out.println("\n  ✔ " + msg);
    }

    private void printError(String msg) {
        System.out.println("\n  ✘ ERROR: " + msg);
    }
}

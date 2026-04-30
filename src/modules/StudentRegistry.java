package modules;

import datastructures.CustomHashTable;
import models.Student;

import java.util.Scanner;

/**
 * STUDENT REGISTRY MODULE
 * Manages all student data using a CustomHashTable.
 *
 * Key  : studentId  (String)
 * Value: Student    (object)

 */
public class StudentRegistry {

    //  Storage — custom hash table
    private final CustomHashTable<String, Student> table;

    private static final String BORDER      = "=".repeat(50);
    private static final String THIN_BORDER = "-".repeat(50);

    //  Constructor — seeds a few demo students
    public StudentRegistry() {
        table = new CustomHashTable<>(16);
        seedDemoData();
    }
//   Stdent data
    private void seedDemoData() {
        table.put("202232392", new Student("202232392",
                "P",
                "SIRAKALALA",
                "202232392@ul.ac.za",
                "0712345678",
                "BA Swim",
                2, 3.5,
                "Active"));
        table.put("240032476", new Student("240032476",
                "S J",
                "MALELE",
                "240032476@ul.ac.za",
                "0740032476",
                "BSc Computer Engineering",
                2, 3.0,
                "Active"));

        table.put("240032536", new Student("240032536",
                "M M",
                "Makgoga",
                "240032536@ul.ac.za",
                "07123345478",
                "BA Observation",
                3, 0.5,
                "Active"));

        table.put("202133247", new Student("202133247",
                "G",
                "CHAUKE",
                "202133247@ul.ac.za",
                "0765445678",
                "BEd Senior FET",
                3, 2.5,
                "Active"));

        table.put("202333287", new Student("202333287",
                "F M",
                "SARILA",
                "202333287@ul.ac.za",
                "071234234",
                "BSc Physical Science",
                2, 0.2,
                "Active"));

        table.put("202333486", new Student("202333486",
                "S",
                "NEMBUDANA",
                "202333486@ul.ac.za",
                "0712345678",
                "Bachelor of Fire Fighter",
                3, 1.5,
                "Suspended"));

        table.put("202333658", new Student("202333658",
                "B",
                "MASEGARE",
                "202333658@ul.ac.za",
                "06455678",
                "BA cooking",
                2, 4.0,
                "Active"));

        table.put("202335158", new Student("202335158",
                "MG",
                "MOOKA",
                "202335158@ul.ac.za",
                "023456768",
                "BSc Talking Science",
                2, 2.0,
                "Active"));

        table.put("240035420", new Student("240035420",
                "NC",
                "MNGOMENI",
                "240035420@ul.ac.za",
                "0645567823",
                "BA Media",
                2, 4.0,
                "Active"));

        table.put("202035570", new Student("202035570",
                "T",
                "MTHEMBU",
                "202035570@ul.ac.za",
                "06455678",
                "MBCHB",
                2, 4.0,
                "Suspended"));

        table.put("240036190", new Student("240036190",
                "Kgodiso",
                "Matlala",
                "240036190@ul.ac.za",
                "7221345678",
                "BA Walking",
                2, 3.0,
                "Active"));

        table.put("240036268", new Student("240036268",
                "MM",
                "SEKUBA",
                "240036268@ul.ac.za",
                "0976323456",
                "BSc Acturial Science",
                4, 2.0,
                "Active"));

        table.put("202236561", new Student("202236561",
                "J",
                "PUSO",
                "202236561@ul.ac.za",
                "06455674548",
                "BSc Forensic Science",
                1, 4.0,
                "Active"));

        table.put("250036945", new Student("250036945",
                "LM",
                "MAMABOLO",
                "250036945@ul.ac.za",
                "06451225678",
                "Optometry",
                1, 1.0,
                "Active"));

        table.put("240037317", new Student("240037317",
                "KP",
                "MOLELE",
                "240037317@ul.ac.za",
                "045342378",
                "BSc Water Science",
                4, 4.0,
                "Active"));

        table.put("250038198", new Student("250038198",
                "PR",
                "MODISHA",
                "250038198@ul.ac.za",
                "0645561278",
                "BSc Soil Science",
                1, 2.1,
                "Active"));

        table.put("202338218", new Student("202338218",
                "DA",
                "MOTHOBEKI",
                "202338218@ul.ac.za",
                "06455678",
                "BSc Geology Science",
                2, 4.0,
                "Suspended"));

        table.put("202238434", new Student("202238434",
                "Frans M",
                "Chokoe",
                "202238434@ul.ac.za",
                "0794181936",
                "BSc Data Engineering",
                2, 3.0,

                "Active"));

        table.put("240038450", new Student("240038450",
                "K",
                "MAPONYA",
                "240038450@ul.ac.za",
                "0792322321",
                "BSc DATA Analyst",
                1, 3.0,
                "Active"));

        table.put("250039408", new Student("250039408",
                "D",
                "MOROPA",
                "250039408@ul.ac.za",
                "1124536457",
                "Pharmacy",
                4, 0.4,
                "Active"));



    }
    //  ADD STUDENT
    public void addStudent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADD NEW STUDENT");
        System.out.println(THIN_BORDER);

        String id = readStudentId(scanner, "Student Number");

        if (table.containsKey(id)) {
            printError("Student Number '" + id + "' already exists.");
            return;
        }

        String firstName = prompt(scanner, "First Name");
        String lastName  = prompt(scanner, "Last Name");
        String email     = prompt(scanner, "Email Address");
        String phone     = prompt(scanner, "Phone Number");
        String programme = prompt(scanner, "Degree (e.g. BSc Computer Science)");

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

        printSuccess("Student '" + student.getFullName() + "' registered with ID: " + id);
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
        String id = readStudentId(scanner, "Enter Student Number:");
        Student s = table.get(id);

        if (s == null) {
            printError("No student found with ID '" + id + "'.");
        } else {
            s.display();
        }
    }

    private void searchByName(Scanner scanner) {
        String query = prompt(scanner, "Enter name").toLowerCase();

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

        String id = readStudentId(scanner, "Enter Student Number to delete");
        Student s = table.get(id);

        if (s == null) {
            printError("Student No. '" + id + "' not found.");
            return;
        }

        s.display();
        String confirm = prompt(scanner, "Confirm delete? This cannot be undone. (yes/no)");

        if (confirm.equalsIgnoreCase("yes")) {
            table.remove(id);
            printSuccess("Student '" + s.getFullName() + "' (" + id + ") has been removed.");
        } else {
            System.out.println("  Deletion cancelled.");
        }
    }

//    need to be added
    //  UPDATE STUDENT
    public void updateStudent(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  UPDATE STUDENT");
        System.out.println(THIN_BORDER);

        String id = readStudentId(scanner, "Enter Student No to update.");
        Student s = table.get(id);

        if (s == null) {
            printError("Student No '" + id + "' not found.");
            return;
        }

        s.display();

        System.out.println("\n  What would you like to update?");
        System.out.println("  1. Email");
        System.out.println("  2. Phone Number");
        System.out.println("  3. Programme");
        System.out.println("  4. Year of Study");
        System.out.println("  5. GPA");
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
                    double gpa = Double.parseDouble(prompt(scanner, "New GPA (0.0 - 4.0)"));
                    if (gpa >= 0.0 && gpa <= 4.0) {
                        s.setGpa(gpa);
                        printSuccess("GPA updated.");
                    } else {
                        printError("GPA must be between 0.0 and 4.0.");
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
    // we will implement
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
        return table.containsKey(studentId);
    }

    /** Returns the full name of the student — used for welcome message. */
    public String getStudentName(String studentId) {
        Student s = table.get(studentId);
        return (s != null) ? s.getFullName() : "Unknown";
    }


    //  Private utility methods and error handlers
    /**
     * Reads and validates a numeric Student ID from the console.
     *
     * Rules enforced:
     *   - Must not be blank
     *   - Must contain digits only (0-9) — no letters, spaces, or symbols
     *   - Loops and re-prompts until a valid ID is entered
     *
     * Why keep it as String?
     *   IDs are stored as String so leading zeros are preserved
     *   (e.g. "0042" stays "0042", not 42) and the hash table
     *   key type does not need to change.
     *
     * Example — valid:   "1001", "2050", "0042"
     * Example — invalid: "S001" → rejected (contains letter)
     *                    "10 01"→ rejected (contains space)
     *                    "abc"  → rejected (not digits)
     *                    ""     → rejected (blank)
     */
    private String readStudentId(Scanner scanner, String label) {
        while (true) {
            String input = prompt(scanner, label).trim();

            if (input.isEmpty()) {
                printError("Student Number cannot be blank. Please enter digits only");
                continue;
            }

            // Check every character is a digit
            boolean allDigits = true;
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i))) {
                    allDigits = false;
                    break;
                }
            }

            if (!allDigits) {
                printError("Invalid Student Number '" + input + "' — Student Numbers must contain digits only.");
                printError("Letters, spaces, and symbols are not allowed. Try again (e.g. 1001).");
                continue;
            }

            return input;   // valid — all digits
        }
    }
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




//first version

//package modules;
//
//import datastructures.CustomHashTable;
//import models.Student;
//
//import java.util.Scanner;

/**
  STUDENT REGISTRY MODULE
  Key  : studentId  (String)
  Value: Student    (object)
 */
//public class StudentRegistry {
//
//    //  Storage — custom hash table
//    private final CustomHashTable<String, Student> table;
//
//    private static final String BORDER      = "=".repeat(50);
//    private static final String THIN_BORDER = "-".repeat(50);
//    //  Constructor — seeds a few demo
//    public StudentRegistry() {
//        table = new CustomHashTable<>(16);
//        seedDemoData();
//    }
//    // HARD CODED RECORDS OF STUDENTS
//    // TO BE REMOVED OR COMMENTED ILL HEAR YOU GROUPMATES
//    private void seedDemoData() {
//        table.put("240036190", new Student( "240036190",
//                                        "Kgodiso",
//                                        "Matlala",
//                                        "kgodiso@ul.ac.za",
//                                        "0712345678",
//                                        "BSc Computer Science",
//                                        2, 3.5,
//                                        "Active"));
//        table.put("202335158", new Student("202335158",
//                                            "Moyahabo ",
//                                            "Mooka",
//                                            "202335158@ul.ac.za",
//                                            "0856789012",
//                                            "BSc Computer Science",
//                                            2, 4.0,
//                                            "Active"));
//    }
//    //  ADD STUDENT
//    public void addStudent(Scanner scanner) {
//        System.out.println("\n" + THIN_BORDER);
//        System.out.println("  ADD NEW STUDENT");
//        System.out.println(THIN_BORDER);
//
//        String id = String.valueOf(Integer.parseInt(prompt(scanner, "Student Number")));
//
//        if (table.containsKey(id)) {
//            printError("Student Number '" + id + "' already exists.");
//            return;
//        }
//
//        String firstName = prompt(scanner, "Full Name");
//        String lastName  = prompt(scanner, "Surname");
//        String email     = prompt(scanner, "Email Address");
//        String phone     = prompt(scanner, "Phone Number");
//        String programme = prompt(scanner, "Programme (e.g. BSc Computer Science)");
//
//        int year = 0;
//        while (year < 1 || year > 4) {
//            try {
//                year = Integer.parseInt(prompt(scanner, "Year of Study (1-4)"));
//                if (year < 1 || year > 4) printError("Year must be between 1 and 4.");
//            } catch (NumberFormatException e) {
//                printError("Please enter a valid number.");
//            }
//        }
//
//        double gpa = -1;
//        while (gpa < 0.0 || gpa > 4.0) {
//            try {
//                gpa = Double.parseDouble(prompt(scanner, "Average (0.0 - 4.0)"));
//                if (gpa < 0.0 || gpa > 4.0) printError("Average must be between 0.0 and 4.0.");
//            } catch (NumberFormatException e) {
//                printError("Please enter a valid number.");
//            }
//        }
//
//        Student student = new Student(id, firstName, lastName, email,
//                                      phone, programme, year, gpa, "Active");
//        table.put(id, student);
//
//        printSuccess("Student '" + student.getFullName() + "' registered with Student number: " + id);
//        student.display();
//    }
//
//    //  SEARCH STUDENT
//    public void searchStudent(Scanner scanner) {
//        System.out.println("\n" + THIN_BORDER);
//        System.out.println("  SEARCH STUDENT");
//        System.out.println(THIN_BORDER);
//        System.out.println("  1. Search by Student Number");
//        System.out.println("  2. Search by Name");
//        System.out.println(THIN_BORDER);
//
//        String choice = prompt(scanner, "Enter choice");
//
//        switch (choice.trim()) {
//            case "1" -> searchById(scanner);
//            case "2" -> searchByName(scanner);
//            default  -> printError("Invalid choice.");
//        }
//    }
//
//    private void searchById(Scanner scanner) {
//        String id = prompt(scanner, "Enter Student Number").toUpperCase();
//        Student s = table.get(id);
//
//        if (s == null) {
//            printError("No student found with student number '" + id + "'.");
//        } else {
//            s.display();
//        }
//    }
//
//    private void searchByName(Scanner scanner) {
//        String query = prompt(scanner, "Enter name (or part of name)").toLowerCase();
//
//        Object[] values = table.getAllValues();
//        boolean  found  = false;
//
//        System.out.println("\n" + THIN_BORDER);
//        System.out.printf("  %-10s %-22s %-30s %-8s %s%n",
//                "Student No.", "Full Name", "Email", "Status", "Year");
//        System.out.println(THIN_BORDER);
//
//        for (Object obj : values) {
//            Student s = (Student) obj;
//            if (s.getFullName().toLowerCase().contains(query) ||
//                s.getFirstName().toLowerCase().contains(query) ||
//                s.getLastName().toLowerCase().contains(query)) {
//                System.out.println(s.toTableRow());
//                found = true;
//            }
//        }
//
//        if (!found) {
//            printError("No students found matching '" + query + "'.");
//        } else {
//            System.out.println(THIN_BORDER);
//        }
//    }
//    //  DELETE STUDENT
//    public void deleteStudent(Scanner scanner) {
//        System.out.println("\n" + THIN_BORDER);
//        System.out.println("  DELETE STUDENT");
//        System.out.println(THIN_BORDER);
//
//        String id = prompt(scanner, "Enter Student Number to delete").toUpperCase();
//        Student s = table.get(id);
//
//        if (s == null) {
//            printError("Student No. '" + id + "' not found.");
//            return;
//        }
//
//        s.display();
//        String confirm = prompt(scanner, "Confirm delete? This cannot be undone. (yes/no)").toLowerCase();
//
//        if (confirm.equalsIgnoreCase("yes")) {
//            table.remove(id);
//            printSuccess("Student '" + s.getFullName() + "' (" + id + ") has been removed.");
//        } else {
//            System.out.println("  Deletion cancelled.");
//        }
//    }
//
//    //  UPDATE STUDENT
//    public void updateStudent(Scanner scanner) {
//        System.out.println("\n" + THIN_BORDER);
//        System.out.println("  UPDATE STUDENT");
//        System.out.println(THIN_BORDER);
//
//        String id = prompt(scanner, "Enter Student Number to update").toUpperCase();
//        Student s = table.get(id);
//
//        if (s == null) {
//            printError("Student Number '" + id + "' not found.");
//            return;
//        }
//
//        s.display();
//
//        System.out.println("\n  What would you like to update?");
//        System.out.println("  1. Email");
//        System.out.println("  2. Phone Number");
//        System.out.println("  3. Programme");
//        System.out.println("  4. Year of Study");
//        System.out.println("  5. Average");
//        System.out.println("  6. Status (Active / Suspended / Graduated)");
//        System.out.println(THIN_BORDER);
//
//        String choice = prompt(scanner, "Enter choice");
//
//        switch (choice.trim()) {
//            case "1" -> {
//                s.setEmail(prompt(scanner, "New email"));
//                printSuccess("Email updated.");
//            }
//            case "2" -> {
//                s.setPhoneNumber(prompt(scanner, "New phone number"));
//                printSuccess("Phone number updated.");
//            }
//            case "3" -> {
//                s.setProgramme(prompt(scanner, "New programme"));
//                printSuccess("Programme updated.");
//            }
//            case "4" -> {
//                try {
//                    int year = Integer.parseInt(prompt(scanner, "New year of study (1-4)"));
//                    if (year >= 1 && year <= 4) {
//                        s.setYearOfStudy(year);
//                        printSuccess("Year of study updated.");
//                    } else {
//                        printError("Year must be between 1 and 4.");
//                    }
//                } catch (NumberFormatException e) {
//                    printError("Invalid number entered.");
//                }
//            }
//            case "5" -> {
//                try {
//                    double gpa = Double.parseDouble(prompt(scanner, "New Average (0.0 - 4.0)"));
//                    if (gpa >= 0.0 && gpa <= 4.0) {
//                        s.setGpa(gpa);
//                        printSuccess("Average updated.");
//                    } else {
//                        printError("Average must be between 0.0 and 4.0.");
//                    }
//                } catch (NumberFormatException e) {
//                    printError("Invalid number entered.");
//                }
//            }
//            case "6" -> {
//                String status = prompt(scanner, "New status (Active / Suspended / Graduated)");
//                if (status.equals("Active") || status.equals("Suspended") || status.equals("Graduated")) {
//                    s.setStatus(status);
//                    printSuccess("Status updated.");
//                } else {
//                    printError("Status must be 'Active', 'Suspended', or 'Graduated'.");
//                }
//            }
//            default -> printError("Invalid choice.");
//        }
//    }
//
//    //  DISPLAY ALL STUDENTS
//    public void displayAllStudents() {
//        if (table.isEmpty()) {
//            printError("No students registered yet.");
//            return;
//        }
//
//        Object[] values = table.getAllValues();
//
//        System.out.println("\n" + BORDER);
//        System.out.println("  ALL REGISTERED STUDENTS  (" + table.size() + " total)");
//        System.out.println(BORDER);
//        System.out.printf("  %-10s %-22s %-30s %-12s %s%n",
//                "Student No.", "Full Name", "Email", "Status", "Year");
//        System.out.println(THIN_BORDER);
//
//        for (Object obj : values) {
//            Student s = (Student) obj;
//            System.out.println(s.toTableRow());
//        }
//
//        System.out.println(BORDER);
//        System.out.println("  Total students: " + table.size());
//        System.out.println(BORDER);
//    }
//
//    //  DISPLAY SINGLE STUDENT (student-facing)
//
//    public void displayStudent(String studentId) {
//        Student s = table.get(studentId);
//        if (s != null) {
//            s.display();
//        } else {
//            printError("Student profile not found.");
//        }
//    }
//
//    //  STATISTICS (admin)
//    public void displayStatistics() {
//        if (table.isEmpty()) {
//            printError("No student data available.");
//            return;
//        }
//
//        Object[] values  = table.getAllValues();
//        int    active    = 0, suspended = 0, graduated = 0;
//        double totalGpa  = 0;
//        int    year1 = 0, year2 = 0, year3 = 0, year4 = 0;
//
//        for (Object obj : values) {
//            Student s = (Student) obj;
//            totalGpa += s.getGpa();
//
//            switch (s.getStatus()) {
//                case "Active"     -> active++;
//                case "Suspended"  -> suspended++;
//                case "Graduated"  -> graduated++;
//            }
//            switch (s.getYearOfStudy()) {
//                case 1 -> year1++;
//                case 2 -> year2++;
//                case 3 -> year3++;
//                case 4 -> year4++;
//            }
//        }
//
//        double avgGpa = totalGpa / table.size();
//
//        System.out.println("\n" + BORDER);
//        System.out.println("  STUDENT STATISTICS");
//        System.out.println(THIN_BORDER);
//        System.out.printf("  %-25s: %d%n",  "Total Students",    table.size());
//        System.out.printf("  %-25s: %d%n",  "Active",            active);
//        System.out.printf("  %-25s: %d%n",  "Suspended",         suspended);
//        System.out.printf("  %-25s: %d%n",  "Graduated",         graduated);
//        System.out.println(THIN_BORDER);
//        System.out.printf("  %-25s: %.2f%n","Average GPA",       avgGpa);
//        System.out.println(THIN_BORDER);
//        System.out.printf("  %-25s: %d%n",  "Year 1",            year1);
//        System.out.printf("  %-25s: %d%n",  "Year 2",            year2);
//        System.out.printf("  %-25s: %d%n",  "Year 3",            year3);
//        System.out.printf("  %-25s: %d%n",  "Year 4",            year4);
//        System.out.println(BORDER);
//    }
//
//    //  HELPERS USED BY Main.java
//
//    /** Returns true if a student with this ID exists — used for login validation. */
//    public boolean studentExists(String studentId) {
//        return table.containsKey(studentId.toUpperCase());
//    }
//
//    /** Returns the full name of the student — used for welcome message. */
//    public String getStudentName(String studentId) {
//        Student s = table.get(studentId.toUpperCase());
//        return (s != null) ? s.getFullName() : "Unknown";
//    }
//
//    //  Private utility methods
//    private String prompt(Scanner scanner, String label) {
//        System.out.print("  " + label + ": ");
//        return scanner.nextLine().trim();
//    }
//
//    private void printSuccess(String msg) {
//        System.out.println("\n  ✔ " + msg);
//    }
//
//    private void printError(String msg) {
//        System.out.println("\n  ✘ ERROR: " + msg);
//    }
//}

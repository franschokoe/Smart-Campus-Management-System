package modules;

import datastructures.CustomGraph;
import datastructures.CustomHashTable;
import models.Course;

import java.util.Scanner;

public class CourseRegistration {

    //  Storage
    private final CustomGraph                    graph;
    private final CustomHashTable<String, Course> courses;

    private static final String BORDER      = "=".repeat(54);
    private static final String THIN_BORDER = "-".repeat(54);

    //  Constructor — seeds demo courses and prereqs

    public CourseRegistration() {
        graph   = new CustomGraph();
        courses = new CustomHashTable<>(16);
        seedDemoData();
    }
    //harded code the value
    private void seedDemoData() {
        // ── Computer Science courses ──
        addCourseInternal(new Course(
                "SCOA021",
                "Computer Architecture",
                28,
                "Computer Science",
                "Semester 2",
                200,
                "Shikwambana and others",
                "Hardware organisation and assembly language."
        ));
        addCourseInternal(new Course(
                "SCSC011",
                "Algorithims",
                18,
                "Computer Science",
                "1",
                250,
                "Malaji",
                "Algorihms and DB"

        ));

        // ── Mathematics courses ──
        addCourseInternal(new Course(
                "SMTA021",
                "Calculus II",
                15,
                "Mathematics",
                "Semester 1",
                150,
                "Prof. Olela",
                "Sequences , Multiple , Vectors"
        ));
        addCourseInternal(new Course(
                "SMTH011",
                "Calculus I",
                15,
                "Mathematics",
                "Semester 1",
                150,
                "Malatji",
                "Limits , integral"
        ));

        graph.addEdge("SMTH011",  "SMTA021");
        graph.addEdge("SCSC011",  "SCOA021");
        graph.addEdge("SMTH011" , "SCOA021");


    }

    /** Internal helper — adds course to both structures. */
    private void addCourseInternal(Course c) {
        courses.put(c.getCourseCode(), c);
        graph.addVertex(c.getCourseCode());
    }

    //  ADD COURSE  (admin)
    public void addCourse(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADD NEW COURSE");
        System.out.println(THIN_BORDER);

        String code = prompt(scanner, "Course Code (e.g. SCSC011)").toUpperCase();
        if (courses.containsKey(code)) {
            printError("Course '" + code + "' already exists.");
            return;
        }

        String name  = prompt(scanner, "Course Name");
        int credits  = readInt(scanner, "Credits (e.g. 15)", 1, 30);
        String dept  = prompt(scanner, "Department");
        String sem   = prompt(scanner, "Semester (e.g. Semester 1)");
        int cap      = readInt(scanner, "Capacity", 1, 1000);
        String lect  = prompt(scanner, "Lecturer name");
        String desc  = prompt(scanner, "Short description");

        Course c = new Course(code, name, credits, dept, sem, cap, lect, desc);
        addCourseInternal(c);
        printSuccess("Course '" + name + "' (" + code + ") added successfully.");
        c.display();
    }

    //  REMOVE COURSE  (admin)
    public void removeCourse(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  REMOVE COURSE");
        System.out.println(THIN_BORDER);

        String code = prompt(scanner, "Enter Course Code to remove").toUpperCase();
        Course c    = courses.get(code);

        if (c == null) { printError("Course '" + code + "' not found."); return; }

        c.display();

        // Warn if other courses depend on this one
        String[] dependents = graph.getCoursesUnlocked(code);
        if (dependents.length > 0) {
            System.out.println("\n WARNING: The following courses use " + code + " as a prerequisite:");
            for (String d : dependents) System.out.println("    - " + d);
            System.out.println("  Removing this course will also remove those prerequisite links.");
        }

        String confirm = prompt(scanner, "Confirm removal? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            courses.remove(code);
            graph.removeVertex(code);
            printSuccess("Course '" + code + "' removed.");
        } else {
            System.out.println("  Removal cancelled.");
        }
    }

    //  ADD PREREQUISITE LINK  (admin)
    public void addPrerequisite(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  ADD PREREQUISITE LINK");
        System.out.println(THIN_BORDER);
//        System.out.println("  This creates a directed edge:  PREREQ ──► TARGET");
//        System.out.println("  meaning: student must complete PREREQ before TARGET.");
        System.out.println(THIN_BORDER);

        String prereq = prompt(scanner, "Prerequisite course code (must complete FIRST)").toUpperCase();
        String target = prompt(scanner, "Target course code (unlocked AFTER prereq)").toUpperCase();

        if (!courses.containsKey(prereq)) { printError("Course '" + prereq + "' not found."); return; }
        if (!courses.containsKey(target)) { printError("Course '" + target + "' not found."); return; }
        if (prereq.equals(target))         { printError("A course cannot be its own prerequisite."); return; }
        if (graph.hasEdge(prereq, target)) { printError("That prerequisite link already exists."); return; }

        // Cycle check — would this create a circular dependency?
        if (graph.wouldCreateCycle(prereq, target)) {
            printError("Cannot add this link — it would create a CIRCULAR prerequisite chain.");
            printError("e.g. " + prereq + " ──► " + target + " ──► ... ──► " + prereq);
            return;
        }

        graph.addEdge(prereq, target);
        printSuccess(prereq + "  ──►  " + target + "  prerequisite link added.");
        System.out.println("  Students must complete " + prereq
                + " before registering for " + target + ".");
    }

    //  REMOVE PREREQUISITE LINK  (admin)
    public void removePrerequisite(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  REMOVE PREREQUISITE LINK");
        System.out.println(THIN_BORDER);

        String prereq = prompt(scanner, "Prerequisite course code").toUpperCase();
        String target = prompt(scanner, "Target course code").toUpperCase();

        if (!graph.hasEdge(prereq, target)) {
            printError("No prerequisite link exists from '" + prereq + "' to '" + target + "'.");
            return;
        }

        graph.removeEdge(prereq, target);
        printSuccess("Prerequisite link " + prereq + " ──► " + target + " removed.");
    }

    //  UPDATE COURSE  (admin)
    public void updateCourse(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  UPDATE COURSE");
        System.out.println(THIN_BORDER);

        String code = prompt(scanner, "Enter Course Code to update").toUpperCase();
        Course c    = courses.get(code);
        if (c == null) { printError("Course '" + code + "' not found."); return; }

        c.display();
        System.out.println("\n  What would you like to update?");
        System.out.println("  1. Course Name");
        System.out.println("  2. Credits");
        System.out.println("  3. Department");
        System.out.println("  4. Semester");
        System.out.println("  5. Capacity");
        System.out.println("  6. Lecturer");
        System.out.println("  7. Description");
        System.out.println(THIN_BORDER);

        String choice = prompt(scanner, "Enter choice");
        switch (choice) {
            case "1" -> { c.setCourseName(prompt(scanner, "New course name"));   printSuccess("Course name updated."); }
            case "2" -> { c.setCredits(readInt(scanner,"New credits",1,30));      printSuccess("Credits updated."); }
            case "3" -> { c.setDepartment(prompt(scanner, "New department"));     printSuccess("Department updated."); }
            case "4" -> { c.setSemester(prompt(scanner, "New semester"));         printSuccess("Semester updated."); }
            case "5" -> { c.setCapacity(readInt(scanner,"New capacity",1,500));   printSuccess("Capacity updated."); }
            case "6" -> { c.setLecturer(prompt(scanner, "New lecturer name"));    printSuccess("Lecturer updated."); }
            case "7" -> { c.setDescription(prompt(scanner, "New description"));   printSuccess("Description updated."); }
            default  -> printError("Invalid choice.");
        }
    }
    //  DISPLAY ALL COURSES  (both roles)
    public void displayAllCourses() {
        String[] codes = graph.getAllVertices();
        if (codes.length == 0) { printError("No courses registered."); return; }

        System.out.println("\n" + BORDER);
        System.out.println("  ALL COURSES  (" + courses.size() + " total)");
        System.out.println(BORDER);
        System.out.printf("  %-8s %-34s %-5s %-15s %s%n",
                "Code", "Name", "Cred", "Semester", "Enrolled");
        System.out.println(THIN_BORDER);

        for (String code : codes) {
            Course c = courses.get(code);
            if (c != null) System.out.println(c.toTableRow());
        }
        System.out.println(BORDER);
        System.out.println("  Total courses: " + courses.size());
        System.out.println(BORDER);
    }
    //  SHOW PREREQUISITES  (both roles)

    public void showPrerequisites(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  COURSE PREREQUISITES");
        System.out.println(THIN_BORDER);

        String code = prompt(scanner, "Enter Course Code").toUpperCase();
        Course c    = courses.get(code);
        if (c == null) { printError("Course '" + code + "' not found."); return; }

        c.display();

        // Direct prerequisites
        String[] direct = graph.getDirectPrerequisites(code);
        System.out.println("\n  DIRECT Prerequisites (complete these first):");
        if (direct.length == 0) {
            System.out.println("    None — this is an entry-level course.");
        } else {
            for (String p : direct) {
                Course pc = courses.get(p);
                String name = (pc != null) ? pc.getCourseName() : "Unknown";
                System.out.println("    ✔ " + p + "  —  " + name);
            }
        }

        // All prerequisites (transitive)
        String[] all = graph.getAllPrerequisites(code);
        if (all.length > direct.length) {
            System.out.println("\n  ALL Prerequisites (direct + transitive):");
            for (String p : all) {
                Course pc = courses.get(p);
                String name = (pc != null) ? pc.getCourseName() : "Unknown";
                System.out.println("    → " + p + "  —  " + name);
            }
        }

        // What this course unlocks
        String[] unlocks = graph.getCoursesUnlocked(code);
        System.out.println("\n  Completing " + code + " UNLOCKS:");
        if (unlocks.length == 0) {
            System.out.println("    Nothing — this is a terminal course.");
        } else {
            for (String u : unlocks) {
                Course uc = courses.get(u);
                String name = (uc != null) ? uc.getCourseName() : "Unknown";
                System.out.println("    ► " + u + "  —  " + name);
            }
        }
        System.out.println(THIN_BORDER);
    }


    //  SHOW FULL PREREQUISITE GRAPH  (admin)

    public void showFullGraph() {
        System.out.println("\n" + BORDER);
        System.out.println("  PREREQUISITE GRAPH — ADJACENCY LIST");
        System.out.println("  Edge A ──► B means: complete A before registering for B");
        System.out.println(BORDER);
        graph.printGraph();
    }

    //  SHOW STUDY ORDER  (topological sort)
    public void showStudyOrder() {
        String[] order = graph.topologicalSort();

        System.out.println("\n" + BORDER);
        System.out.println("  RECOMMENDED STUDY ORDER");
        System.out.println("  (Respects all prerequisite dependencies)");
        System.out.println(BORDER);

        if (order == null) {
            printError("Cannot determine study order — circular prerequisite detected!");
            return;
        }

        for (int i = 0; i < order.length; i++) {
            Course c = courses.get(order[i]);
            String name = (c != null) ? c.getCourseName() : "Unknown";
            System.out.printf("  %2d. %-8s  %s%n", (i + 1), order[i], name);
        }
        System.out.println(BORDER);
    }

    //  REGISTER FOR A COURSE  (student)
    public void registerForCourse(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  REGISTER FOR A COURSE");
        System.out.println(THIN_BORDER);

        displayAllCourses();
        String code = prompt(scanner, "Enter Course Code to register for").toUpperCase();
        Course c    = courses.get(code);

        if (c == null)  { printError("Course '" + code + "' not found."); return; }
        if (c.isFull()) { printError("Course '" + code + "' is FULL. No slots available."); return; }

        // Check prerequisites
        String[] prereqs = graph.getDirectPrerequisites(code);
        if (prereqs.length > 0) {
            System.out.println("\n  This course has the following prerequisites:");
            for (String p : prereqs) {
                Course pc = courses.get(p);
                System.out.println("    - " + p + (pc != null ? "  (" + pc.getCourseName() + ")" : ""));
            }
            String confirm = prompt(scanner, "Have you completed all prerequisites? (yes/no)");
            if (!confirm.equalsIgnoreCase("yes")) {
                printError("Registration denied. Please complete prerequisites first.");
                return;
            }
        }

        c.enrol();
        printSuccess("Successfully registered for: " + code + " — " + c.getCourseName());
        System.out.println("  Slots remaining: " + c.getAvailableSlots());
    }
    //  WITHDRAW FROM A COURSE  (student)
    public void withdrawFromCourse(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  WITHDRAW FROM A COURSE");
        System.out.println(THIN_BORDER);

        String code = prompt(scanner, "Enter Course Code to withdraw from").toUpperCase();
        Course c    = courses.get(code);

        if (c == null) { printError("Course '" + code + "' not found."); return; }

        String confirm = prompt(scanner, "Confirm withdrawal from " + code + "? (yes/no)");
        if (confirm.equalsIgnoreCase("yes")) {
            if (c.withdraw()) {
                printSuccess("Withdrawn from: " + code + " — " + c.getCourseName());
            } else {
                printError("No enrolled students to withdraw.");
            }
        } else {
            System.out.println("  Withdrawal cancelled.");
        }
    }

    //  SEARCH COURSE  (both)
    public void searchCourse(Scanner scanner) {
        System.out.println("\n" + THIN_BORDER);
        System.out.println("  SEARCH COURSE");
        System.out.println(THIN_BORDER);
        System.out.println("  1. Search by Course Code");
        System.out.println("  2. Search by Name / Keyword");
        System.out.println(THIN_BORDER);

        String choice = prompt(scanner, "Enter choice");

        if (choice.equals("1")) {
            String code = prompt(scanner, "Enter Course Code").toUpperCase();
            Course c    = courses.get(code);
            if (c == null) printError("Course '" + code + "' not found.");
            else           c.display();

        } else if (choice.equals("2")) {
            String query  = prompt(scanner, "Enter keyword (name or department)").toLowerCase();
            String[] keys = graph.getAllVertices();
            boolean  found = false;

            System.out.println("\n" + THIN_BORDER);
            System.out.printf("  %-8s %-34s %-5s %-15s %s%n",
                    "Code","Name","Cred","Semester","Enrolled/Cap");
            System.out.println(THIN_BORDER);

            for (String k : keys) {
                Course c = courses.get(k);
                if (c != null &&
                        (c.getCourseName().toLowerCase().contains(query) ||
                                c.getDepartment().toLowerCase().contains(query) ||
                                c.getCourseCode().toLowerCase().contains(query))) {
                    System.out.println(c.toTableRow());
                    found = true;
                }
            }
            if (!found) printError("No courses found matching '" + query + "'.");
            else System.out.println(THIN_BORDER);
        } else {
            printError("Invalid choice.");
        }
    }

    //  DEPARTMENT FILTER  (both)
    public void displayByDepartment(Scanner scanner) {
        String dept = prompt(scanner, "Enter department name").toLowerCase();

        System.out.println("\n" + BORDER);
        System.out.println("  COURSES — " + dept.toUpperCase());
        System.out.println(BORDER);
        System.out.printf("  %-8s %-34s %-5s %-15s %s%n",
                "Code","Name","Cred","Semester","Enrolled/Cap");
        System.out.println(THIN_BORDER);

        String[] keys = graph.getAllVertices();
        boolean  found = false;
        for (String k : keys) {
            Course c = courses.get(k);
            if (c != null && c.getDepartment().toLowerCase().contains(dept)) {
                System.out.println(c.toTableRow());
                found = true;
            }
        }
        if (!found) printError("No courses found in department '" + dept + "'.");
        else System.out.println(BORDER);
    }

    //  PRIVATE HELPERS
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

    private void printSuccess(String msg) { System.out.println("\n  ✔ " + msg); }
    private void printError(String msg)   { System.out.println("\n  ✘ ERROR: " + msg); }
}
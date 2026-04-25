package models;

/**
 * Fields:
 *   courseCode   — unique identifier, e.g. "CS101"  (graph vertex key)
 *   courseName   — full name,         e.g. "Introduction to Programming"
 *   credits      — credit weight,     e.g. 15
 *   department   — owning department, e.g. "Computer Science"
 *   semester     — offered in,        e.g. "Semester 1"
 *   capacity     — max enrolments
 *   enrolled     — current enrolment count
 *   lecturer     — name of lecturer
 *   description  — short course overview
 */
public class Course {

    //  Fields
    private String courseCode;
    private String courseName;
    private int    credits;
    private String department;
    private String semester;
    private int    capacity;
    private int    enrolled;
    private String lecturer;
    private String description;


    //  Constructor
    public Course(String courseCode, String courseName, int    credits, String department, String semester,
                  int    capacity, String lecturer, String description) {
        this.courseCode  = courseCode;
        this.courseName  = courseName;
        this.credits     = credits;
        this.department  = department;
        this.semester    = semester;
        this.capacity    = capacity;
        this.enrolled    = 0;
        this.lecturer    = lecturer;
        this.description = description;
    }


    //  Getter
    public String getCourseCode()     { return courseCode;              }
    public String getCourseName()     { return courseName;              }
    public int    getCredits()        { return credits;                 }
    public String getDepartment()     { return department;              }
    public String getSemester()       { return semester;                }
    public int    getCapacity()       { return capacity;                }
    public int    getEnrolled()       { return enrolled;                }
    public String getLecturer()       { return lecturer;                }
    public String getDescription()    { return description;             }
    public int    getAvailableSlots() { return capacity - enrolled;     }
    public boolean isFull()           { return enrolled >= capacity;    }

    //  Setter
    public void setCourseName(String v)  { this.courseName  = v; }
    public void setCredits(int v)        { this.credits     = v; }
    public void setDepartment(String v)  { this.department  = v; }
    public void setSemester(String v)    { this.semester    = v; }
    public void setCapacity(int v)       { this.capacity    = v; }
    public void setLecturer(String v)    { this.lecturer    = v; }
    public void setDescription(String v) { this.description = v; }

    //  Enrolment helpers
    public boolean enrol()    { if (isFull())      return false; enrolled++; return true; }
    public boolean withdraw() { if (enrolled <= 0) return false; enrolled--; return true; }

    //  Display
    public void display() {
        String thin = "-".repeat(52);
        System.out.println("\n" + thin);
        System.out.println("  COURSE DETAILS");
        System.out.println(thin);
        System.out.printf("  %-20s: %s%n",  "Course Code",  courseCode);
        System.out.printf("  %-20s: %s%n",  "Course Name",  courseName);
        System.out.printf("  %-20s: %d%n",  "Credits",      credits);
        System.out.printf("  %-20s: %s%n",  "Department",   department);
        System.out.printf("  %-20s: %s%n",  "Semester",     semester);
        System.out.printf("  %-20s: %s%n",  "Lecturer",     lecturer);
        System.out.printf("  %-20s: %d / %d  (%d slots open)%n",
                "Enrolment", enrolled, capacity, getAvailableSlots());
        System.out.printf("  %-20s: %s%n",  "Status",       isFull() ? "FULL" : "Open");
        System.out.printf("  %-20s: %s%n",  "Description",  description);
        System.out.println(thin);
    }

    public String toTableRow() {
        return String.format("  %-8s %-34s %-4d %-14s %d/%d",
                courseCode, courseName, credits, semester, enrolled, capacity);
    }

    @Override
    public String toString() { return courseCode + " — " + courseName; }
}
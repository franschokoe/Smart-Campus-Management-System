package models;

/**
 * Represents a student in the Smart Campus system.
 * stored inside the custom hash table.
 */

public class Student {

    //  Fields
    private String studentId;      //used as hash key
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String programme;      // course
    private int    yearOfStudy;    // 1 to 4
    private double gpa;
    private String status;         // Active | Suspended | Graduated

    //  Constructor
    public Student(String studentId, String firstName, String lastName, String email, String phoneNumber,
                   String programme, int    yearOfStudy, double gpa, String status) {
        this.studentId   = studentId;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.email       = email;
        this.phoneNumber = phoneNumber;
        this.programme   = programme;
        this.yearOfStudy = yearOfStudy;
        this.gpa         = gpa;
        this.status      = status;
    }
    //  Getters
    public String getStudentId()   { return studentId;   }
    public String getFirstName()   { return firstName;   }
    public String getLastName()    { return lastName;    }
    public String getFullName()    { return firstName + " " + lastName; }
    public String getEmail()       { return email;       }
    public String getPhoneNumber() { return phoneNumber; }
    public String getProgramme()   { return programme;   }
    public int    getYearOfStudy() { return yearOfStudy; }
    public double getGpa()         { return gpa;         }
    public String getStatus()      { return status;      }

    //  Setters  (only mutable fields)
    public void setEmail(String email)             { this.email = email;             }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setProgramme(String programme)     { this.programme = programme;     }
    public void setYearOfStudy(int yearOfStudy)    { this.yearOfStudy = yearOfStudy; }
    public void setGpa(double gpa)                 { this.gpa = gpa;                 }
    public void setStatus(String status)           { this.status = status;           }

    //  Display
    /** Prints a formatted profile card  */
    public void display() {
        String line = "-".repeat(50);
        System.out.println("\n" + line);
        System.out.println("  STUDENT PROFILE");
        System.out.println(line);
        System.out.printf("  %-18s: %s%n", "Student Number",   studentId);
        System.out.printf("  %-18s: %s%n", "Full Name",    getFullName());
        System.out.printf("  %-18s: %s%n", "Email",        email);
        System.out.printf("  %-18s: %s%n", "Phone",        phoneNumber);
        System.out.printf("  %-18s: %s%n", "Programme",    programme);
        System.out.printf("  %-18s: Year %d%n", "Year of Study", yearOfStudy);
        System.out.printf("  %-18s: %.2f%n", "Average",        gpa);
        System.out.printf("  %-18s: %s%n", "Status",       status);
        System.out.println(line);
    }

    /** Short one-line summary — used in table listings. */
    public String toTableRow() {
        return String.format("  %-10s %-22s %-30s %-8s Year %d",
                studentId, getFullName(), email, status, yearOfStudy);
    }

    @Override
    public String toString() {
        return "Student{id='" + studentId + "', name='" + getFullName() + "', status='" + status + "'}";
    }
}

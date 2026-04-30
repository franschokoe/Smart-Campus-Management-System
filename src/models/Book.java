package models;

/**
 * Represents a library book in the Smart Campus system.
 *
 * A Book is the data object stored inside the CustomLinkedList.
 * Each node in the linked list holds one Book object.
 *
 * Fields:
 *   isbn          — unique identifier,  e.g. "978-0-13-468599-1"
 *   title         — book title,         e.g. "Clean Code"
 *   author        — author name,        e.g. "Robert C. Martin"
 *   genre         — subject area,       e.g. "Computer Science"
 *   publisher     — publisher name,     e.g. "Prentice Hall"
 *   yearPublished — publication year,   e.g. 2008
 *   totalCopies   — how many copies the library owns
 *   availableCopies — how many are currently on the shelf
 *   borrowedBy    — studentId of current borrower (null if available)
 *   dateBorrowed  — when it was borrowed (null if available)
 *   dateDue       — when it must be returned (null if available)
 *   status        — Available | Borrowed | Reserved | Lost
 */
public class Book {
    //  Fields
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private String publisher;
    private int    yearPublished;
    private int    totalCopies;
    private int    availableCopies;
    private String borrowedBy;       // studentId — null when available
    private String borrowerName;     // student name — null when available
    private String dateBorrowed;     // null when available
    private String dateDue;          // null when available
    private String status;           // Available | Borrowed | Reserved | Lost

    //  Constructor
    public Book(String isbn,
                String title,
                String author,
                String genre,
                String publisher,
                int    yearPublished,
                int    totalCopies) {
        this.isbn            = isbn;
        this.title           = title;
        this.author          = author;
        this.genre           = genre;
        this.publisher       = publisher;
        this.yearPublished   = yearPublished;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;   // all copies available at start
        this.borrowedBy      = null;
        this.borrowerName    = null;
        this.dateBorrowed    = null;
        this.dateDue         = null;
        this.status          = "Available";
    }


    //  Getters
    public String getIsbn()             { return isbn;             }
    public String getTitle()            { return title;            }
    public String getAuthor()           { return author;           }
    public String getGenre()            { return genre;            }
    public String getPublisher()        { return publisher;        }
    public int    getYearPublished()    { return yearPublished;    }
    public int    getTotalCopies()      { return totalCopies;      }
    public int    getAvailableCopies()  { return availableCopies;  }
    public String getBorrowedBy()       { return borrowedBy;       }
    public String getBorrowerName()     { return borrowerName;     }
    public String getDateBorrowed()     { return dateBorrowed;     }
    public String getDateDue()          { return dateDue;          }
    public String getStatus()           { return status;           }

    public boolean isAvailable()        { return availableCopies > 0 && !"Lost".equals(status); }
    public boolean isBorrowed()         { return "Borrowed".equals(status);  }
    public boolean isLost()             { return "Lost".equals(status);      }


    //  Setters

    public void setTitle(String title)           { this.title         = title;       }
    public void setAuthor(String author)         { this.author        = author;      }
    public void setGenre(String genre)           { this.genre         = genre;       }
    public void setPublisher(String publisher)   { this.publisher     = publisher;   }
    public void setYearPublished(int year)       { this.yearPublished = year;        }
    public void setTotalCopies(int copies)       { this.totalCopies   = copies;      }
    public void setStatus(String status)         { this.status        = status;      }


    //  Borrow / Return operations

    /**
     * Records a borrow transaction.
     * Returns true if successful, false if no copies are available.
     */
    public boolean borrow(String studentId, String studentName,
                          String dateBorrowed, String dateDue) {
        if (!isAvailable()) return false;
        this.availableCopies--;
        this.borrowedBy   = studentId;
        this.borrowerName = studentName;
        this.dateBorrowed = dateBorrowed;
        this.dateDue      = dateDue;
        this.status       = availableCopies == 0 ? "Borrowed" : "Available";
        return true;
    }

    /**
     * Records a return transaction.
     * Returns true if successful, false if the book was not marked borrowed.
     */
    public boolean returnBook() {
        if (availableCopies >= totalCopies) return false;
        this.availableCopies++;
        this.borrowedBy   = null;
        this.borrowerName = null;
        this.dateBorrowed = null;
        this.dateDue      = null;
        this.status       = "Available";
        return true;
    }

    /**
     * Marks this book as lost.
     * Reduces total and available copies by one.
     */
    public void markAsLost() {
        if (totalCopies > 0) totalCopies--;
        if (availableCopies > 0) availableCopies--;
        if (totalCopies == 0) this.status = "Lost";
    }


    //  Display
    /** Full formatted book card. */
    public void display() {
        String thin = "-".repeat(54);
        System.out.println("\n" + thin);
        System.out.println("  BOOK DETAILS");
        System.out.println(thin);
        System.out.printf("  %-20s: %s%n",  "ISBN",           isbn);
        System.out.printf("  %-20s: %s%n",  "Title",          title);
        System.out.printf("  %-20s: %s%n",  "Author",         author);
        System.out.printf("  %-20s: %s%n",  "Genre",          genre);
        System.out.printf("  %-20s: %s%n",  "Publisher",      publisher);
        System.out.printf("  %-20s: %d%n",  "Year Published", yearPublished);
        System.out.printf("  %-20s: %d / %d copies available%n",
                "Copies", availableCopies, totalCopies);
        System.out.printf("  %-20s: %s%n",  "Status",         status);
        if (isBorrowed() || borrowedBy != null) {
            System.out.println(thin);
            System.out.printf("  %-20s: %s (%s)%n", "Borrowed By", borrowerName, borrowedBy);
            System.out.printf("  %-20s: %s%n",       "Date Borrowed", dateBorrowed);
            System.out.printf("  %-20s: %s%n",       "Due Date",      dateDue);
        }
        System.out.println(thin);
    }

    /** One-line row used in book listing tables. */
    public String toTableRow() {
        return String.format("  %-20s %-30s %-22s %-14s %d/%d  %s",
                isbn, title, author, genre,
                availableCopies, totalCopies, status);
    }

    @Override
    public String toString() {
        return isbn + " — " + title + " by " + author + " [" + status + "]";
    }
}
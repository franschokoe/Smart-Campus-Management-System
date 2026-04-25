package models;

/**
 * Represents a hostel room in the Smart Campus system.
 *
 * A Room is the data object stored in the HostelAllocation module.
 * It tracks its physical details, occupancy status, and which
 * student is currently assigned to it.
 *
 * Fields:
 *   roomNumber   — unique identifier,  e.g. "A101"
 *   block        — hostel block name,  e.g. "Block A"
 *   roomType     — Single / Double / Triple
 *   floor        — floor number
 *   monthlyFee   — rental cost per month
 *   isOccupied   — true if a student is currently assigned
 *   occupantId   — student ID of the current occupant (null if vacant)
 *   occupantName — name of the occupant (null if vacant)
 *   amenities    — comma-separated list, e.g. "Wi-Fi, Air-Con, En-suite"
 */
public class Room {

    // ─────────────────────────────────────────────
    //  Fields
    // ─────────────────────────────────────────────
    private String  roomNumber;
    private String  block;
    private String  roomType;      // Single | Double | Triple
    private int     floor;
    private double  monthlyFee;
    private boolean isOccupied;
    private String  occupantId;
    private String  occupantName;
    private String  amenities;

    // ─────────────────────────────────────────────
    //  Constructor
    // ─────────────────────────────────────────────
    public Room(String roomNumber,
                String block,
                String roomType,
                int    floor,
                double monthlyFee,
                String amenities) {
        this.roomNumber   = roomNumber;
        this.block        = block;
        this.roomType     = roomType;
        this.floor        = floor;
        this.monthlyFee   = monthlyFee;
        this.amenities    = amenities;
        this.isOccupied   = false;
        this.occupantId   = null;
        this.occupantName = null;
    }

    // ─────────────────────────────────────────────
    //  Getters
    // ─────────────────────────────────────────────
    public String  getRoomNumber()   { return roomNumber;   }
    public String  getBlock()        { return block;        }
    public String  getRoomType()     { return roomType;     }
    public int     getFloor()        { return floor;        }
    public double  getMonthlyFee()   { return monthlyFee;   }
    public boolean isOccupied()      { return isOccupied;   }
    public String  getOccupantId()   { return occupantId;   }
    public String  getOccupantName() { return occupantName; }
    public String  getAmenities()    { return amenities;    }
    public String  getStatus()       { return isOccupied ? "Occupied" : "Available"; }

    // ─────────────────────────────────────────────
    //  Setters
    // ─────────────────────────────────────────────
    public void setMonthlyFee(double fee)    { this.monthlyFee = fee;       }
    public void setAmenities(String amen)    { this.amenities  = amen;      }
    public void setRoomType(String type)     { this.roomType   = type;      }

    // ─────────────────────────────────────────────
    //  Occupancy management
    // ─────────────────────────────────────────────

    /**
     * Assigns a student to this room.
     * Returns false if the room is already occupied.
     */
    public boolean assign(String studentId, String studentName) {
        if (isOccupied) return false;
        this.isOccupied   = true;
        this.occupantId   = studentId;
        this.occupantName = studentName;
        return true;
    }

    /**
     * Vacates the room — clears occupant details.
     * Returns false if the room is already vacant.
     */
    public boolean vacate() {
        if (!isOccupied) return false;
        this.isOccupied   = false;
        this.occupantId   = null;
        this.occupantName = null;
        return true;
    }

    // ─────────────────────────────────────────────
    //  Display
    // ─────────────────────────────────────────────

    /** Full formatted room profile card. */
    public void display() {
        String thin = "-".repeat(52);
        System.out.println("\n" + thin);
        System.out.println("  ROOM DETAILS");
        System.out.println(thin);
        System.out.printf("  %-20s: %s%n",  "Room Number",   roomNumber);
        System.out.printf("  %-20s: %s%n",  "Block",         block);
        System.out.printf("  %-20s: %s%n",  "Room Type",     roomType);
        System.out.printf("  %-20s: Floor %d%n", "Floor",    floor);
        System.out.printf("  %-20s: R %.2f / month%n", "Monthly Fee", monthlyFee);
        System.out.printf("  %-20s: %s%n",  "Amenities",     amenities);
        System.out.printf("  %-20s: %s%n",  "Status",        getStatus());
        if (isOccupied) {
            System.out.printf("  %-20s: %s%n", "Occupant ID",   occupantId);
            System.out.printf("  %-20s: %s%n", "Occupant Name", occupantName);
        }
        System.out.println(thin);
    }

    /** One-line row for room listing tables. */
    public String toTableRow() {
        String occupant = isOccupied
                ? occupantId + " (" + occupantName + ")"
                : "—";
        return String.format("  %-6s %-10s %-8s %-6d R%-10.2f %-10s %s",
                roomNumber, block, roomType, floor,
                monthlyFee, getStatus(), occupant);
    }

    @Override
    public String toString() {
        return roomNumber + " [" + block + ", " + roomType + "] — " + getStatus();
    }
}
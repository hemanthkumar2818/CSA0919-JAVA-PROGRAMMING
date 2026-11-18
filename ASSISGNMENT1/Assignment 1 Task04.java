class Ticket {
    private final int ticketNo;
    private final String vehicleNo;
    private final int floor, slot;
    Ticket() { this(1001, "UNKNOWN", 0, 0); } // default constructor
    Ticket(int ticketNo, String vehicleNo) { this(ticketNo, vehicleNo, 0, 0); } // overloaded
    Ticket(int ticketNo, String vehicleNo, int floor, int slot) {
        this.ticketNo = ticketNo; this.vehicleNo = vehicleNo; this.floor = floor; this.slot = slot;
    }
    Ticket(Ticket other) { this(other.ticketNo, other.vehicleNo, other.floor, other.slot); } // copy constructor
    public String toString() {
        return "Ticket " + ticketNo + " | " + vehicleNo + " | Floor " + floor + " | Slot " + slot;
    }
}
public class Task04 {
    public static void main(String[] args) {
        Ticket t1 = new Ticket();
        Ticket t2 = new Ticket(1002, "TN01AB1234", 1, 4);
        Ticket t3 = new Ticket(t2);
        System.out.println(t1);
        System.out.println(t2);
        System.out.println("Copied: " + t3);
    }
}

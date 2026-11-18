class ParkingTicket {
    private static int nextNumber = 1000; // shared by all tickets
    private final int ticketNumber;
    private final String vehicleNo;
    ParkingTicket(String vehicleNo) {
        this.ticketNumber = ++nextNumber;
        this.vehicleNo = vehicleNo;
    }
    static int getTicketsIssued() { return nextNumber - 1000; }
    public String toString() { return ticketNumber + " - " + vehicleNo; }
}
public class Task05 {
    public static void main(String[] args) {
        ParkingTicket a = new ParkingTicket("TN01AA1111");
        ParkingTicket b = new ParkingTicket("TN02BB2222");
        System.out.println(a);
        System.out.println(b);
        System.out.println("Total tickets issued: " + ParkingTicket.getTicketsIssued());
    }
}

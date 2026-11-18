import java.util.*;

class Vehicle {
    String regNo, type;
    Vehicle(String regNo, String type) { this.regNo = regNo; this.type = type; }
}
class ParkingSlot {
    int floor, number;
    Vehicle vehicle;
    ParkingSlot(int floor, int number) { this.floor = floor; this.number = number; }
    boolean isFree() { return vehicle == null; }
}
class ParkingTicket {
    int ticketNo;
    Vehicle vehicle;
    long entryTime;
    ParkingTicket(int ticketNo, Vehicle vehicle) {
        this.ticketNo = ticketNo; this.vehicle = vehicle; this.entryTime = System.currentTimeMillis();
    }
}
public class Task01 {
    public static void main(String[] args) {
        Vehicle v = new Vehicle("TN01AB1234", "Car");
        ParkingSlot slot = new ParkingSlot(0, 1);
        slot.vehicle = v;
        ParkingTicket ticket = new ParkingTicket(1001, v);
        System.out.println("Vehicle: " + v.regNo + " (" + v.type + ")");
        System.out.println("Slot: Floor " + slot.floor + ", Slot " + slot.number);
        System.out.println("Ticket: " + ticket.ticketNo);
    }
}

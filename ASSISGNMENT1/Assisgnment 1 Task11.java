import java.time.*;
import java.util.*;

interface Chargeable { double chargingFee(int kWh); }
abstract class Vehicle {
    private final String regNo;
    Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("Invalid registration");
        this.regNo = regNo.toUpperCase();
    }
    String getRegNo() { return regNo; }
    abstract double ratePerHour();
    double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("Negative duration");
        return ((minutes + 59) / 60) * ratePerHour();
    }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) { super(r); }
    double ratePerHour() { return 10; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    double ratePerHour() { return 30; }
}
class ElectricCar extends Car implements Chargeable {
    ElectricCar(String r) { super(r); }
    public double chargingFee(int kWh) { return kWh * 12.0; }
}
class ParkingTicket {
    private static int next = 1000;
    final int number = ++next;
    final Vehicle vehicle;
    final LocalDateTime entry;
    ParkingTicket(Vehicle v, LocalDateTime entry) { vehicle = v; this.entry = entry; }
}
class ParkingLot {
    private final String[][] slots;
    private final Map<String, ParkingTicket> active = new HashMap<>();
    ParkingLot(int[] capacity) {
        slots = new String[capacity.length][];
        for (int i = 0; i < capacity.length; i++) slots[i] = new String[capacity[i]];
    }
    ParkingTicket enter(Vehicle v, LocalDateTime time) {
        if (active.containsKey(v.getRegNo())) throw new IllegalArgumentException("Duplicate vehicle");
        for (int f = 0; f < slots.length; f++)
            for (int s = 0; s < slots[f].length; s++)
                if (slots[f][s] == null) {
                    slots[f][s] = v.getRegNo();
                    ParkingTicket t = new ParkingTicket(v, time);
                    active.put(v.getRegNo(), t);
                    System.out.println("Allocated floor " + f + ", slot " + s + "; ticket " + t.number);
                    return t;
                }
        System.out.println("Parking full");
        return null;
    }
    double exit(String regNo, LocalDateTime time, int kWh) {
        ParkingTicket t = active.remove(regNo.toUpperCase());
        if (t == null) throw new IllegalArgumentException("Ticket not found");
        long mins = Duration.between(t.entry, time).toMinutes();
        double fee = t.vehicle.calculateFee((int) mins);
        if (t.vehicle instanceof Chargeable c) fee += c.chargingFee(kWh);
        for (String[] floor : slots)
            for (int i = 0; i < floor.length; i++)
                if (regNo.equalsIgnoreCase(floor[i])) floor[i] = null;
        return fee;
    }
}
public class Main {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(new int[]{2, 2});
        LocalDateTime in = LocalDateTime.of(2026, 11, 18, 9, 0);
        LocalDateTime out = in.plusMinutes(130);
        Vehicle bike = new TwoWheeler("TN01AA1111");
        Vehicle car = new Car("TN02BB2222");
        Vehicle ev = new ElectricCar("TN03CC3333");
        lot.enter(bike, in);
        lot.enter(car, in);
        lot.enter(ev, in);
        System.out.println("Bike fee: Rs " + lot.exit(bike.getRegNo(), out, 0));
        System.out.println("Car fee: Rs " + lot.exit(car.getRegNo(), out, 0));
        System.out.println("EV fee: Rs " + lot.exit(ev.getRegNo(), out, 10));
    }
}

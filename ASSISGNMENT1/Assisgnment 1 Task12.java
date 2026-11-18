import java.time.*;
import java.util.*;

interface Chargeable { double chargingFee(int kWh); }
interface ParkingPass { boolean isValidOn(LocalDate date); }
class OneDayPass implements ParkingPass {
    private final LocalDate validDate;
    OneDayPass(LocalDate date) { validDate = date; }
    public boolean isValidOn(LocalDate date) { return validDate.equals(date); }
}
abstract class Vehicle {
    private final String regNo;
    Vehicle(String r) { if (r == null || r.isBlank()) throw new IllegalArgumentException(); regNo = r.toUpperCase(); }
    String getRegNo() { return regNo; }
    abstract double ratePerHour();
    double fee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    double ratePerHour() { return 30; }
}
class EBike extends Vehicle implements Chargeable {
    EBike(String r) { super(r); }
    double ratePerHour() { return 0; }
    @Override double fee(int minutes) { return 25.0; } // flat parking fee
    public double chargingFee(int kWh) { return kWh * 5.0; }
}
class DeliveryVan extends Vehicle {
    DeliveryVan(String r) { super(r); }
    double ratePerHour() { return 60; } // twice car rate during 9 AM–5 PM
    double feeForPeriod(LocalDateTime in, LocalDateTime out) {
        if (out.isBefore(in)) throw new IllegalArgumentException("Invalid times");
        long minutes = Duration.between(in, out).toMinutes();
        if (minutes == 0) return 0;
        int hours = (int)((minutes + 59) / 60);
        int time = in.getHour() * 60 + in.getMinute();
        double rate = (time >= 540 && time < 1020) ? 60 : 30;
        return hours * rate;
    }
}
class ParkingLot {
    private final String[][] slots;
    ParkingLot(int[] capacity) {
        slots = new String[capacity.length][];
        for (int f = 0; f < capacity.length; f++) slots[f] = new String[capacity[f]];
    }
    int[] allocate(String regNo) {
        for (int f = 0; f < slots.length; f++)
            for (int s = 0; s < slots[f].length; s++)
                if (slots[f][s] == null) { slots[f][s] = regNo; return new int[]{f, s}; }
        return null;
    }
}
public class Main {
    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(new int[]{20, 25, 30, 35, 40}); // 5 unequal floors
        LocalDate date = LocalDate.of(2026, 11, 18);
        ParkingPass visitorPass = new OneDayPass(date);
        System.out.println("Visitor one-day pass valid: " + visitorPass.isValidOn(date));

        EBike ebike = new EBike("TN04DD4444");
        int[] eSlot = lot.allocate(ebike.getRegNo());
        System.out.println("E-bike slot: " + Arrays.toString(eSlot));
        System.out.println("E-bike flat parking + charging: Rs " + (ebike.fee(90) + ebike.chargingFee(2)));

        DeliveryVan van = new DeliveryVan("TN05EE5555");
        int[] vSlot = lot.allocate(van.getRegNo());
        LocalDateTime in = LocalDateTime.of(2026, 11, 18, 10, 0);
        System.out.println("Delivery van slot: " + Arrays.toString(vSlot));
        System.out.println("Delivery van fee: Rs " + van.feeForPeriod(in, in.plusMinutes(125)));
    }
}

interface Chargeable { double chargingFee(int kWh); }
abstract class Vehicle {
    private final String regNo;
    Vehicle(String regNo) { this.regNo = regNo; }
    String getRegNo() { return regNo; }
    abstract double ratePerHour();
    double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }
}
class TwoWheeler extends Vehicle {
    TwoWheeler(String r) { super(r); }
    @Override double ratePerHour() { return 10; }
}
class Car extends Vehicle {
    Car(String r) { super(r); }
    @Override double ratePerHour() { return 30; }
}
class ElectricCar extends Car implements Chargeable {
    ElectricCar(String r) { super(r); }
    @Override public double chargingFee(int kWh) { return kWh * 12.0; }
}
public class FeeDemo {
    public static void main(String[] args) {
        Vehicle[] parked = {new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3")};
        for (Vehicle v : parked) {
            double fee = v.calculateFee(130);
            if (v instanceof Chargeable c) fee += c.chargingFee(10);
            System.out.println(v.getClass().getSimpleName() + " " + v.getRegNo() + " -> Rs " + fee);
        }
        System.out.println(new Car("TN02B2").equals(new ElectricCar("TN02B2")));
    }
}

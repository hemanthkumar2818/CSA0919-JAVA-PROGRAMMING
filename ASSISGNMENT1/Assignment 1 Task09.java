interface Chargeable {
    double chargingFee(int units);
    default void showChargingMessage() { System.out.println("Charging facility available"); }
    static int maximumUnitsPerSession() { return 100; }
}
abstract class Vehicle {
    protected final String regNo;
    Vehicle(String regNo) { this.regNo = regNo; }
    abstract double parkingFee(int hours);
}
class ElectricCar extends Vehicle implements Chargeable {
    ElectricCar(String regNo) { super(regNo); }
    @Override double parkingFee(int hours) { return hours * 30.0; }
    @Override public double chargingFee(int units) { return units * 12.0; }
}
public class Task09 {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03CC3333");
        ev.showChargingMessage();
        System.out.println("Parking fee: Rs " + ev.parkingFee(2));
        System.out.println("Charging fee: Rs " + ev.chargingFee(5));
        System.out.println("Max charging units: " + Chargeable.maximumUnitsPerSession());
    }
}

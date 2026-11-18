class Vehicle {
    protected String regNo;
    Vehicle(String regNo) { this.regNo = regNo; }
    void show() { System.out.println("Vehicle registration: " + regNo); }
}
class Car extends Vehicle {
    Car(String regNo) { super(regNo); }
    void carFeature() { System.out.println("Car has four wheels"); }
}
class ElectricCar extends Car {
    ElectricCar(String regNo) { super(regNo); }
    void charge() { System.out.println("Electric car is charging"); }
}
public class Task06 {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03CC3333");
        ev.show();
        ev.carFeature();
        ev.charge();
    }
}

class Vehicle {
    private final String regNo;
    Vehicle(String regNo) { this.regNo = regNo; }
    double calculateFee(int hours) { return hours * 20.0; }
    @Override public String toString() { return "Vehicle[" + regNo + "]"; }
    @Override public boolean equals(Object obj) {
        return obj instanceof Vehicle && regNo.equals(((Vehicle)obj).regNo);
    }
    @Override public int hashCode() { return regNo.hashCode(); }
}
class Car extends Vehicle {
    Car(String regNo) { super(regNo); }
    @Override double calculateFee(int hours) { return hours * 30.0; }
}
public class Task07 {
    public static void main(String[] args) {
        Vehicle v = new Car("TN01AB1234");
        System.out.println("Fee: Rs " + v.calculateFee(3));
        System.out.println(v);
        System.out.println("Same registration: " +
                new Vehicle("TN01AB1234").equals(new Car("TN01AB1234")));
    }
}

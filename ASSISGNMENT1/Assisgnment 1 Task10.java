class EncapsulatedSlot {
    private int floor;
    private int slotNumber;
    private String vehicleNo;

    public EncapsulatedSlot(int floor, int slotNumber) {
        setFloor(floor);
        setSlotNumber(slotNumber);
    }
    public void setFloor(int floor) {
        if (floor < 0) throw new IllegalArgumentException("Floor cannot be negative");
        this.floor = floor;
    }
    public void setSlotNumber(int slotNumber) {
        if (slotNumber < 0) throw new IllegalArgumentException("Slot cannot be negative");
        this.slotNumber = slotNumber;
    }
    public void park(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) throw new IllegalArgumentException("Vehicle number required");
        if (this.vehicleNo != null) throw new IllegalStateException("Slot is occupied");
        this.vehicleNo = vehicleNo;
    }
    public String getVehicleNo() { return vehicleNo; }
    public String toString() { return "Floor " + floor + ", Slot " + slotNumber + ", Vehicle " + vehicleNo; }
}
public class Task10 {
    public static void main(String[] args) {
        EncapsulatedSlot slot = new EncapsulatedSlot(1, 5);
        slot.park("TN01AB1234");
        System.out.println(slot);
        Integer a = 1000; // autoboxing
        Integer b = 1000;
        System.out.println("Use equals for wrapper values: " + a.equals(b));
    }
}

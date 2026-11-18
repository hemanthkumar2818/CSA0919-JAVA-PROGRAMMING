public class Task02 {
    public static void main(String[] args) {
        // Jagged array: each floor may have a different number of slots.
        int[] slotsPerFloor = {2, 3, 1};
        String[][] slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++)
            slots[f] = new String[slotsPerFloor[f]];

        int floor = 1, slot = 2;
        String vehicleNo = "TN02CD5678";
        slots[floor][slot] = vehicleNo;

        int parkingMinutes = 95;
        double hourlyRate = 30.0;
        double billableHours = Math.ceil(parkingMinutes / 60.0);
        double fee = billableHours * hourlyRate;

        System.out.println("Vehicle at floor " + floor + ", slot " + slot + ": " + slots[floor][slot]);
        System.out.println("Parking duration: " + parkingMinutes + " minutes");
        System.out.println("Billable hours: " + (int) billableHours);
        System.out.println("Parking fee: Rs " + fee);
        // Avoid integer division: 95 / 60 is 1, but 95 / 60.0 is 1.5833...
    }
}

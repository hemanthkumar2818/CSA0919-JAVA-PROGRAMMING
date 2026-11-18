import java.util.*;

class Student {
    private final String regNo;
    private final String name;
    Student(String regNo, String name) { this.regNo=regNo; this.name=name; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other=(Student)o;
        return Objects.equals(regNo, other.regNo);
    }
    // TODO 1 completed: equal registration numbers have equal hash codes.
    @Override public int hashCode() { return Objects.hash(regNo); }

    @Override public String toString() { return regNo+" "+name; }
}
public class Attendance {
    public static void main(String[] args) {
        // Test 1: corrected implementation; duplicate registration is removed.
        Set<Student> scans=new HashSet<>();
        scans.add(new Student("192311001","Arun"));
        scans.add(new Student("192311002","Divya"));
        scans.add(new Student("192311001","Arun"));
        System.out.println("Test 1 - expected 2, actual: "+scans.size());

        // Test 2: different names, same registration number.
        Set<Student> duplicateNames=new HashSet<>();
        duplicateNames.add(new Student("REG10","A"));
        duplicateNames.add(new Student("REG10","B"));
        duplicateNames.add(new Student("REG11","C"));
        System.out.println("Test 2 - expected 2, actual: "+duplicateNames.size());

        // Test 3: immutable key fields prevent hash-bucket corruption.
        Set<Student> stable=new HashSet<>();
        stable.add(new Student("REG20","D"));
        System.out.println("Test 3 - expected 1, actual: "+stable.size());

        // Alternative: map keyed by registration number.
        Map<String,Student> byReg=new HashMap<>();
        byReg.put("REG10",new Student("REG10","A"));
        byReg.put("REG10",new Student("REG10","B"));
        System.out.println("HashMap keyed by regNo: "+byReg.size());
    }
}

import java.io.*;
import java.util.*;
import java.sql.Connection;

public class Hostel implements Serializable {
    private static final long serialVersionUID=1L;
    private final String hostelName;
    private final List<Room> rooms=new ArrayList<>();
    private transient Connection liveConnection; // live JDBC resources cannot be serialized
    public Hostel(String name){hostelName=name;}
    public void add(Room room){rooms.add(room);}
    public String toString(){return hostelName+" "+rooms;}

    public static void main(String[] args)throws Exception{
        Hostel h=new Hostel("SIMATS Hostel");
        Room r=new Room(101);r.add(new Resident("Arun","192311001","not-saved"));h.add(r);
        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("hostel.ser"))){out.writeObject(h);}
        try(ObjectInputStream in=new ObjectInputStream(new FileInputStream("hostel.ser"))){
            Hostel restored=(Hostel)in.readObject();System.out.println(restored);
        }
    }
}

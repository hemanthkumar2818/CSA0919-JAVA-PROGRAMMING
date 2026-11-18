import java.io.Serializable;

public class Resident implements Serializable {
    private static final long serialVersionUID=1L;
    private final String name;
    private final String registerNo;
    private transient String password; // do not serialize credentials
    public Resident(String name,String registerNo,String password){
        this.name=name;this.registerNo=registerNo;this.password=password;
    }
    public String toString(){return name+" ("+registerNo+")";}
}

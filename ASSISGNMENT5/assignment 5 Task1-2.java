public class Student {
    private final int id;
    private final String registerNo;
    private final String name;
    public Student(int id,String registerNo,String name){
        this.id=id;this.registerNo=registerNo;this.name=name;
    }
    public int getId(){return id;}
    public String getRegisterNo(){return registerNo;}
    public String getName(){return name;}
    public String toString(){return id+" | "+registerNo+" | "+name;}
}

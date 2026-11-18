import java.sql.*;
import java.util.*;

/** JDBC DAO implementation; configure DB_URL, DB_USER, DB_PASSWORD environment variables. */
public class StudentDao implements Dao<Student,Integer> {
    private final String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus");
    private final String user=System.getenv().getOrDefault("DB_USER","root");
    private final String password=System.getenv().getOrDefault("DB_PASSWORD","");
    private Connection connect() throws SQLException { return DriverManager.getConnection(url,user,password); }

    public void insert(Student s)throws SQLException{
        String sql="INSERT INTO students(id,reg_no,name) VALUES(?,?,?)";
        try(Connection c=connect();PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,s.getId());p.setString(2,s.getRegisterNo());p.setString(3,s.getName());p.executeUpdate();
        }
    }
    public Optional<Student> findById(Integer id)throws SQLException{
        try(Connection c=connect();PreparedStatement p=c.prepareStatement("SELECT id,reg_no,name FROM students WHERE id=?")){
            p.setInt(1,id);try(ResultSet r=p.executeQuery()){
                return r.next()?Optional.of(new Student(r.getInt("id"),r.getString("reg_no"),r.getString("name"))):Optional.empty();
            }
        }
    }
    public List<Student> findAll()throws SQLException{
        List<Student> out=new ArrayList<>();
        try(Connection c=connect();PreparedStatement p=c.prepareStatement("SELECT id,reg_no,name FROM students ORDER BY id");ResultSet r=p.executeQuery()){
            while(r.next())out.add(new Student(r.getInt("id"),r.getString("reg_no"),r.getString("name")));
        }return out;
    }
    public void update(Student s)throws SQLException{
        try(Connection c=connect();PreparedStatement p=c.prepareStatement("UPDATE students SET reg_no=?,name=? WHERE id=?")){
            p.setString(1,s.getRegisterNo());p.setString(2,s.getName());p.setInt(3,s.getId());p.executeUpdate();
        }
    }
    public void delete(Integer id)throws SQLException{
        try(Connection c=connect();PreparedStatement p=c.prepareStatement("DELETE FROM students WHERE id=?")){
            p.setInt(1,id);p.executeUpdate();
        }
    }
}

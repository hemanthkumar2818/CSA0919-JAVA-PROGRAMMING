import java.sql.*;

/** Demonstrates unsafe concatenation vs PreparedStatement. Use only a local disposable test DB. */
public class LoginInjectionDemo {
    static boolean unsafe(Connection c,String id,String pwd)throws SQLException{
        String sql="SELECT COUNT(*) FROM users WHERE id='"+id+"' AND pwd='"+pwd+"'";
        try(Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){r.next();return r.getInt(1)>0;}
    }
    static boolean safe(Connection c,String id,String pwd)throws SQLException{
        String sql="SELECT COUNT(*) FROM users WHERE id=? AND pwd=?";
        try(PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,id);p.setString(2,pwd);
            try(ResultSet r=p.executeQuery()){r.next();return r.getInt(1)>0;}
        }
    }
    public static void main(String[] args)throws Exception{
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus");
        try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),
                System.getenv().getOrDefault("DB_PASSWORD",""))){
            String id=args.length>0?args[0]:"no-such-user";
            String password=args.length>1?args[1]:"' OR '1'='1";
            System.out.println("Unsafe accepts: "+unsafe(c,id,password));
            System.out.println("PreparedStatement accepts: "+safe(c,id,password));
        }
    }
}

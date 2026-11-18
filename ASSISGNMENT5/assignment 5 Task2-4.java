import java.sql.*;
import java.util.*;

/** PreparedStatement batch loader; TODO 1-3 completed. */
public class PassLoader {
    public static int loadAll(Connection con,List<String[]> passes)throws SQLException{
        String sql="INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES(?,?,?,?,?)";
        boolean oldAutoCommit=con.getAutoCommit();con.setAutoCommit(false);
        try(PreparedStatement ps=con.prepareStatement(sql)){
            int n=0,batch=0;
            for(String[] p:passes){
                ps.setString(1,p[0]);ps.setString(2,p[1]);ps.setString(3,p[2]);
                ps.setInt(4,Integer.parseInt(p[3]));ps.setDouble(5,Double.parseDouble(p[4]));
                ps.addBatch();n++;batch++;
                if(batch==1000){ps.executeBatch();batch=0;}
            }
            if(batch>0)ps.executeBatch();
            con.commit();return n;
        }catch(SQLException|RuntimeException e){
            con.rollback();throw e;
        }finally{con.setAutoCommit(oldAutoCommit);}
    }

    static List<String[]> generate(int n){
        List<String[]> rows=new ArrayList<>(n);
        for(int i=1;i<=n;i++)rows.add(new String[]{"P"+i,"REG"+i,"Student"+i,String.valueOf(i%20+1),"4500.0"});
        return rows;
    }
    static long insertWithStatement(Connection c,List<String[]> rows)throws SQLException{
        long start=System.nanoTime();
        try(Statement s=c.createStatement()){
            for(String[] p:rows){
                String sql="INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES('"+
                    p[0]+"','"+p[1]+"','"+p[2]+"',"+p[3]+","+p[4]+")";
                s.executeUpdate(sql);
            }
        }return System.nanoTime()-start;
    }
    static long insertPrepared(Connection c,List<String[]> rows)throws SQLException{
        long start=System.nanoTime();
        try(PreparedStatement p=c.prepareStatement("INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES(?,?,?,?,?)")){
            for(String[] x:rows){p.setString(1,x[0]);p.setString(2,x[1]);p.setString(3,x[2]);p.setInt(4,Integer.parseInt(x[3]));p.setDouble(5,Double.parseDouble(x[4]));p.executeUpdate();}
        }return System.nanoTime()-start;
    }
    public static void main(String[] args)throws Exception{
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus");
        try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),System.getenv().getOrDefault("DB_PASSWORD",""))){
            for(int n:new int[]{1000,10000,100000}){
                List<String[]> rows=generate(n);
                // Use a fresh/empty table before each strategy; clear between measurements.
                try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}
                long st=insertWithStatement(c,rows);
                try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}
                long pt=insertPrepared(c,rows);
                try(Statement s=c.createStatement()){s.executeUpdate("DELETE FROM bus_pass");}
                long start=System.nanoTime();int loaded=loadAll(c,rows);long bt=System.nanoTime()-start;
                System.out.printf("n=%d | Statement %.3f ms | Prepared %.3f ms | Batch %.3f ms | batch rows=%d%n",
                    n,st/1e6,pt/1e6,bt/1e6,loaded);
            }
        }
    }
}

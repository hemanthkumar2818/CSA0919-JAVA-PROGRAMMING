import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

/** Migrates CSV passes into MySQL in batches, records invalid rows and prints JDBC metadata. */
public class PassMigration {
    static class Row {String[] fields;String error;int line;Row(String[] f,String e,int l){fields=f;error=e;line=l;}}
    static List<Row> readAndValidate(Path file)throws IOException{
        List<Row> rows=new ArrayList<>();int lineNo=0;
        try(BufferedReader r=Files.newBufferedReader(file)){
            String line;while((line=r.readLine())!=null){lineNo++;
                String[] f=line.split(",",-1);String error=null;
                if(f.length!=5)error="Expected 5 comma-separated fields";
                else try{Integer.parseInt(f[3]);Double.parseDouble(f[4]);}catch(NumberFormatException e){error="Invalid route or amount";}
                rows.add(new Row(f,error,lineNo));
            }
        }return rows;
    }
    static int migrate(Connection c,List<Row> rows)throws SQLException{
        String sql="INSERT INTO bus_pass(pass_id,reg_no,name,route_no,amount) VALUES(?,?,?,?,?)";
        boolean old=c.getAutoCommit();c.setAutoCommit(false);int total=0;
        try(PreparedStatement p=c.prepareStatement(sql)){
            int batch=0;
            for(Row row:rows){
                if(row.error!=null)continue;
                String[] f=row.fields;
                p.setString(1,f[0]);p.setString(2,f[1]);p.setString(3,f[2]);
                p.setInt(4,Integer.parseInt(f[3]));p.setDouble(5,Double.parseDouble(f[4]));
                p.addBatch();batch++;total++;
                if(batch==500){p.executeBatch();batch=0;}
            }
            if(batch>0)p.executeBatch();
            c.commit();return total;
        }catch(SQLException|RuntimeException e){c.rollback();throw e;}
        finally{c.setAutoCommit(old);}
    }
    static void printMetadata(Connection c)throws SQLException{
        DatabaseMetaData md=c.getMetaData();
        System.out.println("Database: "+md.getDatabaseProductName()+" "+md.getDatabaseProductVersion());
        System.out.println("Driver: "+md.getDriverName()+" "+md.getDriverVersion());
        try(ResultSet tables=md.getTables(c.getCatalog(),null,"%",new String[]{"TABLE"})){
            System.out.println("Tables:");
            while(tables.next())System.out.println(" - "+tables.getString("TABLE_NAME"));
        }
    }
    public static void main(String[] args)throws Exception{
        Path file=Paths.get(args.length>0?args[0]:"bus_passes.csv");
        if(!Files.exists(file)){
            Files.write(file,Arrays.asList(
                "P001,REG001,Arun,12,4500.0",
                "P002,REG002,Divya,7,3800.0",
                "P003,REG003,Invalid,route,100.0",
                "BROKEN,LINE"));
        }
        List<Row> rows=readAndValidate(file);List<Row> good=new ArrayList<>();
        for(Row r:rows){if(r.error==null)good.add(r);else System.out.println("Rejected line "+r.line+": "+r.error);}
        String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus");
        try(Connection c=DriverManager.getConnection(url,System.getenv().getOrDefault("DB_USER","root"),System.getenv().getOrDefault("DB_PASSWORD",""))){
            System.out.println("Migrated rows: "+migrate(c,good));
            printMetadata(c);
        }
    }
}

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PassStore {
    private final Path file;
    public PassStore(Path file){this.file=file;}

    public void add(String passId,String regNo,String name,int route,double amount)throws IOException{
        // CSV-safe escaping: quote fields containing comma, quote or newline.
        String line=String.join(",",csv(passId),csv(regNo),csv(name),String.valueOf(route),String.valueOf(amount));
        try(BufferedWriter w=Files.newBufferedWriter(file,StandardOpenOption.CREATE,StandardOpenOption.APPEND)){
            w.write(line);w.newLine();
        }
    }
    private static String csv(String s){
        if(s.contains(",")||s.contains("\"")||s.contains("\n"))return "\""+s.replace("\"","\"\"")+"\"";
        return s;
    }
    private static String[] parse(String line){
        List<String> fields=new ArrayList<>();StringBuilder b=new StringBuilder();boolean quoted=false;
        for(int i=0;i<line.length();i++){
            char c=line.charAt(i);
            if(c=='"'){
                if(quoted&&i+1<line.length()&&line.charAt(i+1)=='"'){b.append('"');i++;}
                else quoted=!quoted;
            }else if(c==','&&!quoted){fields.add(b.toString());b.setLength(0);}
            else b.append(c);
        }
        if(quoted)return null;
        fields.add(b.toString());return fields.toArray(new String[0]);
    }
    /** Returns five fields or null; malformed lines are skipped. */
    public String[] find(String passId)throws IOException{
        if(!Files.exists(file))return null;
        try(BufferedReader r=Files.newBufferedReader(file)){
            String line;
            while((line=r.readLine())!=null){
                String[] f=parse(line);
                if(f==null||f.length!=5)continue;
                try{Integer.parseInt(f[3]);Double.parseDouble(f[4]);}
                catch(NumberFormatException bad){continue;}
                if(f[0].equals(passId))return f;
            }
        }return null;
    }
    public List<String[]> listByRoute(int route)throws IOException{
        List<String[]> out=new ArrayList<>();if(!Files.exists(file))return out;
        try(BufferedReader r=Files.newBufferedReader(file)){
            String line;while((line=r.readLine())!=null){
                String[] f=parse(line);if(f==null||f.length!=5)continue;
                try{if(Integer.parseInt(f[3])==route)out.add(f);}catch(NumberFormatException ignored){}
            }
        }return out;
    }
    public static void main(String[] args)throws Exception{
        Path f=Files.createTempFile("bus-passes",".csv");PassStore s=new PassStore(f);
        s.add("P1001","192311001","Arun",12,4500);
        Files.write(f,Arrays.asList("P1002,DAMAGED LINE","P1004,REG,Name,not-a-route,100"),StandardOpenOption.APPEND);
        s.add("P1003","192311003","Divya",7,3800);
        System.out.println(Arrays.toString(s.find("P1003")));
        System.out.println(Arrays.toString(s.find("P1002")));
        System.out.println("Route 7 count: "+s.listByRoute(7).size());
        Files.deleteIfExists(f);
    }
}

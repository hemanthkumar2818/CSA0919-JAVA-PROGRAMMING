import java.io.*;
import java.nio.file.*;
public class BufferedLineCounter {
    static long unbuffered(Path p)throws IOException{
        long lines=0;boolean hasData=false;
        try(InputStream in=Files.newInputStream(p)){
            int b;while((b=in.read())!=-1){if(b=='\n')lines++;hasData=true;}
        }
        return lines+(hasData&&lastByteNotNewline(p)?1:0);
    }
    private static boolean lastByteNotNewline(Path p)throws IOException{
        long size=Files.size(p);if(size==0)return false;
        try(RandomAccessFile f=new RandomAccessFile(p.toFile(),"r")){f.seek(size-1);return f.read()!='\n';}
    }
    static long buffered(Path p)throws IOException{
        long lines=0;boolean hasData=false,lastNewline=false;
        try(BufferedInputStream in=new BufferedInputStream(Files.newInputStream(p))){
            int b;while((b=in.read())!=-1){hasData=true;lastNewline=b=='\n';if(lastNewline)lines++;}
        }
        return lines+(hasData&&!lastNewline?1:0);
    }
    static void measure(Path p)throws Exception{
        long t=System.nanoTime(),a=unbuffered(p);long t1=System.nanoTime();
        long b=buffered(p);long t2=System.nanoTime();
        System.out.printf("%s bytes=%d lines=%d | unbuffered=%.3f ms | buffered=%.3f ms%n",
            p.getFileName(),Files.size(p),a,(t1-t)/1e6,(t2-t1)/1e6);
        if(a!=b)throw new IllegalStateException("Counts differ: "+a+" vs "+b);
    }
    public static void main(String[] args)throws Exception{
        for(int mb:new int[]{1,10,50}){
            Path p=Files.createTempFile("attendance-"+mb+"MB",".log");
            try(BufferedWriter w=Files.newBufferedWriter(p)){
                for(int i=0;i<mb*1024*1024/30;i++)w.write("ATTENDANCE,STUDENT,2026-11-18\n");
            }
            measure(p);Files.deleteIfExists(p);
        }
    }
}

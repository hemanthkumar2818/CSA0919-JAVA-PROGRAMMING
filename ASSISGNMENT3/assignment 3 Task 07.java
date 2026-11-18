import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Concurrent and fault-tolerant result processor.
 * Creates sample score files in sample-scores/ on first run.
 * Format: studentId,score. Scores must be between 0 and 100.
 */
public class ResultProcessor {
    static class Result {
        final String file, student;
        final int score;
        final String error;
        Result(String file,String student,int score,String error){
            this.file=file;this.student=student;this.score=score;this.error=error;
        }
        boolean success(){return error==null;}
        public String toString(){return success()?student+" score="+score:file+" FAILED: "+error;}
    }

    static Callable<Result> task(Path file) {
        return () -> {
            try {
                List<String> lines=Files.readAllLines(file);
                if(lines.isEmpty()) throw new IOException("Empty score file");
                String[] parts=lines.get(0).split(",");
                if(parts.length!=2) throw new IllegalArgumentException("Expected studentId,score");
                int score=Integer.parseInt(parts[1].trim());
                if(score<0||score>100) throw new IllegalArgumentException("Score must be 0..100");
                return new Result(file.getFileName().toString(),parts[0].trim(),score,null);
            } catch(Exception e) {
                return new Result(file.getFileName().toString(),null,-1,e.getClass().getSimpleName()+": "+e.getMessage());
            }
        };
    }

    static void createSamples(Path dir) throws IOException {
        Files.createDirectories(dir);
        Files.write(dir.resolve("s001.txt"),Arrays.asList("STU001,87"));
        Files.write(dir.resolve("s002.txt"),Arrays.asList("STU002,92"));
        Files.write(dir.resolve("s003.txt"),Arrays.asList("STU003,74"));
        Files.write(dir.resolve("s004.txt"),Arrays.asList("STU004,105")); // invalid score
        Files.write(dir.resolve("s005.txt"),Arrays.asList("corrupt-data")); // malformed
        // s006.txt intentionally absent: missing-file test
        Files.write(dir.resolve("s007.txt"),Arrays.asList("STU007,66"));
        Files.write(dir.resolve("s008.txt"),Arrays.asList("STU008,91"));
    }

    static void run(int poolSize,List<Path> inputs) throws InterruptedException {
        ExecutorService pool=Executors.newFixedThreadPool(poolSize);
        ConcurrentLinkedQueue<Result> results=new ConcurrentLinkedQueue<>();
        long start=System.nanoTime();
        try {
            List<Future<Result>> futures=new ArrayList<>();
            for(Path p:inputs) futures.add(pool.submit(task(p)));
            for(Future<Result> f:futures) {
                try { results.add(f.get()); }
                catch(ExecutionException e) {
                    results.add(new Result("unknown",null,-1,"Unexpected task failure: "+e.getCause()));
                }
            }
        } finally {
            pool.shutdown();
            if(!pool.awaitTermination(1,TimeUnit.MINUTES)) pool.shutdownNow();
        }
        long elapsed=System.nanoTime()-start;
        int ok=0,failed=0;
        for(Result r:results){if(r.success())ok++;else failed++;System.out.println(r);}
        System.out.printf("Pool=%d | success=%d | failed=%d | total=%d | time=%.3f ms%n",
                poolSize,ok,failed,results.size(),elapsed/1e6);
    }

    public static void main(String[] args) throws Exception {
        Path dir=Paths.get("sample-scores");
        createSamples(dir);
        List<Path> inputs=new ArrayList<>();
        for(int i=1;i<=8;i++) inputs.add(dir.resolve(String.format("s%03d.txt",i)));
        for(int pool:new int[]{1,2,4,8}) {
            System.out.println("\n--- Running with "+pool+" worker(s) ---");
            run(pool,inputs);
        }
    }
}

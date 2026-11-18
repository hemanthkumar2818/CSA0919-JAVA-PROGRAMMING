import java.util.*;

public class TicketBenchmark {
    static final int WARMUPS = 3, RUNS = 5;

    static String buildPlus(int fields) {
        String s = "";
        for (int i = 0; i < fields; i++) s += "FIELD" + i + "=" + i + "\n";
        return s;
    }
    static String buildBuilder(int fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields; i++) sb.append("FIELD").append(i).append('=').append(i).append('\n');
        return sb.toString();
    }
    static String buildBuffer(int fields) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fields; i++) sb.append("FIELD").append(i).append('=').append(i).append('\n');
        return sb.toString();
    }
    static long time(Runnable r) {
        long start = System.nanoTime(); r.run(); return System.nanoTime() - start;
    }
    static void benchmark(String label, int tickets, int fields, int method) {
        for (int w = 0; w < WARMUPS; w++) run(tickets, fields, method);
        long total = 0;
        for (int r = 0; r < RUNS; r++) total += time(() -> run(tickets, fields, method));
        System.out.printf("%-12s tickets=%d fields=%d avg=%.3f ms%n",
                label, tickets, fields, total / (double) RUNS / 1_000_000.0);
    }
    static void run(int tickets, int fields, int method) {
        long checksum = 0;
        for (int t = 0; t < tickets; t++) {
            String s = method == 0 ? buildPlus(fields) :
                       method == 1 ? buildBuilder(fields) : buildBuffer(fields);
            checksum += s.length();
        }
        if (checksum == 0) System.out.println();
    }
    public static void main(String[] args) {
        int[][] workloads = {{1, 6}, {10000, 50}, {1000, 250}};
        for (int[] w : workloads) {
            benchmark("String +", w[0], w[1], 0);
            benchmark("StringBuilder", w[0], w[1], 1);
            benchmark("StringBuffer", w[0], w[1], 2);
            System.out.println();
        }
        System.out.println("Per-ticket text: StringBuilder (local, unsynchronized).");
        System.out.println("Shared log: StringBuffer or, preferably, synchronized/locked logging.");
        System.out.println("Intermediate String count is an estimate; JVM optimizations vary.");
    }
}

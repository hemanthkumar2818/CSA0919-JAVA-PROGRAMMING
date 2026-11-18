import java.util.*;

public class PlaylistBenchmark {
    static volatile long sink;
    static void test(int n) {
        System.out.println("\nPlaylist size: " + n);
        List<Integer> a = new ArrayList<>(), l = new LinkedList<>();
        for (int i=0;i<n;i++) { a.add(i); l.add(i); }

        measure("ArrayList append", () -> { List<Integer>x=new ArrayList<>(); for(int i=0;i<n;i++)x.add(i); });
        measure("LinkedList append", () -> { List<Integer>x=new LinkedList<>(); for(int i=0;i<n;i++)x.add(i); });
        measure("ArrayList front insert/remove", () -> { List<Integer>x=new ArrayList<>(a); for(int i=0;i<100;i++){x.add(0,-1);x.remove(0);} });
        measure("LinkedList front insert/remove", () -> { List<Integer>x=new LinkedList<>(l); for(int i=0;i<100;i++){x.add(0,-1);x.remove(0);} });
        int mid=n/2;
        measure("ArrayList middle insert/remove", () -> { List<Integer>x=new ArrayList<>(a); for(int i=0;i<100;i++){x.add(mid,-1);x.remove(mid);} });
        measure("LinkedList middle insert/remove", () -> { List<Integer>x=new LinkedList<>(l); for(int i=0;i<100;i++){x.add(mid,-1);x.remove(mid);} });
        measure("ArrayList get k", () -> { long z=0; for(int i=0;i<10000;i++)z+=a.get((i*31)%n); sink=z; });
        measure("LinkedList get k", () -> { long z=0; for(int i=0;i<10000;i++)z+=l.get((i*31)%n); sink=z; });
        measure("ArrayList iterate", () -> { long z=0; for(int v:a)z+=v; sink=z; });
        measure("LinkedList iterate", () -> { long z=0; for(int v:l)z+=v; sink=z; });
        measure("ArrayDeque ends", () -> { Deque<Integer>d=new ArrayDeque<>(); for(int i=0;i<n;i++)d.addLast(i); for(int i=0;i<100;i++){d.addFirst(-1);d.removeFirst();} });
    }
    static void measure(String name, Runnable r) {
        long start=System.nanoTime(); r.run();
        System.out.printf("%-34s %.3f ms%n", name,(System.nanoTime()-start)/1e6);
    }
    public static void main(String[] args) {
        test(1000); test(10000); test(50000);
        System.out.println("\nArrayList suits append-heavy, random-access playlists.");
        System.out.println("Use LinkedList only when node/iterator-based edits dominate; ArrayDeque suits double-ended queues.");
    }
}

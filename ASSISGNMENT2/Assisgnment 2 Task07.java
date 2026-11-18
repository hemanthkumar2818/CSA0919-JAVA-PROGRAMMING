import java.util.*;

/*
 Q7 redesign case: repeated sorting of a growing leaderboard.
 Original: sort the complete list every time a report is requested.
 Redesign: maintain a TreeSet in ranking order, so iteration is already sorted.
 A unique sequence number prevents distinct equal-ranked teams from being dropped.
 */
public class RedesignComparison {
    static class Entry {
        final String name; final int points, goalDiff; final long id;
        Entry(String n,int p,int g,long id){name=n;points=p;goalDiff=g;this.id=id;}
        public String toString(){return name+"("+points+","+goalDiff+")";}
    }
    static Comparator<Entry> ranking = Comparator
        .comparingInt((Entry e)->e.points).reversed()
        .thenComparing(Comparator.comparingInt((Entry e)->e.goalDiff).reversed())
        .thenComparing(e->e.name)
        .thenComparingLong(e->e.id);

    static List<Entry> originalReport(List<Entry> data) {
        List<Entry> copy=new ArrayList<>(data);
        copy.sort(ranking);
        return copy;
    }
    static class MaintainedLeaderboard {
        private final TreeSet<Entry> sorted=new TreeSet<>(ranking);
        void add(Entry e){sorted.add(e);}
        List<Entry> report(){return new ArrayList<>(sorted);}
    }
    static long measureOriginal(List<Entry> data,int reports) {
        long start=System.nanoTime();
        for(int i=0;i<reports;i++) originalReport(data);
        return System.nanoTime()-start;
    }
    static long measureRedesign(MaintainedLeaderboard board,int reports) {
        long start=System.nanoTime();
        for(int i=0;i<reports;i++) board.report();
        return System.nanoTime()-start;
    }
    public static void main(String[] args) {
        int n=args.length>0?Integer.parseInt(args[0]):20000;
        int reports=20;
        List<Entry> data=new ArrayList<>();
        MaintainedLeaderboard redesigned=new MaintainedLeaderboard();
        Random random=new Random(7);
        for(int i=0;i<n;i++){
            Entry e=new Entry("Team"+i,random.nextInt(100),random.nextInt(30),i);
            data.add(e); redesigned.add(e);
        }
        // Warm up both code paths
        originalReport(data); redesigned.report();
        long oldNs=measureOriginal(data,reports);
        long newNs=measureRedesign(redesigned,reports);
        System.out.printf("n=%d, reports=%d%n",n,reports);
        System.out.printf("Original repeated sort: %.3f ms%n",oldNs/1e6);
        System.out.printf("TreeSet maintained order: %.3f ms%n",newNs/1e6);
        System.out.println("Same result: "+originalReport(data).equals(redesigned.report()));
        System.out.println("Original: O(n log n) per report; redesign: O(log n) insertion, O(n) ordered traversal/copy.");
        System.out.println("Trade-off: TreeSet uses extra tree-node memory and has O(log n) insertion.");
    }
}

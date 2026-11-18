import java.util.*;

class Team {
    final String name; final int points; final int goalDiff;
    Team(String name,int points,int goalDiff){this.name=name;this.points=points;this.goalDiff=goalDiff;}
    public String toString(){return name+"("+points+","+goalDiff+")";}
}
class Leaderboard<T> {
    private final List<T> items=new ArrayList<>();
    private final Comparator<? super T> order;
    Leaderboard(Comparator<? super T> order){this.order=order;}
    void add(T item){items.add(item);}
    List<T> ranking(){
        List<T> copy=new ArrayList<>(items);
        copy.sort(order); // sorted copy; original insertion order is unchanged
        return copy;
    }
    void addAll(List<? extends T> source){items.addAll(source);}
}
public class LeaderboardDemo {
    public static void main(String[] args) {
        Comparator<Team> byRank =
            Comparator.comparingInt((Team t)->t.points).reversed()
              .thenComparing(Comparator.comparingInt((Team t)->t.goalDiff).reversed())
              .thenComparing(t->t.name);

        Leaderboard<Team> board=new Leaderboard<>(byRank);
        board.add(new Team("Panthers",9,4));
        board.add(new Team("Eagles",9,6));
        board.add(new Team("Cobras",6,1));
        board.add(new Team("Bisons",9,4));
        System.out.println(board.ranking());

        List<Team> second=new ArrayList<>(Arrays.asList(
            new Team("Zebras",12,2),new Team("Aardvarks",12,2),
            new Team("Tigers",12,5),new Team("Lions",8,9)));
        board.addAll(second);
        System.out.println("Expanded ranking: "+board.ranking());
        System.out.println("Original item count retained: "+board.ranking().size());
    }
}

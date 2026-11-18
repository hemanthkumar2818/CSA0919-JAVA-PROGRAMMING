class StationThread extends Thread {
    private final String station;
    private final int scripts;
    StationThread(String station, int scripts) { this.station=station; this.scripts=scripts; }
    @Override public void run() {
        try {
            for (int i=0;i<scripts;i++) {
                Thread.sleep(20);
            }
            System.out.println(station + " scanned " + scripts + " scripts (extends Thread)");
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
class StationTask implements Runnable {
    private final String station;
    private final int scripts;
    StationTask(String station,int scripts){this.station=station;this.scripts=scripts;}
    public void run() {
        try {
            for(int i=0;i<scripts;i++) Thread.sleep(15);
            System.out.println(station+" scanned "+scripts+" scripts (Runnable)");
        } catch(InterruptedException e){Thread.currentThread().interrupt();}
    }
}
public class ThreadStations {
    public static void main(String[] args) throws InterruptedException {
        Thread[] stations = new Thread[4];
        for(int i=0;i<4;i++){
            stations[i]=new StationThread("Station-"+(i+1),25*(i+1));
            stations[i].start();
            System.out.println(stations[i].getName()+" alive? "+stations[i].isAlive());
        }
        for(Thread t:stations) t.join();
        System.out.println("All Thread-based stations completed.");

        Thread[] runnableStations=new Thread[4];
        for(int i=0;i<4;i++){
            runnableStations[i]=new Thread(new StationTask("Station-"+(i+1),25*(i+1)));
            runnableStations[i].start();
        }
        for(Thread t:runnableStations)t.join();
        System.out.println("All Runnable-based stations completed.");
    }
}

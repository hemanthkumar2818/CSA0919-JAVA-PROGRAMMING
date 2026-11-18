import java.util.*;

public class PharmacyInventory {
    static class Medicine {
        final String code, name; int stock;
        Medicine(String code,String name,int stock){this.code=code;this.name=name;this.stock=stock;}
        public String toString(){return code+" "+name+" stock="+stock;}
    }
    static void run(int n) {
        Map<String,Medicine> hash=new HashMap<>();
        Map<String,Medicine> tree=new TreeMap<>();
        Map<String,Medicine> linked=new LinkedHashMap<>();
        for(int i=n-1;i>=0;i--){
            Medicine m=new Medicine(String.format("MED%06d",i),"Medicine"+i,100+i);
            hash.put(m.code,m); tree.put(m.code,m); linked.put(m.code,m);
        }
        String target=String.format("MED%06d",n/2);
        long start=System.nanoTime();
        for(int i=0;i<100000;i++) hash.get(target);
        long hashLookup=System.nanoTime()-start;
        start=System.nanoTime();
        for(int i=0;i<100000;i++) tree.get(target);
        long treeLookup=System.nanoTime()-start;
        start=System.nanoTime();
        for(int i=0;i<100000;i++) linked.get(target);
        long linkedLookup=System.nanoTime()-start;

        System.out.println("\nMedicines="+n);
        System.out.printf("100k get: HashMap %.3f ms | TreeMap %.3f ms | LinkedHashMap %.3f ms%n",
                hashLookup/1e6,treeLookup/1e6,linkedLookup/1e6);
        start=System.nanoTime(); for(Medicine m:tree.values()){} long codeReport=System.nanoTime()-start;
        start=System.nanoTime(); for(Medicine m:linked.values()){} long insertionReport=System.nanoTime()-start;
        System.out.printf("Code-order TreeMap report %.3f ms | insertion-order LinkedHashMap report %.3f ms%n",
                codeReport/1e6,insertionReport/1e6);
        System.out.println("First code in TreeMap: "+tree.keySet().iterator().next());
        System.out.println("First inserted in LinkedHashMap: "+linked.keySet().iterator().next());
    }
    public static void main(String[] args) {
        run(1000); run(10000); run(50000);
        System.out.println("\nRecommendation: HashMap for fastest average lookup; TreeMap for sorted reports; LinkedHashMap for insertion order.");
    }
}

import java.util.concurrent.atomic.AtomicLong;

class Wallet {
    final int id;
    private long balance;
    Wallet(int id,long balance){this.id=id;this.balance=balance;}
    synchronized long balance(){return balance;}
    void debit(long amount){balance-=amount;}
    void credit(long amount){balance+=amount;}
}
public class WalletTransfer {
    // Lock both wallets in stable ID order to prevent circular wait.
    static void transfer(Wallet from, Wallet to, long amount) {
        Wallet first = from.id < to.id ? from : to;
        Wallet second = from.id < to.id ? to : from;
        synchronized(first) {
            synchronized(second) {
                if (from.balance() >= amount) {
                    from.debit(amount);
                    to.credit(amount);
                }
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Wallet a=new Wallet(1,10000), b=new Wallet(2,10000);
        Thread t1=new Thread(()->{for(int i=0;i<10000;i++)transfer(a,b,1);});
        Thread t2=new Thread(()->{for(int i=0;i<10000;i++)transfer(b,a,1);});
        t1.start();t2.start();t1.join();t2.join();
        System.out.println("Wallet A: "+a.balance()+", Wallet B: "+b.balance());
        System.out.println("Total: "+(a.balance()+b.balance()));
    }
}

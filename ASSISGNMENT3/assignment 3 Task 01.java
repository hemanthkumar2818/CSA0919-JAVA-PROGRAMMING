import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

class InvalidWithdrawalException extends Exception {
    InvalidWithdrawalException(String message) { super(message); }
}
class InsufficientFundsException extends Exception {
    InsufficientFundsException(String message) { super(message); }
}
public class ATM {
    private static final double BALANCE = 5000.0;

    static double withdraw(String account, double amount, double balance)
            throws InvalidWithdrawalException, InsufficientFundsException {
        if (account == null || !account.matches("\\d{8,16}"))
            throw new InvalidWithdrawalException("Account number must contain 8-16 digits");
        if (amount <= 0) throw new InvalidWithdrawalException("Amount must be positive");
        if (amount % 100 != 0) throw new InvalidWithdrawalException("Enter amount in multiples of 100");
        if (amount > balance) throw new InsufficientFundsException("Insufficient account balance");
        return balance - amount;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Account number: ");
            String account = sc.nextLine().trim();
            System.out.print("Withdrawal amount: ");
            double amount = Double.parseDouble(sc.nextLine().trim());

            // Optional card-reader file check: pass a filename as the first command-line argument.
            if (args.length > 0) {
                Path cardFile = Paths.get(args[0]);
                if (!Files.exists(cardFile)) throw new IOException("Card-reader file not found: " + cardFile);
            }
            double remaining = withdraw(account, amount, BALANCE);
            System.out.printf("Withdrawal successful. Remaining balance: %.2f%n", remaining);
        } catch (NumberFormatException | InputMismatchException e) {
            System.out.println("Invalid input: enter a numeric withdrawal amount.");
        } catch (InvalidWithdrawalException | InsufficientFundsException e) {
            System.out.println("Transaction declined: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Device/file error: " + e.getMessage());
        } finally {
            System.out.println("ATM session closed.");
            sc.close();
        }
    }
}

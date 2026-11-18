import java.sql.*;

public class WalletTransaction {
    public static void transfer(Connection con,int fromId,int toId,double amount)throws SQLException{
        if(amount<=0)throw new IllegalArgumentException("Amount must be positive");
        boolean old=con.getAutoCommit();con.setAutoCommit(false);
        try{
            try(PreparedStatement debit=con.prepareStatement("UPDATE wallet SET balance=balance-? WHERE wallet_id=? AND balance>=?");
                PreparedStatement credit=con.prepareStatement("UPDATE wallet SET balance=balance+? WHERE wallet_id=?")){
                debit.setDouble(1,amount);debit.setInt(2,fromId);debit.setDouble(3,amount);
                if(debit.executeUpdate()!=1)throw new SQLException("Insufficient funds or source wallet missing");
                credit.setDouble(1,amount);credit.setInt(2,toId);
                if(credit.executeUpdate()!=1)throw new SQLException("Destination wallet missing");
            }
            con.commit();
        }catch(SQLException|RuntimeException e){con.rollback();throw e;}
        finally{con.setAutoCommit(old);}
    }
}

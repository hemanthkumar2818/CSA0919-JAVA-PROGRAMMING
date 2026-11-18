import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/** UI gathers input; FeeService owns validation and JDBC payment logic. */
class FeeService {
    private final String url, user, password;
    FeeService(String url,String user,String password){this.url=url;this.user=user;this.password=password;}

    String pay(String regNo,String amountText) throws Exception {
        if(regNo==null || !regNo.trim().matches("[A-Za-z0-9]{4,20}"))
            throw new IllegalArgumentException("Enter a valid registration number.");
        double amount;
        try { amount=Double.parseDouble(amountText.trim()); }
        catch(Exception e){throw new IllegalArgumentException("Enter a valid amount.");}
        if(amount<=0 || Double.isInfinite(amount) || Double.isNaN(amount))
            throw new IllegalArgumentException("Amount must be greater than zero.");

        String sql="INSERT INTO fees(reg_no, amount, paid_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try(Connection c=DriverManager.getConnection(url,user,password);
            PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,regNo.trim()); ps.setDouble(2,amount);
            ps.executeUpdate();
        }
        return String.format("Payment of %.2f recorded for %s.",amount,regNo.trim());
    }

    String receipt(String regNo) throws SQLException {
        String sql="SELECT reg_no, amount, paid_at FROM fees WHERE reg_no=? ORDER BY paid_at DESC LIMIT 1";
        try(Connection c=DriverManager.getConnection(url,user,password);
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,regNo.trim());
            try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()) return "No receipt found.";
                return "Receipt\\nRegistration: "+rs.getString("reg_no")+
                       "\\nAmount: "+rs.getDouble("amount")+"\\nPaid at: "+rs.getTimestamp("paid_at");
            }
        }
    }
}
public class FeeWindow extends JFrame {
    private final JTextField regNo=new JTextField(15), amount=new JTextField(10);
    private final JLabel status=new JLabel("Enter fee details.");
    private final FeeService service=new FeeService(
        System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus"),
        System.getenv().getOrDefault("DB_USER","root"),
        System.getenv().getOrDefault("DB_PASSWORD",""));

    public FeeWindow(){
        super("Student Fee Payment");
        setLayout(new BorderLayout(8,8));
        JPanel form=new JPanel(new GridLayout(2,2,6,6));
        form.add(new JLabel("Registration number")); form.add(regNo);
        form.add(new JLabel("Amount")); form.add(amount);
        JPanel actions=new JPanel(new FlowLayout());
        JButton pay=new JButton("Pay"), clear=new JButton("Clear"), receipt=new JButton("Receipt");
        actions.add(pay); actions.add(clear); actions.add(receipt);
        add(form,BorderLayout.CENTER); add(actions,BorderLayout.SOUTH); add(status,BorderLayout.NORTH);
        pay.addActionListener(e -> payFee());
        clear.addActionListener(e -> {regNo.setText("");amount.setText("");status.setText("Form cleared.");});
        receipt.addActionListener(e -> showReceipt());
        addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){dispose();}});
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); pack(); setLocationRelativeTo(null);
    }
    private void payFee(){
        String r=regNo.getText(), a=amount.getText();
        setBusy(true);
        SwingWorker<String,Void> worker=new SwingWorker<String,Void>(){
            protected String doInBackground() throws Exception{return service.pay(r,a);}
            protected void done(){setBusy(false);try{status.setText(get());}catch(Exception ex){status.setText("Payment failed.");JOptionPane.showMessageDialog(FeeWindow.this,ex.getCause()==null?ex.getMessage():ex.getCause().getMessage(),"Payment error",JOptionPane.ERROR_MESSAGE);}}
        }; worker.execute();
    }
    private void showReceipt(){
        try { JOptionPane.showMessageDialog(this,service.receipt(regNo.getText()),"Receipt",JOptionPane.INFORMATION_MESSAGE); }
        catch(SQLException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Database error",JOptionPane.ERROR_MESSAGE);}
    }
    private void setBusy(boolean b){setCursor(Cursor.getPredefinedCursor(b?Cursor.WAIT_CURSOR:Cursor.DEFAULT_CURSOR));}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new FeeWindow().setVisible(true));}
}

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;
import java.text.DecimalFormat;

/** Complete-the-code lambda implementation: one ActionListener serves button and Enter. */
public class Converter extends JFrame {
    private final JTextField amount=new JTextField(10);
    private final JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"});
    private final JLabel result=new JLabel(" ");
    private static double rate(String currency){return currency.equals("USD")?0.012:0.011;}
    public Converter(){
        super("Currency Converter");setLayout(new FlowLayout());
        JButton convertButton=new JButton("Convert");
        add(new JLabel("INR"));add(amount);add(target);add(convertButton);add(result);
        // TODO 1 completed: same lambda action for both event sources.
        ActionListener convertAction=e->convert();
        convertButton.addActionListener(convertAction);
        amount.addActionListener(convertAction);
        addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){dispose();}});
        setDefaultCloseOperation(EXIT_ON_CLOSE);pack();setLocationRelativeTo(null);
    }
    private void convert(){
        // TODO 2 completed: parse, format to two decimals, and report invalid input.
        try{
            double inr=Double.parseDouble(amount.getText().trim());
            if(inr<0||Double.isNaN(inr)||Double.isInfinite(inr))throw new NumberFormatException();
            String converted=new DecimalFormat("0.00").format(inr*rate((String)target.getSelectedItem()));
            result.setText(converted+" "+target.getSelectedItem());
            JOptionPane.showMessageDialog(this,result.getText(),"Conversion result",JOptionPane.INFORMATION_MESSAGE);
        }catch(NumberFormatException ex){
            result.setText("Invalid amount");
            JOptionPane.showMessageDialog(this,"Invalid amount","Input error",JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->new Converter().setVisible(true));}
}

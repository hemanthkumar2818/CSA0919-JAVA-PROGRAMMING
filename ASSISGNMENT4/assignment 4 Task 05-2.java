import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Three additional styles alongside the lambda version in Converter.java. */
public class ConverterStyles {
    static abstract class Base extends JFrame {
        final JTextField amount=new JTextField(10);
        final JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"});
        final JLabel result=new JLabel(" ");
        final JButton button=new JButton("Convert");
        Base(String title){
            super(title);setLayout(new FlowLayout());
            add(new JLabel("INR"));add(amount);add(target);add(button);add(result);
            addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
            setDefaultCloseOperation(EXIT_ON_CLOSE);pack();
        }
        void convert(ActionEvent e){
            try{
                double x=Double.parseDouble(amount.getText().trim());
                if(x<0)throw new NumberFormatException();
                double rate="USD".equals(target.getSelectedItem())?0.012:0.011;
                result.setText(String.format("%.2f %s",x*rate,target.getSelectedItem()));
                JOptionPane.showMessageDialog(this,result.getText());
            }catch(NumberFormatException ex){result.setText("Invalid amount");JOptionPane.showMessageDialog(this,"Invalid amount");}
        }
    }
    // (a) Frame itself implements ActionListener.
    static class FrameListener extends JFrame implements ActionListener {
        JTextField amount=new JTextField(10); JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"}); JLabel result=new JLabel();
        FrameListener(){
            super("Frame implements ActionListener");setLayout(new FlowLayout());
            JButton b=new JButton("Convert");add(amount);add(target);add(b);add(result);
            b.addActionListener(this);amount.addActionListener(this);
            addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){dispose();}});
            setDefaultCloseOperation(EXIT_ON_CLOSE);pack();
        }
        public void actionPerformed(ActionEvent e){calculate();}
        void calculate(){try{double x=Double.parseDouble(amount.getText());double r="USD".equals(target.getSelectedItem())?.012:.011;result.setText(String.format("%.2f",x*r));}catch(Exception ex){result.setText("Invalid amount");}}
    }
    // (b) Named inner class.
    static class NamedInner extends Base {
        NamedInner(){super("Named inner class");button.addActionListener(new NamedHandler());amount.addActionListener(new NamedHandler());}
        class NamedHandler implements ActionListener {public void actionPerformed(ActionEvent e){convert(e);}}
    }
    // (c) Anonymous inner class.
    static class Anonymous extends Base {
        Anonymous(){super("Anonymous inner class");ActionListener handler=new ActionListener(){public void actionPerformed(ActionEvent e){convert(e);}};button.addActionListener(handler);amount.addActionListener(handler);}
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            JFrame chooser=new JFrame("Choose event handling style");
            chooser.setLayout(new GridLayout(3,1));
            JButton a=new JButton("Frame listener"),b=new JButton("Named inner"),c=new JButton("Anonymous");
            a.addActionListener(e->new FrameListener().setVisible(true));
            b.addActionListener(e->new NamedInner().setVisible(true));
            c.addActionListener(e->new Anonymous().setVisible(true));
            chooser.add(a);chooser.add(b);chooser.add(c);chooser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);chooser.pack();chooser.setVisible(true);
        });
    }
}

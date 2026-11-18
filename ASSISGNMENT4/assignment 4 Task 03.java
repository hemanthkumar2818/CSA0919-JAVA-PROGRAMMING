import javax.swing.*;
import java.awt.*;
import java.util.*;
public class LibrarySearchScreen extends JFrame {
    private final JTextField query=new JTextField(22);
    private final JComboBox<String> category=new JComboBox<>(new String[]{"All","Books","Journals","Theses"});
    private final DefaultListModel<String> results=new DefaultListModel<>();
    private final JList<String> resultList=new JList<>(results);
    private final JLabel status=new JLabel("Enter a title or keyword.");
    private int page=1;
    public LibrarySearchScreen(){
        super("Campus Library Search");
        setLayout(new BorderLayout(8,8));
        JLabel title=new JLabel("Campus Library",SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD,20f));
        JPanel search=new JPanel(new FlowLayout(FlowLayout.LEFT));
        search.add(new JLabel("Search"));search.add(query);search.add(category);
        JButton searchButton=new JButton("Search");search.add(searchButton);
        JPanel north=new JPanel(new BorderLayout());north.add(title,BorderLayout.NORTH);north.add(search,BorderLayout.SOUTH);
        JPanel center=new JPanel(new BorderLayout());center.add(new JScrollPane(resultList),BorderLayout.CENTER);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prev=new JButton("Prev"),next=new JButton("Next"),reserve=new JButton("Reserve");
        controls.add(prev);controls.add(next);controls.add(reserve);
        JPanel south=new JPanel(new BorderLayout());south.add(controls,BorderLayout.NORTH);south.add(status,BorderLayout.SOUTH);
        add(north,BorderLayout.NORTH);add(center,BorderLayout.CENTER);add(south,BorderLayout.SOUTH);
        searchButton.addActionListener(e->search());
        query.addActionListener(e->search());
        prev.addActionListener(e->{page=Math.max(1,page-1);search();});
        next.addActionListener(e->{page++;search();});
        reserve.addActionListener(e->{String selected=resultList.getSelectedValue();status.setText(selected==null?"Select a result first.":"Reservation requested: "+selected);});
        setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(650,420);setLocationRelativeTo(null);
    }
    private void search(){
        results.clear();
        String q=query.getText().trim();
        if(q.isEmpty()){status.setText("Please enter a search term.");return;}
        for(int i=1;i<=8;i++)results.addElement(category.getSelectedItem()+" result "+i+" for '"+q+"'");
        status.setText("Page "+page+" | "+results.size()+" results (sample data)");
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->new LibrarySearchScreen().setVisible(true));}
}

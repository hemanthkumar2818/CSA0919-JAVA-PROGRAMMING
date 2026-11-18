import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * Layered Swing/JDBC sample. Configure DB_URL, DB_USER, DB_PASSWORD.
 * MySQL Connector/J must be on the runtime classpath.
 */
public class EventRegistrationApp extends JFrame {
    private final RegistrationService service=new RegistrationService(new RegistrationDao());
    private final JTextField studentId=new JTextField(12), studentName=new JTextField(16), email=new JTextField(18);
    private final JComboBox<String> event=new JComboBox<>(new String[]{"Cultural Fest","Tech Symposium","Sports Meet"});
    private final JComboBox<String> filter=new JComboBox<>(new String[]{"All","Cultural Fest","Tech Symposium","Sports Meet"});
    private final DefaultTableModel model=new DefaultTableModel(new String[]{"ID","Student ID","Name","Email","Event","Registered At"},0){
        public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable table=new JTable(model);
    private int offset=0; private static final int PAGE_SIZE=50;

    public EventRegistrationApp(){
        super("Campus Event Registration");
        setLayout(new BorderLayout(8,8));
        JPanel form=new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Student ID"));form.add(studentId);
        form.add(new JLabel("Name"));form.add(studentName);
        form.add(new JLabel("Email"));form.add(email);
        form.add(new JLabel("Event"));form.add(event);
        JButton register=new JButton("Register");form.add(register);
        JPanel top=new JPanel(new BorderLayout());top.add(buildMenu(),BorderLayout.NORTH);top.add(form,BorderLayout.SOUTH);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Filter"));controls.add(filter);
        JButton apply=new JButton("Apply Filter"),prev=new JButton("Previous"),next=new JButton("Next"),export=new JButton("Export CSV");
        controls.add(apply);controls.add(prev);controls.add(next);controls.add(export);
        add(top,BorderLayout.NORTH);add(new JScrollPane(table),BorderLayout.CENTER);add(controls,BorderLayout.SOUTH);
        register.addActionListener(e->register());apply.addActionListener(e->{offset=0;loadPage();});
        filter.addActionListener(e->{offset=0;});
        prev.addActionListener(e->{offset=Math.max(0,offset-PAGE_SIZE);loadPage();});
        next.addActionListener(e->{offset+=PAGE_SIZE;loadPage();});
        export.addActionListener(e->exportCsv());
        setDefaultCloseOperation(EXIT_ON_CLOSE);setSize(1100,550);setLocationRelativeTo(null);
        loadPage();
    }
    private JMenuBar buildMenu(){
        JMenuBar bar=new JMenuBar();JMenu file=new JMenu("File"),help=new JMenu("Help");
        JMenuItem refresh=new JMenuItem("Refresh"),exit=new JMenuItem("Exit"),about=new JMenuItem("About");
        refresh.addActionListener(e->{offset=0;loadPage();});exit.addActionListener(e->dispose());
        about.addActionListener(e->JOptionPane.showMessageDialog(this,"Campus Event Registration – CO4"));
        file.add(refresh);file.add(exit);help.add(about);bar.add(file);bar.add(help);return bar;
    }
    private void register(){
        String sid=studentId.getText().trim(),name=studentName.getText().trim(),mail=email.getText().trim(),ev=(String)event.getSelectedItem();
        if(!sid.matches("[A-Za-z0-9_-]{3,30}")||name.isEmpty()||!mail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")){
            JOptionPane.showMessageDialog(this,"Enter valid student ID, name and email.","Validation",JOptionPane.WARNING_MESSAGE);return;
        }
        setControlsEnabled(false);
        new SwingWorker<Void,Void>(){
            protected Void doInBackground() throws Exception{service.register(sid,name,mail,ev);return null;}
            protected void done(){setControlsEnabled(true);try{get();JOptionPane.showMessageDialog(EventRegistrationApp.this,"Registration successful.");offset=0;loadPage();}catch(Exception ex){Throwable c=ex.getCause();String msg=c==null?ex.getMessage():c.getMessage();JOptionPane.showMessageDialog(EventRegistrationApp.this,msg,"Registration failed",JOptionPane.ERROR_MESSAGE);}}
        }.execute();
    }
    private void loadPage(){
        String selected=(String)filter.getSelectedItem();
        new SwingWorker<List<Registration>,Void>(){
            protected List<Registration> doInBackground() throws Exception{return service.list(selected, PAGE_SIZE, offset);}
            protected void done(){try{model.setRowCount(0);for(Registration r:get())model.addRow(new Object[]{r.id,r.studentId,r.name,r.email,r.event,r.registeredAt});}catch(Exception ex){JOptionPane.showMessageDialog(EventRegistrationApp.this,"Could not load data: "+ex.getMessage(),"Database",JOptionPane.ERROR_MESSAGE);}}
        }.execute();
    }
    private void exportCsv(){
        JFileChooser chooser=new JFileChooser();if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        try(PrintWriter out=new PrintWriter(chooser.getSelectedFile())){
            for(int c=0;c<model.getColumnCount();c++){if(c>0)out.print(",");out.print(model.getColumnName(c));}out.println();
            for(int r=0;r<model.getRowCount();r++){for(int c=0;c<model.getColumnCount();c++){if(c>0)out.print(",");out.print("\""+String.valueOf(model.getValueAt(r,c)).replace("\"","\"\"")+"\"");}out.println();}
        }catch(IOException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Export error",JOptionPane.ERROR_MESSAGE);}
    }
    private void setControlsEnabled(boolean enabled){studentId.setEnabled(enabled);studentName.setEnabled(enabled);email.setEnabled(enabled);event.setEnabled(enabled);}
    public static void main(String[] args){SwingUtilities.invokeLater(()->new EventRegistrationApp().setVisible(true));}
}
class Registration {
    final long id;final String studentId,name,email,event,registeredAt;
    Registration(long id,String s,String n,String e,String ev,String at){this.id=id;studentId=s;name=n;email=e;event=ev;registeredAt=at;}
}
class RegistrationService {
    private final RegistrationDao dao;
    RegistrationService(RegistrationDao dao){this.dao=dao;}
    void register(String sid,String name,String email,String event)throws SQLException{
        if(sid==null||sid.trim().isEmpty()||name==null||name.trim().isEmpty())throw new IllegalArgumentException("Student ID and name are required.");
        dao.insert(sid,name,email,event);
    }
    List<Registration> list(String event,int limit,int offset)throws SQLException{return dao.find(event,limit,offset);}
}
class RegistrationDao {
    private final String url=System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus");
    private final String user=System.getenv().getOrDefault("DB_USER","root");
    private final String password=System.getenv().getOrDefault("DB_PASSWORD","");
    private Connection connect()throws SQLException{return DriverManager.getConnection(url,user,password);}
    void insert(String sid,String name,String email,String event)throws SQLException{
        String sql="INSERT INTO event_registrations(student_id,student_name,email,event_name) VALUES(?,?,?,?)";
        try(Connection c=connect();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,sid);p.setString(2,name);p.setString(3,email);p.setString(4,event);p.executeUpdate();
        }catch(SQLIntegrityConstraintViolationException ex){throw new SQLException("This student is already registered for this event.",ex);}
    }
    List<Registration> find(String event,int limit,int offset)throws SQLException{
        boolean all="All".equals(event);
        String sql="SELECT id,student_id,student_name,email,event_name,registered_at FROM event_registrations "+
                   (all?"":"WHERE event_name=? ")+"ORDER BY id DESC LIMIT ? OFFSET ?";
        try(Connection c=connect();PreparedStatement p=c.prepareStatement(sql)){
            int i=1;if(!all)p.setString(i++,event);p.setInt(i++,limit);p.setInt(i,offset);
            try(ResultSet rs=p.executeQuery()){
                List<Registration> out=new ArrayList<>();
                while(rs.next())out.add(new Registration(rs.getLong("id"),rs.getString("student_id"),rs.getString("student_name"),rs.getString("email"),rs.getString("event_name"),String.valueOf(rs.getTimestamp("registered_at"))));
                return out;
            }
        }
    }
}

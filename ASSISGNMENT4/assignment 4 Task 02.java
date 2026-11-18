import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/** Demonstrates department -> doctor dependency, appointment validation and close confirmation. */
public class AppointmentDesk extends JFrame {
    private final JTextField patientId=new JTextField(12);
    private final JComboBox<String> department=new JComboBox<>(new String[]{"Cardiology","Neurology","Orthopaedics"});
    private final JComboBox<String> doctor=new JComboBox<>();
    private final JComboBox<String> slot=new JComboBox<>(new String[]{"09:00","09:30","10:00","10:30","11:00"});
    private final JLabel status=new JLabel("Ready");
    private boolean bookingInProgress=false;
    private final Map<String,java.util.List<String>> doctors=new LinkedHashMap<>();

    public AppointmentDesk(){
        super("Hospital Outpatient Appointment Desk");
        doctors.put("Cardiology",Arrays.asList("Dr. Anand","Dr. Meera"));
        doctors.put("Neurology",Arrays.asList("Dr. Kumar","Dr. Priya"));
        doctors.put("Orthopaedics",Arrays.asList("Dr. Ravi","Dr. Latha"));
        setLayout(new BorderLayout(8,8));
        JPanel form=new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        form.add(new JLabel("Patient ID"));form.add(patientId);
        form.add(new JLabel("Department"));form.add(department);
        form.add(new JLabel("Doctor"));form.add(doctor);
        form.add(new JLabel("Time slot"));form.add(slot);
        JButton book=new JButton("Book Appointment");
        JPanel bottom=new JPanel(new BorderLayout());bottom.add(book,BorderLayout.WEST);bottom.add(status,BorderLayout.CENTER);
        add(form,BorderLayout.CENTER);add(bottom,BorderLayout.SOUTH);
        department.addActionListener(e->refreshDoctors());
        book.addActionListener(e->bookAppointment());
        addWindowListener(new WindowAdapter(){
            @Override public void windowClosing(WindowEvent e){
                if(bookingInProgress){
                    int answer=JOptionPane.showConfirmDialog(AppointmentDesk.this,
                        "A booking is in progress. Close anyway?","Confirm close",JOptionPane.YES_NO_OPTION);
                    if(answer!=JOptionPane.YES_OPTION)return;
                }
                dispose();
            }
        });
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        refreshDoctors();pack();setLocationRelativeTo(null);
    }
    private void refreshDoctors(){
        doctor.removeAllItems();
        for(String d:doctors.getOrDefault((String)department.getSelectedItem(),Collections.emptyList()))doctor.addItem(d);
    }
    private void bookAppointment(){
        String id=patientId.getText().trim();
        if(!id.matches("[A-Za-z0-9-]{3,20}")){status.setText("Enter a valid patient ID.");return;}
        if(doctor.getSelectedItem()==null || slot.getSelectedItem()==null){status.setText("Choose doctor and slot.");return;}
        bookingInProgress=true;
        String message="Appointment confirmed: "+id+" | "+department.getSelectedItem()+" | "+doctor.getSelectedItem()+" | "+slot.getSelectedItem();
        JOptionPane.showMessageDialog(this,message,"Booking confirmation",JOptionPane.INFORMATION_MESSAGE);
        status.setText("Booking complete.");bookingInProgress=false;
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->new AppointmentDesk().setVisible(true));}
}

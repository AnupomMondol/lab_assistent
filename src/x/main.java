/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Anupom
 */
package x;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import lab_assist.Lab_assist;
import java.sql.DriverManager;
import javax.swing.*;
import java.awt.event.*;
    import java.io.*;
    import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.proteanit.sql.DbUtils;
import javax.imageio.ImageIO;

public class main extends javax.swing.JFrame {

   Connection m_Connection = null;
  Statement m_Statement = null;
  ResultSet m_ResultSet = null;
  PreparedStatement pst = null;
  int countDevice = 0 ; // counting device on LAN
  int loginFlag = 0;

   String m_Driver ="com.mysql.jdbc.Driver";
   String m_Url = "jdbc:mysql://localhost:3306/lab_pc";
   
        
        int col_PCID = 0;
        int col_lab_name = 1;
        int col_pcname = 2;
        int col_ip = 3;
        int col_mac = 4;
        int col_status = 5;
        String ip = null ;
        
        ArrayList<String> name_list = new ArrayList<String>(); // declearing LIST
          ArrayList<String> ip_list = new ArrayList<String>();
          ArrayList<String> mac_list = new ArrayList<String>();
          ArrayList<String> PCID = new ArrayList<String>();
          ArrayList<String> lab_name_list = new ArrayList<String>();
    private Component frm;
    private int LabNotAssign=0;
   
    public main()   {
        try {
    UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
} catch (Exception e) {
    e.printStackTrace();
}
            initComponents();
            setTitle("Assist the LAB");
         
        try {
            setIcon();
        } catch (MalformedURLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
            
            adminFrame.setVisible(false);
            adminFrame.setTitle("Admin Panel");
            adminFrame.getRootPane().setDefaultButton(login_button);
            admin_panel.setVisible(false);
            edit_frame.setVisible(false);
            edit_frame.setTitle("Edit PC Details ");
            details_table_frame.setTitle("Computer Information");
            delete_pcdetails_frame.setTitle("Delete a row");
            delete_pcdetails_frame.setVisible(false);
            set_labname_frame.setTitle("Assign LAB name ");
            set_labname_frame.setVisible(false);
            temp_frame.setVisible(false);
            temp_frame.setTitle("LAB VIEW");
            temp_frame.getRootPane().setDefaultButton(ButtonOkLabveiw);
            progressbarFrame.setTitle("Progess in Search");
            progressbarFrame.setVisible(false);
            
            getIPFrame.setVisible(false);
            pcWithProbFrame.setVisible(false);
            pcWithProbFrame.setTitle("PC with Problem ");
            
            table_update();  
            
            fillcomboInEditDetails();
            fillcomboInDeletePcDetails();
            fillcomboInAssignLab();
           
    }
    
    private void setIcon() throws MalformedURLException, IOException{
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.png")));
    }
    private void table_update() {  
          
          int row_pcname = 0;
          int row_ip = 0;
          int row_mac = 0;
          int row_status = 0;
          int row_lab = 0;
          String mac_db = null;
          String mac_system = null;
          String query = null;
          int ifEmptyDB = 0;
        try {
    Class.forName(m_Driver);
          }
          catch (ClassNotFoundException ex) {
        ex.printStackTrace();
         }
        try {
        m_Connection = DriverManager.getConnection(m_Url, "root", "");
     
          query = "SELECT `pc_name`,`ip`, `mac`,`id` from `pc_details`";   
        
         
        m_Statement = m_Connection.createStatement();
        m_ResultSet = m_Statement.executeQuery(query);
        while (m_ResultSet.next()) {
                
            String namecol = m_ResultSet.getString(1);//retrieve data from database , whole column as a string 
            name_list.add(namecol);                  // string is departed by a list
            String ipcol = m_ResultSet.getString(2); // same as above
            ip_list.add(ipcol);
            String maccol = m_ResultSet.getString(3);//same as above
            mac_list.add(maccol);
            String IDcol = m_ResultSet.getString(4);//same as above
            PCID.add(IDcol);
                //  String col_lab_name = m_ResultSet.getString(5);
                //  lab_name_list.add(col_lab_name);
            
            
            
        }
       
        }
        catch(SQLException ex){
            System.out.print("DB doesnt found ");
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        //// Showing the details from database on Table
     
       for(int i=0;i<name_list.size();i++ ){
           
           jTable1.getModel().setValueAt(name_list.get(i) ,row_pcname , col_pcname); // show the data from a list
           jTable1.getModel().setValueAt(ip_list.get(i) ,row_ip , col_ip);
           jTable1.getModel().setValueAt(mac_list.get(i) ,row_mac , col_mac);
           jTable1.getModel().setValueAt(PCID.get(i) ,row_mac , col_PCID);
         
              
           TableLabName(PCID.get(i),row_lab , col_lab_name);
           
         
           if( mac_list.get(i).equals(getmac(ip_list.get(i))) ){
               jTable1.getModel().setValueAt("ON", row_status, col_status);   
           }
           else
           {
              jTable1.getModel().setValueAt("OFF", row_status, col_status);
           }
           
           row_pcname++;
           row_ip++;
           row_mac++ ;
           row_status++;
           row_lab++;
           
       
       }
        setListnull();
        
    }
    private void ShowLabNameNotAssign()
    {
        if( LabNotAssign > 0){
           try{
              Thread.sleep(4000);
           }catch(Exception e)
           {
               JOptionPane.showMessageDialog(null, e);
           }
              JOptionPane.showMessageDialog(frm, "Please assign the Lab Name .");
        }
                
       
        
    }
    
    private void TableLabName(String PcID,int row_lab ,int col_lab_name){
        String sql = "Select COUNT(*) as no from lab";
        int LabDBEmpty = 0;
        int pcid = Integer.valueOf(PcID);
       try {
            m_Statement = m_Connection.createStatement();
            m_ResultSet = m_Statement.executeQuery(sql);
            
            while(m_ResultSet.next())
            LabDBEmpty = m_ResultSet.getInt("no");   
            
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(LabDBEmpty == 0){
            LabNotAssign++;
        }
         else {
        
          String sql2 = "select lab_name from pc_details, lab where pc_details.id = lab.pc_id and id = "+pcid;
            try {
               m_Statement = m_Connection.prepareStatement(sql2);
               
               m_ResultSet = m_Statement.executeQuery(sql2);
                
               while(m_ResultSet.next()){
                String ReturnLab = m_ResultSet.getString(1);
                jTable1.getModel().setValueAt( ReturnLab,row_lab , col_lab_name);
               }
            } catch (SQLException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
        
         }
        
    
    }
    private void setListnull(){
        name_list.clear();
        ip_list.clear();
          mac_list.clear();
          PCID.clear();
          lab_name_list.clear();
            
        
    }
    void update_table2(){
        String query = "select * from details";
        try {
            
            m_ResultSet = m_Statement.executeQuery(query);
            
            details_table.setModel(DbUtils.resultSetToTableModel(m_ResultSet));
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
        private String mac_from_db(String ip) throws SQLException {   ///// not used
            String query = "select mac from pc_details where ip = "+"'"+ip+"'";
            
            m_ResultSet = m_Statement.executeQuery(query);
            
            m_ResultSet.next();
            String mac = m_ResultSet.getString(1);
            
             
            return null;
        }
       
    private void TempTableUpdate(String labName){
        String sql = "select lab.pc_id,pc_details.`pc_name`,pc_details.`ip`,pc_details.`mac`,lab.lab_name from pc_details,lab where pc_details.id = lab.pc_id and lab.lab_name = '"+labName+"'";
    
        try{
     m_ResultSet = m_Statement.executeQuery(sql);
     tempTable.setModel(DbUtils.resultSetToTableModel(m_ResultSet));
        }
        catch(SQLException ex ){
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void Refresh(){
        int row_status=0;
        for(int i= 0 ; i< name_list.size();i++) {
        if( mac_list.get(i).equals(getmac(ip_list.get(i))) ){
                
               jTable1.getModel().setValueAt("ON", row_status, col_status);
               
           }
           else
           {
              jTable1.getModel().setValueAt("OFF", row_status, col_status);
           }
        row_status ++;
        }
    }
   String hostname(String ip) throws UnknownHostException{
       String return_name;
       InetAddress inetAddress =InetAddress.getByName(ip);
       return_name = inetAddress.getHostName() ;
       return return_name;
    }
   
   String getmac(String ip){
        String command = null;
        command = "arp -a "+ ip;
       String return_mac = null ;
    String process = null;
    String mac[] = new String[5];
    String rmac[] = new String[10];
    try {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        String line;
        int i = 0;
        while ((line = bufferedreader.readLine()) != null) {
            mac[i] = line;

            i++;
        }

        rmac = mac[3].split("    ");
        StringBuffer result = new StringBuffer();
        for (int n=1 ; n<rmac.length-1;n++){
            result.append(rmac[n]);
        }
         return_mac = result.toString().replaceAll("\\s+", "");
       // System.out.print(return_mac);
    } catch (Exception e) {
        //System.out.println("mac cant find");
    }
        //return_mac= return_mac.replaceAll("\\s+", "");
        return return_mac;
    }
   
    void null_table(){
        for(int i =0; i< jTable1.getRowCount();i++){
            for(int j=0; j< jTable1.getColumnCount(); j++){
                jTable1.getModel().setValueAt( "" , i , j);
            }
        }
        
    }
    
    String Time(){
        

    Calendar Date = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm a");
    String S = sdf.format(Date.getTime());


        return S;
    }
    boolean password_match(){
        String admin_set_name = "1";
       String admin_set_password = "1";
        if(admin_name.getText().equalsIgnoreCase(admin_set_name) && admin_password.getText().equalsIgnoreCase(admin_set_password) )
      { 
          return true;              
       }
        else
       {
          return false;  
          
       }
    }
    boolean  pc_match_exitpc(String mac) throws SQLException{
        int s ;
        String query = "select mac from pc_details ";
        m_ResultSet = m_Statement.executeQuery(query);
            
       while( m_ResultSet.next()){
       String result = m_ResultSet.getString(1) ;
       
       if(result.equals(mac))
       {System.out.print("Sending true");
         return true ;
       }
       }
       return false ;
        
    }
    private void fillcomboInEditDetails(){
        ComboboxEditFrame.removeAllItems();
        try{
            String sql = "select pc_name from pc_details ";
            m_ResultSet = m_Statement.executeQuery(sql);
            while( m_ResultSet.next()){
            String name = m_ResultSet.getString("pc_name");
            ComboboxEditFrame.addItem(name);
                
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void fillcomboInDeletePcDetails(){
        ComboBox_delete_pcdatails.removeAllItems();
        try{
            String sql = "select name_of_device from details ";
            m_ResultSet = m_Statement.executeQuery(sql);
            while( m_ResultSet.next()){
            String name = m_ResultSet.getString("name_of_device");
            ComboBox_delete_pcdatails.addItem(name);
                
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void fillcomboInAssignLab(){
        labpcComboBox.removeAllItems();
        try{
            String sql = "select pc_name from pc_details ";
            m_ResultSet = m_Statement.executeQuery(sql);
            while( m_ResultSet.next()){
            String name = m_ResultSet.getString("pc_name");
            labpcComboBox.addItem(name);
                
            }
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    private void updateTableLabName(int id,String labName){
        id = id-1;
        String idDB = Integer.toString(id);
        jTable1.getModel().setValueAt(labName ,id , col_lab_name);
        
    }
        
    public void PCWithProb(){
        int j = 0;
       for(int i=0; i<jTable1.getRowCount();i++){
           if(jTable1.getModel().getValueAt(i, 5) == "OFF")
           {
               pcProbTable.getModel().setValueAt(jTable1.getModel().getValueAt(i, 0), j, 0);
               pcProbTable.getModel().setValueAt(jTable1.getModel().getValueAt(i, 1), j, 1);
               pcProbTable.getModel().setValueAt(jTable1.getModel().getValueAt(i, 3), j, 2);
               pcProbTable.getModel().setValueAt(jTable1.getModel().getValueAt(i, 4), j, 3);
               pcProbTable.getModel().setValueAt(jTable1.getModel().getValueAt(i, 5), j, 4);
            j++;
           }
      
      }
        
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        adminFrame = new javax.swing.JFrame();
        admin_name = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        admin_password = new javax.swing.JTextField();
        login_button = new javax.swing.JButton();
        closeAdminFrame = new javax.swing.JButton();
        edit_frame = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        ok_edit_frame = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        edit_details = new javax.swing.JTextField();
        ComboboxEditFrame = new javax.swing.JComboBox();
        closeONedit_frame = new javax.swing.JButton();
        details_table_frame = new javax.swing.JFrame();
        jScrollPane2 = new javax.swing.JScrollPane();
        details_table = new javax.swing.JTable();
        close_detailstable_button = new javax.swing.JButton();
        delete_details_button = new javax.swing.JButton();
        delete_pcdetails_frame = new javax.swing.JFrame();
        jLabel6 = new javax.swing.JLabel();
        delete_pcdetails_button = new javax.swing.JButton();
        ComboBox_delete_pcdatails = new javax.swing.JComboBox();
        closeONdelete_pcdetails_frame = new javax.swing.JButton();
        temp_frame = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        tempTable = new javax.swing.JTable();
        closeOfTempTable = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        TextFieldLabveiw = new javax.swing.JTextField();
        ButtonOkLabveiw = new javax.swing.JButton();
        set_labname_frame = new javax.swing.JFrame();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labpcComboBox = new javax.swing.JComboBox();
        Assign_labname_txt = new javax.swing.JTextField();
        okButtonAssignLab = new javax.swing.JButton();
        Button_cancelAssignlab = new javax.swing.JButton();
        progressbarFrame = new javax.swing.JFrame();
        jLabelprogress = new javax.swing.JLabel();
        getIPFrame = new javax.swing.JFrame();
        jLabel10 = new javax.swing.JLabel();
        getIPText = new javax.swing.JTextField();
        getIPButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        pcWithProbFrame = new javax.swing.JFrame();
        jScrollPane4 = new javax.swing.JScrollPane();
        pcProbTable = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        admin1st_button = new javax.swing.JButton();
        admin_panel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        searchPCButton = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        delete_button = new javax.swing.JButton();
        button_details = new javax.swing.JButton();
        AssignLabButton = new javax.swing.JButton();
        ButtonPcWithProb = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        Button_labveiw = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();

        jLabel1.setText("Admin name :");

        jLabel2.setText("Password :");

        login_button.setText("Log In");
        login_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                login_buttonActionPerformed(evt);
            }
        });

        closeAdminFrame.setText("Close");
        closeAdminFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAdminFrameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout adminFrameLayout = new javax.swing.GroupLayout(adminFrame.getContentPane());
        adminFrame.getContentPane().setLayout(adminFrameLayout);
        adminFrameLayout.setHorizontalGroup(
            adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminFrameLayout.createSequentialGroup()
                .addGroup(adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(adminFrameLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(30, 30, 30)
                        .addGroup(adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(admin_name)
                            .addComponent(admin_password, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
                    .addGroup(adminFrameLayout.createSequentialGroup()
                        .addGap(107, 107, 107)
                        .addComponent(login_button, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(closeAdminFrame)))
                .addContainerGap(89, Short.MAX_VALUE))
        );
        adminFrameLayout.setVerticalGroup(
            adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminFrameLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(admin_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24)
                .addGroup(adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(admin_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(62, 62, 62)
                .addGroup(adminFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(login_button)
                    .addComponent(closeAdminFrame))
                .addContainerGap(102, Short.MAX_VALUE))
        );

        jLabel4.setText("Enter PC name :");

        ok_edit_frame.setText("OK");
        ok_edit_frame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ok_edit_frameActionPerformed(evt);
            }
        });
        ok_edit_frame.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ok_edit_frameKeyPressed(evt);
            }
        });

        jLabel5.setText("Details :");

        edit_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_detailsActionPerformed(evt);
            }
        });

        ComboboxEditFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComboboxEditFrameActionPerformed(evt);
            }
        });

        closeONedit_frame.setText("Close");
        closeONedit_frame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeONedit_frameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout edit_frameLayout = new javax.swing.GroupLayout(edit_frame.getContentPane());
        edit_frame.getContentPane().setLayout(edit_frameLayout);
        edit_frameLayout.setHorizontalGroup(
            edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edit_frameLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(edit_frameLayout.createSequentialGroup()
                        .addComponent(ok_edit_frame, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(closeONedit_frame))
                    .addComponent(edit_details, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboboxEditFrame, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        edit_frameLayout.setVerticalGroup(
            edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edit_frameLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboboxEditFrame, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(edit_frameLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 71, Short.MAX_VALUE))
                    .addGroup(edit_frameLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(edit_details)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(edit_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ok_edit_frame, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeONedit_frame, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48))
        );

        details_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Name of PC", "Mac_id", "details"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        details_table.setColumnSelectionAllowed(true);
        jScrollPane2.setViewportView(details_table);
        details_table.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        close_detailstable_button.setText("Close");
        close_detailstable_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_detailstable_buttonActionPerformed(evt);
            }
        });

        delete_details_button.setText("Delete a device details");
        delete_details_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_details_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout details_table_frameLayout = new javax.swing.GroupLayout(details_table_frame.getContentPane());
        details_table_frame.getContentPane().setLayout(details_table_frameLayout);
        details_table_frameLayout.setHorizontalGroup(
            details_table_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_table_frameLayout.createSequentialGroup()
                .addGroup(details_table_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(details_table_frameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane2))
                    .addGroup(details_table_frameLayout.createSequentialGroup()
                        .addGap(117, 117, 117)
                        .addComponent(delete_details_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 480, Short.MAX_VALUE)
                        .addComponent(close_detailstable_button)))
                .addGap(49, 49, 49))
        );
        details_table_frameLayout.setVerticalGroup(
            details_table_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, details_table_frameLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(details_table_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(close_detailstable_button)
                    .addComponent(delete_details_button))
                .addGap(7, 7, 7))
        );

        jLabel6.setText("name of device :");

        delete_pcdetails_button.setText("Delete");
        delete_pcdetails_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_pcdetails_buttonActionPerformed(evt);
            }
        });

        closeONdelete_pcdetails_frame.setText("Close");
        closeONdelete_pcdetails_frame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeONdelete_pcdetails_frameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout delete_pcdetails_frameLayout = new javax.swing.GroupLayout(delete_pcdetails_frame.getContentPane());
        delete_pcdetails_frame.getContentPane().setLayout(delete_pcdetails_frameLayout);
        delete_pcdetails_frameLayout.setHorizontalGroup(
            delete_pcdetails_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delete_pcdetails_frameLayout.createSequentialGroup()
                .addGroup(delete_pcdetails_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(delete_pcdetails_frameLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ComboBox_delete_pcdatails, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(delete_pcdetails_frameLayout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(delete_pcdetails_button)
                        .addGap(40, 40, 40)
                        .addComponent(closeONdelete_pcdetails_frame, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(99, Short.MAX_VALUE))
        );
        delete_pcdetails_frameLayout.setVerticalGroup(
            delete_pcdetails_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(delete_pcdetails_frameLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(delete_pcdetails_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboBox_delete_pcdatails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(delete_pcdetails_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(delete_pcdetails_button)
                    .addComponent(closeONdelete_pcdetails_frame))
                .addContainerGap(146, Short.MAX_VALUE))
        );

        tempTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "pc_id", "PC Name", "IP ID", "MAC ID", "LAB Name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tempTable);

        closeOfTempTable.setText("CLOSE");
        closeOfTempTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeOfTempTableActionPerformed(evt);
            }
        });

        jLabel9.setText("Enter Lab Name :");

        ButtonOkLabveiw.setText("OK");
        ButtonOkLabveiw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonOkLabveiwActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout temp_frameLayout = new javax.swing.GroupLayout(temp_frame.getContentPane());
        temp_frame.getContentPane().setLayout(temp_frameLayout);
        temp_frameLayout.setHorizontalGroup(
            temp_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(temp_frameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(temp_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(temp_frameLayout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TextFieldLabveiw, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ButtonOkLabveiw, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, temp_frameLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, temp_frameLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(closeOfTempTable)
                        .addGap(19, 19, 19))))
        );
        temp_frameLayout.setVerticalGroup(
            temp_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(temp_frameLayout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(temp_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TextFieldLabveiw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ButtonOkLabveiw))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(closeOfTempTable)
                .addGap(35, 35, 35))
        );

        jLabel7.setText("Choose PC name  :");

        jLabel8.setText("Assign lab name  :");

        labpcComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labpcComboBoxActionPerformed(evt);
            }
        });

        okButtonAssignLab.setText("OK");
        okButtonAssignLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonAssignLabActionPerformed(evt);
            }
        });

        Button_cancelAssignlab.setText("Cancel");
        Button_cancelAssignlab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_cancelAssignlabActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout set_labname_frameLayout = new javax.swing.GroupLayout(set_labname_frame.getContentPane());
        set_labname_frame.getContentPane().setLayout(set_labname_frameLayout);
        set_labname_frameLayout.setHorizontalGroup(
            set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(set_labname_frameLayout.createSequentialGroup()
                .addGroup(set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(set_labname_frameLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(labpcComboBox, 0, 175, Short.MAX_VALUE)
                            .addComponent(Assign_labname_txt)))
                    .addGroup(set_labname_frameLayout.createSequentialGroup()
                        .addGap(124, 124, 124)
                        .addComponent(okButtonAssignLab)
                        .addGap(61, 61, 61)
                        .addComponent(Button_cancelAssignlab)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        set_labname_frameLayout.setVerticalGroup(
            set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(set_labname_frameLayout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labpcComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Assign_labname_txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(set_labname_frameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButtonAssignLab)
                    .addComponent(Button_cancelAssignlab))
                .addContainerGap(121, Short.MAX_VALUE))
        );

        jLabelprogress.setText("Seaching PCs ...");

        javax.swing.GroupLayout progressbarFrameLayout = new javax.swing.GroupLayout(progressbarFrame.getContentPane());
        progressbarFrame.getContentPane().setLayout(progressbarFrameLayout);
        progressbarFrameLayout.setHorizontalGroup(
            progressbarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressbarFrameLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabelprogress, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(158, Short.MAX_VALUE))
        );
        progressbarFrameLayout.setVerticalGroup(
            progressbarFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(progressbarFrameLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabelprogress)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jLabel10.setText("Enter first 3 Bytes of  IP address of LAN : ");

        getIPText.setText("ex:172.168.137");
        getIPText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getIPTextMouseClicked(evt);
            }
        });
        getIPText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getIPTextActionPerformed(evt);
            }
        });

        getIPButton.setText("OK");
        getIPButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getIPButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout getIPFrameLayout = new javax.swing.GroupLayout(getIPFrame.getContentPane());
        getIPFrame.getContentPane().setLayout(getIPFrameLayout);
        getIPFrameLayout.setHorizontalGroup(
            getIPFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getIPFrameLayout.createSequentialGroup()
                .addGroup(getIPFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(getIPFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(getIPFrameLayout.createSequentialGroup()
                            .addGap(60, 60, 60)
                            .addComponent(getIPText, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(getIPFrameLayout.createSequentialGroup()
                            .addGap(118, 118, 118)
                            .addComponent(getIPButton))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        getIPFrameLayout.setVerticalGroup(
            getIPFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getIPFrameLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(getIPText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(getIPButton)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jLabel12.setText("jLabel12");

        pcProbTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "PCID", "Lab Name", "IP Adress", "Mac ID", "Stutas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(pcProbTable);
        pcProbTable.getColumnModel().getColumn(0).setResizable(false);

        jButton2.setText("CLOSE");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pcWithProbFrameLayout = new javax.swing.GroupLayout(pcWithProbFrame.getContentPane());
        pcWithProbFrame.getContentPane().setLayout(pcWithProbFrameLayout);
        pcWithProbFrameLayout.setHorizontalGroup(
            pcWithProbFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pcWithProbFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pcWithProbFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pcWithProbFrameLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addGap(37, 37, 37))
                    .addGroup(pcWithProbFrameLayout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(48, Short.MAX_VALUE))))
        );
        pcWithProbFrameLayout.setVerticalGroup(
            pcWithProbFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pcWithProbFrameLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "PC ID", "Lab name", "pc name", "ip address", "mac id", "status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setResizable(false);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(3).setMinWidth(125);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(3).setMaxWidth(150);
        jTable1.getColumnModel().getColumn(4).setMinWidth(125);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(200);
        jTable1.getColumnModel().getColumn(4).setMaxWidth(150);

        jButton1.setText("Details");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        admin1st_button.setText("log in as admin");
        admin1st_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                admin1st_buttonActionPerformed(evt);
            }
        });

        admin_panel.setBackground(new java.awt.Color(181, 202, 222));

        jLabel3.setText("Admin Panel");

        searchPCButton.setText("search for pc");
        searchPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchPCButtonActionPerformed(evt);
            }
        });

        jButton36.setText("Log out");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        delete_button.setText("Delete all PC's");
        delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_buttonActionPerformed(evt);
            }
        });

        button_details.setText("Edit Details");
        button_details.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_detailsActionPerformed(evt);
            }
        });

        AssignLabButton.setText("Assign Lab");
        AssignLabButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AssignLabButtonActionPerformed(evt);
            }
        });

        ButtonPcWithProb.setText("PC with Prob");
        ButtonPcWithProb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonPcWithProbActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout admin_panelLayout = new javax.swing.GroupLayout(admin_panel);
        admin_panel.setLayout(admin_panelLayout);
        admin_panelLayout.setHorizontalGroup(
            admin_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(admin_panelLayout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(170, 170, 170))
            .addGroup(admin_panelLayout.createSequentialGroup()
                .addGroup(admin_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(button_details, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AssignLabButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchPCButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(delete_button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ButtonPcWithProb, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        admin_panelLayout.setVerticalGroup(
            admin_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(admin_panelLayout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(AssignLabButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchPCButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(delete_button)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(button_details)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ButtonPcWithProb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton36)
                .addGap(0, 188, Short.MAX_VALUE))
        );

        jButton38.setText("Exit");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        Button_labveiw.setText("Lab View");
        Button_labveiw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_labveiwActionPerformed(evt);
            }
        });

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/x/refresh-icon.png"))); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        jMenuBar1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 204, 255), new java.awt.Color(153, 204, 255), new java.awt.Color(153, 204, 255), new java.awt.Color(153, 204, 255)));
        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenu1.setText("File");

        jMenuItem1.setText("Exit");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("About");
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(admin1st_button, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(Button_labveiw)
                        .addGap(68, 68, 68)
                        .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(refreshButton))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 675, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(admin_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(refreshButton)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                    .addComponent(admin_panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(admin1st_button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Button_labveiw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //details.setVisible(true);  
       // details.setSize(500, 500);
        update_table2();
        
        details_table_frame.setVisible(true);
        details_table_frame.setSize(700, 500);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void SearchPC(){
        progressbarFrame.setVisible(true);
         progressbarFrame.setSize(300, 100);
         progressbarFrame.setLocation(500, 500);
        
        
        String pc = "pc";
         int row_pcname = 0;
         int row_ip = 0;
         int row_mac = 0;
         int row_status = 0;
         int pc_name = 1;
         int flag = 0;
         int count = 0 ;
         int pc_new_count = 0;
         
         Component frm = null;
         String query_insert = null; 
         
         String[] ip_array = new String[30];
         String[] given_ip = new String[30];
        
          
         JOptionPane.showMessageDialog(frm, "It'll Take few moments \n click to start process");
         
         progressbarFrame.setVisible(true);
         for (int i = 1; i < 255; i++) {
                int percent = (i/255)*100;
                String Progress = String.valueOf(percent);
                jLabelprogress.setText(jLabelprogress.getText() + Progress );
                if(getmac(ip+i) != null){ 
                    System.out.println("success to connect to " + ip +i);
                    ip_array[count] =ip+i;
                    count++;
                    
                } 
                else
                {
                    System.out.println("Failed to connect to "+ ip +i );
                }
         }
              for(int j = 0 ; j <count; j++){ 
            try {
                try {
                   if(pc_match_exitpc(getmac(ip_array[j])))
                       continue;
                    else
                   {
                       query_insert = "insert into pc_details (pc_name,ip,mac) VALUES ( " + "'" + hostname(ip_array[j]) +"','"+ip_array[j]+"','"+getmac(ip_array[j])+"')" ;
                       pc_new_count ++;
                   }
                } catch (UnknownHostException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
                m_Statement.executeUpdate(query_insert);   // insert all the data to table
            } catch (SQLException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
              }
          JOptionPane.showMessageDialog(frm, "search complite .");
          if(pc_new_count != 0)
          JOptionPane.showMessageDialog(frm," "+pc_new_count+" pc found");
          else
          {JOptionPane.showMessageDialog(frm,"no new pc are found");}
          
          table_update();
          fillcomboInEditDetails();
          fillcomboInAssignLab();
    }
   
    
    
 
    
    private void admin1st_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_admin1st_buttonActionPerformed
        adminFrame.setVisible(true);
        adminFrame.setSize(400, 300);
        
        
    }//GEN-LAST:event_admin1st_buttonActionPerformed

    private void login_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_login_buttonActionPerformed
       
       Component frm = null;
       
       if(password_match() )
      { 
          admin_panel.setVisible(true);
          adminFrame.setVisible(false);
          loginFlag = 1 ;
       }
        else
       {
          JOptionPane.showMessageDialog(frm,
    "Wrong name and password . re-enter them",
    "Log-in error",
    JOptionPane.ERROR_MESSAGE);   
          
       }
       
       
    }//GEN-LAST:event_login_buttonActionPerformed

    private void searchPCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchPCButtonActionPerformed
        getIPFrame.setVisible(true);
        getIPFrame.setSize(400, 300); 
        
        
    }//GEN-LAST:event_searchPCButtonActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        admin_panel.setVisible(false);
        loginFlag = 0;
    }//GEN-LAST:event_jButton36ActionPerformed

    private void ok_edit_frameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ok_edit_frameActionPerformed
        edit_frame.setVisible(false);
        String pc = null;
        String pcdetail = null;
        String date = null;
        pc = (String)ComboboxEditFrame.getSelectedItem();
        pcdetail = edit_details.getText();
        date = Time();
        if(pc == null && pcdetail == null){
            
            JOptionPane.showMessageDialog(rootPane, "you haven't enter nothing");
        }
        else {
            
        int reply = JOptionPane.showConfirmDialog(rootPane, "are u sure about this ","Confirmation",JOptionPane.YES_NO_OPTION);
        if(reply == JOptionPane.YES_OPTION){
        String query = "insert into details(name_of_device,detail,edit_date) values('"+pc+"','"+pcdetail+"','"+date+"')";
        try {
            m_Statement.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
        }
        fillcomboInDeletePcDetails();
    }//GEN-LAST:event_ok_edit_frameActionPerformed

    private void button_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_detailsActionPerformed
       edit_frame.setVisible(true);
       edit_frame.setSize(400, 300);
       
       
    }//GEN-LAST:event_button_detailsActionPerformed

    private void edit_detailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_detailsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_edit_detailsActionPerformed

    private void delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_buttonActionPerformed
        Component frm = null;
        int reply = JOptionPane.showConfirmDialog(frm, "Are you sure to delete all PCs","Confirmation",JOptionPane.YES_NO_OPTION);
        if(reply == JOptionPane.YES_OPTION){
        String query1 = "delete  from pc_details";
        String query2 = "ALTER TABLE pc_details AUTO_INCREMENT = 1";
        String query3 = "delete  from lab" ;
        try {
            m_Statement.execute(query1);
             m_Statement.execute(query2);
             m_Statement.execute(query3);
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        null_table();
        setListnull();
        fillcomboInEditDetails();
        fillcomboInDeletePcDetails();
        fillcomboInAssignLab();
    }//GEN-LAST:event_delete_buttonActionPerformed
    }
    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
       System.exit(0);
    }//GEN-LAST:event_jButton38ActionPerformed

    private void delete_details_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_details_buttonActionPerformed
        Component frm = null;
     if(loginFlag  == 1){
         delete_pcdetails_frame.setVisible(true);
         delete_pcdetails_frame.setSize(400, 200);
     }
         else
     {
         //JOptionPane.showMessageDialog(frm,"To delete content you have log in as admin ",JOptionPane.ERROR_MESSAGE); 
      JOptionPane.showMessageDialog(frm, "You have to log in to perform this action");
     }
    }//GEN-LAST:event_delete_details_buttonActionPerformed

    private void delete_pcdetails_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_pcdetails_buttonActionPerformed
         Component frm = null;
         int reply = JOptionPane.showConfirmDialog(frm, "Are you sure to delete a device details ","Confirmation",JOptionPane.YES_NO_OPTION);
         if(reply == JOptionPane.YES_OPTION)            
         {
         String comboname = null;
         comboname = (String)ComboBox_delete_pcdatails.getSelectedItem();
         String query = "delete from details where name_of_device = '"+comboname+"'";
        try {
            m_Statement.execute(query);
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
         update_table2();
         delete_pcdetails_frame.setVisible(false);
         
    }//GEN-LAST:event_delete_pcdetails_buttonActionPerformed
    }
    private void close_detailstable_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_detailstable_buttonActionPerformed
       details_table_frame.setVisible(false);
    }//GEN-LAST:event_close_detailstable_buttonActionPerformed

    private void ok_edit_frameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ok_edit_frameKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_ok_edit_frameKeyPressed

    private void ComboboxEditFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComboboxEditFrameActionPerformed
       
    }//GEN-LAST:event_ComboboxEditFrameActionPerformed

    private void AssignLabButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AssignLabButtonActionPerformed
        set_labname_frame.setVisible(true);
        set_labname_frame.setSize(400,300);
    }//GEN-LAST:event_AssignLabButtonActionPerformed

    private void okButtonAssignLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonAssignLabActionPerformed
        Statement stmt=null;
        ResultSet rslt = null;
        int idDB1 = 0;
        int idDB2 = 0;
        boolean flag = true;
        
        String comboname = (String)labpcComboBox.getSelectedItem();
        String sql = "Select * from pc_details where pc_name = '"+comboname+"'";
        String sql3 = "Select * from lab ";
        String LabName = Assign_labname_txt.getText();
        
        try {
            
            stmt = m_Connection.createStatement();
            rslt = stmt.executeQuery(sql);
            
            
            while(rslt.next())
            {
                 idDB1 = rslt.getInt("id");
            }
         
        } catch (SQLException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            stmt = m_Connection.createStatement();
            rslt = stmt.executeQuery(sql3);
            while(rslt.next()){
                idDB2 = rslt.getInt("pc_id");
                if(idDB2 == idDB1)
                    flag = false ;
                 
            }
             set_labname_frame.setVisible(false);
            Component frm = null;
           if(flag == false)
               JOptionPane.showMessageDialog(frm, "This PC is already Assigned \n Do you want to replace this ?");
            if(flag == true){
                     
                     String sql2 = "insert into lab(pc_id,lab_name) values('"+idDB1+"','"+LabName+"')";
                     stmt.execute(sql2);
            }
            else
            {
                   String sql4 = "update lab set lab_name = '"+LabName+"' where pc_id = "+idDB1+" "; 
                   stmt.execute(sql4);
            }
            
            
        }catch(SQLException ex){
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
      
        updateTableLabName(idDB1,LabName);
        Assign_labname_txt.setText(null);
    }//GEN-LAST:event_okButtonAssignLabActionPerformed

    private void Button_labveiwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_labveiwActionPerformed
        temp_frame.setVisible(true);
        temp_frame.setSize(600, 530);
    }//GEN-LAST:event_Button_labveiwActionPerformed

    private void ButtonOkLabveiwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonOkLabveiwActionPerformed
        String labName = null;
        boolean flag = true;
        labName = TextFieldLabveiw.getText();
        String sql = "select * from lab ";
        try
        {
            m_ResultSet = m_Statement.executeQuery(sql);
            
            while(m_ResultSet.next()){
                String r = m_ResultSet.getString(2);
               // System.out.print(r);
                if(labName.matches(r))
                    flag = false;
            }
        }catch(SQLException ex){
            
        }
        Component frm = null;
        if(flag == false){
           TempTableUpdate(labName);}
        else
        { JOptionPane.showMessageDialog(frm, "There is no such a LAB");
       
         for (int i = 0; i < tempTable.getRowCount(); i++)
          for(int j = 0; j < tempTable.getColumnCount(); j++) {
          tempTable.setValueAt("", i, j);
      
   }
}
        
        
    }//GEN-LAST:event_ButtonOkLabveiwActionPerformed

    private void closeOfTempTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeOfTempTableActionPerformed
        temp_frame.setVisible(false);
    }//GEN-LAST:event_closeOfTempTableActionPerformed

    private void Button_cancelAssignlabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_cancelAssignlabActionPerformed
        set_labname_frame.setVisible(false);
    }//GEN-LAST:event_Button_cancelAssignlabActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
         Refresh();          
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void closeONdelete_pcdetails_frameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeONdelete_pcdetails_frameActionPerformed
        delete_pcdetails_frame.setVisible(false);
    }//GEN-LAST:event_closeONdelete_pcdetails_frameActionPerformed

    private void closeONedit_frameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeONedit_frameActionPerformed
        edit_frame.setVisible(false);
    }//GEN-LAST:event_closeONedit_frameActionPerformed

    private void closeAdminFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAdminFrameActionPerformed
       adminFrame.setVisible(false);
    }//GEN-LAST:event_closeAdminFrameActionPerformed

    private void getIPTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_getIPTextMouseClicked
        getIPText.setText(null);
    }//GEN-LAST:event_getIPTextMouseClicked

    private void getIPButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getIPButtonActionPerformed
       
        ip = getIPText.getText();
        ip = ip + "." ;
        getIPFrame.setVisible(false);
         
         SearchPC();
         progressbarFrame.setVisible(false);
    }//GEN-LAST:event_getIPButtonActionPerformed

    private void getIPTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getIPTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_getIPTextActionPerformed

    private void labpcComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labpcComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_labpcComboBoxActionPerformed

    private void ButtonPcWithProbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonPcWithProbActionPerformed
       pcWithProbFrame.setVisible(true);
       pcWithProbFrame.setSize(700, 500);
       PCWithProb();
    }//GEN-LAST:event_ButtonPcWithProbActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        pcWithProbFrame.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed
  
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new main().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AssignLabButton;
    private javax.swing.JTextField Assign_labname_txt;
    private javax.swing.JButton ButtonOkLabveiw;
    private javax.swing.JButton ButtonPcWithProb;
    private javax.swing.JButton Button_cancelAssignlab;
    private javax.swing.JButton Button_labveiw;
    private javax.swing.JComboBox ComboBox_delete_pcdatails;
    private javax.swing.JComboBox ComboboxEditFrame;
    private javax.swing.JTextField TextFieldLabveiw;
    private javax.swing.JButton admin1st_button;
    private javax.swing.JFrame adminFrame;
    private javax.swing.JTextField admin_name;
    private javax.swing.JPanel admin_panel;
    private javax.swing.JTextField admin_password;
    private javax.swing.JButton button_details;
    private javax.swing.JButton closeAdminFrame;
    private javax.swing.JButton closeONdelete_pcdetails_frame;
    private javax.swing.JButton closeONedit_frame;
    private javax.swing.JButton closeOfTempTable;
    private javax.swing.JButton close_detailstable_button;
    private javax.swing.JButton delete_button;
    private javax.swing.JButton delete_details_button;
    private javax.swing.JButton delete_pcdetails_button;
    private javax.swing.JFrame delete_pcdetails_frame;
    private javax.swing.JTable details_table;
    private javax.swing.JFrame details_table_frame;
    private javax.swing.JTextField edit_details;
    private javax.swing.JFrame edit_frame;
    private javax.swing.JButton getIPButton;
    private javax.swing.JFrame getIPFrame;
    private javax.swing.JTextField getIPText;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton38;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelprogress;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JComboBox labpcComboBox;
    private javax.swing.JButton login_button;
    private javax.swing.JButton okButtonAssignLab;
    private javax.swing.JButton ok_edit_frame;
    private javax.swing.JTable pcProbTable;
    private javax.swing.JFrame pcWithProbFrame;
    private javax.swing.JFrame progressbarFrame;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton searchPCButton;
    private javax.swing.JFrame set_labname_frame;
    private javax.swing.JTable tempTable;
    private javax.swing.JFrame temp_frame;
    // End of variables declaration//GEN-END:variables
}

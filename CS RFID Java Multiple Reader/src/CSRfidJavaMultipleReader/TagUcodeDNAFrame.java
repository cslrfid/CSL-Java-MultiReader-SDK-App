package CSRfidJavaMultipleReader;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;

public class TagUcodeDNAFrame extends javax.swing.JFrame implements AsyncCallbackEventListener{

    // <editor-fold defaultstate="collapsed" desc="Variable">
    private class ButtonState{
        final public static int Start = 0;
        final public static int Stop = 1;
        final public static int StartTAM1 = 2;
        final public static int StopTAM1 = 3;
        final public static int StartTAM2 = 4;
        final public static int StopTAM2 = 5;
        final public static int Unknown = 6;
    }
    private DefaultTableModel inventoryTableModel;
    private int startButtonState = ButtonState.Unknown;
    private ArrayList InventoryListItems = new ArrayList();
    
    private java.util.Timer msgRateTimer = null;

    private final int MAX_THREAD = 50;
    private Thread[] m_run_process = new Thread[MAX_THREAD];
    private TIDInventory[] m_inventory = new TIDInventory[MAX_THREAD];
    private UcodeDNATest[] m_test = new UcodeDNATest[MAX_THREAD];
    private int lastThreadId = -1;
    
    private AsyncCallbackEventListener cls = this;
    private int region = 0;
    
    final JFXPanel fxPanel = new JFXPanel();
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Init">
    /** Creates new form TagInventoryFrame */
    public TagUcodeDNAFrame(int region) {
        initComponents();

        //Center the dialog
        Dimension size = getSize();
        setLocation((Main.screen.width - size.width)/2, (Main.screen.height - size.height)/2);

        inventoryTableModel = (DefaultTableModel)table_inventory.getModel();

        this.setIconImage(new ImageIcon("CSL Logo.jpg").getImage());
        this.region = region;
    }
    // </editor-fold>

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_start = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_inventory = new javax.swing.JTable();
        btn_clear = new javax.swing.JButton();
        tf_selected = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btn_authenticate = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lb_result = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tf_key0 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tf_accesspw = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        btn_authenticate1 = new javax.swing.JButton();
        btn_authenticate2 = new javax.swing.JButton();
        lb_alert = new javax.swing.JLabel();
        tf_key1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Ucode DNA");
        setBackground(java.awt.SystemColor.info);
        setBounds(new java.awt.Rectangle(0, 0, 320, 240));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_start.setBackground(new java.awt.Color(0, 192, 0));
        btn_start.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btn_start.setText("Search DNA Tag");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });
        getContentPane().add(btn_start, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 157, -1, -1));

        table_inventory.setAutoCreateRowSorter(true);
        table_inventory.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        table_inventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PC", "EPC", "TID", "Pass", "Fail"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table_inventory.setRowHeight(25);
        table_inventory.getTableHeader().setReorderingAllowed(false);
        table_inventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_inventoryMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table_inventory);
        if (table_inventory.getColumnModel().getColumnCount() > 0) {
            table_inventory.getColumnModel().getColumn(0).setPreferredWidth(60);
            table_inventory.getColumnModel().getColumn(1).setPreferredWidth(250);
            table_inventory.getColumnModel().getColumn(2).setPreferredWidth(250);
            table_inventory.getColumnModel().getColumn(3).setPreferredWidth(10);
            table_inventory.getColumnModel().getColumn(4).setPreferredWidth(10);
        }

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 738, 151));

        btn_clear.setBackground(java.awt.Color.cyan);
        btn_clear.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btn_clear.setText("Clear");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });
        getContentPane().add(btn_clear, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 157, 80, -1));

        tf_selected.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        getContentPane().add(tf_selected, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 194, 360, -1));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel1.setText("Selected EPC");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 197, 158, -1));

        btn_authenticate.setBackground(new java.awt.Color(0, 192, 0));
        btn_authenticate.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btn_authenticate.setText("TAM1 Authenticate");
        btn_authenticate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_authenticateActionPerformed(evt);
            }
        });
        getContentPane().add(btn_authenticate, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 211, -1));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel4.setText("Must be 96 bits");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(536, 197, -1, -1));

        lb_result.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lb_result.setText("         ");
        getContentPane().add(lb_result, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 344, 155, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel2.setText("TAM1 Key0 (hex)");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 265, 158, -1));

        tf_key0.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        tf_key0.setText("0123456789ABCDEF0123456789ABCDEF");
        getContentPane().add(tf_key0, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 262, 372, -1));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel5.setText("Must be 128 bits");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 265, -1, -1));

        tf_accesspw.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        tf_accesspw.setText("12345678");
        getContentPane().add(tf_accesspw, new org.netbeans.lib.awtextra.AbsoluteConstraints(172, 228, 94, -1));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel6.setText("Access Password");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 231, 158, -1));

        btn_authenticate1.setBackground(new java.awt.Color(0, 192, 0));
        btn_authenticate1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btn_authenticate1.setText("Start TAM1 Authenticate Test");
        btn_authenticate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_authenticate1ActionPerformed(evt);
            }
        });
        getContentPane().add(btn_authenticate1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 381, -1, -1));

        btn_authenticate2.setBackground(new java.awt.Color(0, 192, 0));
        btn_authenticate2.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btn_authenticate2.setText("Start TAM2 Authenticate Test");
        btn_authenticate2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_authenticate2ActionPerformed(evt);
            }
        });
        getContentPane().add(btn_authenticate2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 418, -1, -1));

        lb_alert.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        lb_alert.setForeground(new java.awt.Color(0, 204, 0));
        getContentPane().add(lb_alert, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 376, 430, 80));

        tf_key1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        tf_key1.setText("0123456789ABCDEF0123456789ABCDEF");
        getContentPane().add(tf_key1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 300, 373, -1));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel7.setText("Must be 128 bits");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 300, -1, -1));

        jLabel3.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        jLabel3.setText("TAM2 Key1 (hex)");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 150, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // <editor-fold defaultstate="collapsed" desc="Event">
    public synchronized void AsyncCallbackEvent(AsyncCallbackEventArgs ev)
    {                
        TagCallbackInfo record = ev.info;

        int index = Collections.binarySearch(InventoryListItems, ev.info);
        if (index > -1)
        {
            if (record.source.startsWith("DNA"))
            {
                if (record.result)
                {
                    inventoryTableModel.setValueAt((Integer)inventoryTableModel.getValueAt(index, 3)+1, index, 3);
                }
                else
                {
                    inventoryTableModel.setValueAt((Integer)inventoryTableModel.getValueAt(index, 4)+1, index, 4);
                }
            }
        }
        else
        {
            if (record.source.startsWith("DNA"))
            {
                Object[] entry;
                String bip;
                InventoryListItems.add(record);
                if (record.result)
                {
                    entry = new Object[] {"", record.epc.ToString(), "", 1, 0};
                    msgRateTimer = new java.util.Timer();
                    msgRateTimer.schedule(new MsgRateTask(), 10, 500);
                    if (startButtonState == ButtonState.StopTAM1)
                    {
                        bip = "TAM1_alert.mp3";
                        SaveTAM1Log(record.epc.ToString(), true);
                    }
                    else
                    {
                        bip = "TAM2_alert.mp3";
                        SaveTAM2Log(record.epc.ToString(), record.data, true);
                    }
                    Media hit = new Media(new File(bip).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(hit);
                    mediaPlayer.play();
                }
                else
                {
                    entry = new Object[] {"", record.epc.ToString(), "", 0, 1};
                    SaveTAM1Log(record.epc.ToString(), false);
                }
                inventoryTableModel.addRow(entry);
                Collections.sort(InventoryListItems);
            }
            else
            {
                if (record.tid.ToString().startsWith("e2c06f92") ||
                    record.tid.ToString().startsWith("e2c06b12") ||
                    record.tid.ToString().startsWith("e2c06892"))
                {
                    InventoryListItems.add(record);
                    Object[] entry = new Object[] {record.pc.ToString(), record.epc.ToString(), record.tid.ToString()};
                    inventoryTableModel.addRow(entry);
                    Collections.sort(InventoryListItems);
                }
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="UI Handle">
    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_startActionPerformed
        if (startButtonState == ButtonState.Unknown)
        {
            Start();
        }
        else if (startButtonState == ButtonState.Stop)
        {
            Stop();
        }
    }//GEN-LAST:event_btn_startActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Stop();
        evt.getWindow().dispose();
        new NetFinderFrame().setVisible(true);
    }//GEN-LAST:event_formWindowClosing

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        SetStartButtonState(ButtonState.Unknown);
    }//GEN-LAST:event_formWindowOpened

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        int len = inventoryTableModel.getRowCount();
        if (len > 0)
        {
            for (int i = len-1; i >= 0; i--)
                inventoryTableModel.removeRow(i);
        }
        InventoryListItems.clear();
    }//GEN-LAST:event_btn_clearActionPerformed

    private void btn_authenticateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_authenticateActionPerformed
        if (startButtonState == ButtonState.Stop)
        {
            Stop();
        }
        UcodeDNA authenticate = new UcodeDNA(Main.deviceName[0]);
        if (authenticate.StartAuthenticate(Main.ipAddress[0], tf_selected.getText(), tf_key0.getText(), tf_accesspw.getText()))
            lb_result.setText("Pass");
        else
            lb_result.setText("Fail");
        authenticate.Disconnect();
    }//GEN-LAST:event_btn_authenticateActionPerformed

    private void table_inventoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_inventoryMouseClicked
        tf_selected.setText(table_inventory.getValueAt(table_inventory.getSelectedRow(), 1).toString());
    }//GEN-LAST:event_table_inventoryMouseClicked

    private void btn_authenticate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_authenticate1ActionPerformed
        if (startButtonState == ButtonState.Unknown)
        {
            StartTAM1Test();
        }
        else if (startButtonState == ButtonState.StopTAM1)
        {
            StopTAM1Test();
        }
    }//GEN-LAST:event_btn_authenticate1ActionPerformed

    private void btn_authenticate2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_authenticate2ActionPerformed
        if (startButtonState == ButtonState.Unknown)
        {
            StartTAM2Test();
        }
        else if (startButtonState == ButtonState.StopTAM2)
        {
            StopTAM2Test();
        }
    }//GEN-LAST:event_btn_authenticate2ActionPerformed

    private void Start()
    {
        for (lastThreadId = 0; lastThreadId < Main.ipAddress.length; lastThreadId++)
        {
            m_inventory[lastThreadId] = new TIDInventory(Main.deviceName[lastThreadId]);
            m_inventory[lastThreadId].addAsyncCallbackEventListener(cls);
            m_run_process[lastThreadId] = new Thread(new StartInventoryThread(Main.ipAddress[lastThreadId], Main.macAddress[lastThreadId], m_inventory[lastThreadId]));
            m_run_process[lastThreadId].start();
        }
        --lastThreadId;

        try
        {
            Thread.sleep(1000);
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

        SetStartButtonState(ButtonState.Stop);
    }

    private void Stop()
    {
        for (lastThreadId = 0; lastThreadId < Main.ipAddress.length; lastThreadId++)
        {
            if (m_inventory[lastThreadId] != null)
            {
                m_inventory[lastThreadId].stopInventory = true;
                try
                {
                    Thread.sleep(200);
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
                m_inventory[lastThreadId].removeAsyncCallbackEventListener(cls);
                m_inventory[lastThreadId].Disconnect();
            }
        }
        lastThreadId = -1;
        SetStartButtonState(ButtonState.Unknown);
    }
    
    private void StartTAM1Test()
    {
        btn_clearActionPerformed(null);
        
        for (lastThreadId = 0; lastThreadId < Main.ipAddress.length; lastThreadId++)
        {
            m_test[lastThreadId] = new UcodeDNATest(Main.deviceName[lastThreadId]);
            m_test[lastThreadId].addAsyncCallbackEventListener(cls);
            m_run_process[lastThreadId] = new Thread(new StartTestThread(Main.ipAddress[lastThreadId], Main.macAddress[lastThreadId], m_test[lastThreadId], 1));
            m_run_process[lastThreadId].start();
        }
        --lastThreadId;

        try
        {
            Thread.sleep(1000);
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

        SetStartButtonState(ButtonState.StopTAM1);
    }

    private void StopTAM1Test()
    {
        for (lastThreadId = 0; lastThreadId < Main.ipAddress.length; lastThreadId++)
        {
            if (m_test[lastThreadId] != null)
            {
                m_test[lastThreadId].stopInventory = true;
                try
                {
                    Thread.sleep(200);
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
                m_test[lastThreadId].removeAsyncCallbackEventListener(cls);
                m_test[lastThreadId].Disconnect();
            }
        }
        lastThreadId = -1;
        SetStartButtonState(ButtonState.Unknown);
        if (msgRateTimer != null)
        {
            msgRateTimer.cancel();
            msgRateTimer = null;
        }
        lb_alert.setText("");
    }
    
    private void StartTAM2Test()
    {
        btn_clearActionPerformed(null);
        
        for (lastThreadId = 0; lastThreadId < Main.ipAddress.length; lastThreadId++)
        {
            m_test[lastThreadId] = new UcodeDNATest(Main.deviceName[lastThreadId]);
            m_test[lastThreadId].addAsyncCallbackEventListener(cls);
            m_run_process[lastThreadId] = new Thread(new StartTestThread(Main.ipAddress[lastThreadId], Main.macAddress[lastThreadId], m_test[lastThreadId], 2));
            m_run_process[lastThreadId].start();
        }
        --lastThreadId;

        try
        {
            Thread.sleep(1000);
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

        SetStartButtonState(ButtonState.StopTAM2);
    }

    private void StopTAM2Test()
    {
        for (lastThreadId = 0; lastThreadId < Main.ipAddress.length; lastThreadId++)
        {
            if (m_test[lastThreadId] != null)
            {
                m_test[lastThreadId].stopInventory = true;
                try
                {
                    Thread.sleep(200);
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
                m_test[lastThreadId].removeAsyncCallbackEventListener(cls);
                m_test[lastThreadId].Disconnect();
            }
        }
        lastThreadId = -1;
        SetStartButtonState(ButtonState.Unknown);
        if (msgRateTimer != null)
        {
            msgRateTimer.cancel();
            msgRateTimer = null;
        }
        lb_alert.setText("");
    }

    private void SetStartButtonState(int state)
    {
        if (state == startButtonState)
            return;
        switch (state)
        {
            case ButtonState.Stop:
                startButtonState = ButtonState.Stop;
                btn_start.setText("Stop");
                btn_start.setBackground(java.awt.Color.red);
                btn_clear.setEnabled(false);
                break;
            case ButtonState.Start:
                startButtonState = ButtonState.Start;
                btn_start.setText("Search DNA Tag");
                btn_start.setBackground(new java.awt.Color(0, 192, 0));
                btn_clear.setEnabled(true);
                break;
            case ButtonState.StopTAM1:
                startButtonState = ButtonState.StopTAM1;
                btn_authenticate1.setText("Stop TAM1 Authenticate Test");
                btn_authenticate1.setBackground(java.awt.Color.red);
                break;
            case ButtonState.StartTAM1:
                startButtonState = ButtonState.StartTAM1;
                btn_authenticate1.setText("Start TAM1 Authenticate Test");
                btn_authenticate1.setBackground(new java.awt.Color(0, 192, 0));
                break;
            case ButtonState.StopTAM2:
                startButtonState = ButtonState.StopTAM2;
                btn_authenticate2.setText("Stop TAM2 Authenticate Test");
                btn_authenticate2.setBackground(java.awt.Color.red);
                break;
            case ButtonState.StartTAM2:
                startButtonState = ButtonState.StartTAM2;
                btn_authenticate2.setText("Start TAM2 Authenticate Test");
                btn_authenticate2.setBackground(new java.awt.Color(0, 192, 0));
                break;
            case ButtonState.Unknown:
                startButtonState = ButtonState.Unknown;
                btn_start.setText("Search DNA Tag");
                btn_start.setBackground(new java.awt.Color(0, 192, 0));
                btn_clear.setEnabled(true);
                btn_authenticate1.setText("Start TAM1 Authenticate Test");
                btn_authenticate1.setBackground(new java.awt.Color(0, 192, 0));
                btn_authenticate2.setText("Start TAM2 Authenticate Test");
                btn_authenticate2.setBackground(new java.awt.Color(0, 192, 0));
                break;
        }
    }

    class StartInventoryThread implements Runnable
    {
        private String ip;
        private String mac;
        private TIDInventory inventory;

        StartInventoryThread(String ip, String mac, TIDInventory inventory) {
            this.ip = ip;
            this.mac = mac;
            this.inventory = inventory;
        }

        public void run() {                   
            inventory.StartInventory(ip, region);
        }
    }
    
    class StartTestThread implements Runnable
    {
        private String ip;
        private String mac;
        private UcodeDNATest test;
        private int TAM;

        StartTestThread(String ip, String mac, UcodeDNATest test, int TAM) {
            this.ip = ip;
            this.mac = mac;
            this.test = test;
            this.TAM = TAM;
        }

        public void run() {
            if (TAM == 1)
                test.StartAuthenticateTest(Main.ipAddress[0], tf_key0.getText(), tf_accesspw.getText(), TAM);
            else
                test.StartAuthenticateTest(Main.ipAddress[0], tf_key1.getText(), tf_accesspw.getText(), TAM);
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Timer Task">
    public class MsgRateTask extends TimerTask {
        public void run() {
            if (startButtonState == ButtonState.StopTAM1)
            {
                if (lb_alert.getText().startsWith(" "))
                {
                    lb_alert.setForeground(Color.GREEN);
                    lb_alert.setText("TAM1 Authenticated Tag");
                }
                else
                {
                    lb_alert.setForeground(Color.GREEN);
                    lb_alert.setText(" ");
                }
            }
            else
            {
                if (lb_alert.getText().startsWith(" "))
                {
                    lb_alert.setForeground(Color.BLUE);
                    lb_alert.setText("TAM2 Authenticated Tag");
                }
                else
                {
                    lb_alert.setForeground(Color.BLUE);
                    lb_alert.setText(" ");
                }
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Log">
    public void SaveTAM1Log(String epc, boolean result) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        PrintWriter out;

        try {
            date = new Date();
            if (result)
            {
                out = new PrintWriter(new BufferedWriter(new FileWriter(Main.applicationSettings + "EPC "+ epc + " successful TAM1 authentication.txt", true)));
            }
            else
            {
                out = new PrintWriter(new BufferedWriter(new FileWriter(Main.applicationSettings + "EPC "+ epc + " unsuccessful TAM1 authentication.txt", true)));
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Could not write to log file " + e.toString());
        }
    }
    
    public void SaveTAM2Log(String epc, String data, boolean result) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        PrintWriter out;

        try {
            date = new Date();
            if (result)
            {
                out = new PrintWriter(new BufferedWriter(new FileWriter(Main.applicationSettings + "EPC "+ epc + " successful TAM2 authentication.txt", true)));
                out.println(data);
            }
            else
            {
                out = new PrintWriter(new BufferedWriter(new FileWriter(Main.applicationSettings + "EPC "+ epc + " unsuccessful TAM2 authentication.txt", true)));
            }
            out.close();
        } catch (IOException e) {
            System.out.println("Could not write to log file " + e.toString());
        }
    }
    // </editor-fold>

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TagInventoryFrame(0).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_authenticate;
    private javax.swing.JButton btn_authenticate1;
    private javax.swing.JButton btn_authenticate2;
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_start;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lb_alert;
    private javax.swing.JLabel lb_result;
    private javax.swing.JTable table_inventory;
    private javax.swing.JTextField tf_accesspw;
    private javax.swing.JTextField tf_key0;
    private javax.swing.JTextField tf_key1;
    private javax.swing.JTextField tf_selected;
    // End of variables declaration//GEN-END:variables

}

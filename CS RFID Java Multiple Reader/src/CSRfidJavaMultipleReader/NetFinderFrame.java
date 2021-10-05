package CSRfidJavaMultipleReader;

import Netfinder.Structures.DeviceInformation;
import Netfinder.Events.DeviceFoundEventListener;
import Netfinder.Events.DeviceFoundEventArgs;
import Netfinder.Constants.Mode;
import javax.swing.*;
import java.awt.*;
import Netfinder.*;

/**
 *
 * @author gene.yeung
 */
public class NetFinderFrame extends javax.swing.JFrame implements DeviceFoundEventListener{

    // <editor-fold defaultstate="collapsed" desc="Variable">
    private String Info_Search = "Press \"Search\" button to search all CS203 in the same subnet.";
    private String Info_Select = "Hold \"Ctrl\" to select multiple CS203 devices on the list to connect.";
    private boolean m_start = false;
    private DefaultListModel deviceListModel;

    public static DeviceInformation selectedDeviceInfo = new DeviceInformation();
    public static boolean AssignOK = false;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Init">
    /** Creates new form NetFinderFrame */
    public NetFinderFrame() {
        initComponents();

        //Center the dialog
        Dimension size = getSize();
        setLocation((Main.screen.width - size.width)/2, (Main.screen.height - size.height)/2);

        lbl_info.setText(Info_Search);
        deviceListModel = (DefaultListModel)list_device.getModel();

        Main.netfinder = new NetFinder();

        AttachCallback(true);

        this.setIconImage(new ImageIcon("CSL Logo.jpg").getImage());

        this.setTitle("Search Device - ver "+Main.demeAppVer);
    }
    // </editor-fold>

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_inventory = new javax.swing.JButton();
        lbl_info = new javax.swing.JLabel();
        btn_clear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        list_device = new javax.swing.JList();
        btn_start = new javax.swing.JButton();
        btn_ucode7 = new javax.swing.JButton();
        btn_tidinventory = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Search Device");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        btn_inventory.setBackground(java.awt.Color.cyan);
        btn_inventory.setText("Inventory");
        btn_inventory.setEnabled(false);
        btn_inventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_inventoryActionPerformed(evt);
            }
        });

        lbl_info.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbl_info.setForeground(java.awt.Color.blue);
        lbl_info.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_info.setText("Select any RFID reader on the list");

        btn_clear.setBackground(java.awt.Color.orange);
        btn_clear.setText("Clear");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        list_device.setModel(new DefaultListModel());
        list_device.setCellRenderer(new ListRenderer());
        list_device.setFixedCellHeight(65);
        list_device.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                list_deviceValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(list_device);

        btn_start.setBackground(java.awt.Color.green);
        btn_start.setText("Search");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });

        btn_ucode7.setBackground(java.awt.Color.cyan);
        btn_ucode7.setText("UCODE 7");
        btn_ucode7.setEnabled(false);
        btn_ucode7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ucode7ActionPerformed(evt);
            }
        });

        btn_tidinventory.setBackground(java.awt.Color.cyan);
        btn_tidinventory.setText("TID Inventory");
        btn_tidinventory.setEnabled(false);
        btn_tidinventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_tidinventoryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(btn_start, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_ucode7, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_tidinventory)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_info, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(lbl_info)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_start, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_inventory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_ucode7, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tidinventory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Event Handle">
    private void AttachCallback(boolean en)
    {
        if (en)
        {
            Main.netfinder.addDeviceFoundEventListener(this);
        }
        else
        {
            Main.netfinder.removeDeviceFoundEventListener(this);
        }
    }

    public void DeviceFoundEvent(DeviceFoundEventArgs ev)
    {
        DeviceInformation entry = ev.info;
        deviceListModel.addElement(entry);
        lbl_info.setText(Info_Select);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Handle">
    private void btn_inventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_inventoryActionPerformed
        if (m_start) {
            m_start = false;
            Main.netfinder.Stop();
            btn_start.setBackground(Color.green);
            btn_start.setText("Search");
        }

        int selectedSize = list_device.getSelectedIndices().length;
        if (selectedSize > 50) selectedSize = 50;
        Main.ipAddress = new String[selectedSize];
        Main.macAddress = new String[selectedSize];
        Main.deviceName = new String[selectedSize];
        for (int i = 0; i < selectedSize; i++)
        {
            selectedDeviceInfo = (DeviceInformation)list_device.getSelectedValues()[i];
            Main.ipAddress[i] = Main.netfinder.GetIpName(selectedDeviceInfo.ip);
            Main.macAddress[i] = Main.netfinder.GetMacName(selectedDeviceInfo.mac);
            Main.deviceName[i] = selectedDeviceInfo.device_name;
        }

        new TagInventoryFrame().setVisible(true);

        Close();
}//GEN-LAST:event_btn_inventoryActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        lbl_info.setText(Info_Search);
        btn_inventory.setEnabled(false);
        btn_ucode7.setEnabled(false);
        btn_tidinventory.setEnabled(false);
        Main.netfinder.ClearDeviceList();
        deviceListModel.clear();
        Main.netfinder.ResearchDevice();
}//GEN-LAST:event_btn_clearActionPerformed

    private void list_deviceValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_list_deviceValueChanged
        if (list_device.getSelectedIndex() >= 0) {
            switch (((DeviceInformation)list_device.getSelectedValue()).mode) {
                case Mode.Bootloader:
                    btn_inventory.setEnabled(false);
                    btn_ucode7.setEnabled(false);
                    btn_tidinventory.setEnabled(false);
                    break;
                case Mode.Normal:
                    btn_inventory.setEnabled(true);
                    btn_ucode7.setEnabled(true);
                    btn_tidinventory.setEnabled(true);
                    break;
            }
        } else {
            btn_inventory.setEnabled(false);
            btn_ucode7.setEnabled(false);
            btn_tidinventory.setEnabled(false);
        }
}//GEN-LAST:event_list_deviceValueChanged

    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_startActionPerformed
        if (!m_start) {
            m_start = true;
            Main.netfinder.SearchDevice();
            btn_start.setBackground(Color.red);
            btn_start.setText("Stop");
        } else {
            m_start = false;
            Main.netfinder.Stop();
            btn_start.setBackground(Color.green);
            btn_start.setText("Search");
        }
        btn_inventory.setEnabled(false);
}//GEN-LAST:event_btn_startActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Close();
    }//GEN-LAST:event_formWindowClosing

    private void btn_ucode7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ucode7ActionPerformed
        if (m_start) {
            m_start = false;
            Main.netfinder.Stop();
            btn_start.setBackground(Color.green);
            btn_start.setText("Search");
        }

        int selectedSize = 1;
        Main.ipAddress = new String[selectedSize];
        Main.macAddress = new String[selectedSize];
        Main.deviceName = new String[selectedSize];
        for (int i = 0; i < selectedSize; i++)
        {
            selectedDeviceInfo = (DeviceInformation)list_device.getSelectedValues()[i];
            Main.ipAddress[i] = Main.netfinder.GetIpName(selectedDeviceInfo.ip);
            Main.macAddress[i] = Main.netfinder.GetMacName(selectedDeviceInfo.mac);
            Main.deviceName[i] = selectedDeviceInfo.device_name;
        }

        new TagUcode7Frame().setVisible(true);

        Close();
    }//GEN-LAST:event_btn_ucode7ActionPerformed

    private void btn_tidinventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_tidinventoryActionPerformed
        if (m_start) {
            m_start = false;
            Main.netfinder.Stop();
            btn_start.setBackground(Color.green);
            btn_start.setText("Search");
        }

        int selectedSize = list_device.getSelectedIndices().length;
        if (selectedSize > 50) selectedSize = 50;
        Main.ipAddress = new String[selectedSize];
        Main.macAddress = new String[selectedSize];
        Main.deviceName = new String[selectedSize];
        for (int i = 0; i < selectedSize; i++)
        {
            selectedDeviceInfo = (DeviceInformation)list_device.getSelectedValues()[i];
            Main.ipAddress[i] = Main.netfinder.GetIpName(selectedDeviceInfo.ip);
            Main.macAddress[i] = Main.netfinder.GetMacName(selectedDeviceInfo.mac);
            Main.deviceName[i] = selectedDeviceInfo.device_name;
        }

        new TagTIDInventoryFrame().setVisible(true);

        Close();
    }//GEN-LAST:event_btn_tidinventoryActionPerformed

    private void Close()
    {
        AttachCallback(false);
        Main.netfinder.Stop();
        Main.netfinder.ClearDeviceList();
        Main.netfinder.Dispose();
        dispose();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="List Renderer">
    class ListRenderer extends JPanel implements ListCellRenderer {
        //private final JTextArea contentArea;
        //private final JLabel device_name = new JLabel();
        private final JCheckBox device_name = new JCheckBox();
        private final JLabel description = new JLabel();
        private final JLabel IP_address = new JLabel();
        private final JLabel MAC_address = new JLabel();
        //private JCheckBox checkbox = new JCheckBox();;

        public ListRenderer() {
            setOpaque(true);
            device_name.setOpaque(true);

            device_name.setFont(new java.awt.Font("Arial", 1, 18));
            device_name.setText("");
            description.setText("");
            IP_address.setFont(new java.awt.Font("Arial", 1, 18));
            IP_address.setForeground(java.awt.Color.blue);
            IP_address.setText("IP Address:");
            MAC_address.setText("MAC Address:");

            javax.swing.GroupLayout ListItemLayout = new javax.swing.GroupLayout(this);
            setLayout(ListItemLayout);
            ListItemLayout.setHorizontalGroup(
                ListItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ListItemLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(ListItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(device_name, 300, 300, 300)
                        .addComponent(description, 300, 300, 300))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                    .addGroup(ListItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(IP_address, 250, 250, 250)
                        .addComponent(MAC_address, 250, 250, 250)))
            );
            ListItemLayout.setVerticalGroup(
                ListItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ListItemLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(ListItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(device_name)
                        .addComponent(IP_address))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(ListItemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(description)
                        .addComponent(MAC_address))
                    .addContainerGap(15, Short.MAX_VALUE))
            );
        }

        public Component getListCellRendererComponent(
                                               JList list,
                                               Object value,
                                               int index,
                                               boolean isSelected,
                                               boolean cellHasFocus)
        {
            device_name.setSelected (isSelected);

            /*if (isSelected && !device_name.isSelected())
            {
                 device_name.setSelected(true);
            }
            else
                 device_name.setSelected(false);*/
            setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, false));
            DeviceInformation entry = (DeviceInformation)value;
            setBackground(entry.mode == Mode.Normal ? Color.green : Color.yellow);
            device_name.setBackground(entry.mode == Mode.Normal ? Color.green : Color.yellow);
            device_name.setText(entry.device_name);
            description.setText(entry.description);
            IP_address.setText("IP Address: "+ Main.netfinder.GetIpName(entry.ip));
            MAC_address.setText("MAC Address: "+Main.netfinder.GetMacName(entry.mac));
            return this;
        }
    }
    // </editor-fold>

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NetFinderFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_clear;
    private javax.swing.JButton btn_inventory;
    private javax.swing.JButton btn_start;
    private javax.swing.JButton btn_tidinventory;
    private javax.swing.JButton btn_ucode7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_info;
    private javax.swing.JList list_device;
    // End of variables declaration//GEN-END:variables

}
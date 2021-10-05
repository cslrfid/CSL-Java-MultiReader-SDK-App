package Netfinder;

import Netfinder.Structures.DeviceInformation;
import Netfinder.Events.UpdatePercentEventArgs;
import Netfinder.Events.UpdatePercentEventListener;
import Netfinder.Events.AssignResultEventArgs;
import Netfinder.Events.UpdateResultEventListener;
import Netfinder.Events.DeviceFoundEventListener;
import Netfinder.Events.DeviceFoundEventArgs;
import Netfinder.Events.UpdateResultEventArgs;
import Netfinder.Events.AssignResultEventListener;
import Netfinder.Constants.Result;
import Netfinder.Constants.RecvOperation;
import Netfinder.Constants.UpdateResult;
import java.util.*;
import java.net.*;
import java.io.*;
import Tools.*;

/**
 * Search device on ethernet
 */
public class NetFinder {
    // <editor-fold defaultstate="collapsed" desc="Variable">
    private int m_rand = 0;
    private static final Object m_lock = new Object();
    private int m_operation = RecvOperation.CLOSED;
    private DatagramSocket m_broadcast = null;
    private boolean m_stop = true;
    private boolean m_stoped = true;
    private boolean m_research = false;
    private NetDisplay m_netdisplay = new NetDisplay();
    private byte[] m_CS203IP = new byte[4];
    private boolean m_isBroadcast = true;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Public Event Handler">
    private Vector DeviceFoundEventSubscribers = new Vector();
    /**
     * add DeviceFoundEvent listener
     * @param cls class that implement the DeviceFoundEventListener interface
     */
    public void addDeviceFoundEventListener(DeviceFoundEventListener cls){
        DeviceFoundEventSubscribers.add(cls);
    }
    /**
     * remove DeviceFoundEvent listener
     * @param cls class that implement the DeviceFoundEventListener interface
     */
    public void removeDeviceFoundEventListener(DeviceFoundEventListener cls){
        DeviceFoundEventSubscribers.remove(cls);
    }

    private Vector AssignResultEventSubscribers = new Vector();
    /**
     * add AssignResultEvent listener
     * @param cls class that implement the AssignResultEventListener interface
     */
    public void addAssignResultEventListener(AssignResultEventListener cls){
        AssignResultEventSubscribers.add(cls);
    }
    /**
     * remove AssignResultEvent listener
     * @param cls class that implement the AssignResultEventListener interface
     */
    public void removeAssignResultEventListener(AssignResultEventListener cls){
        AssignResultEventSubscribers.remove(cls);
    }

    private Vector UpdateResultEventSubscribers = new Vector();
    /**
     * add UpdateResultEvent listener
     * @param cls class that implement the UpdateResultEventListener interface
     */
    public void addUpdateResultEventListener(UpdateResultEventListener cls){
        UpdateResultEventSubscribers.add(cls);
    }
    /**
     * remove UpdateResultEvent listener
     * @param cls class that implement the UpdateResultEventListener interface
     */
    public void removeUpdateResultEventListener(UpdateResultEventListener cls){
        UpdateResultEventSubscribers.remove(cls);
    }

    private Vector UpdatePercentEventSubscribers = new Vector();
    /**
     * add UpdatePercentEvent listener
     * @param cls class that implement the UpdatePercentEventListener interface
     */
    public void addUpdatePercentEventListener(UpdatePercentEventListener cls){
        UpdatePercentEventSubscribers.add(cls);
    }
    /**
     * remove UpdatePercentEvent listener
     * @param cls class that implement the UpdatePercentEventListener interface
     */
    public void removeUpdatePercentEventListener(UpdatePercentEventListener cls){
        UpdatePercentEventSubscribers.remove(cls);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Default Constructor
     */
    public NetFinder()
    {
        try {
            m_broadcast = new DatagramSocket();
            m_broadcast.setBroadcast(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Start Listening here
        m_operation = RecvOperation.IDLE;

        new Thread(new RecvThread()).start();
    }

    /**
     * Dispose resource
     */
    public void Dispose()
    {
        //Stop Operation before dispose
        Stop();

        m_operation = RecvOperation.CLOSED;

        try
        {
            Thread.sleep(1);
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

        if (m_broadcast != null)
            m_broadcast.close();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Search and Stop">
    /**
    * Start to broadcast search device on ethernet continuously until Stop function called.
    */
    public void SearchDevice()
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return;
        }

        m_isBroadcast = true;

        m_operation = RecvOperation.SEARCH;

        new Thread(new StartDeviceDiscovery()).start();
    }

    /**
    * Start to direct search device on ethernet continuously until Stop function called.
    * @param ip Destination IP Address
    */
    public void DirectSearchDevice(byte[] ip)
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return;
        }

        m_isBroadcast = false;

       if (ip.length != 4)
            return;

        m_CS203IP[0] = ip[0];
        m_CS203IP[1] = ip[1];
        m_CS203IP[2] = ip[2];
        m_CS203IP[3] = ip[3];

        m_operation = RecvOperation.SEARCH;

        new Thread(new StartDeviceDiscovery()).start();
    }

    /**
     * Start to re-search device on ethernet continuously until Stop function called.
     */
    public void ResearchDevice()
    {
        if (m_operation == RecvOperation.SEARCH)
        {
            synchronized (m_lock)
            {
                m_research = true;
            }
        }
    }


    /**
    * Stop to search
    */
    public void Stop()
    {
        m_operation = RecvOperation.IDLE;

        synchronized (m_lock)
        {

            m_stop = true;

            while (!m_stoped)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (Exception ex)
                {
                    ex.getMessage();
                }
            }
        }
    }

    class StartDeviceDiscovery implements Runnable
    {
        StartDeviceDiscovery() {
        }

        public void run() {
            m_stoped = false;
            m_stop = false;

            while (true)
            {
                SendBroadcast();
                do
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (Exception ex)
                    {
                        ex.getMessage();
                    }

                    ResendBroadcast();

                    if (m_research)
                        break;

                } while (!m_stop);

                m_research = false;

                if (m_stop)
                    break;
            }
            m_stoped = true;
        }
    }

    /**
    * Clear all device list
    */
    public void ClearDeviceList()
    {
        m_netdisplay.RemoveAllEntries();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Assign">
    /**
     * Change IP Address
     * @param TargetMAC Target MAC address
     * @param CS203IP IP to be changed
     * @return true if success
     */
    public boolean AssignDevice(byte[] TargetMAC, byte[] CS203IP)
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return false;
        }

        if (CS203IP == null || CS203IP.length != 4 || TargetMAC == null || TargetMAC.length != 6)
            return false;

        DeviceInformation entry = GetTargetEntry(TargetMAC);

        System.arraycopy(CS203IP, 0, m_CS203IP, 0, 4);

        m_operation = RecvOperation.ASSIGN;

        return SendAssignment(entry.ip, CS203IP, TargetMAC, entry.device_name, entry.DHCP, entry.dhcp_retry, entry.subnet, entry.gateway);
    }
    /**
     * Change IP Address and TCP timeout
     * @param TargetMAC Target MAC address
     * @param CS203IP CS203IP IP to be changed
     * @param DHCP_Retry DHCP retry count
     * @return true if success
     */
    public boolean AssignDevice(byte[] TargetMAC, byte[] CS203IP, short DHCP_Retry)
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return false;
        }

        if (CS203IP == null || CS203IP.length != 4 || TargetMAC == null || TargetMAC.length != 6)
            return false;

        DeviceInformation entry = GetTargetEntry(TargetMAC);

        System.arraycopy(CS203IP, 0, m_CS203IP, 0, 4);

        m_operation = RecvOperation.ASSIGN;

        return SendAssignment(entry.ip, CS203IP, TargetMAC, entry.device_name, entry.DHCP, DHCP_Retry, entry.subnet, entry.gateway);
    }
    /**
     * Change IP Address, device name and TCP timeout
     * @param TargetMAC Target MAC address
     * @param CS203IP CS203IP IP to be changed
     * @param DeviceName device name
     * @param DHCP_Retry DHCP retry count
     * @return true if success
     */
    public boolean AssignDevice(byte[] TargetMAC, byte[] CS203IP, String DeviceName, short DHCP_Retry)
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return false;
        }

        if (CS203IP == null || CS203IP.length != 4 || DeviceName == null || DeviceName.length() > 31)
            return false;

        DeviceInformation entry = GetTargetEntry(TargetMAC);

        System.arraycopy(CS203IP, 0, m_CS203IP, 0, 4);

        m_operation = RecvOperation.ASSIGN;

        return SendAssignment(entry.ip, CS203IP, TargetMAC, DeviceName, entry.DHCP, DHCP_Retry, entry.subnet, entry.gateway);
    }
    /**
     * Change IP Address, device name, TCP timeout, DHCP mode, subnet mask and gateway address
     * @param TargetMAC Target MAC address
     * @param CS203IP CS203IP IP to be changed
     * @param DeviceName device name
     * @param DHCP_Retry DHCP retry count
     * @param Dhcp DHCP mode
     * @param Subnet Subnet mask
     * @param Gateway Gateway address
     * @return true if success
     */
    public boolean AssignDevice(byte[] TargetMAC, byte[] CS203IP, String DeviceName, short DHCP_Retry, boolean Dhcp, byte[] Subnet, byte[] Gateway)
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return false;
        }

        if (CS203IP == null || CS203IP.length != 4 || DeviceName == null || DeviceName.length() > 31)
            return false;

        DeviceInformation entry = GetTargetEntry(TargetMAC);

        System.arraycopy(CS203IP, 0, m_CS203IP, 0, 4);

        m_operation = RecvOperation.ASSIGN;

        return SendAssignment(entry.ip, CS203IP, TargetMAC, DeviceName, Dhcp, DHCP_Retry, Subnet, Gateway);
    }

    /**
     * Change IP Address, device name, TCP timeout and DHCP modeC
     * @param TargetMAC Target MAC address
     * @param CS203IP CS203IP IP to be changed
     * @param DeviceName device name
     * @param DHCP_Retry DHCP retry count
     * @param Dhcp DHCP mode
     * @return true if success
     */
    public boolean AssignDevice(byte[] TargetMAC, byte[] CS203IP, String DeviceName, short DHCP_Retry, boolean Dhcp)
    {
        if (m_operation != RecvOperation.IDLE)
        {
            return false;
        }

        if (CS203IP == null || CS203IP.length != 4 || DeviceName == null || DeviceName.length() > 31)
            return false;

        DeviceInformation entry = GetTargetEntry(TargetMAC);

        System.arraycopy(CS203IP, 0, m_CS203IP, 0, 4);

        m_operation = RecvOperation.ASSIGN;

        return SendAssignment(entry.ip, CS203IP, TargetMAC, DeviceName, Dhcp, DHCP_Retry, entry.subnet, entry.gateway);
    }

    private DeviceInformation GetTargetEntry(byte[] TargetMAC)
    {
        byte[] table_mac = new byte[6];
        // Check the mac address of each cell in the table
        for (int k = 0; k < m_netdisplay.GetNumCells(); k++)
        {

            // Fill <table_mac> with the mac address from the table
            m_netdisplay.GetMACAddress(k, table_mac);

            // If the mac address matches a device in the table,
            // ignore this packet

            boolean equal = true;
            for (int j = 0; j < table_mac.length; j++)
            {
                equal &= table_mac[j] == TargetMAC[j];
                if (!equal)
                    break;
            }

            if (equal)
                return m_netdisplay.GetEntry(k);
        }
        return null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Send">
    private void SendBroadcast()
    {
        Random rand = new Random();
        byte[] buff = new byte[] { 0, 0, 0, 0 };

        m_rand = rand.nextInt(0x8000); // rand returns a number between 0 and 0x7FFF

        buff[2] = (byte)(m_rand >> 8);
        buff[3] = (byte)(m_rand & 0x00FF);

        try
        {
            InetAddress broadcast;
            if (m_isBroadcast)
            {
                broadcast=InetAddress.getByName("255.255.255.255");
            }
            else
            {
                broadcast=InetAddress.getByAddress(m_CS203IP);
            }
            DatagramPacket sendPacket = new DatagramPacket(buff,buff.length,broadcast,3040);
            m_broadcast.send(sendPacket);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ResendBroadcast()
    {
        Random rand = new Random();
        byte[] buff = new byte[] { 0, 0, 0, 0 };

        m_rand = rand.nextInt(0x8000); // rand returns a number between 0 and 0x7FFF

        buff[2] = (byte)(m_rand >> 8);
        buff[3] = (byte)(m_rand & 0x00FF);

        try
        {
            InetAddress broadcast;
            if (m_isBroadcast)
            {
                broadcast=InetAddress.getByName("255.255.255.255");
            }
            else
            {
                broadcast=InetAddress.getByAddress(m_CS203IP);
            }
            DatagramPacket sendPacket = new DatagramPacket(buff,buff.length,broadcast,3040);
            m_broadcast.send(sendPacket);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean SendAssignment(byte[] TARGET_IP, byte[] CS203_IP, byte[] CS203_MAC, String NAME, boolean Dhcp, short DHCP_Retry, byte[] Subnet, byte[] Gateway)
    {
        byte[] m_assignment_buff = BuildAssignBuffer(CS203_IP, NAME, Dhcp, CS203_MAC, (short)9091, DHCP_Retry, Subnet, Gateway);

        if (m_assignment_buff == null)
            return false;

        //-----------------------------------------------------------------
        // Send the packet directly to the device's MAC address (use new IP address)
        //-----------------------------------------------------------------
        try
        {
            /*byte[] IP = new byte[4];
            IP[0] = (byte)TARGET_IP[0];
            IP[1] = (byte)TARGET_IP[1];
            IP[2] = (byte)TARGET_IP[2];
            IP[3] = (byte)TARGET_IP[3];
            InetAddress broadcast=InetAddress.getByAddress(IP);*/
            InetAddress broadcast=InetAddress.getByName("255.255.255.255");
            DatagramPacket sendPacket = new DatagramPacket(m_assignment_buff,m_assignment_buff.length,broadcast,3040);
            m_broadcast.send(sendPacket);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private byte[] BuildAssignBuffer(byte[] CS203IP, String DEVICENAME, boolean Dhcp, byte[] MAC, short PORT, short DHCP_Retry, byte[] Subnet, byte[] Gateway)
    {
        //Check Input Value
        if (CS203IP == null || CS203IP.length != 4 ||
            MAC == null || MAC.length != 6 ||
            Subnet == null || Subnet.length != 4 ||
            Gateway == null || Gateway.length != 4)
            return null;

        byte[] IP = new byte[4]; // contains the old IP address
        byte[] buf = new byte[17];
        byte[] m_assignment_buff = new byte[65];

        int i;
        Random rand = new Random();

        //-----------------------------------------------------------------
        // Fill buffer with Assignment Packet
        //-----------------------------------------------------------------
        //
        for (i = 0; i < m_assignment_buff.length; i++)
        {
            m_assignment_buff[i] = 0;
        }

        m_rand = rand.nextInt(0x8000); // rand returns a number between 0 and 0x7FFF

        m_assignment_buff[0] = 0x02;
        m_assignment_buff[1] = 0x00;
        m_assignment_buff[2] = (byte)(m_rand >> 8);
        m_assignment_buff[3] = (byte)(m_rand & 0x00FF);


        // Get IP Address
        m_assignment_buff[4] = (byte)CS203IP[0]; //m_assignment_buff[4] ~ [7]
        m_assignment_buff[5] = (byte)CS203IP[1];
        m_assignment_buff[6] = (byte)CS203IP[2];
        m_assignment_buff[7] = (byte)CS203IP[3];
        //Array.Copy(SERVERIP, 0, m_assignment_buff, 8, 4);   //m_assignment_buff[8] ~ [11]

        m_assignment_buff[12] = (byte)(PORT / 256);
        m_assignment_buff[13] = (byte)(PORT % 256);

        m_assignment_buff[14] = (byte)(DHCP_Retry & 0xff);

        m_assignment_buff[15] = (byte)(Dhcp ? 0x0 : 0x1);

        /*m_assignment_buff[16] = (byte)0x00;
        m_assignment_buff[17] = (byte)0x05;
        m_assignment_buff[18] = (byte)0x7b;
        m_assignment_buff[19] = (byte)0x82;
        m_assignment_buff[20] = (byte)0x00;
        m_assignment_buff[21] = (byte)0x07;*/

        //MAC address (buff 16 - 21)
        m_assignment_buff[16] = (byte)MAC[0]; //m_assignment_buff[16] ~ [21]
        m_assignment_buff[17] = (byte)MAC[1];
        m_assignment_buff[18] = (byte)MAC[2];
        m_assignment_buff[19] = (byte)MAC[3];
        m_assignment_buff[20] = (byte)MAC[4];
        m_assignment_buff[21] = (byte)MAC[5];
        
        /*m_assignment_buff[16] = (byte)0x00; //m_assignment_buff[16] ~ [21]
        m_assignment_buff[17] = (byte)0x05;
        m_assignment_buff[18] = (byte)0x7b;
        m_assignment_buff[19] = (byte)0x77;
        m_assignment_buff[20] = (byte)0x00;
        m_assignment_buff[21] = (byte)0x85;*/

        // buff 22 + 23 are zero
        byte[] bytes = DEVICENAME.getBytes();

        System.arraycopy(bytes, 0, m_assignment_buff, 24, bytes.length);    //m_assignment_buff[16] ~ [21]

        m_assignment_buff[55] = (byte)'\0';

        // Get Subnet
        m_assignment_buff[56] = (byte)Subnet[0];
        m_assignment_buff[57] = (byte)Subnet[1];
        m_assignment_buff[58] = (byte)Subnet[2];
        m_assignment_buff[59] = (byte)Subnet[3];

        // Get Gateway
        m_assignment_buff[60] = (byte)Gateway[0];
        m_assignment_buff[61] = (byte)Gateway[1];
        m_assignment_buff[62] = (byte)Gateway[2];
        m_assignment_buff[63] = (byte)Gateway[3];
        
        m_assignment_buff[64] = 0;

        return m_assignment_buff;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Receive">
    class RecvThread implements Runnable
    {
        RecvThread() {
        }

        public void run() {
            while (true)
            {
                switch (m_operation)
                {
                    case RecvOperation.ASSIGN:
                        ReceivePacketFromAssign();
                        break;
                    case RecvOperation.SEARCH:
                        ReceivePacketFromSearch();
                        break;
                    case RecvOperation.IDLE:
                        try
                        {
                            Thread.sleep(1);
                        }
                        catch (Exception ex)
                        {
                            ex.getMessage();
                        }
                        break;
                    case RecvOperation.CLOSED:
                        return;
                }
            }
        }
    }

    private boolean ReceivePacketFromSearch()
    {

        byte[] buffer = new byte[4096];
        int num_bytes = 0;
        int check = 0;

        //---------------------------------------------------------------
        // Receive Packet from Buffer
        //---------------------------------------------------------------
        try
        {
            DatagramPacket getPacket = new DatagramPacket(buffer,buffer.length);
            m_broadcast.receive(getPacket);
            num_bytes = getPacket.getLength();
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("timeout");
            return false;
        }
        catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        // Check for errors
        if (num_bytes >= 4096)
        {
            // Handle Error
            System.out.println("Error receiving data");
            return false;
        }

        //---------------------------------------------------------------
        // Verify Packet
        //---------------------------------------------------------------
        //
        //	Check minimum packet size
        //  Check packet type field (must be 0x01)
        //	Verify random number
        //
        //
        if ((num_bytes >= 40) && (buffer[0] == 0x01) && (buffer[2] == (byte)((m_rand >> 8) & 0x00FF))
                                                     && (buffer[3] == (byte)(m_rand & 0x00FF))
          )
        {
            //---------------------------------------------------------------
            // Check if entry already exists
            //---------------------------------------------------------------
            byte[] mac = new byte[6];
            byte[] table_mac = new byte[6];
            int k;

            // Fill <mac> with the mac address from the packet

            //memcpy(mac, &buffer[14], 6);
            System.arraycopy(buffer, 14, mac, 0, 6);

            // Check the mac address of each cell in the table
            for (k = 0; k < m_netdisplay.GetNumCells(); k++)
            {

                // Fill <table_mac> with the mac address from the table
                m_netdisplay.GetMACAddress(k, table_mac);

                // If the mac address matches a device in the table,
                // ignore this packet

                boolean equal = true;
                for (int j = 0; j < table_mac.length; j++)
                {
                    equal &= table_mac[j] == mac[j];
                    if (!equal)
                        break;
                }

                if (equal)
                {
                    return false;
                }

            }

            //---------------------------------------------------------------
            // Add Entry
            //---------------------------------------------------------------

            DeviceInformation entry = new DeviceInformation();

            entry.mode = (int)buffer[1];
            entry.port = (short)m_broadcast.getPort();

            int i = 4; // Start buffer index at 4

            entry.time_on_powered.days = MemoryMap.unsignedByteToInt(buffer[i++]) << 8;
            entry.time_on_powered.days |= MemoryMap.unsignedByteToInt(buffer[i++]);

            entry.time_on_powered.hours = MemoryMap.unsignedByteToInt(buffer[i++]);
            entry.time_on_powered.minutes = MemoryMap.unsignedByteToInt(buffer[i++]);


            entry.time_on_network.days = MemoryMap.unsignedByteToInt(buffer[i++]) << 8;
            entry.time_on_network.days |= MemoryMap.unsignedByteToInt(buffer[i++]);

            entry.time_on_network.hours = MemoryMap.unsignedByteToInt(buffer[i++]);
            entry.time_on_network.minutes = MemoryMap.unsignedByteToInt(buffer[i++]);

            entry.time_on_powered.seconds = MemoryMap.unsignedByteToInt(buffer[i++]);
            entry.time_on_network.seconds = MemoryMap.unsignedByteToInt(buffer[i++]);

            entry.mac[0] = buffer[i++];
            entry.mac[1] = buffer[i++];
            entry.mac[2] = buffer[i++];
            entry.mac[3] = buffer[i++];
            entry.mac[4] = buffer[i++];
            entry.mac[5] = buffer[i++];

            entry.ip[0] = buffer[i++];
            entry.ip[1] = buffer[i++];
            entry.ip[2] = buffer[i++];
            entry.ip[3] = buffer[i++];

            //entry.serverip[0] = buffer[i++];
            //entry.serverip[1] = buffer[i++];
            //entry.serverip[2] = buffer[i++];
            //entry.serverip[3] = buffer[i++];
            //Skip 4  bytes
            i += 4;
            //entry.serverport = (ushort)(buffer[i++] << 8 | buffer[i++]);
            i += 1;
            check = (int)buffer[i++];
            entry.dhcp_retry = MemoryMap.unsignedByteToShort(buffer[i++]);
            entry.DHCP = (buffer[i++] == 0);

            //if (check > 1)
            {
                entry.subnet[0] = buffer[i++];
                entry.subnet[1] = buffer[i++];
                entry.subnet[2] = buffer[i++];
                entry.subnet[3] = buffer[i++];

                entry.gateway[0] = buffer[i++];
                entry.gateway[1] = buffer[i++];
                entry.gateway[2] = buffer[i++];
                entry.gateway[3] = buffer[i++];
            }

            byte[] temp = new byte[buffer.length - i];

            System.arraycopy(buffer, i, temp, 0, temp.length);

            try
            {
                String myString = new String(temp, 0, buffer.length - i, "US-ASCII");

                String[] str = myString.split(String.valueOf('\0'), 5);

                entry.device_name = str[0];

                entry.description = str[1];

                entry.time_on_powered.name = str[2];

                entry.time_on_network.name = str[3];

                if (entry.description.startsWith("Image"))
                {
                    entry.version = entry.description.substring(7);
                }
                else if (entry.description.startsWith("Firmware"))
                {
                    entry.version = entry.description.substring(21);
                }
                else
                    entry.version = "0.0.0";
                
                if (check == 1)
                    entry.description += " (USB mode. Cannot connect.)";
                else if (check == 3)
                    entry.description += " (RS232 mode. Cannot connect.)";
                else if (check == 4)
                    entry.description += " (DBUART mode. Cannot connect.)";
                
                entry.gateway_check_mode = buffer[i+str[0].length()+str[1].length()+str[2].length()+str[3].length()+4];

                // Add the entry
                m_netdisplay.AddEntry(entry);

                DeviceFoundRaiseEvent(entry);

                return true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else if ((buffer[0] == 0x00) && (buffer[2] == (byte)((m_rand >> 8) & 0x00FF))
                                   && (buffer[3] == (byte)(m_rand & 0x00FF)))
        {
            // Discard Packet
            // We have received an identity request from another host.
            // This is a very rare case in which the PC randomly chooses port 3040
            // for the netfinder app and the same random number is chosen.
            //return ;
        }
        else
        {
            //System.out.println("Invalid Search Packet Received or Random Number Mismatch");
            return false;
        }
        return false;
    }

    private boolean ReceivePacketFromAssign()
    {
        byte[] buffer = new byte[4096];
        int num_bytes;
        InetAddress addr;

        //---------------------------------------------------------------
        // Receive Packet from Buffer
        //---------------------------------------------------------------
        try
        {
            do
            {
                m_broadcast.setSoTimeout(5000);
                DatagramPacket getPacket = new DatagramPacket(buffer,buffer.length);
                m_broadcast.receive(getPacket);
                num_bytes = getPacket.getLength();

                addr = getPacket.getAddress();
            } while (!addr.getHostAddress().equals(GetIpName(m_CS203IP)));

        }
        catch (SocketTimeoutException e)
        {
            m_operation = RecvOperation.IDLE;
            AssignResultRaiseEvent(Result.TIMEOUT);
            return false;
        }
        catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        // Check for errors
        if (num_bytes >= 4096)
        {
            // Handle Error
            System.out.println("Error receiving data");
            return false;
        }

        try
        {
            m_broadcast.setSoTimeout(0);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //---------------------------------------------------------------
        // Verify Packet
        //---------------------------------------------------------------
        if ((num_bytes == 4) && (buffer[0] == 0x03) &&
            (buffer[2] == (byte)((m_rand >> 8) & 0x00FF)) && (buffer[3] == (byte)(m_rand & 0x00FF)))
        {

            m_operation = RecvOperation.IDLE;

            // Decode ACK type
            switch (buffer[1])
            {
                case 0x01: // Programming Sucessful
                    AssignResultRaiseEvent(Result.ACCEPTED);
                    break;
                case 0x00: // Address Rejected due to mismatched MAC address
                    AssignResultRaiseEvent(Result.REJECTED);
                    break;
                default: // Unknown Error
                    AssignResultRaiseEvent(Result.UNKNOWN);
                    break;
            }

        }
        else
        {
            //System.out.println("Invalid Assign Packet Received");
            return false;
        }
        return false;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="AsyncUpdateBootloader">
    /**
     * Async update bootloader
     * @param IP target IP address
     * @param bootloaderFile bootloader file
     */
    public void AsyncUpdateBootloader(byte[] IP, File bootloaderFile)
    {
        new Thread(new UpdateEbootThread(IP, bootloaderFile)).start();
    }

    class UpdateEbootThread implements Runnable
    {
        byte[] TARGET_IP;
        File bootloaderFile;
        int totalpercent = 0;
        byte[] Mode1 = new byte[] { (byte)'E', (byte)'B', (byte)'O', (byte)'O', (byte)'T', (byte)'U', (byte)'P', (byte)'G', (byte)'R', (byte)'A', (byte)'D', (byte)'E' };

        UpdateEbootThread(byte[] IP, File bootloaderFile) {
            this.TARGET_IP = IP;
            this.bootloaderFile = bootloaderFile;
        }

        public void run() {
            try
            {
                InetSocketAddress destAddr = new InetSocketAddress(InetAddress.getByAddress(TARGET_IP), 1515);
                Socket m_tcpsocket = new Socket();
                m_tcpsocket.setSoTimeout(4000);
                m_tcpsocket.connect(destAddr, 4000);

                //start eboot update
                byte[] OutBuf = new byte[132];
                int block = 0;
                //Trigger Eboot update mode
                m_tcpsocket.getOutputStream().write(Mode1);
                if (DecodeAck(block, m_tcpsocket))
                {
                    try {
                        FileInputStream fis = new FileInputStream(bootloaderFile);

                        // Here BufferedInputStream is added for fast reading.
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        DataInputStream dis = new DataInputStream(bis);

                        dis.skipBytes(0x800);
                        for (block = 1; block < 113; block++)
                        {

                            dis.read(OutBuf, 4, 128);

                            OutBuf[0] = 0;
                            OutBuf[1] = (byte)EBOOT.DATA;
                            OutBuf[2] = 0;
                            OutBuf[3] = (byte)block;

                            m_tcpsocket.getOutputStream().write(OutBuf);

                            if (!DecodeAck(block, m_tcpsocket))
                            {
                                UpdateResultRaiseEvent(UpdateResult.FAIL);
                                break;
                            }

                            totalpercent = (block * 100 / 112);

                            UpdatePercentRaiseEvent(totalpercent);

                            try
                            {
                                Thread.sleep(1);//Sleep 1 milsec to let it update progress
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }

                        // dispose all the resources after using them.
                        fis.close();
                        bis.close();
                        dis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    UpdateResultRaiseEvent(UpdateResult.SUCCESS);
                }
                else
                {
                    UpdateResultRaiseEvent(UpdateResult.FAIL);
                }

                m_tcpsocket.close();
            }
            catch (Exception e) {
                UpdateResultRaiseEvent(UpdateResult.FAIL);
                e.printStackTrace();
            }
        }
    }
    
    private class EBOOT
    {
        // Define packet types
        public static final int RRQ = 0x01;
        public static final int WRQ = 0x02;
        public static final int DATA = 0x03;
        public static final int ACK = 0x04;
        public static final int ERROR = 0x05;
    }
    
    private class EBOOT_ERR
    {
        // Error Codes
        public static final int NOT_DEFINED             = 0; // Not defined, see error message (if any).
        public static final int INVALID_OPCODE           = 1; // File not found.
        public static final int INVALID_BLOCK_NUMBER     = 2; // Access violation.
        public static final int INVALID_BLOCK_LENGTH     = 3; // Disk full or allocation exceeded.
        public static final int FLASH_ERROR              = 4; // Illegal operation.
        public static final int INVALID_PACKET_SIZE      = 5; // Unknown transfer ID.
    }

    @SuppressWarnings("empty-statement")
    private boolean DecodeAck(int block, Socket sock)
    {
        boolean ret = false;
        int type = EBOOT.ACK;
        int code = EBOOT_ERR.NOT_DEFINED; ;
        byte[] ch = new byte[4];
        int num_bytes;

        try
        {
            num_bytes = sock.getInputStream().read(ch);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        type = MemoryMap.unsignedByteToInt(ch[1]);
        code = MemoryMap.unsignedByteToInt(ch[3]);

        if (type == EBOOT.ACK)
        {
            if ((int)ch[3] == block)
                ret = true;
        }
        else if (type == EBOOT.ERROR)
        {
            switch (code)
            {
                case EBOOT_ERR.NOT_DEFINED:
                    System.out.println("Error: NOT DEFINED");
                    break;
                case EBOOT_ERR.INVALID_OPCODE:
                    System.out.println("Error: INVALID OPCODE");
                    break;
                case EBOOT_ERR.INVALID_BLOCK_NUMBER:
                    System.out.println(String.format("Error: INVALID BLOCK NUMBER %d", block));
                    break;
                case EBOOT_ERR.INVALID_BLOCK_LENGTH:
                    System.out.println("Error: INVALID BLOCK LENGTH");
                    break;
                case EBOOT_ERR.FLASH_ERROR:
                    System.out.println("Error: FLASH ERROR");
                    break;
                case EBOOT_ERR.INVALID_PACKET_SIZE:
                    System.out.println("Error: INVALID PACKET SIZE");
                    break;
                default:
                    System.out.println("Error: UNKNOW");
                    break;
            }
        }

        return ret;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="AsyncUpdateImage">
    /**
     * Async update image
     * @param IP target IP address
     * @param imageFile image file
     */
    public void AsyncUpdateImage(byte[] IP, File imageFile)
    {
        new Thread(new UpdateImageThread(IP, imageFile)).start();
    }

    class UpdateImageThread implements Runnable
    {
        byte[] TARGET_IP;
        File imageFile;

        UpdateImageThread(byte[] IP, File imageFile) {
            this.TARGET_IP = IP;
            this.imageFile = imageFile;
        }

        public void run() {
            try
            {
                int totalpercent = 0, prevpercent = -1, totalsent = 0;
                int len = 0, filelen = 0, rcvPort = 0;
                int packetNr = 0;
                byte[] sndBuffer = CreateRequestPacket(Opcodes.Write, "boot.img", Modes.Octet);
                byte[] rcvBuffer = new byte[516];
                byte[] fileBuffer = new byte[512];

                InetAddress destAddr = InetAddress.getByAddress(TARGET_IP);
                DatagramSocket tftpSocket = new DatagramSocket();
                tftpSocket.setSoTimeout(4000);

                // Request Writing to TFTP Server
                DatagramPacket sendPacket = new DatagramPacket(sndBuffer,sndBuffer.length,destAddr,69);
                tftpSocket.send(sendPacket);

                DatagramPacket getPacket = new DatagramPacket(rcvBuffer,rcvBuffer.length);
                tftpSocket.receive(getPacket);
                len = getPacket.getLength();
                rcvPort = getPacket.getPort();

                FileInputStream fis = new FileInputStream(imageFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
                DataInputStream dis = new DataInputStream(bis);
                filelen = dis.available();
                while (true)
                {
                    // handle any kind of error
                    if (MemoryMap.unsignedByteToInt(rcvBuffer[1]) == Opcodes.Error)
                    {
                        fis.close();
                        bis.close();
                        dis.close();
                        tftpSocket.close();
                        UpdateResultRaiseEvent(UpdateResult.FAIL);
                        return;
                    }

                    // expect the next packet ack
                    if ((MemoryMap.unsignedByteToInt(rcvBuffer[1]) == Opcodes.Ack) &&
                      (((MemoryMap.unsignedByteToInt(rcvBuffer[2])<< 8) & 0xff00) | MemoryMap.unsignedByteToInt(rcvBuffer[3])) == packetNr)
                    {
                        len = dis.read(fileBuffer, 0, 512);
                        if (len > 0)
                            sndBuffer = CreateDataPacket(++packetNr, fileBuffer);
                        else
                            sndBuffer = CreateDataPacket(++packetNr, null);
                        sendPacket = new DatagramPacket(sndBuffer,sndBuffer.length,destAddr,rcvPort);
                        tftpSocket.send(sendPacket);
                        totalsent += 512;
                    }

                    totalpercent = (totalsent * 100 / filelen);

                    if (totalpercent != prevpercent)
                    {
                        UpdatePercentRaiseEvent(totalpercent);
                        try
                        {
                            Thread.sleep(1);//Sleep 1 milsec to let it update progress
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        prevpercent = totalpercent;
                    }

                    // we are done
                    if (sndBuffer.length < 516)
                    {
                        break;
                    }
                    else
                    {
                        getPacket = new DatagramPacket(rcvBuffer,rcvBuffer.length);
                        tftpSocket.receive(getPacket);
                        len = getPacket.getLength();
                    }
                }

                fis.close();
                bis.close();
                dis.close();
                tftpSocket.close();
                UpdateResultRaiseEvent(UpdateResult.SUCCESS);
            }
            catch (Exception e) {
                UpdateResultRaiseEvent(UpdateResult.FAIL);
                e.printStackTrace();
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="TFTP packet">
    /// <summary>
    /// TFTP opcodes
    /// </summary>
    private class Opcodes
    {
        public static final int Unknown = 0;
        public static final int Read = 1;
        public static final int Write = 2;
        public static final int Data = 3;
        public static final int Ack = 4;
        public static final int Error = 5;
    }

    /// <summary>
    /// TFTP modes
    /// </summary>
    enum Modes
    {
        Unknown,
        NetAscii,
        Octet,
        Mail
    }
    
    /// <summary>
    /// Creates the request packet.
    /// </summary>
    /// <param name="opCode">The op code.</param>
    /// <param name="remoteFile">The remote file.</param>
    /// <param name="tftpMode">The TFTP mode.</param>
    /// <returns>the ack packet</returns>
    private byte[] CreateRequestPacket(int opCode, String remoteFile, Modes tftpMode)
    {
        // Create new Byte array to hold Initial
        // Read Request Packet
        int pos = 0;
        String modeAscii = tftpMode.toString().toLowerCase();
        byte[] ret = new byte[modeAscii.length() + remoteFile.length() + 4];

        // Set first Opcode of packet to indicate
        // if this is a read request or write request
        ret[pos++] = 0;
        ret[pos++] = (byte)opCode;

        try
        {
            // Convert Filename to a char array
            System.arraycopy(remoteFile.getBytes("US-ASCII"), 0, ret, pos, remoteFile.length());
            pos += remoteFile.length();
            ret[pos++] = 0;
            System.arraycopy(modeAscii.getBytes("US-ASCII"), 0, ret, pos, modeAscii.length());
            pos += modeAscii.length();
            ret[pos] = 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return ret;
    }

    /// <summary>
    /// Creates the data packet.
    /// </summary>
    /// <param name="blockNr">The packet nr.</param>
    /// <param name="data">The data.</param>
    /// <returns>the data packet</returns>
    private byte[] CreateDataPacket(int blockNr, byte[] data)
    {
        int datalen = 0;
        if (data == null)
            datalen = 0;
        else
            datalen = data.length;

        // Create Byte array to hold ack packet
        byte[] ret = new byte[4 + datalen];

        // Set first Opcode of packet to TFTP_ACK
        ret[0] = 0;
        ret[1] = (byte)Opcodes.Data;
        ret[2] = (byte)((blockNr >> 8) & 0xff);
        ret[3] = (byte)(blockNr & 0xff);
        if (datalen > 0)
            System.arraycopy(data, 0, ret, 4, datalen);
        return ret;
    }

    /// <summary>
    /// Creates the ack packet.
    /// </summary>
    /// <param name="blockNr">The block nr.</param>
    /// <returns>the ack packet</returns>
    private byte[] CreateAckPacket(int blockNr)
    {
        // Create Byte array to hold ack packet
        byte[] ret = new byte[4];

        // Set first Opcode of packet to TFTP_ACK
        ret[0] = 0;
        ret[1] = (byte)Opcodes.Ack;

        // Insert block number into packet array
        ret[2] = (byte)((blockNr >> 8) & 0xff);
        ret[3] = (byte)(blockNr & 0xff);
        return ret;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Raise Event">
    private void DeviceFoundRaiseEvent(DeviceInformation info)
    {
        for(int i=0, size = DeviceFoundEventSubscribers.size(); i < size; i++)
        {
            ((DeviceFoundEventListener)DeviceFoundEventSubscribers.get(i)).DeviceFoundEvent(new DeviceFoundEventArgs(this, info));
        }
    }
    private void AssignResultRaiseEvent(int result)
    {
        for(int i=0, size = AssignResultEventSubscribers.size(); i < size; i++)
        {
            ((AssignResultEventListener)AssignResultEventSubscribers.get(i)).AssignResultEvent(new AssignResultEventArgs(this, result));
        }
    }
    private void UpdateResultRaiseEvent(int result)
    {
        for(int i=0, size = UpdateResultEventSubscribers.size(); i < size; i++)
        {
            ((UpdateResultEventListener)UpdateResultEventSubscribers.get(i)).UpdateResultEvent(new UpdateResultEventArgs(this, result));
        }
    }
    private void UpdatePercentRaiseEvent(int percent)
    {
        for(int i=0, size = UpdatePercentEventSubscribers.size(); i < size; i++)
        {
            ((UpdatePercentEventListener)UpdatePercentEventSubscribers.get(i)).UpdatePercentEvent(new UpdatePercentEventArgs(this, percent));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Extern Function">
    /**
     * Get current operation state
     * @return operation state
     * @see CSLibrary.Net.Constants.RecvOperation
     */
    public int GetOperation()
    {
        return m_operation;
    }

    /**
     * Get ip address name
     * @param ip ip address in byte array format
     * @return ip address in String format
     */
    public String GetIpName(byte[] ip)
    {
        if (ip == null || ip.length != 4)
            return null;

        return String.format("%d.%d.%d.%d", MemoryMap.unsignedByteToInt(ip[0]), MemoryMap.unsignedByteToInt(ip[1]), MemoryMap.unsignedByteToInt(ip[2]), MemoryMap.unsignedByteToInt(ip[3]));
    }

    /**
     * Get mac address name
     * @param mac mac address in byte array format
     * @return mac address in String format
     */
    public String GetMacName(byte[] mac)
    {
        if (mac == null || mac.length != 6)
            return null;

        return String.format("%02X-%02X-%02X-%02X-%02X-%02X",
                            MemoryMap.unsignedByteToInt(mac[0]),
                            MemoryMap.unsignedByteToInt(mac[1]),
                            MemoryMap.unsignedByteToInt(mac[2]),
                            MemoryMap.unsignedByteToInt(mac[3]),
                            MemoryMap.unsignedByteToInt(mac[4]),
                            MemoryMap.unsignedByteToInt(mac[5]));
    }
    // </editor-fold>
}

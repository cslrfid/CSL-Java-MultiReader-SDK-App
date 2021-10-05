/**
 *
 * @author Gene Yeung - Convergence Systems Ltd
 *
 */

package CSRfidJavaMultipleReader;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UcodeDNATest{

    private Thread reset;
    private String ipAddress = "192.168.25.203";
    private String macAddress = "";
    private String applicationSettings = ""; 
    private String deviceName = "";
    public boolean stopInventory = false;
    public boolean found = false;
    
    private String selected_epc = "", custom_data = "";
    
    private Socket TCPDataSocket = null;
    private DataOutputStream TCPDataOut = null;
    private DataInputStream TCPDataIn = null;
    
    private Vector AsyncCallbackEventSubscribers = new Vector();
    public void addAsyncCallbackEventListener(AsyncCallbackEventListener cls){
        AsyncCallbackEventSubscribers.add(cls);
    }
    public void removeAsyncCallbackEventListener(AsyncCallbackEventListener cls){
        AsyncCallbackEventSubscribers.remove(cls);
    }
    
    public UcodeDNATest(String deviceName)
    {
        this.deviceName = deviceName;
    }
    
    public boolean Connect(String ip)
    {
        ipAddress = ip;
        
        byte[] inData=new byte[50];
        byte[] command_buff = new byte[8];
        int len=0;
                
        try {
        
            //open udp cport
            DatagramSocket UDPCtrlSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(ipAddress);
            byte[] bytes = address.getAddress();
            
            //build command packet
            command_buff[0] = (byte)0x80;
            command_buff[1] = (byte)bytes[0];
            command_buff[2] = (byte)bytes[1];
            command_buff[3] = (byte)bytes[2];
            command_buff[4] = (byte)bytes[3];
            command_buff[5] = 1;
            command_buff[6] = 0x17;
            command_buff[7] = 1;

            //send packet
            DatagramPacket sendPacket = new DatagramPacket(command_buff,command_buff.length,address,3041);

            //Enable TCP Notifications
            while(true)
            {
                UDPCtrlSocket.send(sendPacket);
                System.out.println("Enable TCP Notifications");
                
                try {
                    UDPCtrlSocket.setSoTimeout(300);
                    DatagramPacket getPacket = new DatagramPacket(inData,inData.length);
                    UDPCtrlSocket.receive(getPacket);
                    len = getPacket.getLength();
                    
                    if(len == 4 && 
                       inData[0] == (byte)0x81 && 
                       inData[1] == (byte)0x01)
                    {
                        break;
                    }
                }
                catch (Exception ex)
                {
                    System.err.println(ex.getMessage());
                }
            }
            
            UDPCtrlSocket.close();
            
            Thread.sleep(10);
            
            //open iport (1515)
            TCPDataSocket = new Socket(ipAddress, 1515);
            TCPDataOut = new DataOutputStream(TCPDataSocket.getOutputStream());
            TCPDataIn = new DataInputStream(new BufferedInputStream(TCPDataSocket.getInputStream()));
            
            return true;
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
            return false;
        }
    }
    
    public void Disconnect()
    {           
        try 
        {
            TCPDataOut.write(hexStringToByteArray("4003000000000000"));
            Thread.sleep(100);
            TCPDataSocket.close();
            Thread.sleep(4000);
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }
    }
    
    public boolean StartAuthenticateTest(String ip, String key, String access_pw, int TAM) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        String IPAddress, cmdString;
        int power = 300; // 300=30.0dBm
        int port=0; // antenna port number
        byte[] inData=new byte[1024];
        byte[] response=new byte[32];
        int len=0;
        int Country = 2;
        boolean ret = false;
        
        while (!Connect(ip)){};

        try {
            //Send Abort command
            TCPDataOut.write(hexStringToByteArray("4003000000000000"));
            System.out.println("Send Abort command 0x4003000000000000");
            while (true)
            {
                if(TCPDataIn.available() >= 8)
                {
                    len=TCPDataIn.read(inData, 0, 8);
                    if (byteArrayToHexString(inData,len).startsWith("4003BFFCBFFCBFFC"))
                    {
                        // Check Abort command response
                        System.out.println("Abort response");
                        clearReadBuffer(TCPDataIn);
                        break;
                    }
                }
            }

            if (port != 0)
            {
                //Select Antenna port ANT_PORT_SEL
                TCPDataOut.write(hexStringToByteArray("7001010700000000"));
                System.out.println("Send ANT_PORT_SEL command 0x7001010700000000");
                Thread.sleep(1);

                TCPDataOut.write(hexStringToByteArray("7001020700000000"));
                System.out.println("Send ANT_PORT_CFG command 0x7001020700000000");
                Thread.sleep(1);

                cmdString=String.format("70010107%02X000000", port & 0xFF);
                TCPDataOut.write(hexStringToByteArray(cmdString));
                System.out.println("Send ANT_PORT_SEL command 0x" + cmdString);
                Thread.sleep(1);

                TCPDataOut.write(hexStringToByteArray("7001020701000000"));
                System.out.println("Send ANT_PORT_CFG command 0x7001020701000000");
                Thread.sleep(1);
            }
            else
            {
                //Select Antenna port ANT_PORT_SEL
                TCPDataOut.write(hexStringToByteArray("7001010700000000"));
                System.out.println("Send ANT_PORT_SEL command 0x7001010700000000");
                Thread.sleep(1);
            }

            //QUERY_CFG Command for continuous inventory
            TCPDataOut.write(hexStringToByteArray("70010007ffff0000"));
            System.out.println("QUERY_CFG (continuous mode) command 70010007ffff0000");
            Thread.sleep(1);
            
            //Set inventory algorithm
            TCPDataOut.write(hexStringToByteArray("7001020900000000"));     //fixed q
            System.out.println("Set INV_SEL  command 7001020900000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001030901000000"));     //q = 1
            System.out.println("Set INV_ALG_PARM_0  command 7001030901000000");
            Thread.sleep(1);

            //Enable inventory read 1 bank
            TCPDataOut.write(hexStringToByteArray("7001010903000100")); // enable inventory read 1 bank
            System.out.println("Send INV_CFG command 7001010903000100");
            Thread.sleep(1);

            //Select TID bank
            TCPDataOut.write(hexStringToByteArray("7001020a02000000"));
            System.out.println("Send TAGACC_BANK command 7001020a02000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001030a00000000")); // offset 0
            System.out.println("Send TAGACC_PTR command 7001030a00000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001040a06000000")); // read 6 words
            System.out.println("Send TAGACC_PTR command 7001040a06000000");
            Thread.sleep(1);

            //Start inventory - send (HST_CMD)
            long timer=System.currentTimeMillis();
            clearReadBuffer(TCPDataIn);
            TCPDataOut.write(hexStringToByteArray("700100f00f000000"));
            System.out.println("Start write - send (HST_CMD) 700100f00f000000");
            while(true)
            {
                if(TCPDataIn.available() >= 8)
                {
                    timer=System.currentTimeMillis();                        
                    //get packet header first
                    len=TCPDataIn.read(inData, 0, 8);

                    //if (len<8)
                    //    continue;

                    if (byteArrayToHexString(inData,len).startsWith("9898989898989898"))
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " TCP Notification Received.");
                        continue;
                    }
                    else if (byteArrayToHexString(inData,len).startsWith("02000780")
                                || byteArrayToHexString(inData,len).startsWith("01000780"))
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Antenna Cycle End Notification Received");
                        continue;
                    }
                    else if (byteArrayToHexString(inData,len).startsWith("4003BFFCBFFCBFFC"))
                    {
                        // Check Abort command response
                        System.out.println("All tag data has been returned");
                        clearReadBuffer(TCPDataIn);
                        if (found)
                        {
                            if (StartAuthenticate(ip, selected_epc, key, access_pw, TAM))
                            {
                                if (TAM == 1)
                                    AsyncCallbackRaiseEvent(new TagCallbackInfo(true, new S_EPC(selected_epc), "DNA"));
                                else
                                    AsyncCallbackRaiseEvent(new TagCallbackInfo(true, new S_EPC(selected_epc), custom_data, "DNA"));
                            }
                            else
                            {
                                AsyncCallbackRaiseEvent(new TagCallbackInfo(false, new S_EPC(selected_epc), "DNA"));
                            }
                        }
                        found = false;
                        // Restart inventory
                        TCPDataOut.write(hexStringToByteArray("700100f00f000000"));
                        System.out.println("Start write - send (HST_CMD) 700100f00f000000");
                        continue;
                    }

                    int pkt_ver = (int)(inData[0] & 0xFF);
                    int flags = (int) (inData[1] & 0xFF);
                    int pkt_type = (int) (inData[2] & 0xFF) + ((int)(inData[3] & 0xFF) << 8);
                    int pkt_len = (int) (inData[4] & 0xFF) + ((int)(inData[5] & 0xFF) << 8);
                    int datalen = pkt_len * 4;
                    if (pkt_ver != 0x01 && pkt_ver != 0x02 && pkt_ver != 0x03)
                    {
                        System.out.println("Unrecognized packet header: " + byteArrayToHexString(inData, len));
                        continue;
                    }

                    //wait until the full packet data has come in
                    boolean inCompletePacket=false;
                    while(TCPDataIn.available() < datalen)
                    {
                        if (System.currentTimeMillis() - timer >= 3000) {
                            System.out.println("Incomplete packet returned.  Packet header: " + byteArrayToHexString(inData, len));
                            len=TCPDataIn.read(inData, 0, TCPDataIn.available());
                            System.out.println("Packet body: " + byteArrayToHexString(inData, len));
                            inCompletePacket=true;
                            break;
                        }
                    }
                    if (inCompletePacket)
                    {
                        try {
                            date = new Date();
                            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ipAddress + ".txt", true)));
                            out.println(dateFormat.format(date) + " Incomplete data packet due to reader connection lost");
                            out.close();
                        } catch (IOException e) {
                            System.out.println("Could not write to log file " + e.toString());
                        }
                        break;
                    }
                    //finish reading
                    TCPDataIn.read(inData, 8, datalen);

                    if (pkt_type == 0x8001 || pkt_type == 0x0001)
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Command End Packet: " + byteArrayToHexString(inData,len+datalen));
                        break;
                    }
                    else if (pkt_type == 0x8000 || pkt_type == 0x0000)
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Command Begin Packet: " + byteArrayToHexString(inData,len+datalen));
                        continue;
                    }
                    else if (pkt_type == 0x8005 || pkt_type == 0x0005)
                    {
                        byte[] PC = new byte[2];
                        byte[] EPC = new byte[64];
                        byte[] TID = new byte[64];
                        int epclen = 0;
                        for (int cnt = 0; cnt < 2; cnt++)
                        {
                            PC[cnt] = inData[20 + cnt];
                        }
                        epclen = ((PC[0] >> 3) * 2);
                        for (int cnt = 0; cnt < epclen; cnt++)
                        {
                            EPC[cnt] = inData[22 + cnt];
                        }
                        for (int cnt = 0; cnt < (datalen - 16 - epclen); cnt++)
                        {
                            TID[cnt] = inData[22 + epclen + cnt];
                        }
                        String STID = byteArrayToHexString(TID, datalen - 16 - epclen);
                        if (STID.startsWith("E2C06F92") ||
                            STID.startsWith("E2C06B12") ||
                            STID.startsWith("E2C06892"))
                        {
                            System.out.println("Found");
                            selected_epc = byteArrayToHexString(EPC,epclen);
                            found = true;
                            //Send Abort command
                            TCPDataOut.write(hexStringToByteArray("4003000000000000"));
                            System.out.println("Send Abort command 0x4003000000000000");
                        }
                        continue;
                    }
                    else if (pkt_type == 0x0006)
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Tag Access Packet: " + byteArrayToHexString(inData,len+datalen));
                        if (inData[1] == 0)
                        {
                            for (int i = 0; i < 16; i++)
                                response[i] = inData[i+20];
                            String result = byteArrayToHexString(AES.decrypt(hexStringToByteArray(key), response), 16);
                            if (result.startsWith("96C5"))
                            {
                                if (result.startsWith("FD5D8048F48DD09AAD22", 12))
                                {
                                    ret = true;
                                }
                            }
                        }
                        continue;
                    }
                    else
                    {
                        System.out.println(dateFormat.format(date) + " Other Packet: " + byteArrayToHexString(inData,len+datalen));
                        continue;
                    }
                }
                else {
                    if (System.currentTimeMillis() - timer >= 2000) {
                        System.out.println("Connection lost.  Please reconnect");
                        System.out.println("Close Connections");

                        Disconnect();
                        try {
                            date = new Date();
                            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ipAddress + ".txt", true)));
                            out.println(dateFormat.format(date) + " Reader connection lost");
                            out.close();
                        } catch (IOException e) {
                            System.out.println("Could not write to log file " + e.toString());
                        }

                        System.out.println("Reconnect");
                        while (!Connect(ip)){};

                        break;
                    }
                }
                Thread.sleep(1); // save CPU usage
            }
            //Send Abort command
            clearReadBuffer(TCPDataIn);
            TCPDataOut.write(hexStringToByteArray("4003000000000000"));
            System.out.println("Send Abort command 0x4003000000000000");
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }
        
        return ret;
    }
    
    public boolean StartAuthenticate(String ip, String selected_epc, String key, String access_pw, int TAM) 
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        byte[] inData=new byte[1024];
        byte[] response=new byte[16];
        byte[] data=new byte[16];
        int len=0, retry = 0;
        boolean ret = false;
        
        try {
            //Set QUERY_CFG
            TCPDataOut.write(hexStringToByteArray("7001000980010000"));
            System.out.println("Set QUERY_CFG command 7001000980010000");
            Thread.sleep(1);
            
            //Set select
            TCPDataOut.write(hexStringToByteArray("7001000800000000"));
            System.out.println("Send TAGMSK_DESC_SEL command 7001000800000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001010809000000"));
            System.out.println("Send TAGMSK_DESC_CFG command 7001010809000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001020801000000")); // select epc bank
            System.out.println("Send TAGMSK_BANK command 7001020801000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001030820000000"));
            System.out.println("Send TAGMSK_PTR command 7001030820000000");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray("7001040860000000"));
            System.out.println("Send TAGMSK_LEN command 7001040860000000");
            Thread.sleep(1);
            
            byte[] epcheader = hexStringToByteArray(selected_epc);            
            TCPDataOut.write(hexStringToByteArray(String.format("70010508%02X%02X%02X%02X", epcheader[0], epcheader[1],epcheader[2], epcheader[3])));
            System.out.println("Send TAGMSK_0_3 command");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray(String.format("70010608%02X%02X%02X%02X", epcheader[4], epcheader[5],epcheader[6], epcheader[7])));
            System.out.println("Send TAGMSK_4_7 command");
            Thread.sleep(1);
            TCPDataOut.write(hexStringToByteArray(String.format("70010708%02X%02X%02X%02X", epcheader[8], epcheader[9],epcheader[10], epcheader[11])));
            System.out.println("Send TAGMSK_8_11 command");
            Thread.sleep(1);
            
            byte[] access = hexStringToByteArray(access_pw);
            //Set access password
            TCPDataOut.write(hexStringToByteArray(String.format("7001060A%02X%02X%02X%02X", access[3], access[2], access[1], access[0])));
            System.out.println("Send TAGACC_ACCPWD command");
            Thread.sleep(1);
            
            //Set INV_CFG
            TCPDataOut.write(hexStringToByteArray("7001010940400000"));
            System.out.println("Send INV_CFG command 7001010940400000");
            Thread.sleep(1);
            
            //Set authenticate parameters
            if (TAM == 1)
            {
                TCPDataOut.write(hexStringToByteArray("7001000f03800100")); // SenRep = 1; IncRepLen = 1; CSI = 0; Length = 96
                System.out.println("Send command 7001000f03800100");
                Thread.sleep(1);
                // TAM1 message = 0x0000fd5d8048f48dd09aad22
                TCPDataOut.write(hexStringToByteArray("7001010f5dfd0000")); 
                System.out.println("Send command 7001010f5dfd0000");
                Thread.sleep(1);
                TCPDataOut.write(hexStringToByteArray("7001020f8df44880")); 
                System.out.println("Send command 7001020f8df44880");
                Thread.sleep(1);
                TCPDataOut.write(hexStringToByteArray("7001030f22ad9ad0")); 
                System.out.println("Send command 7001030f22ad9ad0");
                Thread.sleep(1);
            }
            else
            {
                TCPDataOut.write(hexStringToByteArray("7001000f03e00100")); // SenRep = 1; IncRepLen = 1; CSI = 0; Length = 120
                System.out.println("Send command 7001000f03800100");
                Thread.sleep(1);
                // TAM2 message = 0x2001fd5d8048f48dd09aad2200001100
                TCPDataOut.write(hexStringToByteArray("7001010f5dfd0120")); 
                System.out.println("Send command 7001010f5dfd0120");
                Thread.sleep(1);
                TCPDataOut.write(hexStringToByteArray("7001020f8df44880")); 
                System.out.println("Send command 7001020f8df44880");
                Thread.sleep(1);
                TCPDataOut.write(hexStringToByteArray("7001030f22ad9ad0")); 
                System.out.println("Send command 7001030f22ad9ad0");
                Thread.sleep(1);
                TCPDataOut.write(hexStringToByteArray("7001040f00110000")); 
                System.out.println("Send command 7001040f00110000");
                Thread.sleep(1);
            }
            
            //Start authenticate - send (HST_CMD)
            long timer=System.currentTimeMillis();
            clearReadBuffer(TCPDataIn);
            TCPDataOut.write(hexStringToByteArray("700100f050000000"));
            System.out.println("Start write - send (HST_CMD) 700100f050000000");
            while(true)
            {
                if(TCPDataIn.available() >= 8)
                {
                    timer=System.currentTimeMillis();                        
                    //get packet header first
                    len=TCPDataIn.read(inData, 0, 8);

                    //if (len<8)
                    //    continue;

                    if (byteArrayToHexString(inData,len).startsWith("9898989898989898"))
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " TCP Notification Received.");
                        continue;
                    }
                    else if (byteArrayToHexString(inData,len).startsWith("02000780")
                                || byteArrayToHexString(inData,len).startsWith("01000780"))
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Antenna Cycle End Notification Received");
                        continue;
                    }
                    else if (byteArrayToHexString(inData,len).startsWith("4003BFFCBFFCBFFC"))
                    {
                        // Check Abort command response
                        System.out.println("All tag data has been returned");
                        break ;
                    }

                    int pkt_ver = (int)(inData[0] & 0xFF);
                    int flags = (int) (inData[1] & 0xFF);
                    int pkt_type = (int) (inData[2] & 0xFF) + ((int)(inData[3] & 0xFF) << 8);
                    int pkt_len = (int) (inData[4] & 0xFF) + ((int)(inData[5] & 0xFF) << 8);
                    int datalen = pkt_len * 4;
                    if (pkt_ver != 0x01 && pkt_ver != 0x02 && pkt_ver != 0x03)
                    {
                        System.out.println("Unrecognized packet header: " + byteArrayToHexString(inData, len));
                        continue;
                    }

                    //wait until the full packet data has come in
                    boolean inCompletePacket=false;
                    while(TCPDataIn.available() < datalen)
                    {
                        if (System.currentTimeMillis() - timer >= 3000) {
                            System.out.println("Incomplete packet returned.  Packet header: " + byteArrayToHexString(inData, len));
                            len=TCPDataIn.read(inData, 0, TCPDataIn.available());
                            System.out.println("Packet body: " + byteArrayToHexString(inData, len));
                            inCompletePacket=true;
                            break;
                        }
                    }
                    if (inCompletePacket)
                    {
                        try {
                            date = new Date();
                            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ipAddress + ".txt", true)));
                            out.println(dateFormat.format(date) + " Incomplete data packet due to reader connection lost");
                            out.close();
                        } catch (IOException e) {
                            System.out.println("Could not write to log file " + e.toString());
                        }
                        break;
                    }
                    //finish reading
                    TCPDataIn.read(inData, 8, datalen);

                    if (pkt_type == 0x8001 || pkt_type == 0x0001)
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Command End Packet: " + byteArrayToHexString(inData,len+datalen));
                        if (ret == false && retry < 3)
                        {
                            TCPDataOut.write(hexStringToByteArray("700100f050000000"));
                            System.out.println("Start write - send (HST_CMD) 700100f050000000");
                            ++retry;
                            continue;
                        }
                        else
                            break;
                    }
                    else if (pkt_type == 0x8000 || pkt_type == 0x0000)
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Command Begin Packet: " + byteArrayToHexString(inData,len+datalen));
                        continue;
                    }
                    else if (pkt_type == 0x0006)
                    {
                        date = new Date();
                        System.out.println(dateFormat.format(date) + " Tag Access Packet: " + byteArrayToHexString(inData,len+datalen));
                        if (inData[1] == 0)
                        {
                            if (TAM == 1)
                            {
                                for (int i = 0; i < 16; i++)
                                    response[i] = inData[i+20];
                            }
                            else
                            {
                                for (int i = 0; i < 16; i++)
                                {
                                    response[i] = inData[i+20];
                                    data[i] = inData[i+36];
                                }
                            }
                            String result = byteArrayToHexString(AES.decrypt(hexStringToByteArray(key), response), 16);
                            if (result.startsWith("96C5"))
                            {
                                if (result.startsWith("FD5D8048F48DD09AAD22", 12))
                                {
                                    ret = true;
                                    if (TAM == 2)
                                    {
                                        custom_data = byteArrayToHexString(AES.decrypt(hexStringToByteArray(key), data, response), 16);
                                        System.out.println(result);
                                    }
                                }
                            }
                        }
                        continue;
                    }
                    else
                    {
                        System.out.println(dateFormat.format(date) + " Other Packet: " + byteArrayToHexString(inData,len+datalen));
                        continue;
                    }
                }
                else {
                    if (System.currentTimeMillis() - timer >= 2000) {
                        System.out.println("Connection lost.  Please reconnect");
                        System.out.println("Close Connections");

                        Disconnect();
                        try {
                            date = new Date();
                            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ipAddress + ".txt", true)));
                            out.println(dateFormat.format(date) + " Reader connection lost");
                            out.close();
                        } catch (IOException e) {
                            System.out.println("Could not write to log file " + e.toString());
                        }

                        System.out.println("Reconnect");
                        while (!Connect(ip)){};

                        break;
                    }
                }
                Thread.sleep(1); // save CPU usage
            }
            
            //Restore parameters
            //Enable inventory read 1 bank
            TCPDataOut.write(hexStringToByteArray("7001010903000100")); // enable inventory read 1 bank
            System.out.println("Send INV_CFG command 7001010903000100");
            Thread.sleep(1);
            
            //Set QUERY_CFG
            TCPDataOut.write(hexStringToByteArray("7001000900000000"));
            System.out.println("Set QUERY_CFG command 7001000900000000");
            Thread.sleep(1);
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }
        
        return ret;
    }
    
    private void AsyncCallbackRaiseEvent(TagCallbackInfo data)
    {
        for(int i=0, size = AsyncCallbackEventSubscribers.size(); i < size; i++)
        {
            ((AsyncCallbackEventListener)AsyncCallbackEventSubscribers.get(i)).AsyncCallbackEvent(new AsyncCallbackEventArgs(this, data));
        }
    }
    
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public String charArrayToHexString(char[] a,int length) {    
    String returnString="";
    for (int j = 0; j < length; j++) {
        byte c =  (byte)a[j];
        int uc=(int)(c & 0xFF);
        if (Integer.toHexString(uc).length() == 1) returnString+="0";
        returnString+=Integer.toHexString(uc);
    }
    returnString=returnString.toUpperCase();
    return returnString;
    }
    public String byteArrayToHexString(byte[] a,int length) {
    String returnString="";
    for (int j = 0; j < length; j++) {
        int uc=(int)(a[j] & 0xFF);
        if (Integer.toHexString(uc).length() == 1) returnString+="0";
        returnString+=Integer.toHexString(uc);
    }
    returnString=returnString.toUpperCase();
    return returnString;
    }

    public void clearReadBuffer(DataInputStream data) {
        byte[] inData = new byte[1024];
        int len = 0;
        try {
            Thread.sleep(10);
            if(data.available() != 0)
            {
                len=data.read(inData);
            }
            System.out.printf("Return: %s \n", byteArrayToHexString(inData,len));
        } catch (Exception e) {
            System.out.println("Could not clear buffer: " + e.getMessage());
        }
    }
    
    private int[] fccFreqTable = new int[]
    {
        0x4F0E1800, /*915.75 MHz   */
        0x4D0E1800, /*915.25 MHz   */
        0x1D0E1800, /*903.25 MHz   */
        0x7B0E1800, /*926.75 MHz   */
        0x790E1800, /*926.25 MHz   */
        0x210E1800, /*904.25 MHz   */
        0x7D0E1800, /*927.25 MHz   */
        0x610E1800, /*920.25 MHz   */
        0x5D0E1800, /*919.25 MHz   */
        0x350E1800, /*909.25 MHz   */
        0x5B0E1800, /*918.75 MHz   */
        0x570E1800, /*917.75 MHz   */
        0x250E1800, /*905.25 MHz   */
        0x230E1800, /*904.75 MHz   */
        0x750E1800, /*925.25 MHz   */
        0x670E1800, /*921.75 MHz   */
        0x4B0E1800, /*914.75 MHz   */
        0x2B0E1800, /*906.75 MHz   */
        0x470E1800, /*913.75 MHz   */
        0x690E1800, /*922.25 MHz   */
        0x3D0E1800, /*911.25 MHz   */
        0x3F0E1800, /*911.75 MHz   */
        0x1F0E1800, /*903.75 MHz   */
        0x330E1800, /*908.75 MHz   */
        0x270E1800, /*905.75 MHz   */
        0x410E1800, /*912.25 MHz   */
        0x290E1800, /*906.25 MHz   */
        0x550E1800, /*917.25 MHz   */
        0x490E1800, /*914.25 MHz   */
        0x2D0E1800, /*907.25 MHz   */
        0x590E1800, /*918.25 MHz   */
        0x510E1800, /*916.25 MHz   */
        0x390E1800, /*910.25 MHz   */
        0x3B0E1800, /*910.75 MHz   */
        0x2F0E1800, /*907.75 MHz   */
        0x730E1800, /*924.75 MHz   */
        0x370E1800, /*909.75 MHz   */
        0x5F0E1800, /*919.75 MHz   */
        0x530E1800, /*916.75 MHz   */
        0x450E1800, /*913.25 MHz   */
        0x6F0E1800, /*923.75 MHz   */
        0x310E1800, /*908.25 MHz   */
        0x770E1800, /*925.75 MHz   */
        0x430E1800, /*912.75 MHz   */
        0x710E1800, /*924.25 MHz   */
        0x650E1800, /*921.25 MHz   */
        0x630E1800, /*920.75 MHz   */
        0x6B0E1800, /*922.75 MHz   */
        0x1B0E1800, /*902.75 MHz   */
        0x6D0E1800, /*923.25 MHz   */
    };
    /// <summary>
    /// FCC Frequency Channel number
    /// </summary>
    private int FCC_CHN_CNT = 50;
    private int[] fccFreqSortedIdx = new int[]{
        26, 25, 1, 48, 47,
        3, 49, 35, 33, 13,
        32, 30, 5, 4, 45,
        38, 24, 8, 22, 39,
        17, 18, 2, 12, 6,
        19, 7, 29, 23, 9,
        31, 27, 15, 16, 10,
        44, 14, 34, 28, 21,
        42, 11, 46, 20, 43,
        37, 36, 40, 0, 41,
    };
    
    private int[] twFreqTable = new int[]
    {
        0x7D0E1800, /*927.25MHz   10*/
        0x730E1800, /*924.75MHz   5*/
        0x6B0E1800, /*922.75MHz   1*/
        0x750E1800, /*925.25MHz   6*/
        0x7F0E1800, /*927.75MHz   11*/
        0x710E1800, /*924.25MHz   4*/
        0x790E1800, /*926.25MHz   8*/
        0x6D0E1800, /*923.25MHz   2*/
        0x7B0E1800, /*926.75MHz   9*/
        0x690E1800, /*922.25MHz   0*/
        0x770E1800, /*925.75MHz   7*/
        0x6F0E1800, /*923.75MHz   3*/
    };
    /// <summary>
    /// Taiwan Frequency Channel number
    /// </summary>
    private int TW_CHN_CNT = 12;
    private int[] twFreqSortedIdx = new int[]{
        10, 5, 1, 6,
        11, 4, 8, 2,
        9, 0, 7, 3,
    };
    
    private int[] cnFreqTable = new int[]
    {
        0xD31c3000, /*922.375MHz   */
        0xD11c3000, /*922.125MHz   */
        0xCD1c3000, /*921.625MHz   */
        0xC51c3000, /*920.625MHz   */
        0xD91c3000, /*923.125MHz   */
        0xE11c3000, /*924.125MHz   */
        0xCB1c3000, /*921.375MHz   */
        0xC71c3000, /*920.875MHz   */
        0xD71c3000, /*922.875MHz   */
        0xD51c3000, /*922.625MHz   */
        0xC91c3000, /*921.125MHz   */
        0xDF1c3000, /*923.875MHz   */
        0xDD1c3000, /*923.625MHz   */
        0xDB1c3000, /*923.375MHz   */
        0xCF1c3000, /*921.875MHz   */
        0xE31c3000, /*924.375MHz   */
    };
    /// <summary>
    /// China Frequency Channel number
    /// </summary>
    private int CN_CHN_CNT = 16;
    private int[] cnFreqSortedIdx = new int[]{
	      7, 6, 4, 0,
	      10, 14, 3, 1,
	      9, 8, 2, 13,
	      12, 11, 5, 15,
	      };
}

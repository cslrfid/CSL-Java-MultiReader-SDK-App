package Netfinder.Structures;

import Netfinder.Constants.Mode;

/**
 * Netfinder information return from device
 */
public class DeviceInformation {
    /**
    * Device mode
    */
    public int mode = Mode.Unknown;
    /**
    * Total time on network
    */
    public TimeEvent time_on_network = new TimeEvent();
    /**
    * Total time on power on device
    */
    public TimeEvent time_on_powered = new TimeEvent();
    /**
    * MAC address
    */
    public byte[] mac = new byte[6];
    /**
    * IP address
    */
    public byte[] ip = new byte[4];
    /**
    * Subnet mask
    */
    public byte[] subnet = new byte[4];
    /**
    * Gateway address
    */
    public byte[] gateway = new byte[4];
    /**
    * UDP Port
    */
    public short port; // Get port from UDP header
    /**
    * enable or disable DHCP
    */
    public boolean DHCP;
    /**
    * DHCP retry count
    */
    public short dhcp_retry;
    /**
    * Device name, user can change it.
    */
    public String device_name;
    /**
    * Mode discription
    */
    public String description;
    /**
    * Version
    */
    public String version;
    /**
    * Gateway check mode
    */
    public short gateway_check_mode;
}

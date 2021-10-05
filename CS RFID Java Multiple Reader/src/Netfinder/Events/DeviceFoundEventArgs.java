package Netfinder.Events;

import Netfinder.Structures.DeviceInformation;
import java.util.*;

/**
 * Search device callback event argument
 */
public class DeviceFoundEventArgs extends EventObject{
    /**
     * DeviceInfomation
     * @see CSLibrary.Net.Structures.DeviceInfomation
     */
    public DeviceInformation info;
    /**
     * Constructor
     * @param source class that handle this event
     * @param info DeviceInfomation
     */
    public DeviceFoundEventArgs(Object source, DeviceInformation info)
    {
        super(source);
        this.info = info;
    }
}

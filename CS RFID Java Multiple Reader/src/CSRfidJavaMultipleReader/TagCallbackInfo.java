package CSRfidJavaMultipleReader;

/**
 * Tag Callback Information
 */
public class TagCallbackInfo implements Comparable{
    /**
     * Index number, First come with small number.
     */
    public int index = -1;
    /**
     * The Receive Signal Strength Indicator (RSSI).
     */
    public float rssi;//4
    /**
     * IP
     */
    public String source = "";
    /**
     * Device Name
     */
    public String deviceName = "";
    /**
     * Total count
     */
    public int count = 1;//4
    /**
     * PC Data
     */
    public S_PC pc;
    /**
     * EPC Data
     */
    public S_EPC epc;
    /**
     * TID Data
     */
    public S_EPC tid;
    /**
     * Constructor
     */
    boolean result;
    String data = "";
    public TagCallbackInfo() { }
    /**
     * Constructor
     * @param index
     * @param rssi
     * @param count
     * @param pc
     * @param epc
     * @param port
     */
    public TagCallbackInfo(int index, float rssi, int count, S_PC pc, S_EPC epc, String source, String deviceName)
    {
        this.index = index;
        this.rssi = rssi;
        this.count = count;
        this.pc = pc;
        this.epc = epc;
        this.source = source;
        this.deviceName = deviceName;
    }

    /**
     * Constructor
     * @param rssi
     * @param pc
     * @param epc
     * @param port
     */
    public TagCallbackInfo(float rssi, S_PC pc, S_EPC epc, String source, String deviceName)
    {
        this.rssi = rssi;
        this.pc = pc;
        this.epc = epc;
        this.source = source;
        this.deviceName = deviceName;
    }

    /**
     * Constructor
     * @param index
     * @param pc
     * @param epc
     * @param port
     */
    public TagCallbackInfo(int index, S_PC pc, S_EPC epc, String source, String deviceName)
    {
        this.index = index;
        this.pc = pc;
        this.epc = epc;
        this.source = source;
        this.deviceName = deviceName;
    }

    /**
     * Constructor
     * @param pc
     * @param epc
     * @param port
     */
    public TagCallbackInfo(S_PC pc, S_EPC epc, String source, String deviceName)
    {
        this.pc = pc;
        this.epc = epc;
        this.source = source;
        this.deviceName = deviceName;
    }
    
    public TagCallbackInfo(S_PC pc, S_EPC epc, S_EPC tid)
    {
        this.pc = pc;
        this.epc = epc;
        this.tid = tid;
    }
    
    public TagCallbackInfo(boolean result, S_EPC epc, String source)
    {
        this.source = source;
        this.result = result;
        this.epc = epc;
    }
    
    public TagCallbackInfo(boolean result, S_EPC epc, String data, String source)
    {
        this.source = source;
        this.result = result;
        this.data = data;
        this.epc = epc;
    }

    /**
     * Search and sorting compare
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        TagCallbackInfo that;
        that = (TagCallbackInfo)o;
        return this.epc.ToString().compareTo(that.epc.ToString());
    }
}

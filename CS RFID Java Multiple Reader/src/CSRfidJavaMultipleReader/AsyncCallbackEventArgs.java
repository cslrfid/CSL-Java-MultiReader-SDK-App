package CSRfidJavaMultipleReader;

import java.util.*;

/**
 * Inventory or tag search callback event argument
 */
public class AsyncCallbackEventArgs extends EventObject {
    /**
     * Callback Tag Information
     */
    public TagCallbackInfo info = new TagCallbackInfo();
    /**
     * Constructor
     * @param source class that handle this event
     * @param record Tag Information
     */
    public AsyncCallbackEventArgs(Object source, TagCallbackInfo info)
    {
        super(source);
        this.info = info;
    }
}

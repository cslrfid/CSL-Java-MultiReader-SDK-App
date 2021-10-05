package Netfinder.Events;

import java.util.*;

/**
 * Update bootloader/image callback event argument
 */
public class UpdateResultEventArgs extends EventObject{
    /**
     * Result
     * @see CSLibrary.Net.Constants.UpdateResult
     */
    public int result;
    /**
     * Constructor
     * @param source class that handle this event
     * @param result Result
     * @see CSLibrary.Net.Constants.UpdateResult
     */
    public UpdateResultEventArgs(Object source, int result)
    {
        super(source);
        this.result = result;
    }
}

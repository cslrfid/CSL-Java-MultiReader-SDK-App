package Netfinder.Events;

import java.util.*;

/**
 * Assign device callback event argument
 */
public class AssignResultEventArgs extends EventObject{
    /**
     * Result
     * @see CSLibrary.Net.Constants.Result
     */
    public int result;
    /**
     * Constructor
     * @param source class that handle this event
     * @param result Result
     * @see CSLibrary.Net.Constants.Result
     */
    public AssignResultEventArgs(Object source, int result)
    {
        super(source);
        this.result = result;
    }
}

package Netfinder.Events;

import java.util.*;

/**
 * Update total updated percentage callback event argument
 */
public class UpdatePercentEventArgs extends EventObject{
    /**
     * Updated percentage
     */
    public int percent;
    /**
     * Constructor
     * @param source class that handle this event
     * @param percent updated percentage
     */
    public UpdatePercentEventArgs(Object source, int percent)
    {
        super(source);
        this.percent = percent;
    }
}

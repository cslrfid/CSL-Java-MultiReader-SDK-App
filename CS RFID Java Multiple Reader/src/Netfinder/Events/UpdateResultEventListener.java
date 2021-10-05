package Netfinder.Events;

/**
 * Update bootloader/image callback event interface
 */
public interface UpdateResultEventListener {
    /**
     * callback API
     * @param ev
     */
    void UpdateResultEvent(UpdateResultEventArgs ev);
}

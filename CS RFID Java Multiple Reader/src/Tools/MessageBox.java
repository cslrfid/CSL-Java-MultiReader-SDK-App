package Tools;

import javax.swing.JOptionPane;

/**
 * Provides messagebox function to show normal, error and warning message
 */
public class MessageBox {

    /**
     * Show normal message box
     * @param message message to be shown
     */
    public static void InfoShow(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show error message box
     * @param message message to be shown
     */
    public static void ErrorShow(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show warning message box
     * @param message message to be shown
     */
    public static void WarningShow(String message)
    {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}

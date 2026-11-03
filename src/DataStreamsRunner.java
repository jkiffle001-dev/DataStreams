import javax.swing.SwingUtilities;

/**
 * Lab 09 - Java Data Streams
 * Launches the DataStreams search GUI.
 *
 * @author Eyassu Kiffle
 */
public class DataStreamsRunner
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new DataStreamsFrame().setVisible(true));
    }
}

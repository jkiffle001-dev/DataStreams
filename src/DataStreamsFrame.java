import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lab 09 - Java Data Streams
 * Swing GUI that loads a text file with a JFileChooser, shows it on the left,
 * and uses Java stream processing (Files.lines + filter with a lambda) to show
 * only the lines that contain the search string on the right.
 *
 * @author Eyassu Kiffle
 */
public class DataStreamsFrame extends JFrame
{
    private final JTextArea originalTA = new JTextArea(25, 40);
    private final JTextArea filteredTA = new JTextArea(25, 40);
    private final JTextField searchTF = new JTextField(25);
    private final JButton loadBtn = new JButton("Load File");
    private final JButton searchBtn = new JButton("Search File");
    private final JButton quitBtn = new JButton("Quit");
    private final JLabel statusLbl = new JLabel("Load a text file to begin.");
    final JFileChooser fileChooser = new JFileChooser();

    private Path filePath = null;   // the currently loaded file

    public DataStreamsFrame()
    {
        super("Lab 09 - Java Data Streams");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        searchBtn.setEnabled(false);   // only allowed after a file is loaded

        loadBtn.addActionListener(e -> loadFile());
        searchBtn.addActionListener(e -> searchFile());
        searchTF.addActionListener(e -> { if (searchBtn.isEnabled()) searchFile(); });
        quitBtn.addActionListener(e -> System.exit(0));

        fileChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(new JLabel("Search String:"));
        panel.add(searchTF);
        panel.add(statusLbl);
        return panel;
    }

    private JPanel createCenterPanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.add(createTextPanel(originalTA, "Original File"));
        panel.add(createTextPanel(filteredTA, "Filtered Results"));
        return panel;
    }

    private JScrollPane createTextPanel(JTextArea ta, String title)
    {
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scroller = new JScrollPane(ta);
        scroller.setBorder(BorderFactory.createTitledBorder(title));
        return scroller;
    }

    private JPanel createBottomPanel()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.add(loadBtn);
        panel.add(searchBtn);
        panel.add(quitBtn);
        return panel;
    }

    /** Lets the user pick a file with the JFileChooser and shows it on the left. */
    private void loadFile()
    {
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;   // user cancelled
        }

        Path selected = fileChooser.getSelectedFile().toPath();

        // Read the whole file with a stream; try-with-resources closes it for us
        try (Stream<String> lines = Files.lines(Paths.get(selected.toString())))
        {
            originalTA.setText(lines.collect(Collectors.joining("\n")));
            originalTA.setCaretPosition(0);
            filteredTA.setText("");
            filePath = selected;
            searchBtn.setEnabled(true);
            statusLbl.setText("Loaded: " + selected.getFileName());
        }
        catch (IOException | UncheckedIOException ex)
        {
            JOptionPane.showMessageDialog(this, "Could not read the file:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Uses a Stream with a lambda filter to keep only the lines containing the search string. */
    private void searchFile()
    {
        String searchString = searchTF.getText();
        if (searchString.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a search string.",
                    "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Stream<String> lines = Files.lines(Paths.get(filePath.toString())))
        {
            List<String> matches = lines
                    .filter(line -> line.contains(searchString))   // the lambda filter
                    .collect(Collectors.toList());

            filteredTA.setText(String.join("\n", matches));
            filteredTA.setCaretPosition(0);
            statusLbl.setText(matches.size() + " line(s) contain \"" + searchString + "\"");
        }
        catch (IOException | UncheckedIOException ex)
        {
            JOptionPane.showMessageDialog(this, "Could not search the file:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamSaver {

    private JFrame frame;
    private JTextField videoLinkField;
    private JTextField downloadPathField;
    private JComboBox<String> qualityComboBox;
    private JLabel notificationLabel;
    private JProgressBar progressBar;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                StreamSaver window = new StreamSaver();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public StreamSaver() {
        initialize();
    }

    private void initialize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(new ImageIcon("C:\\Users\\shara\\OneDrive\\Desktop\\ad\\OIP (1).jpeg")
                .getImage().getScaledInstance(screenWidth, screenHeight, Image.SCALE_SMOOTH)));
        background.setBounds(0, 0, screenWidth, screenHeight);
        frame.setContentPane(background);

        frame.getContentPane().setBackground(new Color(240, 248, 255));

        JLabel headerLabel = new JLabel("StreamSaver");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(Color.WHITE);  // Set the font color to white
        headerLabel.setBounds(280, 20, 300, 40);
        frame.getContentPane().add(headerLabel);

        JLabel playlistLinkLabel = createRoundedLabel("Playlist Link:");
        playlistLinkLabel.setBounds(50, 100, 120, 30);
        frame.getContentPane().add(playlistLinkLabel);

        videoLinkField = createRoundedTextField();
        videoLinkField.setBounds(200, 100, 400, 30);
        frame.getContentPane().add(videoLinkField);

        JLabel saveFolderLabel = createRoundedLabel("Save to Folder:");
        saveFolderLabel.setBounds(50, 150, 120, 30);
        frame.getContentPane().add(saveFolderLabel);

        downloadPathField = createRoundedTextField();
        downloadPathField.setBounds(200, 150, 300, 30);
        frame.getContentPane().add(downloadPathField);

        JButton browseButton = createRoundedButton("Browse");
        browseButton.setBounds(520, 150, 100, 30);
        browseButton.addActionListener(e -> browseFolder());
        frame.getContentPane().add(browseButton);

        JLabel qualityLabel = createRoundedLabel("Select Quality:");
        qualityLabel.setBounds(50, 200, 120, 30);
        frame.getContentPane().add(qualityLabel);

        // Removed "Audio Only" from the quality selection options
        qualityComboBox = new JComboBox<>(new String[]{"Best", "720p", "480p", "360p"});
        qualityComboBox.setBounds(200, 200, 150, 30);
        frame.getContentPane().add(qualityComboBox);

        JButton downloadButton = createRoundedButton("Download Playlist");
        downloadButton.setFont(new Font("Georgia", Font.BOLD, 16));
        downloadButton.setBounds(300, 250, 200, 50);
        downloadButton.addActionListener(e -> downloadPlaylist());
        frame.getContentPane().add(downloadButton);

        notificationLabel = new JLabel("Ready to download...");
        notificationLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        notificationLabel.setForeground(Color.WHITE);  // Set to white font color as requested
        notificationLabel.setBounds(50, 350, 700, 30);
        frame.getContentPane().add(notificationLabel);

        progressBar = new JProgressBar();
        progressBar.setBounds(50, 400, 700, 30);
        progressBar.setStringPainted(true);
        progressBar.setValue(0);
        frame.getContentPane().add(progressBar);

        JLabel footerLabel = new JLabel("This tool is intended solely for educational purposes, and we fully adhere to YouTube's terms and conditions.");
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerLabel.setForeground(Color.WHITE);  
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center the text
        footerLabel.setBounds(50, 440, 700, 30);  // Adjusted position and width to fit within the frame width
        frame.getContentPane().add(footerLabel);

    }

    private JLabel createRoundedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setOpaque(true);
        label.setBackground(new Color(220, 220, 220));
        label.setBorder(new LineBorder(new Color(150, 150, 150), 2, true));
        return label;
    }

    private JTextField createRoundedTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(new LineBorder(new Color(150, 150, 150), 2, true));
        return textField;
    }

    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setBorder(new LineBorder(new Color(70, 130, 180), 2, true));
        return button;
    }

    private void browseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            downloadPathField.setText(selectedFolder.getAbsolutePath());
        }
    }

    private void downloadPlaylist() {
        String youtubeUrl = videoLinkField.getText();
        String downloadFolder = downloadPathField.getText();
        String quality = (String) qualityComboBox.getSelectedItem();

        if (youtubeUrl.isEmpty() || downloadFolder.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please provide both the Playlist Link and Save Folder!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String qualityOption;
        switch (quality) {
            case "720p":
                qualityOption = "bestvideo[height<=720]+bestaudio/best[height<=720]";
                break;
            case "480p":
                qualityOption = "bestvideo[height<=480]+bestaudio/best[height<=480]";
                break;
            case "360p":
                qualityOption = "bestvideo[height<=360]+bestaudio/best[height<=360]";
                break;
            case "Audio Only":
                qualityOption = "bestaudio";
                break;
            default:
                qualityOption = "best";
        }

        new Thread(() -> {
            try {
                String ytDlpPath = "C:\\Users\\shara\\OneDrive\\Desktop\\ad\\yt-dlp.exe";
                List<String> videoUrls = new ArrayList<>();
                String listCommand = ytDlpPath + " --flat-playlist --get-id " + youtubeUrl;
                @SuppressWarnings("deprecation")
                Process listProcess = Runtime.getRuntime().exec(listCommand);
                BufferedReader reader = new BufferedReader(new InputStreamReader(listProcess.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    videoUrls.add("https://www.youtube.com/watch?v=" + line);
                }
                listProcess.waitFor();

                int totalVideos = videoUrls.size();
                AtomicInteger completed = new AtomicInteger(0);

                ExecutorService executor = Executors.newFixedThreadPool(3);  // Limit to 3 concurrent downloads
                for (String videoUrl : videoUrls) {
                    executor.submit(() -> {
                        try {
                            String downloadCommand = ytDlpPath + " -f \"" + qualityOption + "\" -o \"" 
                                + downloadFolder + "/%(playlist_title)s/%(playlist_index)s - %(title)s.%(ext)s\" " 
                                + videoUrl;

                            @SuppressWarnings("deprecation")
                            Process downloadProcess = Runtime.getRuntime().exec(downloadCommand);
                            int exitCode = downloadProcess.waitFor();

                            int progress = (int) ((completed.incrementAndGet() / (float) totalVideos) * 100);
                            SwingUtilities.invokeLater(() -> {
                                progressBar.setValue(progress);
                                if (exitCode == 0) {
                                    notificationLabel.setText("Video " + completed.get() + " of " + totalVideos + " downloaded.");
                                } else {
                                    notificationLabel.setText("Failed to download video " + completed.get() + ".");
                                }

                                if (completed.get() == totalVideos) {
                                    executor.shutdown();
                                    JOptionPane.showMessageDialog(frame, "Playlist download complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    notificationLabel.setText("All videos downloaded.");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                notificationLabel.setText("Error occurred during download.");
            }
        }).start();
    }
}
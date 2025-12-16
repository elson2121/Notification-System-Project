import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


 /* The Client application with a GUI to send and receive chat messages.
 * It connects to the SimpleServerGUI running on localhost:8888.
 */
public class SimpleClientGUI extends JFrame {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8888;
    private static final Logger LOGGER = Logger.getLogger(SimpleClientGUI.class.getName());

    private JTextArea chatLog;
    private JTextField messageInput;
    private JTextField filePathField;
    private File selectedFile = null;

    private PrintWriter textOut;
    private DataOutputStream dataOut; // For sending raw bytes
    private Socket socket;

    private final static String clientNickname = "Client-" + (int) (Math.random() * 1000);

    public SimpleClientGUI() {
        super("TCP File Client - " + clientNickname + " connected to " + SERVER_IP + ":" + PORT);
        initializeGUI();
        connectToServer();
    }

    private void initializeGUI() {
        // --- 1. Menu Bar Setup (New Requirement) ---
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> closeConnection());
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar); // Add the menu bar to the JFrame

        // --- 2. Chat Log Setup ---
        chatLog = new JTextArea();
        chatLog.setEditable(false);
        chatLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(chatLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder("💬 Chat & Transfer Log"));

        // --- 3. Input Panel Setup (Modified Layout) ---
        
        // **A. The Send Button (Green, front of Text Area)
        JButton sendButton = new JButton("Send");
        sendButton.setBackground(new Color(40, 167, 69)); // Greenish color
        sendButton.setForeground(Color.WHITE);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.addActionListener(e -> sendData());
        
        // B. The Text Input Field (The main area)
        messageInput = new JTextField();
        messageInput.addActionListener(e -> sendData());
        
        // C. The Choose File Button (Green, behind Send Button)
        JButton chooseFileButton = new JButton("Choose File...");
        chooseFileButton.setBackground(new Color(40, 167, 69)); // Greenish color
        chooseFileButton.setForeground(Color.WHITE);
        chooseFileButton.setOpaque(true);
        chooseFileButton.setBorderPainted(false);
        chooseFileButton.addActionListener(e -> selectFile());
        
        // D. File Path Display (For status/info)
        filePathField = new JTextField("No file selected.");
        filePathField.setEditable(false);
        filePathField.setFont(new Font("Monospaced", Font.ITALIC, 10));

        // Create the input bar with the buttons and text field
        JPanel inputBarPanel = new JPanel(new BorderLayout(5, 5));
        inputBarPanel.add(messageInput, BorderLayout.CENTER);
        
        JPanel buttonGroupPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // To hold the two buttons side-by-side
        buttonGroupPanel.add(sendButton); // This is the "front" button
        buttonGroupPanel.add(chooseFileButton); // This is the "back" button
        
        inputBarPanel.add(buttonGroupPanel, BorderLayout.EAST);
        
        // Combine the main input bar and the file path field in the SOUTH
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Add components in the desired order
        southPanel.add(inputBarPanel); 
        southPanel.add(filePathField);
// --- 4. Main Window Setup ---
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // --- (The rest of the class methods remain unchanged) ---

    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatLog.append(message + "\n");
            chatLog.setCaretPosition(chatLog.getDocument().getLength());
        });
        LOGGER.info(message);
    }
    
    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Transfer");
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            logMessage("[INFO] File ready for transfer: " + selectedFile.getName());
        } else {
            selectedFile = null;
            filePathField.setText("No file selected.");
        }
    }

    private void sendData() {
        String message = messageInput.getText();

        if (socket == null || socket.isClosed()) {
            logMessage("ERROR: Not connected to server.");
            return;
        }

        if (selectedFile != null) {
            sendFile(selectedFile);
        } else if (!message.trim().isEmpty()) {
            sendChatMessage(message);
        }
        
        // Clear text input and file selection after sending
        messageInput.setText("");
        selectedFile = null;
        filePathField.setText("No file selected.");
    }
    
    private void sendChatMessage(String message) {
        try {
            String fullMessage = clientNickname + ": " + message;
            
            // Send the message as a regular text line
            textOut.println(fullMessage);
            logMessage("[SENT] " + fullMessage);

            if (message.equalsIgnoreCase("bye")) {
                closeConnection();
            }
        } catch (Exception e) {
            logMessage("Error sending message: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Send error", e);
        }
    }

    private void sendFile(File file) {
        try (FileInputStream fileIn = new FileInputStream(file)) {
            
            String fileName = file.getName();
            long fileSize = file.length();
            
            // 1. Send the file header command (text stream)
            String header = "FILE_TRANSFER:" + fileName + ":" + fileSize;
            textOut.println(header);
            
            logMessage("[INFO] Starting file transfer: " + fileName + " (" + (fileSize / 1024) + " KB)");
            
            // 2. Send the raw file bytes (data stream)
            byte[] buffer = new byte[4096];
            int read;
            
            while ((read = fileIn.read(buffer)) > 0) {
                dataOut.write(buffer, 0, read);
            }
            dataOut.flush(); // Ensure all bytes are sent immediately
            
            logMessage("[SUCCESS] File transfer complete: " + fileName);

        } catch (FileNotFoundException e) {
            logMessage("ERROR: File not found: " + file.getName());
            LOGGER.log(Level.SEVERE, "File not found", e);
        } catch (IOException e) {
            logMessage("ERROR: IO error during file transfer: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "File send error", e);
            closeConnection();
        }
    }
private void connectToServer() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, PORT);
                
                // Initialize the two main stream types for sending: text and binary data
                textOut = new PrintWriter(socket.getOutputStream(), true); // Text out (Auto-flush)
                dataOut = new DataOutputStream(socket.getOutputStream()); // Binary out

                logMessage("Successfully connected to the server!");

                // Start the receiving thread
                receiveData(socket);

            } catch (IOException e) {
                logMessage("Connection Error: Server might not be running or is unreachable. " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Connection error", e);
            }
        }).start();
    }
    
    private void receiveData(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             DataInputStream dataIn = new DataInputStream(socket.getInputStream())) { // Use raw stream for file reading
            
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                
                if (serverResponse.startsWith("FILE_TRANSFER:")) {
                    // Protocol match: Server is sending a file
                    String[] parts = serverResponse.split(":");
                    if (parts.length == 3) {
                        String fileName = parts[1];
                        long fileSize = Long.parseLong(parts[2]);
                        receiveFile(dataIn, fileName, fileSize);
                    } else {
                        logMessage("[ERROR] Invalid file transfer command from server.");
                    }
                } else {
                    // Regular chat message
                    logMessage(serverResponse);
                }
            }
        } catch (IOException e) {
            logMessage("Connection to server closed or interrupted: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void receiveFile(DataInputStream dataIn, String fileName, long fileSize) {
        // Save files to a known location, e.g., the user's home directory
        String userHome = System.getProperty("user.home");
        File outputFile = new File(userHome, "Received_" + fileName);

        logMessage("[RECEIVING] Incoming file: " + fileName + " (" + (fileSize / 1024) + " KB). Saving to: " + outputFile.getAbsolutePath());

        try (FileOutputStream fileOut = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            long remaining = fileSize;
            int read;

            while (remaining > 0 && (read = dataIn.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                fileOut.write(buffer, 0, read);
                remaining -= read;
                // Note: The loop depends on the remote side closing the socket or
                // the stream to signify end-of-file, or reaching the expected size.
            }
            
            if (remaining == 0) {
                 logMessage("[RECEIVED SUCCESS] File saved as: " + outputFile.getName());
            } else {
                 logMessage("[RECEIVED WARNING] File size mismatch! Expected: " + fileSize + ", Remaining: " + remaining);
            }
           
        } catch (IOException e) {
            logMessage("[ERROR] Failed to save file: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "File receive error", e);
        }
    }
private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                logMessage("Connection to server closed.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing client socket", e);
        }
        SwingUtilities.invokeLater(() -> {
            // Disable text input
            messageInput.setEnabled(false);
            
            // The following code finds and disables the 'Send' and 'Choose File' buttons
            Component southPanel = this.getContentPane().getComponent(1);
            if (southPanel instanceof JPanel) {
                for (Component comp : ((JPanel) southPanel).getComponents()) {
                    if (comp instanceof JPanel) {
                        for (Component subComp : ((JPanel) comp).getComponents()) {
                            if (subComp instanceof JPanel) {
                                for (Component buttonComp : ((JPanel) subComp).getComponents()) {
                                    if (buttonComp instanceof JButton) {
                                        buttonComp.setEnabled(false);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleClientGUI::new);
    }
}
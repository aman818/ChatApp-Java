import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientGUI extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JTextArea messageArea;
    private JTextField messageInput;
    private JButton sendButton;

    public ClientGUI() {
        setTitle("Client Chat");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen

        // UI Components
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        messageInput = new JTextField();
        sendButton = new JButton("Send");

        // Layout
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageInput, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listener
        sendButton.addActionListener(e -> sendMessage());
        messageInput.addActionListener(e -> sendMessage());

        setVisible(true);

        // Start chat connection
        startClient();
    }

    private void startClient() {
        try {
            socket = new Socket("127.0.0.1", 7777);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            readMessages();
        } catch (Exception e) {
            showMessage("Error connecting to server.");
        }
    }

    private void readMessages() {
        Runnable reader = () -> {
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    showMessage("Server: " + msg);
                    if (msg.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
            } catch (Exception e) {
                showMessage("Connection closed.");
            }
        };
        new Thread(reader).start();
    }

    private void sendMessage() {
        String msg = messageInput.getText();
        if (!msg.trim().isEmpty()) {
            out.println(msg);
            showMessage("You: " + msg);
            messageInput.setText("");
        }
    }

    private void showMessage(String message) {
        messageArea.append(message + "\n");
    }

    public static void main(String[] args) {
        new ClientGUI();
    }
}

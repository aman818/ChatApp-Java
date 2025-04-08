import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ServerGUI extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    public ServerGUI() {
        setTitle("Server Chat");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scroll, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        setVisible(true);
        startServer();
    }

    private void startServer() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(7777);
                chatArea.append("Server is ready. Waiting for client...\n");

                socket = serverSocket.accept();
                chatArea.append("Client connected.\n");

                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                readMessages();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void readMessages() {
        new Thread(() -> {
            String msg;
            try {
                while ((msg = br.readLine()) != null) {
                    if (msg.equalsIgnoreCase("exit")) {
                        chatArea.append("Client ended the chat.\n");
                        break;
                    }
                    chatArea.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                chatArea.append("Connection closed.\n");
            }
        }).start();
    }

    private void sendMessage() {
        String msg = inputField.getText();
        chatArea.append("You: " + msg + "\n");
        out.println(msg);
        inputField.setText("");

        if (msg.equalsIgnoreCase("exit")) {
            try {
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            chatArea.append("Chat ended.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}

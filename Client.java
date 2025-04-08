
import java.io.*;
import java.net.*;

class Client{

    BufferedReader br;
    PrintWriter out;
    Socket socket;
    public Client(){
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection done.");


            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();
        } catch (Exception e) {
            System.out.println("Error connecting to server:");
            e.printStackTrace();
        }
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (!socket.isClosed()) {
                    String msg = br.readLine();
                    if (msg == null || msg.equals("exit")) {
                        System.out.println("Server closed the chat.");
                        socket.close(); // Close socket here to clean up
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start();
    }
    

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                while (!socket.isClosed()) {
                    String content = userInput.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equalsIgnoreCase("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed.");
            }
        };

        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("This is client....");
        new Client();
    }
}
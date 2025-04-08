
import java.io.*;
import java.net.*;

class Server {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    //Constructor..
    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connection");
            System.out.println("waiting....");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

        } catch (Exception e) {
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
                        System.out.println("Client closed the chat.");
                        socket.close(); // Close server-side socket
                        break;
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (Exception e) {
                System.out.println("Connection closed.");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        //thread - data user lega and then send krega client tak
        Runnable r2 = () -> {
            System.out.println("Writer started");
            while (true) { 
                try {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is server.. going to start server");
        new Server();
    }
}

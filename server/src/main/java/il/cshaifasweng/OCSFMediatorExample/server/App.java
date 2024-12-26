package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

public class App {
    private static TicTacToeServer server;

    public static void main(String[] args) {
        int port = 3000; // Default port

        // Check if a port argument is provided and parse it
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);  // Use the port provided as input
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 3000.");
            }
        }

        // Initialize the server with the dynamic port
        server = new TicTacToeServer(port);
        try {
            server.listen();
            System.out.println("TicTacToe Server is running on port " + port + "...");
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

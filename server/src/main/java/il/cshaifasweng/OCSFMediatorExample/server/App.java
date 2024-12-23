package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;

public class App {
    private static TicTacToeServer server;

    public static void main(String[] args) {
        server = new TicTacToeServer(3000);
        try {
            server.listen();
            System.out.println("TicTacToe Server is running on port 3000...");
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

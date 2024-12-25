// Updated TicTacToeServer.java
package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

public class TicTacToeServer extends AbstractServer {
    private final ArrayList<ConnectionToClient> players = new ArrayList<>();
    private boolean gameStarted = false;
    private int[][] board = new int[3][3];

    public TicTacToeServer(int port) {
        super(port);
        resetBoard();
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client)
    {
        try
        {
            String message = msg.toString();
            System.out.println("Handling message from client: " + message);

            if (message.startsWith("MOVE")) {
                if (isBoardFull()) {
                    for (ConnectionToClient player : players) {
                        player.sendToClient("DRAW");
                    }
                }
                String symbol = client.getInfo("symbol").toString();
                String fullMessage = message + " " + symbol;

                System.out.println("Received move message: " + fullMessage);

                // Update the board state
                String[] parts = message.split(" ");
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                board[row][col] = symbol.equals("X") ? 1 : 2;

                // Forward the move message to all other clients
                for (ConnectionToClient player : players)
                {
                    player.sendToClient(fullMessage);
                }


            }
            else if (message.startsWith("WIN") || message.startsWith("LOSE") || message.startsWith("DRAW"))
            {
                if (message.startsWith("DRAW"))
                {
                    for (ConnectionToClient player : players) {
                        player.sendToClient("DRAW");
                    }
                }
                else
                {
                    for (ConnectionToClient player : players) {
                        if (player == client) {
                            player.sendToClient("WIN");
                        } else {
                            player.sendToClient("LOSE");
                        }
                    }

                    gameStarted = false;
                    resetBoard();
                }
            }

            else if (!gameStarted && message.startsWith("CONNECT")) {
                client.setInfo("symbol", (players.size() == 0 ? "X" : "O"));
                if (!players.contains(client)) {
                    players.add(client);
                    System.out.println("Added new player, total: " + players.size());
                    if (players.size() == 2) {
                        gameStarted = true;
                        players.get(0).sendToClient("START X");
                        players.get(1).sendToClient("START O");
                        System.out.println("Game started with players X and O.");
                    }
                }
            }

        }
        catch (IOException e) {
            System.err.println("Error handling message from client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isBoardFull() {
        // Check if the board is full
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = 0; // Clear the board
            }
        }
    }

    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        players.remove(client);
        gameStarted = false;
        resetBoard();
    }
}

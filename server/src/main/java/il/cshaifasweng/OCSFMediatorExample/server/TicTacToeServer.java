
// Updated TicTacToeServer.java
package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;

public class TicTacToeServer extends AbstractServer {
    private final ArrayList<ConnectionToClient> players = new ArrayList<>();
    private boolean gameStarted = false;

    public TicTacToeServer(int port) {
        super(port);
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client)
    {
        try
        {
            String message = msg.toString();
            System.out.println("Handling message from client: " + message);

            if (message.startsWith("MOVE")) {
                String symbol = client.getInfo("symbol").toString(); // Retrieve the player's symbol
                String fullMessage = message + " " + symbol; // Append the symbol to the original message

                System.out.println("Received move message: " + fullMessage);

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
                        if (player == client) {
                            player.sendToClient("DRAW");
                        }
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

                    gameStarted = false; // End the game once it's over
                }
            }

                         else if (!gameStarted && message.startsWith("CONNECT")) {
                            client.setInfo("symbol", (players.size() == 0 ? "X" : "O")); // Assign X to the first player and O to the second
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



    @Override
    protected void clientDisconnected(ConnectionToClient client) {
        players.remove(client);
        gameStarted = false; // Reset the game state
    }
}
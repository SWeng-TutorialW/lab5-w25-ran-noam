package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;

public class SecondaryController {

    @FXML
    private Button button00;
    @FXML
    private Button button01;
    @FXML
    private Button button02;
    @FXML
    private Button button10;
    @FXML
    private Button button11;
    @FXML
    private Button button12;
    @FXML
    private Button button20;
    @FXML
    private Button button21;
    @FXML
    private Button button22;
    @FXML
    private Label statusLabel;

    private Button[][] buttons;
    private char playerSymbol;
    private boolean myTurn;

    private TicTacToeNetworkClient networkClient;

    @FXML
    public void initialize() {
        buttons = new Button[][] {
                {button00, button01, button02},
                {button10, button11, button12},
                {button20, button21, button22}
        };

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                final int row = i;
                final int col = j;
                buttons[i][j].setOnAction(e -> {
                    try {
                        handleMove(row, col);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }

        connectToServer();
    }

    private String serverIp;
    private int port = 3000;

    // Method to set the IP address
    public void setIpAddress(String ip) {
        this.serverIp = ip;
    }


    public void setPort(int port) {
        this.port = port;
    }

    private void connectToServer() {
        try {
            networkClient = new TicTacToeNetworkClient(serverIp, port);
            networkClient.openConnection();
            networkClient.sendToServer("CONNECT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMove(int row, int col) throws IOException {
        if (myTurn && buttons[row][col].getText().equals("")) {
            buttons[row][col].setText(String.valueOf(playerSymbol));
            myTurn = false;
            try {
                if (checkForDraw()) {
                    statusLabel.setText("It's a draw!");
                    disableButtons();
                    networkClient.sendToServer("DRAW");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error sending move to server: " + e.getMessage());
            }
            if (checkForWin()) {
                statusLabel.setText("You win!");
                disableButtons();
                networkClient.sendToServer("WIN " + playerSymbol);
            } else {
                statusLabel.setText("Waiting for opponent...");
                try {
                    networkClient.sendToServer("MOVE " + row + " " + col);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error sending move to server: " + e.getMessage());
                }
            }
        } else {
            if (!buttons[row][col].getText().equals("")) {
                System.out.println("Attempted to move but button at " + row + ", " + col + " is already filled.");
            } else {
                System.out.println("Attempted to move but it's not your turn.");
            }
        }
    }

    private boolean checkForWin() {
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().equals("") &&
                    buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                    buttons[i][1].getText().equals(buttons[i][2].getText())) {
                return true;
            }
            if (!buttons[0][i].getText().equals("") &&
                    buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                    buttons[1][i].getText().equals(buttons[2][i].getText())) {
                return true;
            }
        }
        if (!buttons[0][0].getText().equals("") &&
                buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][2].getText())) {
            return true;
        }
        if (!buttons[0][2].getText().equals("") &&
                buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][0].getText())) {
            return true;
        }
        return false;
    }

    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }

    public void handleServerMessage(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("MOVE")) {
                String[] parts = message.split(" ");
                int row = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                char symbol = parts[3].charAt(0);
                buttons[row][col].setText(String.valueOf(symbol));
                if (checkForDraw()) {
                    try {
                        statusLabel.setText("It's a draw!");
                        disableButtons();
                        networkClient.sendToServer("DRAW");
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error sending move to server: " + e.getMessage());
                    }
                } else {
                    myTurn = (symbol != playerSymbol); // Toggle turn
                    statusLabel.setText(myTurn ? "Your turn" : "Waiting for opponent...");
                }
            } else if (message.startsWith("WIN")) {
                statusLabel.setText("You win!");
                disableButtons();
            } else if (message.startsWith("LOSE")) {
                statusLabel.setText("You lose!");
                disableButtons();
            } else if (message.startsWith("START")) {
                playerSymbol = message.charAt(6);
                myTurn = playerSymbol == 'X';
                statusLabel.setText(myTurn ? "Your turn" : "Waiting for opponent...");
                System.out.println("Game started. You are " + playerSymbol);
            }
        });
    }

    private boolean checkForDraw() {

        boolean allFilled = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    allFilled = false;
                    break;
                }
            }
            if (!allFilled) break;
        }

        if (allFilled && !checkForWin()) {
            System.out.println("Draw detected");
            return true;
        } else {
            return false;
        }
    }

    private class TicTacToeNetworkClient extends AbstractClient {
        public TicTacToeNetworkClient(String host, int port) {
            super(host, port);
        }

        @Override
        protected void handleMessageFromServer(Object msg) {
            if (msg instanceof String) {
                handleServerMessage((String) msg);
            }
        }
    }
}

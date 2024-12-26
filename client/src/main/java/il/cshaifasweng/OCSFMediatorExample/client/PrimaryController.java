package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PrimaryController {

	@FXML
	private TextField ipField; // Text field for IP input
	@FXML
	private TextField portField; // Text field for Port input
	@FXML
	private Button connectButton; // Button to trigger connection

	@FXML
	public void initialize() {
		connectButton.setOnAction(event -> handleConnect()); // Set button action to handleConnect
	}

	private void handleConnect() {
		String ip = ipField.getText().trim(); // Get the trimmed IP input
		String portText = portField.getText().trim(); // Get the trimmed Port input

		if (!ip.isEmpty() && !portText.isEmpty()) {
			try {
				int port = Integer.parseInt(portText); // Parse the port number
				if (port < 1 || port > 65535) {
					throw new NumberFormatException("Port out of range");
				}

				// Dynamically update the client with the new IP and Port
				SimpleClient.setHostAndPort(ip, port);
				SimpleClient client = SimpleClient.getClient(); // Get the updated client instance

				// Open the connection
				client.openConnection();

				// Switch to the game screen (secondary.fxml)
				App.setRoot("secondary");
			} catch (NumberFormatException e) {
				showErrorAlert("Invalid Port", "Please enter a valid port number between 1 and 65535.");
			} catch (IOException e) {
				showErrorAlert("Connection Failed", "Unable to connect to the server at " + ip + ":" + portText);
				e.printStackTrace();
			}
		} else {
			showErrorAlert("Invalid Input", "Please enter both a valid IP address and port number.");
		}
	}

	private void showErrorAlert(String title, String message) {
		// Utility method to display error alerts
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait(); // Wait for the user to acknowledge the alert
	}
}

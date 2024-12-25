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
	private Button connectButton; // Button to trigger connection

	@FXML
	public void initialize() {
		connectButton.setOnAction(event -> handleConnect()); // Set button action to handleConnect
	}

	private void handleConnect() {
		String ip = ipField.getText().trim(); // Get the trimmed IP input
		if (!ip.isEmpty()) {
			try {
				// Dynamically update the client with the new IP
				SimpleClient.setHostAndPort(ip, 3000);
				SimpleClient client = SimpleClient.getClient(); // Get the updated client instance

				// Open the connection
				client.openConnection();

				// Switch to the game screen (secondary.fxml)
				App.setRoot("secondary");
			} catch (IOException e) {
				// Show an error alert if connection fails
				showErrorAlert("Connection Failed", "Unable to connect to the server at IP: " + ip);
				e.printStackTrace();
			}
		} else {
			// Show an error alert if the IP field is empty
			showErrorAlert("Invalid Input", "Please enter a valid IP address.");
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

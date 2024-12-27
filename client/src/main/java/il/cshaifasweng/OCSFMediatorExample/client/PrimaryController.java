package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class PrimaryController {

	@FXML
	private TextField ipField;
	@FXML
	private TextField portField;
	@FXML
	private Button connectButton;

	@FXML
	public void initialize() {
		connectButton.setOnAction(event -> handleConnect());
	}

	private void handleConnect() {
		String ip = ipField.getText().trim();
		String portText = portField.getText().trim();

		if (!ip.isEmpty() && !portText.isEmpty())
		{
			try {
				int port = Integer.parseInt(portText);
				if (port < 1 || port > 65535) {
					throw new NumberFormatException("Port out of range");
				}


				SimpleClient.setHostAndPort(ip, port);
				SimpleClient client = SimpleClient.getClient();


				client.openConnection();


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

		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}

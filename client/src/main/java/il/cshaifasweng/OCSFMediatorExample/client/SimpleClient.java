package il.cshaifasweng.OCSFMediatorExample.client;

import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Warning;

public class SimpleClient extends AbstractClient {

	private static SimpleClient client = null;
	private static String host = "localhost"; // Default host
	private static int port = 3000;           // Default port

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("Message received from server: " + msg);
		if (msg.getClass().equals(Warning.class)) {
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
		} else {
			String message = msg.toString();
			System.out.println("Processing message: " + message);
			EventBus.getDefault().post(message); // Assuming there's an appropriate handler
		}
	}

	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(host, port);
		}
		return client;
	}

	public static void setHostAndPort(String newHost, int newPort) {
		host = newHost;
		port = newPort;
		client = new SimpleClient(host, port); // Reinitialize with new host and port
	}
}

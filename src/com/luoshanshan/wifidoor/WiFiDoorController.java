package com.luoshanshan.wifidoor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WiFiDoorController {
	private String serverIpAddress;
	private int port;
	int threshold = 10;
	boolean wifiConnected = false;
	boolean isDoorOn = false;
	
	public WiFiDoorController(String address, int port) {
		this.serverIpAddress = address;
		this.port = port;
	}
	
	public void openDoor() {
		ledCommand("LED ON");
		sendCommand("DOOR ON");
	}

	private void ledCommand(String cmd) {
		if (!wifiConnected) {
			if (serverIpAddress != null) {
				sendCommand(cmd);
			}
		}
	}

	private void sendCommand(String cmd) {
		BufferedWriter clientOut;
		String outMsg;

		try {
			Socket s = new Socket(serverIpAddress, port);
			wifiConnected = true;
			clientOut = new BufferedWriter(new OutputStreamWriter(
					s.getOutputStream()));
			outMsg = cmd;
			clientOut.write(outMsg);
			clientOut.flush();

			clientOut.close();
			s.close();
			wifiConnected = false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

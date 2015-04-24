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

	private void doorControl(int x, int y) {
		if (y <= threshold) {
			if (!isDoorOn) {
				isDoorOn = true;
				ledCommand("LED ON");
			}
		} else {
			if (isDoorOn) {
				isDoorOn = false;
				ledCommand("LED OFF");
			}
		}
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

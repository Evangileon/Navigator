package com.luoshanshan.wifidoor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class WiFiDoorController {
	private String serverIpAddress;
	private int port;
	int threshold = -70;
	int prevRssi = 0;
	boolean wifiConnected = false;
	boolean isDoorOn = false;
	
	private Handler wifiHandler;
	private Semaphore wifiShot = new Semaphore(0);
	
	private boolean requestDoor = false;
	private WifiThread wifiThread;
	
	@SuppressLint("HandlerLeak")
	public WiFiDoorController(String address, int port) {
		this.serverIpAddress = address;
		this.port = port;
		
		wifiHandler = new Handler() {

            public void handleMessage(Message msg) {
                // process incoming messages here
                int rssi = msg.arg1;
                int deviceId = msg.arg2;
                
                if (deviceId != 1) {
                	return;
                }
                
                requestDoor = doorControlOn(rssi);
                
                // one wifi operation
                wifiShot.release();
            }
        };
        
        wifiThread = new WifiThread();
        wifiThread.setDaemon(true);
        wifiThread.start();
	}
	
	public Handler getWifiHandler() {
		return wifiHandler;
	}

	class WifiThread extends Thread{
        public void run(){
        	while(true) {
        		try {
					wifiShot.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		if(requestDoor == true){
                    if(!isDoorOn) {
                        openDoor();
                        isDoorOn = true;
                    }
                } else{
                    if(isDoorOn){
                        closeDoor();
                        isDoorOn = false;
                    }
                }
        	}
        }
    }
	
	public void openDoor() {
		ledCommand("LED ON");
		sendCommand("DOOR ON");
	}
	
	private void closeDoor(){
        ledCommand("LED OFF");
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
	
	private boolean doorControlOn(int vRssi){
        boolean ret = false;
        if(vRssi >= threshold) {
            if(prevRssi >= threshold)
                ret = true;
        }
        else {
            if(prevRssi < threshold)
                ret = false;
        }

        prevRssi = vRssi;
        return ret;
    }
}

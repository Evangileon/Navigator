package com.luoshanshan.wifidoor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class WiFiDoorController {
    private String serverIpAddress;
    private int port;
    int threshold = -70;
    boolean wifiConnected = false;
    boolean isDoorOn = false;
    private Handler wifiHandler;
    private Thread wifiThread;
    int prevRssi = 0;

    public WiFiDoorController(String address, int sport) {
        serverIpAddress = address;
        port = sport;
        wifiThread = new wifiThread();
        wifiThread.start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class wifiThread extends Thread{

        public void run(){
            Looper.prepare();

            wifiHandler = new Handler() {

                public void handleMessage(Message msg) {
                    // process incoming messages here
                    int rssi = msg.arg1;

                    if(doorControlOn(rssi) == true){
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
            };

            Looper.loop();
        }

    }

    public Handler getWifiHandler(){
        return wifiHandler;
    }

    public Thread getWifiThread(){
        return wifiThread;
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

    private void openDoor() {
        ledCommand("LED ON");
//		sendCommand("DOOR ON");
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
}
package com.luoshanshan.bluetoothle;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

public class BluetoothLE {

	private BluetoothAdapter BTAdapter;
	int DeviceCnt = 0;
	private int NumOfDevice = 3;

	Context context;
	//ArrayList<Handler> externalHandlers = new ArrayList<>();
	Handler externalHandler;
	Handler wifiHandler;

	private boolean gattConnectReadRssiOn;

	public BluetoothLE(Context context) {
		this.context = context;
		initialize();
	}

	public void registerHandler(Handler handler) {
		//externalHandlers.add(handler);
		externalHandler = handler;
	}
	
	public void registerWifiHandler(Handler handler) {
		//externalHandlers.add(handler);
		wifiHandler = handler;
	}
	
	private void sendMessageToHandlers(Message msg) {
//		for (Handler handler : externalHandlers) {
//			handler.sendMessage(msg);
//		}
		externalHandler.sendMessage(msg);
		wifiHandler.sendMessage(msg);
	}

	private void initialize() {
		// Initializes Bluetooth adapter.
		BluetoothManager bluetoothManager = (BluetoothManager) context
				.getSystemService(Context.BLUETOOTH_SERVICE);
		BTAdapter = bluetoothManager.getAdapter();
		System.out.println("Adapter: " + BTAdapter);
	}

	// Start the Bluetooth Scan
	public void BTScanStart() {

		if (BTAdapter == null) {
			System.out.println("Bluetooth NOT supported. Aborting.");
			return;
		}

		if (BTAdapter.isEnabled()) {
			System.out.println("Bluetooth is enabled...");
			BTAdapter.startLeScan(mLeScanCallback);
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

			if (device.getName().equals("HMSoft")) {
				if (isAvailableDevice(device.getAddress())) {
					DeviceCnt++;
					if (DeviceCnt == NumOfDevice) {
						setGattConnectReadRssiOn(true);
						BTAdapter.stopLeScan(mLeScanCallback);
					} else if (DeviceCnt > NumOfDevice)
						return;

					device.connectGatt(null, true, mGattCallback);
				}
			}
			// System.out.println("scan record:" + scanRecord);
		}
	};

	// Gatt Callback
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			if (newState == BluetoothProfile.STATE_CONNECTED) {

				Thread rssiRequest = new Thread(new RSSIRequestThread(gatt));
				rssiRequest.setDaemon(true);
				rssiRequest.start();
			}
			if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				System.out.println(gatt.getDevice() + ": Disconnected.. ");
			}
		}

		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
			Message msg = Message.obtain();
			msg.arg1 = rssi;
			msg.arg2 = getDeviceId(gatt);
			sendMessageToHandlers(msg);
			System.out.println("device: " + msg.arg2 + " , RSSI = " + msg.arg1);
			// System.out.println("#########data received###########");
		}
	};

	/**
	 * One thread per Bluetooth
	 */
	class RSSIRequestThread implements Runnable {
		private BluetoothGatt gatt;

		public RSSIRequestThread(BluetoothGatt mgat) {
			this.gatt = mgat;
		}

		@Override
		public void run() {
			while (isGattConnectReadRssiOn()) {
				gatt.readRemoteRssi();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			gatt.disconnect();
		}
	}

	private int getDeviceId(BluetoothGatt gatt) {
		int id = 0;
		switch (gatt.getDevice().getAddress()) {
		case "B4:99:4C:57:B6:E2":
			id = 1;
			break;
		case "78:A5:04:3E:C5:8F":
			id = 2;
			break;
		case "B4:99:4C:67:8A:E4":
			id = 3;
			break;

		default:
			break;
		}
		return id;
	}

	boolean isAvailableDevice(String s) {
		if (s.equals("B4:99:4C:57:B6:E2") || s.equals("B4:99:4C:67:8A:E4")
				|| s.equals("78:A5:04:3E:C5:8F"))
			return true;
		else
			return false;
	}

	public boolean isGattConnectReadRssiOn() {
		return gattConnectReadRssiOn;
	}

	public void setGattConnectReadRssiOn(boolean gattConnectReadRssiOn) {
		this.gattConnectReadRssiOn = gattConnectReadRssiOn;
	}
}

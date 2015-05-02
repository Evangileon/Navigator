package com.jun.realtime.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import com.jiangming.positioning.PositioningAlgorithm;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class SampleDynamicXYDataSource implements Runnable {
	
	private PositioningAlgorithm positioningAlgorithm;

	List<Point> path = new ArrayList<>();
	int[] rssiValues = new int[3];
	boolean[] deviceScanned = new boolean[3];

	private Handler handler = null;
	
	private boolean plotAvailable = false;
	
	@SuppressLint("HandlerLeak")
	public void initializeHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO action when receive bluetooth data
				int rssi = msg.arg1;
				int deviceId = msg.arg2 - 1;
				
				if (deviceId > 2 || deviceId < 0) {
					return;
				}
					
				rssiValues[deviceId] = rssi;
				deviceScanned[deviceId] = true;
				
				for (boolean b : deviceScanned) {
					if (b == false) {
						// not sufficent rssi
						return;
					}
				}
				
				// then calculate the x-y position
				Point newPoint = positioningAlgorithm.getPositionUsingRSSI(rssiValues);
				System.out.println("X = " + newPoint.getX() + " , Y = " + newPoint.getY());
				if (newPoint != null && plotAvailable) {
					addPoint(linearScaling(newPoint));
					plotAvailable = false;
				}
					
				// clear scan flags
				for (int i = 0; i < deviceScanned.length; i++) {
					deviceScanned[i] = false;
				}
			}
		};
	}
	
	public Point linearScaling(Point point) {
		int x = point.x * 75 / 12;
		int y = point.y * 155 / 30;
		
		return new Point(x, y);
	}

	public Handler getHandler() {
		return handler;
	}

	// encapsulates management of the observers watching this datasource for
	// update events:
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	private MyObservable notifier;
	private boolean keepRunning = false;

	public SampleDynamicXYDataSource() {
		notifier = new MyObservable();
		positioningAlgorithm = new PositioningAlgorithm();
		initializeHandler();
	}

	public void stopThread() {
		keepRunning = false;
	}

	@Override
	public void run() {
		
		try {
			keepRunning = true;
			while (keepRunning) {

				Thread.sleep(1000); // decrease or remove to speed up the
									// refresh rate.

				// TODO checking bluetooth devices status here, add one point
				// refresh plot every 1 second
				plotAvailable = true;
				notifier.notifyObservers();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void addPoint(Point point) {
		if (point == null) {
			return;
		}
		addPoint(point.x, point.y);
	}

	/**
	 * Add one point to the end of path
	 * 
	 * @param x
	 *            axis
	 * @param y
	 *            axis
	 */
	public void addPoint(int x, int y) {
		if (path == null) {
			path = new ArrayList<>();
		}

		path.add(new Point(x, y));
	}

	/**
	 * Used by series size
	 * 
	 * @return size of actual number of points
	 */
	public int getItemCount() {
		return path.size();
	}

	public Number getX(int index) {
		if (index >= path.size() || index < 0) {
			return 0;
		}

		return path.get(index).getX();
	}

	public Number getY(int index) {
		if (index >= path.size() || index < 0) {
			return 0;
		}

		return path.get(index).getY();
	}

	public void addObserver(Observer observer) {
		notifier.addObserver(observer);
	}

	public void removeObserver(Observer observer) {
		notifier.deleteObserver(observer);
	}

}
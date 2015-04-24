package com.jun.realtime.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.os.Handler;
import android.os.Message;

public class SampleDynamicXYDataSource implements Runnable {

	List<Point> path = new ArrayList<>();

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO action when receive bluetooth data
			int rssi = msg.arg1;
			int deviceId = msg.arg2;

		}
	};

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
				notifier.notifyObservers();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

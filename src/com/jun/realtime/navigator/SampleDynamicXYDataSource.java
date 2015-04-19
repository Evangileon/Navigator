package com.jun.realtime.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


public class SampleDynamicXYDataSource implements Runnable {
	
	class Point {
		int x;
		int y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
		
	}
	
	List<Point> path = new ArrayList<>();
	
	// encapsulates management of the observers watching this datasource for update events:
    class MyObservable extends Observable {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    }
    
    private static final double FREQUENCY = 5; // larger is lower frequency
    private static final int MAX_AMP_SEED = 100;
    private static final int MIN_AMP_SEED = 10;
    private static final int AMP_STEP = 1;
    public static final int SINE1 = 0;
    public static final int SINE2 = 1;
    private static final int SAMPLE_SIZE = 30;
    private int phase = 0;
    private int sinAmp = 1;
    private MyObservable notifier;
    private boolean keepRunning = false;

    {
        notifier = new MyObservable();
    }

    public void stopThread() {
        keepRunning = false;
    }

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
            keepRunning = true;
            boolean isRising = true;
            while (keepRunning) {

                Thread.sleep(10); // decrease or remove to speed up the refresh rate.
                phase++;
                if (sinAmp >= MAX_AMP_SEED) {
                    isRising = false;
                } else if (sinAmp <= MIN_AMP_SEED) {
                    isRising = true;
                }

                if (isRising) {
                    sinAmp += AMP_STEP;
                } else {
                    sinAmp -= AMP_STEP;
                }
                notifier.notifyObservers();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
	
	public int getItemCount(int series) {
        return SAMPLE_SIZE;
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

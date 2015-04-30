package com.jun.realtime.navigator;

import com.androidplot.Plot;
import java.util.Observable;
import java.util.Observer;

public class PlotUpdater implements Observer {
	Plot<?, ?, ?> plot;
	 
    public PlotUpdater(Plot<?, ?, ?> plot) {
        this.plot = plot;
    }

    @Override
    public void update(Observable o, Object arg) {
        plot.redraw();
    }
}

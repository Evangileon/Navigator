package com.jun.realtime.navigator;

import com.androidplot.xy.XYSeries;

public class SampleDynamicXYSeries implements XYSeries {
	private SampleDynamicXYDataSource datasource;
    private int seriesIndex;
    private String title;

    public SampleDynamicXYSeries(SampleDynamicXYDataSource dataSource, int seriesIndex, String title) {
        this.datasource = dataSource;
        this.seriesIndex = seriesIndex;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int size() {
        return datasource.getItemCount(seriesIndex);
    }

    @Override
    public Number getX(int index) {
        return datasource.getX(index);
    }

    @Override
    public Number getY(int index) {
        return datasource.getY(index);
    }
}

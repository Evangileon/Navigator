package com.jun.realtime.navigator;

import com.androidplot.xy.XYSeries;

public class SampleDynamicXYSeries implements XYSeries {
	private SampleDynamicXYDataSource datasource;
    private String title;

    public SampleDynamicXYSeries(SampleDynamicXYDataSource dataSource, String title) {
        this.datasource = dataSource;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int size() {
        return datasource.getItemCount();
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
package com.jun.realtime.navigator;

import java.text.DecimalFormat;
import java.util.Arrays;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.jun.realtime.navigator.R;
import com.jun.realtime.navigator.MainActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getName();

	private int SERIES_LEN = 50;
	private Shader WHITE_SHADER = new LinearGradient(1, 1, 1, 1, Color.WHITE,
			Color.WHITE, Shader.TileMode.REPEAT);

	private XYPlot plot;
	private SimpleXYSeries series;
	
	private XYPlot dynamicPlot;
	private PlotUpdater plotUpdater;
	private Thread sampleThread;
	private SampleDynamicXYDataSource sampleData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		plot = (XYPlot) findViewById(R.id.graph_metrics);
//
//		// For debugging.
//		plot.setMarkupEnabled(true);
//
//		// Format Graph
//		plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
//		plot.getGraphWidget().getGridBackgroundPaint().setShader(WHITE_SHADER);
//		plot.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
//		plot.getGraphWidget().getDomainGridLinePaint()
//				.setPathEffect(new DashPathEffect(new float[] { 3, 3 }, 1));
//		plot.getGraphWidget().getRangeGridLinePaint().setColor(Color.BLACK);
//		plot.getGraphWidget().getRangeGridLinePaint()
//				.setPathEffect(new DashPathEffect(new float[] { 3, 3 }, 1));
//		plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
//		plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
//		// plot.getGraphWidget().setMarginTop(10);
//
//		// Customize domain and range labels.
//		plot.setDomainLabel("x-vals");
//		plot.setRangeLabel("y-vals");
//		plot.setRangeValueFormat(new DecimalFormat("0"));
//
//		// Make the domain and range step correctly
//		plot.setRangeBoundaries(40, 160, BoundaryMode.FIXED);
//		plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 20);
//		plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 60);
//		plot.setTicksPerDomainLabel(2);
//
//		series = (SimpleXYSeries) getSeries();
//		LineAndPointFormatter lpFormat = new LineAndPointFormatter(Color.BLACK,
//				Color.BLACK, null, // No fill
//				new PointLabelFormatter(Color.TRANSPARENT) // Don't show text at
//															// points
//		);
//		plot.addSeries(series, lpFormat);
//		plot.redraw();
		
		// get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);
 
        plotUpdater = new PlotUpdater(dynamicPlot);
 
        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
 
        // getInstance and position datasets:
        sampleData = new SampleDynamicXYDataSource();
        SampleDynamicXYSeries pathSeries = new SampleDynamicXYSeries(sampleData, 0, "Navigator Path");
 
        LineAndPointFormatter formatter = new LineAndPointFormatter(
                                Color.rgb(0, 0, 0), null, null, null);
        formatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        formatter.getLinePaint().setStrokeWidth(10);
        dynamicPlot.addSeries(pathSeries, formatter);
 
        // hook up the plotUpdater to the data model:
        sampleData.addObserver(plotUpdater);
 
        // thin out domain tick labels so they dont overlap each other:
        dynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(5);
 
        dynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setRangeStepValue(10);
 
        dynamicPlot.setRangeValueFormat(new DecimalFormat("###.#"));
 
        // uncomment this line to freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);
 
        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);
	}
	
	public void onGraphStyleToggle(View v) {
		boolean styleOn = ((ToggleButton) v).isChecked();

		/*
		 * RectF graphRect = plot.getGraphWidget().getGridRect(); float
		 * segmentSize = 1.0f/6.0f; LinearGradient lg = new LinearGradient( 0,
		 * graphRect.top, 0, graphRect.bottom, new int[]{ Color.RED,
		 * Color.YELLOW, Color.GREEN, Color.WHITE}, new float[]{ 0,
		 * segmentSize*2, segmentSize*3, segmentSize*5 }, Shader.TileMode.REPEAT
		 * ); plot.getGraphWidget().getGridBackgroundPaint().setShader(lg);
		 */

		RectF rect = plot.getGraphWidget().getGridRect();
		BitmapShader myShader = new BitmapShader(Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.graph_background), 1, (int) rect.height(),
				false), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		Matrix m = new Matrix();
		m.setTranslate(rect.left, rect.top);
		myShader.setLocalMatrix(m);
		if (styleOn)
			plot.getGraphWidget().getGridBackgroundPaint().setShader(myShader);
		else
			plot.getGraphWidget().getGridBackgroundPaint()
					.setShader(WHITE_SHADER);

		plot.redraw();

	}
	
	@Override
    public void onResume() {
        // kick off the data generating thread:
        sampleThread = new Thread(sampleData);
        sampleThread.start();
        super.onResume();
    }
 
    @Override
    public void onPause() {
        sampleData.stopThread();
        super.onPause();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

package com.jun.realtime.navigator;

import java.text.DecimalFormat;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

import com.luoshanshan.bluetoothle.*;
import com.luoshanshan.wifidoor.WiFiDoorController;

public class MainActivity extends Activity {

	//private static final String TAG = MainActivity.class.getName();
	
	private final String wifiServerAddress = "192.168.43.4";
	private final int wifiServerPort = 5001;

	private Shader WHITE_SHADER = new LinearGradient(1, 1, 1, 1, Color.WHITE,
			Color.WHITE, Shader.TileMode.REPEAT);

	private XYPlot dynamicPlot;
	private PlotUpdater plotUpdater;
	private Thread sampleThread;
	private SampleDynamicXYDataSource sampleData;
	private BluetoothLE gatt;
	
	private Thread wifiThread;
	private WiFiDoorController wifi;

	private Button btnSearchBluetooth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initializePlot();
		initializeBluetooth();
		initializeWiFi();
	}
	
	private void initializeWiFi() {
		wifi = new WiFiDoorController(wifiServerAddress, wifiServerPort);
		
		
		wifiThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					wifi.openDoor();
				}
			}
		});
		
		wifiThread.setDaemon(true);
		wifiThread.start();
	}

	private void initializeBluetooth() {
		
		gatt = new BluetoothLE(this.getApplicationContext());
		gatt.registerHandler(sampleData.getHandler());

		btnSearchBluetooth = (Button) findViewById(R.id.button_search_bluetooth);
		btnSearchBluetooth.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				gatt.BTScanStart();
			}
		});
	}

	@Override
	protected void onDestroy() {
		gatt.setGattConnectReadRssiOn(false);
		/*** make sure all gatt connection is closed ****/
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	/**
	 * Must before initializeBluetooth
	 */
	private void initializePlot() {
		// get handles to our View defined in layout.xml:
		dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);

		plotUpdater = new PlotUpdater(dynamicPlot);

		// only display whole numbers in domain labels
		dynamicPlot.getGraphWidget().setDomainValueFormat(
				new DecimalFormat("0"));

		// getInstance and position datasets:
		sampleData = new SampleDynamicXYDataSource();
		SampleDynamicXYSeries pathSeries = new SampleDynamicXYSeries(
				sampleData, "Navigator Path");

		LineAndPointFormatter formatter = new LineAndPointFormatter(Color.rgb(
				0, 0, 0), null, null, null);
		formatter.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
		formatter.getLinePaint().setStrokeWidth(10);
		dynamicPlot.addSeries(pathSeries, formatter);

		// hook up the plotUpdater to the data model:
		sampleData.addObserver(plotUpdater);

		// thin out domain tick labels so they dont overlap each other:
		dynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
		dynamicPlot.setDomainStepValue(5);

		dynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
		dynamicPlot.setRangeStepValue(5);

		dynamicPlot.setDomainLabel("X-vals");
		dynamicPlot.setRangeLabel("Y-vals");

		dynamicPlot.setRangeValueFormat(new DecimalFormat("###"));

		// uncomment this line to freeze the range boundaries:
		dynamicPlot.setRangeBoundaries(0, 160, BoundaryMode.FIXED);
		dynamicPlot.setDomainBoundaries(0, 90, BoundaryMode.FIXED);

		// create a dash effect for domain and range grid lines:
		DashPathEffect dashFx = new DashPathEffect(new float[] {
				PixelUtils.dpToPix(3), PixelUtils.dpToPix(3) }, 0);
		dynamicPlot.getGraphWidget().getDomainGridLinePaint()
				.setPathEffect(dashFx);
		dynamicPlot.getGraphWidget().getRangeGridLinePaint()
				.setPathEffect(dashFx);
	}

	public void onGraphStyleToggle(View v) {
		boolean styleOn = ((ToggleButton) v).isChecked();

		RectF rect = dynamicPlot.getGraphWidget().getGridRect();
		BitmapShader myShader = new BitmapShader(Bitmap.createScaledBitmap(
				BitmapFactory.decodeResource(getResources(),
						R.drawable.graph_background), 1, (int) rect.height(),
				false), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		Matrix m = new Matrix();
		m.setTranslate(rect.left, rect.top);
		myShader.setLocalMatrix(m);
		if (styleOn) {
			dynamicPlot.getGraphWidget().getGridBackgroundPaint()
					.setShader(myShader);
		} else {
			dynamicPlot.getGraphWidget().getGridBackgroundPaint()
					.setShader(WHITE_SHADER);
		}

		dynamicPlot.redraw();
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

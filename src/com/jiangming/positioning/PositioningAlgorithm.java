package com.jiangming.positioning;

import com.jun.realtime.navigator.*;

public class PositioningAlgorithm {

	int cnt = 0;
	int[] rssiMean = { 0, 0, 0 };
	static int weakRssiThreshold = -90;
	static int strongRssiThreshold = -80;
	boolean getPointFlag = false;

	private int[][] estimationTable = { { -91, -100, -100, 0, 0 },
			{ -88, -100, -100, 0, 3 }, { -84, -100, -100, 0, 6 },
			{ -82, -100, -100, 0, 9 }, { -78, -100, -100, 0, 12 },
			{ -71, -100, -100, 3, 12 }, { -79, -91, -100, 6, 12 },
			{ -85, -85, -100, 9, 12 }, { -87, -79, -100, 12, 12 },
			{ -88, -74, -100, 15, 12 }, { -91, -76, -100, 18, 12 },
			{ -92, -84, -88, 21, 12 }, { -92, -86, -82, 24, 12 },
			{ -94, -89, -73, 27, 12 }, { -94, -91, -60, 30, 12 },
			{ -94, -91, -62, 30, 9 }, { -94, -91, -77, 30, 6 },
			{ -94, -91, -81, 30, 3 }, { -94, -91, -86, 30, 0 },
			{ -94, -91, -88, 30, -3 } };

	public final static String estimationDataPath = "data/estimation-data.txt";

	// public Point getPositionUsingRSSI(int[] rssiValues) {
	//
	// if (rssiValues == null) {
	// return null;
	// }
	//
	// double calculate = 0.0;
	// double lastEstimation = Double.MAX_VALUE;
	// int lastSlot = -1;
	//
	// for (int i = 0; i < estimationTable.length; i++) {
	// calculate = Math
	// .sqrt((Math.pow((rssiValues[0] - estimationTable[i][0]), 2)
	// + Math.pow((rssiValues[1] - estimationTable[i][1]),
	// 2) + Math.pow(
	// (rssiValues[2] - estimationTable[i][2]), 2)));
	//
	// if (calculate < lastEstimation) {
	// lastEstimation = calculate;
	// lastSlot = i;
	// }
	// }
	//
	// return new Point(estimationTable[lastSlot][4],
	// estimationTable[lastSlot][3]);
	// }

	/**
	 * Calculate the position point using RSSI
	 * 
	 * @param rssiValues
	 *            input from bluetooth
	 * @return null if this set of RSSI doesn't produce a point, otherwise a
	 *         point
	 */
	public Point getPositionUsingRSSI(int[] rssiValues) {

		// int x=0;int y = 0;
		// static int cnt = 0;
		// static int[] rssiMean = {0,0,0};
		// int weakRssiThreshold = -90;
		// int strongRssiThreshold = -80;
		// boolean getPointFlag = false;
		int x = 0;
		int y = 0;
		int cnt = 0;

		if ((rssiValues[0] >= 0) || (rssiValues[1] >= 0)
				|| (rssiValues[2] >= 0)) {
			x = 0;
			y = 0;
		}

		if (cnt < 5) {
			rssiMean[0] += rssiValues[0];
			rssiMean[1] += rssiValues[1];
			rssiMean[2] += rssiValues[2];
			cnt++;
		} else {
			rssiMean[0] /= 5;
			rssiMean[1] /= 5;
			rssiMean[2] /= 5;
			cnt = 0;
			getPointFlag = true;
		}

		/************************************
		 * rssiMean[0]:BLE_1 rssiMean[1]:BLE_2 rssiMean[2]:BLE_3
		 **************************************/
		if (getPointFlag == true) {
			if (rssiMean[0] <= weakRssiThreshold) {
				if (rssiMean[1] >= weakRssiThreshold) {
					/****** P3 *****************/
					x = 24;
					y = 12;
				} else {
					if (rssiMean[2] <= strongRssiThreshold) {
						/******** P1 ***********/
						x = 30;
						y = 6;
					} else {
						/******* P2 ******************/
						x = 30;
						y = 12;
					}
				}
			} else {
				if (rssiMean[1] >= strongRssiThreshold) {
					/****** P4 ******************/
					x = 15;
					y = 12;
				} else {
					if (rssiMean[1] >= weakRssiThreshold) {
						/****** P5 **************/
						x = 9;
						y = 12;
					} else {
						if (rssiMean[0] >= strongRssiThreshold) {
							/********* P6 **************/
							x = 12;
							y = 0;
						} else {
							/******** P7 **************/
							x = 6;
							y = 0;
						}
					}
				}
			}
			rssiMean[0] = 0;
			rssiMean[1] = 0;
			rssiMean[2] = 0;
			getPointFlag = false;
		}

		return new Point(x, y);

	}
}

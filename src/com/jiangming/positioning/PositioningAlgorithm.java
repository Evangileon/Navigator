package com.jiangming.positioning;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.jun.realtime.navigator.*;

public class PositioningAlgorithm {

	private int[][] estimationTable = null;
	public final static String estimationDataPath = "data/estimation_data.txt";

	/**
	 * Calculate the position point using RSSI
	 * 
	 * @param rssiValues
	 *            input from bluetooth
	 * @return null if this set of RSSI doesn't produce a point, otherwise a
	 *         point
	 */
	public Point getPositionUsingRSSI(int[] rssiValues) {

		if (rssiValues == null) {
			return null;
		}

		double calculate = 0.0;
		double lastEstimation = Double.MAX_VALUE;
		int lastSlot = -1;

		for (int i = 0; i < rssiValues.length; i++) {
			calculate = Math
					.sqrt((Math.pow((rssiValues[0] - estimationTable[i][0]), 2)
							+ Math.pow((rssiValues[1] - estimationTable[i][1]), 2)
							+ Math.pow((rssiValues[2] - estimationTable[i][2]), 2)));
			
			if(calculate < lastEstimation){
				lastEstimation = calculate;
				lastSlot = i;		
			}
		}

		return new Point(estimationTable[lastSlot][3], estimationTable[lastSlot][4]);
	}

	public void readDataFromTxtFile() {

		ArrayList<String> list = new ArrayList<String>();
		String s;

		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					estimationDataPath));
			BufferedReader br = new BufferedReader(isr);
			while ((s = br.readLine()) != null) {
				list.add(s);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		estimationTable = new int[list.size()][];

		for (int i = 0; i < list.size(); i++) {
			String[] params = list.get(i).split(" ");
			estimationTable[i] = new int[params.length];

			for (int j = 0; j < params.length; j++) {
				estimationTable[i][j] = Integer.parseInt(params[j]);
			}
		}
	}
}

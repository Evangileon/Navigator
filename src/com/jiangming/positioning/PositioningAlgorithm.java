package com.jiangming.positioning;

import com.jun.realtime.navigator.*;

public class PositioningAlgorithm {

	private int[][] estimationTable = {
			{-91, -100, -100, 0, 0},
			{-88, -100, -100, 0, 3},
			{-84, -100, -100, 0, 6},
			{-82, -100, -100, 0, 9},
			{-78, -100, -100, 0, 12},
			{-71, -100, -100, 3, 12},
			{-79, -91, -100, 6, 12},
			{-85, -85, -100, 9, 12},
			{-87, -79, -100, 12, 12},
			{-88, -74, -100, 15, 12},
			{-91, -76, -100, 18, 12},
			{-92, -84, -88, 21, 12},
			{-92, -86, -82, 24, 12},
			{-94, -89, -73, 27, 12},
			{-94, -91, -60, 30, 12},
			{-94, -91, -62, 30, 9},
			{-94, -91, -77, 30, 6},
			{-94, -91, -81, 30, 3},
			{-94, -91, -86, 30, 0},
			{-94, -91, -88, 30, -3}
	};
	
	public final static String estimationDataPath = "data/estimation-data.txt";

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

		for (int i = 0; i < estimationTable.length; i++) {
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

//	public void readDataFromTxtFile() {
//
//		ArrayList<String> list = new ArrayList<String>();
//		String s;
//
//		try {
//			InputStreamReader isr = new InputStreamReader(new FileInputStream(
//					estimationDataPath));
//			BufferedReader br = new BufferedReader(isr);
//			while ((s = br.readLine()) != null) {
//				list.add(s);
//			}
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//
//		estimationTable = new int[list.size()][];
//
//		for (int i = 0; i < list.size(); i++) {
//			String[] params = list.get(i).split(" ");
//			estimationTable[i] = new int[params.length];
//
//			for (int j = 0; j < params.length; j++) {
//				estimationTable[i][j] = Integer.parseInt(params[j]);
//			}
//		}
//	}
}

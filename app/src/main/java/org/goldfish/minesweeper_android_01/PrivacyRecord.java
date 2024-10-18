package org.goldfish.minesweeper_android_01;

import com.amap.api.location.AMapLocation;

import java.sql.Timestamp;

public record PrivacyRecord(Timestamp enterTime,
                      Timestamp exitTime, AMapLocation location) {

	public PrivacyRecord {
		if (enterTime == null) {
			throw new NullPointerException("enterTime is null");
		}
		if (exitTime == null) {
			throw new NullPointerException("exitTime is null");
		}
		if (location == null) {
			throw new NullPointerException("location is null");
		}
	}

	@Override
	public String toString() {
		return "PrivacyRecord{" +
			"enterTime=" + enterTime +
			", exitTime=" + exitTime +
			", location=" + location.getLocationDetail() +
			'}';
	}

	/**
	 * 展示格式
	 * 两行
	 * 第一行：进入日期 离开日期 经度
	 * 第二行：进入时间 离开时间 纬度
	 * @return
	 */
	public String[][] showFormat(){
		String[][] result = new String[2][3];
		String[] enterTime = this.enterTime.toString().split(" ");
		String[] exitTime = this.exitTime.toString().split(" ");
		if(enterTime.length != 2 || exitTime.length != 2){
			throw new IllegalArgumentException("时间格式错误");
		}
		result[0][0] = enterTime[0];
		result[0][1] = exitTime[0];
		result[0][2] = Math.abs((int)location.getLongitude()) + "°" + (location.getLongitude() > 0 ? "E" : "W");
		result[1][0] = enterTime[1];
		result[1][1] = exitTime[1];
		result[1][2] = Math.abs((int)location.getLatitude()) + "°" + (location.getLatitude() > 0 ? "N" : "S");

		return result;
	}

}
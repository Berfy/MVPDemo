package com.wlb.agent.ui.user.helper.ocr.parser;

public class DrivingLicense {
	/**
	 * 车牌号
	 */
	public String licenseNo;
	/**
	 * 车主
	 */
	public String owner;
	/**
	 * 厂牌型号
	 */
	public String model;
	/**
	 * 车架号
	 */
	public String vin;
	/**
	 * 发动机号
	 */
	public String enginNo;
	/**
	 * 注册日期
	 */
	public long registerDate;

	public String date;

	@Override
	public String toString() {
		return "DrivingLicense [licenseNo=" + licenseNo + ", owner=" + owner + ", model=" + model + ", vin=" + vin
				+ ", enginNo=" + enginNo + ", registerDate=" + registerDate + "]";
	}

}

package com.wlb.agent.core.data.insurance.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.insurance.entity.Insurance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 保险公司报价详情
 * 
 * @author 张全
 */
public class InsurancePriceResponse extends BaseResponse {

	public double totalPremium;//总保费
	public String companyCode;
	public String companyName;
	public long vehicleStartDate;
	public long vehicleEndDate;
	public long businessStartDate;
	public long businessEndDate;
	public int occurredNum;
	public InsurancePrice totalPrice;
	public List<Insurance> primary; //主险
	public List<Insurance> additional;//附加险
	public Insurance vehicle;


	public static class InsurancePrice implements Serializable{
		public double businessPrice;
		public double vehiclePrice;
		public double taxPrice;
		public double diffPrice;
		public double exemptPrice;
		public double businessTotalPrice;
		public double vehicleTotalPrice;
	}


	/**
	 * 获取投保的商业险种
	 *
	 * @return
	 */
	public List<Insurance> getSelectedCommericalInsurances() {
		List<Insurance> insuranceList = new ArrayList<Insurance>();
		for (Insurance item : primary) {
			if (item.isSelected()) {
				insuranceList.add(item);
			}
		}
		for (Insurance item : additional) {
			if (item.isSelected()) {
				insuranceList.add(item);
			}
		}
		return insuranceList;
	}

	/**
	 * 是否投保了商业险
	 *
	 * @return
	 */
	public boolean isCommericalSelected() {
		return getSelectedCommericalInsurances().size() > 0;

	}

	/**
	 * 是否投保了交强险
	 *
	 * @return
	 */
	public boolean isJqxSelected() {
		return vehicle.isSelected();
	}

	/**
	 * 获取选中的不计免赔险种
	 *
	 * @return
	 */
	public List<Insurance> getCheckedBjmp() {
		List<Insurance> checkedBjmp = new ArrayList<>();
		for (Insurance insurance : primary) {
			if (insurance.isBJMPChecked()) {
				checkedBjmp.add(insurance);
			}
		}
		for (Insurance insurance : additional) {
			if (insurance.isSelected() && insurance.isBJMPChecked()) {
				checkedBjmp.add(insurance);
			}
		}
		return checkedBjmp;
	}

	/**
	 * 获取车损险
	 *
	 * @return
	 */
	public Insurance getDamageInsurance() {
		for (Insurance insurance : primary) {
			if (insurance.isDAMAGE()) {
				return insurance;
			}
		}
		return null;
	}

}

package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.CommissionDetailInfo;

/**
 * 钱包流水明细
 */
public class WalletFlowDetailResponse extends BaseResponse {
	/**
	 * ID
	 */
	public long billId;
	/**
	 * 1表示挣钱；2表示扣钱
	 */
	public int billType;

	/**
	 * 1佣金；2提现
	 */
	public int category;
	/**
	 * 状态信息
	 */
	public String statusText;
	/**
	 * 金额
	 */
	public double amount;// 佣金总额
	/**
	 * 时间戳
	 */
	public long timestamp;// 所得税金额

	/**
	 * 卡号
	 */
	public String bankCrad;
	public CommissionDetailInfo detail;

}

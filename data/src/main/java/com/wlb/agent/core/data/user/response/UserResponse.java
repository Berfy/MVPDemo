package com.wlb.agent.core.data.user.response;

import android.text.TextUtils;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.entity.ThirdParty;

import java.util.ArrayList;
import java.util.List;

public class UserResponse extends BaseResponse {
	/**
	 * 用户id
	 */
	public String token;
	/**
	 * 是否已登录
	 */
	public boolean isLogin;
	/**
	 *等级 1钻石， 2金牌，3银牌
	 */
	public String my_grade;
	/**
	 * 用户名
	 */
	public String account;
	/**
	 * 邀请码
	 */
	public String inviteCode;
	/**
	 * 姓名
	 */
	public String name;
	/**
	 * 身份证号
	 */
	public String identityCard;
	/**
	 * 银行名称
	 */
	public String bankName;
	/**
	 * 银行卡号
	 */
	public String bankCard;
	/**
	 * 通讯地址——详细地址
	 */
	public String address;

	/**
	 * 用户头像url
	 */
	public String avatar;

	/**
	 * 用户昵称
	 */
	public String nick_name;
	/**
	 * 是否加V
	 */
	private int v_flag;

	/**
	 * 积分
	 */
	public int score;

	/**
	 * 城市
	 */
	public String city;

	/**
	 * 上传证件相关
	 * real_name : 姓名
	 * certificate_no :身份证
	 * cert_url： [url1, url2] 身份证正反面url
	 */
	public IdAuthInfo id_auth_info=new IdAuthInfo();
	/**
	 * 用户绑定的email
	 */
	public String email;
	/**
	 * 用户电话号码
	 */
	public String phone;

	/**
	 * 第三方账号相关
	 * type : 1:QQ 2:微信 3：Sina
	 * is_band：1已绑定  0未绑定
	 */
	public List<ThirdParty> third_party = new ArrayList();

	public boolean isVFlag(){
		return v_flag==1;
	}

	public boolean isUnBind(){
		return TextUtils.isEmpty(phone);
	}

}

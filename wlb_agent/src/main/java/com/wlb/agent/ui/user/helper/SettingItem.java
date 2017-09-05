package com.wlb.agent.ui.user.helper;

import com.android.util.LContext;
import com.wlb.agent.R;

/**
 * @author 张全
 */
public class SettingItem {
	public int id;
	//左边
	public int leftIcon;
	public String subject;
	public boolean showNewFlag;//是否显示新消息提示
	//右边
	public int rightIcon=R.drawable.user_arrow_right;
	public String rightTxt;
	public String rightSelTxt;
	
	public SettingGroup ownerGroup;

	public SettingItem(int id, int subject, SettingGroup settingGroup) {
		this(id, LContext.getString(subject), settingGroup);
	}
	public SettingItem(int id, String subject, SettingGroup settingGroup) {
		this.id = id;
		this.subject = subject;
		this.ownerGroup  = settingGroup;
	}
}

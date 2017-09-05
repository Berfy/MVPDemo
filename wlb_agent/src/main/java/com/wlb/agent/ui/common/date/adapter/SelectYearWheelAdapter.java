package com.wlb.agent.ui.common.date.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wlb.agent.R;
import com.wlb.agent.ui.common.date.AbstractWheelTextAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectYearWheelAdapter extends AbstractWheelTextAdapter {

	private String[] strs;
	private List<String> list;
	private int gravity;

	public SelectYearWheelAdapter(Context context, String[] strs) {
		super(context, R.layout.adapter_select_year_wheel_layout, NO_RESOURCE);
		// TODO Auto-generated constructor stub
		this.strs = strs;
		list = new ArrayList<String>();
	}

	public SelectYearWheelAdapter(Context context, List<String> list) {
		super(context, R.layout.adapter_select_year_wheel_layout, NO_RESOURCE);
		// TODO Auto-generated constructor stub
		strs = new String[] {};
		this.list = list;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		if (strs.length == 0) {
			return list.size();
		} else {
			return strs.length;
		}
	}

	@Override
	protected CharSequence getItemText(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = super.getItem(index, convertView, parent);
		TextView tvValue = (TextView) view
				.findViewById(R.id.adapter_select_year_tv_value);
		if (strs.length == 0) {
			tvValue.setText(list.get(index));
		} else {
			tvValue.setText(strs[index]);
		}
		if (gravity == 0) {
			tvValue.setGravity(Gravity.CENTER_HORIZONTAL);
		} else if (gravity == 1) {
			tvValue.setGravity(Gravity.RIGHT);
		}else if (gravity == 2) {
			tvValue.setGravity(Gravity.LEFT);
		}
		return view;
	}

	public int getGravity() {
		return gravity;
	}

	public void setGravity(int gravity) {
		this.gravity = gravity;
	}

}

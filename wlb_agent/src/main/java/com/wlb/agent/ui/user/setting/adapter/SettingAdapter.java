package com.wlb.agent.ui.user.setting.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlb.agent.R;
import com.wlb.agent.ui.user.helper.SettingGroup;
import com.wlb.agent.ui.user.helper.SettingItem;

import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 设置列表
 * 
 * @author 张全
 */
public class SettingAdapter extends ListAdapter<SettingGroup> {
	private OnClickListener onClickListener;

	public SettingAdapter(Context context, List<SettingGroup> list,
			OnClickListener onClickListener, int layoutId) {
		super(context, list, layoutId);
		this.onClickListener = onClickListener;
	}

	private void setClickListener(SettingItem item, View container) {
		container.setOnClickListener(onClickListener);
		container.setId(item.id);
		container.setTag(R.id.obj_tag, item);
	}

	@Override
	public void setViewData(ViewHolder holder, SettingGroup group) {
		LinearLayout mGroupView = holder.getView
				(R.id.item_group);

		mGroupView.removeAllViews();
		List<SettingItem> items = group.getItems();
//		if (group.topMargin > 0) {
//			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, group.topMargin);
//			View view = new View(getContext());
//			view.setBackgroundColor(getContext().getResources().getColor(R.color.common_line1));
//			mGroupView.addView(view,layoutParams);
//		}

		int size = items.size();
		for (int i = 0; i < size; i++) {
			SettingItem item = items.get(i);
			View view = View.inflate(getContext(), R.layout.setting_item, null);
			ImageView iconView = (ImageView) view.findViewById(R.id.item_icon);
			TextView nameView = (TextView) view.findViewById(R.id.item_name);
			ImageView mNewFlag = (ImageView) view.findViewById(R.id.item_new);
			TextView rightTextView = (TextView) view
					.findViewById(R.id.item_right_txt);
			TextView rightTextSelView = (TextView) view.findViewById(R.id.item_rightSel_txt);
			ImageView rightImgView = (ImageView) view
					.findViewById(R.id.item_arrow);
			View bottomLineView = view.findViewById(R.id.item_bottomline);

			iconView.setImageResource(item.leftIcon);
			nameView.setText(item.subject);

			if (null != item.rightTxt) {
				rightTextView.setText(item.rightTxt);
			}
			if (null != item.rightSelTxt) {
				rightTextSelView.setText(item.rightSelTxt);
			}
			if (item.rightIcon > 0) {
				rightImgView.setImageResource(item.rightIcon);
			} else {
				rightImgView.setVisibility(View.GONE);
			}

			if (item.showNewFlag) {
				mNewFlag.setVisibility(View.VISIBLE);
			}

			if (i == size - 1) {
				bottomLineView.setVisibility(View.GONE);
			}
			setClickListener(item, view);
			mGroupView.addView(view);
		}
	}
}

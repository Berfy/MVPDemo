package common.widget.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自动滚动的ViewPager适配器
 * 
 * @author zhangquan
 * 
 * @param <E>
 *            实体数据类型
 */
public abstract class AutoScrollPagerAdapter<E> extends PagerAdapter {
	protected Context mContext;
	private int layoutId;
	protected  SparseArray<View> views=new SparseArray<>();
	private List<E> mList = Collections.synchronizedList(new ArrayList<E>());
	private final int count = 5000;

	public AutoScrollPagerAdapter(Context context, List<E> list, int layoutId) {
		this.mContext = context;
		this.layoutId = layoutId;
		if (null != list && !list.isEmpty()) {
			mList.addAll(list);
			list = mList;
		}
		resetViews();
	}

	public void refresh(List<E> list) {
		this.mList = list;
		resetViews();
		notifyDataSetChanged();
	}

	public List<E> getList() {
		return mList;
	}

	protected void resetViews() {
		views.clear();
		int dataSize = getList().size();
		int viewCount = dataSize > 1 ? dataSize * 3 : dataSize;
		for (int i = 0; i < viewCount; i++) {
			views.put(i, View.inflate(mContext, layoutId, null));
		}
	}

	@Override
	public int getCount() {
		return getList().size() > 1 ? count : getList().size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int viewIndex = position % views.size();

		View childView = views.get(viewIndex);

		if (null == childView) {
			for(int i=0;i< views.size();i++){
				View view = views.valueAt(i);
				if (null == view.getParent()) {
					childView=view;
					break;
				}
			}
		}
		if (null == childView) {
			childView = View.inflate(mContext, layoutId, null);
			views.put(viewIndex, childView);
		}

		try {
			container.addView(childView);
		} catch (Exception e) {
			e.printStackTrace();
			childView = View.inflate(mContext, layoutId, null);
			views.put(viewIndex, childView);
			container.addView(childView);
		}

		int dataIndex = position % getList().size();
		E data = getList().get(dataIndex);
		setItemData(childView, dataIndex, data);
		return childView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		int viewIndex = position % views.size();
		try {
			container.removeView(views.get(viewIndex));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 给item设置数据
	 * 
	 * @param contentView
	 *            当前页view
	 * @param position
	 *            当前页面下标
	 * @param data
	 *            给当前页设置的数据
	 */
	public abstract void setItemData(View contentView, int position, E data);
}

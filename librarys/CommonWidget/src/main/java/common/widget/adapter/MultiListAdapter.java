package common.widget.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter基类(多个子类型)
 *
 * @author Berfy
 */
public abstract class MultiListAdapter<E> extends BaseAdapter {

    private List<List<E>> mData;
    private List<E> mList = new ArrayList<>();
    protected Context mContext;
    private int[] mLayoutIds;//所有布局集合
    private int[] mPostions;//布局起始位置集合，与layoutIds长度必须一致

    public MultiListAdapter(Context context, int[] layoutIds) {
        mContext = context;
        mLayoutIds = layoutIds;
    }

    public void setData(List<List<E>> datas) {
        mList.clear();
        mData = datas;
        mPostions = new int[mData.size()];
        int count = 0;
        int i = 0;
        if (null != mData) {
            for (List<E> list : mData) {
                mPostions[i] = count;
                i++;
                count += list.size();
                mList.addAll(list);
            }
        }
        notifyDataSetChanged();
    }

    public int[] getPositions() {
        return mPostions;
    }

    public Context getContext() {
        return mContext;
    }

    public int getCount() {
        return mList.size();
    }

    public List<E> getData() {
        return mList;
    }

    public E getItem(int pPosition) {
        return mList.get(pPosition);
    }

    public long getItemId(int pPosition) {
        return pPosition;
    }

    public void setLayoutId(int[] layoutIds) {
        this.mLayoutIds = layoutIds;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mPostions) {
            int size = mPostions.length;
            if (size > 1) {
                for (int i = size - 1; i >= 0; i--) {
                    if (position >= mPostions[i]) {
                        return i;
                    }
                }
            } else {
                return mPostions[0];
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        if (null != mLayoutIds) {
            return mLayoutIds.length;
        }
        return super.getViewTypeCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder viewHolder = ViewHolder.getView(mContext, position,
                convertView, mLayoutIds[type]);
        setViewData(position, type, viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void setViewData(int position, int type, ViewHolder holder, E data);
}

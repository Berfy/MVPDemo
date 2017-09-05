package common.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter基类
 *
 * @author zhangquan
 */
public abstract class ListAdapter<E> extends BaseAdapter {

    private List<E> mList = Collections.synchronizedList(new ArrayList<E>());
    protected Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mLayoutId;

    public ListAdapter(Context context, List<E> list, int layoutId) {
        mContext = context;
        this.mLayoutId = layoutId;
        mLayoutInflater = LayoutInflater.from(mContext);
        if (null != list) {
            mList.addAll(list);
        }
    }

    public void refresh(List<E> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return mContext;
    }

    public int getCount() {
        return mList.size();
    }

    public E getItem(int pPosition) {
        if (mList.size() > pPosition) {
            return mList.get(pPosition);
        } else {
            return null;
        }
    }

    public long getItemId(int pPosition) {
        return pPosition;
    }

    public List<E> getList() {
        return mList;
    }

    public void setList(List<E> mList) {
        this.mList = mList;
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public void setLayoutInflater(LayoutInflater mLayoutInflater) {
        this.mLayoutInflater = mLayoutInflater;
    }

    public void setLayoutId(int layoutId) {
        this.mLayoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = ViewHolder.getView(mContext, position,
                convertView, mLayoutId);
        setViewData(viewHolder, getItem(position));
        return viewHolder.getConvertView();
    }

    public abstract void setViewData(ViewHolder holder, E data);
}

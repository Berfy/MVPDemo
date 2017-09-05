package common.widget.gridlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by JiaHongYa
 */
public abstract class GridListAdapter<T> {
    private final GridListDataSetObservable mDataSetObservable = new GridListDataSetObservable();
    protected List<T> dataList;
    protected Context context;
    protected LayoutInflater inflater;
    private int layoutId;

    public GridListAdapter(Context context, int layoutId, List<T> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.layoutId = layoutId;
        inflater = LayoutInflater.from(context);
    }

    public List<T> getList() {
        return dataList;
    }

    public int getCount() {
        return dataList.size();
    }

    public T getItem(int position) {
        return dataList.get(position);
    }


    public View getView(int position, ViewGroup convertView, ViewGroup parent) {
        View view = inflater.inflate(layoutId, convertView, false);
        T data = getItem(position);
        bindView(data, view);
        return view;
    }

    protected abstract void bindView(T data, View view);

    public void registerDataSetObserver(GridListDataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(GridListDataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    //单条数据发生变化
    public void notifyItemChanged(int pos) {
        mDataSetObservable.notifyItemChanged(pos);
    }
}

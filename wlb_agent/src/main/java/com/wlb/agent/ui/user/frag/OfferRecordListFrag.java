package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.offer.entity.OfferRecord;
import com.wlb.agent.core.data.offer.response.OfferResponse;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.user.adapter.UserOfferRecordListAdapter;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.adapter.ListAdapter;

/**
 * Created by Berfy on 2017/7/24.
 * 报价记录
 */
public class OfferRecordListFrag extends SimpleFrag implements View.OnClickListener {

    private PopupWindowUtil mPopupWindowUtil;

    private ListFrag mListFrag;

    @BindView(R.id.layout_item1)
    LinearLayout mLlItem1;
    @BindView(R.id.layout_item2)
    LinearLayout mLlItem2;
    @BindView(R.id.frame_layout)
    FrameLayout mFrameLayout;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.user_offer_record,
                OfferRecordListFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_offer_record_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mListFrag = new ListFrag();
        getChildFragmentManager().beginTransaction().replace(R.id.frame_layout, mListFrag).commit();
    }

    @OnClick({R.id.layout_item1, R.id.layout_item2})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_item1:
                mPopupWindowUtil.showUserOfferRecordLeft(mLlItem1, 0, new PopupWindowUtil.OnPopListSelectListener() {
                    @Override
                    public void select(int position) {

                    }
                });
                break;
            case R.id.layout_item2:
                mPopupWindowUtil.showUserOfferRecordRight(mLlItem2, 0, new PopupWindowUtil.OnPopListSelectListener() {
                    @Override
                    public void select(int position) {

                    }
                });
                break;
        }
    }

    public static class ListFrag extends CommonListFrag {

        public ListFrag() {
        }

        @Override
        protected void initParams(Bundle savedInstanceState) {

        }

        @Override
        protected Task executeTask(long lastId, ICallback callback) {
            OfferResponse offerResponse = new OfferResponse();
            List<OfferRecord> datas = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                OfferRecord offerRecord = new OfferRecord("", "京1234" + i, "", "", "");
                datas.add(offerRecord);
            }
            offerResponse.setData(datas);
            callback.success(offerResponse);
            callback.end();
            return new Task(null, null);
        }

        @Override
        protected ListAdapter initAdapterWithData(List mDataList) {
            UserOfferRecordListAdapter adapter = new UserOfferRecordListAdapter(mContext, new UserOfferRecordListAdapter.OnLookListener() {
                @Override
                public void look(OfferRecord offerRecord) {
                    ToastUtil.show("查看报价记录");
                }
            });
            adapter.refresh(mDataList);
            return adapter;
        }

        @Override
        protected ListData getDataListFromResponse(BaseResponse baseResponse) {
            OfferResponse response = (OfferResponse) baseResponse;
            List<OfferRecord> orders = response.getData();
            long lastId = orders.size();
            return new ListData(lastId, orders);
        }

        @Override
        protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }
}

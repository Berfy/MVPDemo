package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.device.TimeUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.Score;
import com.wlb.agent.core.data.user.response.ScoreResponse;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.user.adapter.UserScoreListAdapter;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import common.widget.adapter.ListAdapter;

/**
 * Created by Berfy on 2017/7/25.
 * 积分
 */
public class UserScoreFrag extends CommonListFrag {

    private View mHeaderView;

    public static void start(Context context) {
        SimpleFragAct.start(context, new SimpleFragAct.SimpleFragParam(R.string.user_score, UserScoreFrag.class));
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        getTitleBar().setRightText(R.string.user_score_rule);
        getTitleBar().setOnRightTxtClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebViewFrag.start(mContext, new WebViewFrag.WebViewParam("", false, "http://www.baidu.com", null));
            }
        });
        mHeaderView = View.inflate(mContext, R.layout.adapter_user_score_list_item_top, null);
        addListHead(mHeaderView);
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
        List<Score> datas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Score score = new Score("", "标题" + i, "", TimeUtil.getCurrentTime(), i % 2 == 0 ? 0 : 1, 100);
            datas.add(score);
        }
        ScoreResponse response = new ScoreResponse(datas);
        callback.success(response);
        callback.end();
        return new Task(null, null);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        UserScoreListAdapter adapter = new UserScoreListAdapter(mContext);
        adapter.setList(mDataList);
        return adapter;
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        ScoreResponse scoreResponse = (ScoreResponse) baseResponse;
        int lastId = scoreResponse.getData().size() - 1;
        return new ListData(lastId, scoreResponse.getData());
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

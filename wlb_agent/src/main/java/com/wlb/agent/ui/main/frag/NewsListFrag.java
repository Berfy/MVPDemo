package com.wlb.agent.ui.main.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.LContext;
import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.NewsInfo;
import com.wlb.agent.core.data.agentservice.response.NewsListResponse;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.ui.main.adapter.NewsAdapter;

import java.util.List;

import common.widget.adapter.ListAdapter;

/**
 * 知识库
 */
public class NewsListFrag extends CommonListFrag {
    private static final String PARAM = "code";
    private String code;


    public static Bundle getParamBundle(String code) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM, code);
        return bundle;
    }


    @Override
    protected void initParams(Bundle savedInstanceState) {
        if (null != getArguments()) code = getArguments().getString(PARAM);
        setDivider(getResources().getDrawable(R.drawable.line));
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
        return AgentServiceClient.doGetNewsList(code, lastId, getPageCount(), callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        return new NewsAdapter(mContext, mDataList, R.layout.news_list_item);
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        NewsListResponse response = (NewsListResponse) baseResponse;
        long lastId = 0;
        List<NewsInfo> dataList = response.list;
        if (!dataList.isEmpty()) {
            //最后一条数据的id
            lastId = dataList.get(dataList.size() - 1).id;
        }
        return new ListData(response.list, lastId);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NewsInfo entity = (NewsInfo) mAdapter.getList().get(position);
        if (TextUtils.isEmpty(entity.url)) {
            return;
        }

        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        webViewParam.url = entity.url;
        webViewParam.title = entity.title;
        webViewParam.shouldResetTitle = false;
        webViewParam.shareInfo = new WebViewFrag.ShareInfo();
        webViewParam.shareInfo.link = entity.url;
        webViewParam.shareInfo.title = LContext.appName;
        webViewParam.shareInfo.content = entity.title;
        WebViewFrag.start(mContext, webViewParam);
    }
}

package com.wlb.agent.ui.main.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.R;
import com.wlb.agent.common.service.BackgroundTaskService;
import com.wlb.agent.core.data.agentservice.AgentServiceClient;
import com.wlb.agent.core.data.agentservice.entity.MessageInfo;
import com.wlb.agent.core.data.agentservice.response.MessageResponse;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.main.adapter.MsgListAdapter;
import com.wlb.agent.ui.user.frag.WalletFlowFrag;

import java.util.List;

import common.widget.adapter.ListAdapter;

/**
 * 消息列表
 *
 * @author 张全
 */
public class MsgListFrag extends CommonListFrag {

    private static final String PARAM = "messageType";
    private int category;//1公告、2消息

    public static Bundle getParamBundle(int category) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM, category);
        return bundle;
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        category = getArguments().getInt(PARAM);
        setPageCount(10);
        setDividerWithHeight(getResources().getDrawable(R.color.c_e9e9e9), 10, true);
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
        return AgentServiceClient.doMsgList(lastId, category, getPageCount(), callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        return new MsgListAdapter(mContext, mDataList, R.layout.msg_list_item);
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        MessageResponse response = (MessageResponse) baseResponse;
        long lastId = 0;
        List<MessageInfo> list = response.list;
        if (!list.isEmpty()) {
            lastId = list.get(list.size() - 1).msgId;
        }
        return new ListData(list, lastId);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MessageInfo messageInfo = (MessageInfo) getAdapter().getList().get(position);
        if (category == 2 && !messageInfo.isReaded()) {
            //消息 发送已读回执
            BackgroundTaskService.start(mContext, messageInfo);
        }
        int msgType = messageInfo.msgType;
        switch (msgType) {
            case 1://佣金
                WalletFlowFrag.start(mContext);
                break;
            case 2: //保单
                long articleId = messageInfo.articleId;
                if (articleId > 0) {
                    //要修改
//                    InsuranceOrderDetailFrag.start(mContext, articleId);
                }
                break;
            default:
                if (!TextUtils.isEmpty(messageInfo.articleUrl)) {
                    //打开链接
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url=messageInfo.articleUrl;
                    webViewParam.title=messageInfo.title;
                    WebViewFrag.start(mContext,webViewParam);
                }
                break;
        }
        //点击过后标记为已读
        messageInfo.setReaded(true);
        getAdapter().notifyDataSetChanged();
    }
}

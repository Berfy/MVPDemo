package com.wlb.agent.core.data.agentservice;

import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.response.GsonParser;
import com.android.util.http.task.Task;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.DataConfig;
import com.wlb.agent.core.data.agentservice.response.BannerResponse;
import com.wlb.agent.core.data.agentservice.response.MessageResponse;
import com.wlb.agent.core.data.agentservice.response.NewsCategoryResponse;
import com.wlb.agent.core.data.agentservice.response.NewsListResponse;
import com.wlb.agent.core.data.agentservice.response.UploadImgReponse;
import com.wlb.agent.core.data.agentservice.response.VersionResponse;
import com.wlb.agent.core.data.base.RequestBuilder;
import com.wlb.agent.core.data.base.SimpleJsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 张全
 */
public class AgentServiceClient {

    /**
     * 上传文件
     * @param img
     * @param callback
     * @return
     */
    public static Task doUploadFile(String img,ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.UPLOAD_FILE, callback, new GsonParser(UploadImgReponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(img);
            postJson.put("imgs", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }
    /**
     * 获取Banner信息
     *
     * @param callback
     * @return
     */
    public static Task doGetBanner(int type,ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.COMMON_BANNER, callback, new GsonParser(BannerResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 版本检测升级
     *
     * @param callback
     * @return
     */
    public static Task doCheckUpdate(ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.APP_UPDATE, callback, new GsonParser(VersionResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("channel", DataConfig.umeng_channel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * H5版本检测
     *
     * @param callback
     * @return
     */
    public static Task doCheckH5Update(ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.APP_H5_UPDATE, callback, new GsonParser(VersionResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("os", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 知识库分类
     *
     * @return
     */
    public static Task doGetNewCategory(ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.LIBRARY_CLASSIFY,
                callback, new GsonParser<>(NewsCategoryResponse.class));
        builder.setPostJsonData(builder.getPostJson());
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 知识库分类列表
     *
     * @param code     分类code
     * @param lastId   上一次请求的最后一条数据的id，首次请求传0
     * @param pageNum  每页显示的条数(请求的条数)
     * @param callback
     * @return
     */
    public static Task doGetNewsList(final String code, long lastId, int pageNum, ICallback callback) {

        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.LIBRARY_CLASSIFY_LIST,
                callback, new GsonParser<>(NewsListResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("code", code);
            postJson.put("lastId", lastId);
            postJson.put("pageNum", pageNum);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 未读消息数
     *
     * @param callback
     * @return
     */
    public static Task doGetUnreadMessageCount(ICallback callback) {

        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.MSG_UNREADCOUNT, callback, new GsonParser<>(MessageResponse.class));
        JSONObject postJson = builder.getPostJson();
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 消息列表
     *
     * @param lastMsgId   最后一条消息的Id,第一次为0
     * @param messageType 消息类型 公告：1；消息：2
     * @param pageCount   每页请求的消息条数
     * @param callback
     * @return
     */
    public static Task doMsgList(long lastMsgId, int messageType, int pageCount, ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.MSG_LIST, callback,  new GsonParser<>(MessageResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("lastMsgId", lastMsgId);
            postJson.put("category", messageType);
            postJson.put("pageCount", pageCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 消息已读汇报
     *
     * @param msgId
     * @param messageType
     * @param callback
     * @return
     */
    public static Task doMsgReadReport(long msgId, int messageType, ICallback callback) {
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.MSG_READ, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("msgId", msgId);
            postJson.put("messageType", messageType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 上传联系人
     * @param contacts
     * @param callback
     * @return
     */
    public static Task doPostContacts(String contacts,ICallback callback){
        RequestBuilder builder= RequestBuilder.postBuilder(ApiHost.CONTACTS, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("contact", contacts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

}

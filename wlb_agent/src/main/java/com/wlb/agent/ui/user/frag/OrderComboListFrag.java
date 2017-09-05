package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.combo.ComboClient;
import com.wlb.agent.core.data.combo.response.ComboResponse;
import com.wlb.agent.core.data.combo.response.ComboResponse.ComboEntity;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 订单可购买的套餐列表
 *
 * @author 张全
 */

public class OrderComboListFrag extends ComboListFrag {
    private static final String PARAM = "ORDERNO";
    private String orderNo;
    private TextView et_name;
    private TextView et_icard;
    private TextView et_phone;
    private View headView;
    private ViewGroup radioGroup;

    public static void start(Context context, String orderNo) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM, orderNo);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("超值套餐", OrderComboListFrag.class);
        param.paramBundle = bundle;
        SimpleFragAct.start(context, param);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.combo_list_frag;
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        super.initParams(savedInstanceState);
        getTitleBar().setRightText(null);
        orderNo = getArguments().getString(PARAM);
        getListView().setOnRefreshListener(null);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        headView = inflater.inflate(R.layout.combo_list_head, null, false);
        et_name = (TextView) headView.findViewById(R.id.et_name);
        et_icard = (TextView) headView.findViewById(R.id.et_icard);
        et_phone = (TextView) headView.findViewById(R.id.et_phone);
        headView.findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    return;
                }
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.url = H5.CONTACT_PERSON;
                WebViewFrag.start(mContext, webViewParam);
            }
        });
        radioGroup = (ViewGroup) headView.findViewById(R.id.radioGroup);
        int childCount = radioGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = radioGroup.getChildAt(i);
            childView.setTag(i);
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectItem((Integer) v.getTag());
                }
            });
        }
        getListView().addHeaderView(headView);

        addAction(Constant.IntentAction.CONTACTINFO);
    }

    private void setSelectItem(int index) {
        View childView = radioGroup.getChildAt(index);
        childView.setSelected(!childView.isSelected());
        int childCount = radioGroup.getChildCount();
        for (int j = 0; j < childCount; j++) {
            if (j != index) {
                radioGroup.getChildAt(j).setSelected(false);
            }
        }
        int visibility = View.GONE;
        if(index==childCount-1&&childView.isSelected()){
            visibility=View.VISIBLE;
        }
        setBannerVisibility(visibility);
    }
    private void setBannerVisibility(int visibility){
        headView.findViewById(R.id.banner1).setVisibility(visibility);
        headView.findViewById(R.id.banner2).setVisibility(visibility);
        headView.findViewById(R.id.banner3).setVisibility(visibility);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (containsAction(Constant.IntentAction.CONTACTINFO)) {
            String contact = (String) pushEvent.getData();
            try {
                JSONObject jsonObject = new JSONObject(contact);
                String name = jsonObject.optString("userName");
                String idcard = jsonObject.optString("identityCard");
                String phone = jsonObject.optString("phoneNO");
                et_name.setText(name);
                et_icard.setText(idcard);
                et_phone.setText(phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Subscription executeTask(long lastId, Subscriber<BaseResponse> subscriber) {
        return ComboClient.doOrderComboList(orderNo)
                .doOnNext(new Action1<ComboResponse>() {
                    @Override
                    public void call(ComboResponse comboResponse) {
                        if (comboResponse.isSuccessful()) {
                            List<ComboEntity> list = comboResponse.getList();
                            boolean hasSelectedItem = false;
                            for (ComboEntity item : list) {
                                if (item.isSelect == 1) {
                                    hasSelectedItem = true;
                                    break;
                                }
                            }
                            if (hasSelectedItem) {
                                int type = comboResponse.getType();
                                if (type > 0) {
                                    View chidView = radioGroup.getChildAt(type - 1);
                                    if (chidView != null) {
                                        chidView.setSelected(true);
                                    }
                                    if(type==radioGroup.getChildCount()){
                                        setBannerVisibility(View.VISIBLE);
                                    }
                                }
                                et_name.setText(comboResponse.getName());
                                et_icard.setText(comboResponse.getCardNo());
                                et_phone.setText(comboResponse.getPhone());
                            }
                        }
                    }
                })
                .subscribe(subscriber);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<ComboEntity> list = getAdapter().getList();
        ComboEntity comboEntity = list.get(position);
        comboEntity.isSelect = comboEntity.isSelect == 1 ? 0 : 1;
        getAdapter().notifyDataSetChanged();
    }

    @OnClick({R.id.save})
    public void save(View v) {
        List<ComboEntity> list = getAdapter().getList();
        final ArrayList<ComboEntity> comboEntities = new ArrayList<>();
        for (ComboEntity comboEntity : list) {
            if (comboEntity.isSelect == 1) {
                comboEntities.add(comboEntity);
            }
        }
        int selType = 0;
        String name = null;
        String icard = null;
        String phone = null;
        if (!comboEntities.isEmpty()) {
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                boolean selected = radioGroup.getChildAt(i).isSelected();
                if (selected) selType = i + 1;
            }
            if(selType==0){
                ToastUtil.show("请选择套餐使用人");
                return ;
            }

            if (selType == 4) {
                name = et_name.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastUtil.show("姓名不能为空");
                    return;
                }
                icard = et_icard.getText().toString();
                if (!UserUtil.checkShowCardNo(icard)) {
                    return;
                }
                phone = et_phone.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.show("手机号不能为空");
                    return;
                }
                if (phone.length() != 11) {
                    ToastUtil.show("手机号必须为11位");
                    return;
                }
            }
        }

        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isAdd) return;
        Subscription subscription = ComboClient.doUseComboList(orderNo, comboEntities, selType, name, icard, phone)
                .subscribe(new Subscriber<BaseResponse>() {
                    @Override
                    public void onStart() {
                        isAdd = true;
                    }

                    @Override
                    public void onCompleted() {
                        isAdd = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        isAdd = false;
                        ToastUtil.show(R.string.req_fail);
                    }

                    @Override
                    public void onNext(BaseResponse comboResponse) {
                        if (comboResponse.isSuccessful()) {
                            EventBus.getDefault().post(new PushEvent(Constant.IntentAction.COMBO));
                            close();
                        } else {
                            ToastUtil.show(comboResponse.msg);
                        }
                    }
                });
        addSubscription(subscription);
    }

    private boolean isAdd;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

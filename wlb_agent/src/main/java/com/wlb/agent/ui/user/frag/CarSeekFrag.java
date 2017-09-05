package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

/**
 * 车辆搜索
 * @author 张全
 */

public class CarSeekFrag extends SimpleFrag {
    private CarListFrag carListFrag;
    private EditText tv_seekContent;

    public  static void start(Context context){
        SimpleFragAct.start(context,new SimpleFragAct.SimpleFragParam("搜索",CarSeekFrag.class));
    }
    @Override
    protected int getLayoutId() {
        return R.layout.car_seek_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        carListFrag = replaceFragment(R.id.carlist, CarListFrag.class, null);
        tv_seekContent = findViewById(R.id.seek_content);
        tv_seekContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = StringUtil.trim(tv_seekContent);
                    if (TextUtils.isEmpty(keyword)) {
                        ToastUtil.show("请输入搜索内容");
                        return false;
                    }
                    carListFrag.loadCarList(keyword.toUpperCase());
                    hideSoftInput();
                }
                return false;
            }
        });

        tv_seekContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSoftInput(tv_seekContent);
            }
        },300);
    }
}

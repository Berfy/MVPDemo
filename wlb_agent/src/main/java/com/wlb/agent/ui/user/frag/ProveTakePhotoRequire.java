package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;

import com.wlb.agent.R;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

/**
 * 实名认证拍照要求
 * <p>
 * Created by 曹泽琛.
 */
public class ProveTakePhotoRequire extends SimpleFrag {

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("身份证拍照要求",
                ProveTakePhotoRequire.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.prove_takephoto_require;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }
}

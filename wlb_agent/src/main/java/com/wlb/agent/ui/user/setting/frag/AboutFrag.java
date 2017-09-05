package com.wlb.agent.ui.user.setting.frag;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.android.util.LContext;
import com.wlb.agent.R;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;

/**
 * Created by JiaHongYa
 * 关于我们
 */
public class AboutFrag extends SimpleFrag {

    @BindView(R.id.tv_about_channel)
    TextView mTvChannel;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam("关于我们", AboutFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.about_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        TextView versions = findViewById(R.id.versions);//版本号
        String presentVersion = LContext.versionName;
        versions.setText("我来保" + "v" + presentVersion);
//        mTvChannel.setText(AppInfoUtil.getApplicationMetaData(getActivity(), "UMENG_CHANNEL"));
    }
}

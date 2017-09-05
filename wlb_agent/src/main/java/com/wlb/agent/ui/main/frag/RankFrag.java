package com.wlb.agent.ui.main.frag;

import android.os.Bundle;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.OnClick;

/**
 * @author 张全
 * 排行榜
 */
public class RankFrag extends SimpleFrag {

    @Override
    protected int getLayoutId() {
        return R.layout.team_rank_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    @OnClick({R.id.rank_member, R.id.rank_premuim, R.id.rank_commission})
    public void onClick(View v) {
        String url = null;
        switch (v.getId()) {
            case R.id.rank_member:
                url = H5.MEMBER_NUMS_RANK;
                break;
            case R.id.rank_premuim:
                url = H5.MEMBER_PREMIUMS_RANK;
                break;
            case R.id.rank_commission:
                url = H5.MEMBER_COMMISSIONS_RANK;
                break;
        }
        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        webViewParam.url = url;
        SimpleFragAct.SimpleFragParam simpleFragParam = WebViewFrag.getStartParam(webViewParam);
        simpleFragParam .coverTilteBar(true);
        WebViewFrag.start(mContext, simpleFragParam);
    }
}

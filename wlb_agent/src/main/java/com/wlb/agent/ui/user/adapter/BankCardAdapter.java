package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.BankCardInfo;
import com.wlb.common.imgloader.ImageFactory;

import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by JiaHongYa
 */

public class BankCardAdapter extends ListAdapter<BankCardInfo> {
    private DisplayImageOptions imageOptions;
    private int DEF_ICON = R.drawable.bank_itom;

    public BankCardAdapter(Context context, List<BankCardInfo> list, int layoutId) {
        super(context, list, layoutId);
        imageOptions = ImageFactory.getImageOptions(DEF_ICON);
    }

    @Override
    public void setViewData(ViewHolder holder, BankCardInfo data) {
        holder.setText(R.id.bank_card, data.bank_name);//银行卡名称

        String firstCode = data.bank_no.substring(0, data.bank_no.length() - 4);//银行卡头几位
        String lastCode = data.bank_no.substring(data.bank_no.length() - 4);//银行卡后四位

//        holder.setText(R.id.bank_card_message, data.bank_no.replace(firstCode, "**** **** **** ") + lastCode);
        holder.setText(R.id.bank_card_message, data.bank_no);

        if (data.isDefault()) { //是否是默认银行卡
            holder.setBackgroundResource(R.id.select_card, R.drawable.ic_underwrite_checked);
        } else {
            holder.setBackgroundResource(R.id.select_card, R.drawable.ic_underwrite_check);
        }

        //银行logo
        ImageView img_bankLogo = holder.getView(R.id.bank_card_icon);
        if (!TextUtils.isEmpty(data.bank_logo)) {
            ImageFactory.getUniversalImpl().getImg(data.bank_logo, img_bankLogo, null, imageOptions);
        } else {
            img_bankLogo.setImageResource(DEF_ICON);
        }
    }
}

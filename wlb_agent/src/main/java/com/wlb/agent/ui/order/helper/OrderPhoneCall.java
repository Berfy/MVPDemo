package com.wlb.agent.ui.order.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.wlb.agent.R;

import static com.android.util.LContext.getString;

/**
 * 张全
 */

public class OrderPhoneCall {
    public static void call(Context context,String servicePhone){
        String phone = servicePhone;
        if(TextUtils.isEmpty(phone))
            phone = getString(R.string.about_phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }
}

package com.wlb.agent.ui.order.helper;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.R;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.common.imgloader.ImageFactory;

import java.text.DecimalFormat;

/**
 * @author 张全
 */
public final class InsuranceUtil {

    public synchronized static String buildYuan(double price) {
        String temp = String.valueOf(price);
        // 容错处理
        if ("NaN".equals(temp)) {
            return "0.0元";
        }
        if (price == 0) {
            return "0.0元";
        }

//		return String.valueOf(price)+"元";
        try {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(price) + "元";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price + "元";
    }

    /**
     * 格式为："￥888"或"￥888.08"或"￥888.8"
     *
     * @return
     */
    public synchronized static String buildPrice(double price) {
        String temp = String.valueOf(price);
        // 容错处理
        if ("NaN".equals(temp)) {
            return "￥0";
        }
        if (price == 0) {
            return "￥0";
        }

//        return "￥" + String.valueOf(price);

//		// 保留小数点后两位
        try {

//			BigDecimal bd = new BigDecimal(price);
//			double f1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//			String str = "￥" + String.valueOf(f1);
//			if (str.substring(str.length() - 1).equals("0")) {
//				str = str.substring(0, str.length() - 2);
//			}
//			return str;

            DecimalFormat df = new DecimalFormat("#.00");
            return "￥" + df.format(price);
        } catch (Exception e) {
            e.printStackTrace();
            return "￥" + price;
        }
    }

    /**
     * 格式为："888.00"或"888.08"或"888.80"
     *
     * @return
     */
    public synchronized static String buildPricePoint(double price) {
        String temp = String.valueOf(price);
        // 容错处理
        if ("NaN".equals(temp)) {
            return "0";
        }
        if (price == 0) {
            return "0";
        }
        return String.valueOf(price);

//		// 保留小数点后两位
//
//		DecimalFormat df = new DecimalFormat("#.00");
//		return df.format(price);
    }

    public static void setImageOption(String companyCode, ImageView companyIconView) {
        if (companyCode.equals("tpy")) {
            companyIconView.setBackgroundResource(R.drawable.ic_taipingyang);
        } else if (companyCode.equals("rb")) {
            companyIconView.setBackgroundResource(R.drawable.ic_renbao);
        } else if (companyCode.equals("pa")) {
            companyIconView.setBackgroundResource(R.drawable.ic_pingan);
        } else {
            DisplayImageOptions imageOptions = ImageFactory.getImageOptions(R.drawable.rb);
            String companyIcon = ApiHost.getCompanyIconUrl(companyCode);
            ImageFactory.getUniversalImpl().getImg(companyIcon, companyIconView, null, imageOptions);
        }
    }

}

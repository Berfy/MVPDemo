package com.wlb.agent.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.adapter.CommonPopListAdapter;
import com.wlb.agent.ui.common.date.OnWheelChangedListener;
import com.wlb.agent.ui.common.date.WheelView;
import com.wlb.agent.ui.common.date.adapter.SelectYearWheelAdapter;
import com.wlb.agent.ui.common.view.CountButton;
import com.wlb.agent.ui.common.view.Pwd6KeyBorad;
import com.wlb.agent.ui.main.adapter.CarCityGridListAdapter;
import com.wlb.agent.ui.main.adapter.CarNumGridListAdapter;
import com.wlb.agent.ui.order.helper.OrderPhoneCall;
import com.wlb.agent.ui.user.util.UserUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.android.util.device.DeviceUtil.getScreenWidthPx;
import static com.wlb.agent.R.id.btn_cancel;
import static com.wlb.agent.R.id.btn_ok;
import static com.wlb.agent.R.id.tv_title;

public class PopupWindowUtil {

    private final String TAG = "PopupWindowUtil";
    private Context mContext;
    private PopupWindow mPopupWindow;
    private boolean mIsAnimming;//是否正在动画
    private TranslateAnimation mAnim_open = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
    private TranslateAnimation mAnim_close = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);

    public PopupWindowUtil(Context context) {
        mContext = context;
        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());// 这样设置才能点击屏幕外dismiss窗口
        mAnim_open.setDuration(300);
        mAnim_open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimming = true;
                LogUtil.e(TAG, "mAnim_open  onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimming = false;
                LogUtil.e(TAG, "mAnim_open  onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mAnim_close.setDuration(300);
        mAnim_close.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsAnimming = true;
                LogUtil.e(TAG, "mAnim_close  onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mIsAnimming = false;
                LogUtil.e(TAG, "mAnim_close  onAnimationEnd");
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private boolean isCanShow() {
        try {
            if (!((Activity) mContext).isFinishing() && null != mPopupWindow && !mPopupWindow.isShowing()) {
                LogUtil.e(TAG, "可以显示");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.e(TAG, "不可以显示");
        return false;
    }

    public void showConfirmPop(String title, String content, String okText, String cancelText, OnPopConfirmctListener okOnClickListener) {
        View popView = View.inflate(mContext, R.layout.pop_confirm, null);
        mPopupWindow.setContentView(popView);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) popView.findViewById(tv_title)).setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            ((TextView) popView.findViewById(R.id.tv_content)).setText(content);
        }
        if (!TextUtils.isEmpty(okText)) {
            ((Button) popView.findViewById(btn_ok)).setText(okText);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            ((Button) popView.findViewById(btn_cancel)).setText(cancelText);
        }
        if (null != okOnClickListener) {
            popView.findViewById(btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    okOnClickListener.ok();
                    dismiss();
                }
            });
            popView.findViewById(btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    okOnClickListener.cancel();
                    dismiss();
                }
            });
        }
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        show(Gravity.CENTER);
    }

    public void showOrderSendCodeConfirmPop(String mobile,
                                            OnOrderSendCodeListener onOrderSendCodeListener) {
        View popView = View.inflate(mContext, R.layout.pop_order_send_code, null);
        mPopupWindow.setContentView(popView);
        TextView tv_mobile = (TextView) popView.findViewById(R.id.tv_mobile);
        EditText edit_code = (EditText) popView.findViewById(R.id.edit_code);
        tv_mobile.setText(mobile);
        if (null != onOrderSendCodeListener) {
            popView.findViewById(btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String code = edit_code.getText().toString().trim();
                    if (TextUtils.isEmpty(code)) {
                        ToastUtil.show(R.string.login_tip_code_null);
                        return;
                    }
                    onOrderSendCodeListener.submit(code);
                    dismiss();
                }
            });
            popView.findViewById(btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            popView.findViewById(R.id.tv_change_mobile).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOrderSendCodeListener.changeMobile();
                    dismiss();
                }
            });
        }
        show(Gravity.CENTER);
    }

    public void showChangeMobileConfirmPop(OnOrderChangeMobileListener onOrderChangeMobileListener) {
        View popView = View.inflate(mContext, R.layout.pop_order_send_code_change_mobile, null);
        mPopupWindow.setContentView(popView);
        EditText edit_mobile = (EditText) popView.findViewById(R.id.edit_mobile);
        EditText edit_code = (EditText) popView.findViewById(R.id.edit_code);
        CountButton countButton = (CountButton) popView.findViewById(R.id.btn_get_code);
        if (null != onOrderChangeMobileListener) {
            popView.findViewById(btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mobile = edit_mobile.getText().toString().trim();
                    String code = edit_code.getText().toString().trim();
                    if (!UserUtil.checkPhoneNum(mobile)) {
                        return;
                    }
                    if (TextUtils.isEmpty(code)) {
                        ToastUtil.show(R.string.login_tip_code_null);
                        return;
                    }
                    onOrderChangeMobileListener.submit(mobile, code);
                    dismiss();
                }
            });
            popView.findViewById(R.id.btn_get_code).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mobile = edit_mobile.getText().toString().trim();
                    if (!UserUtil.checkPhoneNum(mobile)) {
                        return;
                    }
                    onOrderChangeMobileListener.sendCode(countButton, mobile);
                }
            });
            popView.findViewById(R.id.tv_hotline).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOrderChangeMobileListener.callHotline();
                }
            });
            popView.findViewById(btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
        show(Gravity.CENTER);
    }

    public void showHotLine() {
        View popView = View.inflate(mContext, R.layout.pop_confirm, null);
        mPopupWindow.setContentView(popView);
        ((TextView) popView.findViewById(R.id.tv_content)).setText(mContext.getString(R.string.hotline_tip));
        popView.findViewById(btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderPhoneCall.call(mContext, mContext.getString(R.string.about_phone));
                dismiss();
            }
        });
        popView.findViewById(btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        show(Gravity.CENTER);
    }

    /**
     * 首页车牌号-城市键盘
     */
    public void showCarCity(OnCarCitySelectListener onCarCitySelectListener) {
        View popView = View.inflate(mContext, R.layout.pop_car_city_keyboard, null);
        mAnim_open.setDuration(100);
        mAnim_close.setDuration(100);
        mPopupWindow.setContentView(popView);
        CarCityGridListAdapter carCityGridListAdapter = new CarCityGridListAdapter(mContext);
        GridView gridView = (GridView) popView.findViewById(R.id.gridView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) gridView.getLayoutParams();
        layoutParams.width = getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 20);
        gridView.setLayoutParams(layoutParams);
        gridView.setNumColumns(8);
        gridView.setAdapter(carCityGridListAdapter);
        carCityGridListAdapter.refresh(DataParseUtil.getInstance(mContext).getCarCitys());
        RelativeLayout layout_anim = (RelativeLayout) popView.findViewById(R.id.layout_anim);
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });

        popView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != onCarCitySelectListener) {
                    onCarCitySelectListener.select(carCityGridListAdapter.getList().get(i).getCarNum());
                }
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });
        if (!mIsAnimming) {
            layout_anim.startAnimation(mAnim_open);
            show(Gravity.BOTTOM);
            onCarCitySelectListener.opened();
        }
    }

    /**
     * 首页车牌号键盘
     */
    public void showCarNumber(OnCarNumberSelectListener onCarNumberSelectListener) {
        View popView = View.inflate(mContext, R.layout.pop_car_number_keyboard, null);
        mAnim_open.setDuration(200);
        mAnim_close.setDuration(200);
        mPopupWindow.setContentView(popView);
        CarNumGridListAdapter carNumGridListAdapter = new CarNumGridListAdapter(mContext, DeviceUtil.dip2px(mContext, 20),
                DeviceUtil.dip2px(mContext, 5), 8, 0);
        GridView gridView = (GridView) popView.findViewById(R.id.gridView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) gridView.getLayoutParams();
        layoutParams.width = getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 20);
        gridView.setLayoutParams(layoutParams);
        gridView.setNumColumns(8);
        gridView.setAdapter(carNumGridListAdapter);
        carNumGridListAdapter.refresh(DataParseUtil.getInstance(mContext).getCarNumber());
        LinearLayout layout_anim = (LinearLayout) popView.findViewById(R.id.layout_anim);
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data = carNumGridListAdapter.getList().get(i);
                if (data.equals("delete")) {
                    onCarNumberSelectListener.delete();
                } else if (data.equals("0")) {

                } else if (data.equals("确定")) {
                    if (!mIsAnimming)
                        layout_anim.startAnimation(mAnim_close);
                } else {
                    onCarNumberSelectListener.input(carNumGridListAdapter.getList().get(i).trim().toUpperCase());
                }
            }
        });
        if (!mIsAnimming) {
            layout_anim.startAnimation(mAnim_open);
            show(Gravity.BOTTOM);
            onCarNumberSelectListener.opened();
        }
    }

    /**
     * 6位支付密码键盘
     */
    public void showPwdKeyBoard(OnPwd6SelectListener onPwd6SelectListener) {
        View popView = View.inflate(mContext, R.layout.pop_car_number_keyboard, null);
        mAnim_open.setDuration(200);
        mAnim_close.setDuration(200);
        mPopupWindow.setContentView(popView);
        CarNumGridListAdapter carNumGridListAdapter = new CarNumGridListAdapter(mContext, DeviceUtil.dip2px(mContext, 20),
                DeviceUtil.dip2px(mContext, 4), 3, DeviceUtil.dip2px(mContext, 80));
        GridView gridView = (GridView) popView.findViewById(R.id.gridView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) gridView.getLayoutParams();
        layoutParams.width = getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 20);
        gridView.setLayoutParams(layoutParams);
        gridView.setNumColumns(3);
        gridView.setAdapter(carNumGridListAdapter);
        carNumGridListAdapter.refresh(DataParseUtil.getInstance(mContext).getPwdKeyBoradDatas());
        LinearLayout layout_anim = (LinearLayout) popView.findViewById(R.id.layout_anim);
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });
        if (null != onPwd6SelectListener)
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String data = carNumGridListAdapter.getList().get(i);
                    if (data.equals("delete")) {
                        onPwd6SelectListener.delete();
                    } else if (data.equals(" ")) {

                    } else if (data.equals("确定")) {
                        if (!mIsAnimming)
                            layout_anim.startAnimation(mAnim_close);
                    } else {
                        onPwd6SelectListener.input(carNumGridListAdapter.getList().get(i).trim().toUpperCase());
                    }
                }
            });
        if (!mIsAnimming) {
            layout_anim.startAnimation(mAnim_open);
            show(Gravity.BOTTOM);
        }
    }

    /**
     * 确认车辆信息-填写指南
     */
    public void showConfirmCarInfoHelp() {
        View popView = View.inflate(mContext, R.layout.pop_confirm_car_info_help, null);
        mPopupWindow.setContentView(popView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) popView.findViewById(R.id.iv_icon).getLayoutParams();
        layoutParams.width = DeviceUtil.getScreenWidthPx(mContext)
                - DeviceUtil.dip2px(mContext, 40);
        layoutParams.height = (int) ((DeviceUtil.getScreenWidthPx(mContext)
                - DeviceUtil.dip2px(mContext, 40)) / 1.8);
        popView.findViewById(R.id.v_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        show(Gravity.CENTER);
    }

    private String[] years = new String[200];
    private String[] months = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月",
            "9月", "10月", "11月", "12月"};
    private int mIndexYear = 0;
    private static int mIndexMonth = 0;
    private static int dayCount;
    private String[] days = null;
    private int mStartYear = 1900;


    /**
     * 初始化时分秒数据
     */
    private void initTime() {
        for (int i = 0; i < 200; i++) {
            years[i] = i + mStartYear + "年";
        }
    }

    /**
     * 天数格式初始化
     */
    private void initDays() {
        for (int i = 0; i < days.length; i++) {
            if (i < 9) {
                days[i] = "0" + String.valueOf(i + 1) + "日";
            } else {
                days[i] = String.valueOf(i + 1) + "日";
            }
        }
    }

    public void showDateChoose(int year, int month, int day, OnSelectDateListener onSelectDateListener) {
        mIndexYear = year;
        mIndexMonth = month;
        dayCount = StringUtil.month(Integer.valueOf(mIndexYear),
                Integer.valueOf(mIndexMonth));
        initTime();
        days = new String[dayCount];
        initDays();
        View ageView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_date_wheelview, null);
        mPopupWindow.setContentView(ageView);
        /** 年 */
        SelectYearWheelAdapter adapter = new SelectYearWheelAdapter(mContext,
                years);
        /** 月 */
        SelectYearWheelAdapter adapter1 = new SelectYearWheelAdapter(mContext,
                months);
        /** 日 */
        SelectYearWheelAdapter adapter2 = new SelectYearWheelAdapter(mContext,
                days);
        final WheelView wv_age = (WheelView) ageView
                .findViewById(R.id.wheelview);
        final WheelView wv_age1 = (WheelView) ageView
                .findViewById(R.id.wheelview1);
        final WheelView wv_age2 = (WheelView) ageView
                .findViewById(R.id.wheelview2);
        wv_age.setVisibility(View.VISIBLE);
        wv_age1.setVisibility(View.VISIBLE);
        wv_age2.setVisibility(View.VISIBLE);
        wv_age.setWheelBackground(R.color.white);
        wv_age.setWheelForeground(R.drawable.ic_date_select);
        wv_age1.setWheelBackground(R.color.white);
        wv_age1.setWheelForeground(R.drawable.ic_date_select);
        wv_age2.setWheelBackground(R.color.white);
        wv_age2.setWheelForeground(R.drawable.ic_date_select);
        LinearLayout llMain = (LinearLayout) ageView
                .findViewById(R.id.wv_ll_main);
        Button bt_back = (Button) ageView.findViewById(R.id.wv_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMain.startAnimation(mAnim_close);
            }
        });
        ageView.findViewById(R.id.wheelview_dialog_li).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llMain.startAnimation(mAnim_close);
            }
        });
        Button bt_sure = (Button) ageView.findViewById(R.id.wv_sure);
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String year = (wv_age.getCurrentItem() + mStartYear) + "";
                String month = wv_age1.getCurrentItem() + 1 < 10 ? "0"
                        + (wv_age1.getCurrentItem() + 1) : (wv_age1
                        .getCurrentItem() + 1) + "";
                String day = wv_age2.getCurrentItem() + 1 < 10 ? "0"
                        + (wv_age2.getCurrentItem() + 1) : (wv_age2
                        .getCurrentItem() + 1) + "";
                String changeString = year + "-" + month + "-" + day;
                // TODO判断hour的值，然后对测量时段进行正确设置
                // setTvType(hour);
                Log.d("MainActivity", changeString);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    if (format.parse(changeString).getTime() > System
                            .currentTimeMillis()) {
                        ToastUtil.show(
                                "您选择的日期大于当前日期，请正确选择！");
                    } else {
                        llMain.startAnimation(mAnim_close);
                        if (null != onSelectDateListener) {
                            onSelectDateListener.select(year, month, day);
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        wv_age.setViewAdapter(adapter);
        wv_age1.setViewAdapter(adapter1);
        wv_age2.setViewAdapter(adapter2);
        wv_age.setCyclic(true);
        wv_age1.setCyclic(true);
        wv_age2.setCyclic(true);
        wv_age.setVisibleItems(5);
        wv_age1.setVisibleItems(5);
        wv_age2.setVisibleItems(5);
        wv_age.setCurrentItem(year - mStartYear);
        wv_age1.setCurrentItem(month - 1);
        wv_age2.setCurrentItem(day - 1);
        wv_age.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub

                dayCount = StringUtil.month(
                        Integer.valueOf(newValue + mStartYear),
                        Integer.valueOf(mIndexMonth));
                days = new String[dayCount];
                initDays();
                for (int i = 0; i < dayCount; i++) {
                    days[i] = i + 1 + "日";
                }
                int data_old_item = wv_age2.getCurrentItem();
                SelectYearWheelAdapter adapter2 = new SelectYearWheelAdapter(
                        mContext, days);
                wv_age2.setViewAdapter(adapter2);
                wv_age2.setCyclic(true);
                if (data_old_item > (dayCount - 1)) {
                    wv_age2.setCurrentItem(dayCount - 1);
                } else {
                    wv_age2.setCurrentItem(data_old_item);
                }
                wv_age2.postInvalidate();
                wv_age2.refreshDrawableState();
            }
        });
        wv_age1.addChangingListener(new OnWheelChangedListener() {// 月份改变，每月天数跟着改变

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                mIndexMonth = newValue + 1;
                dayCount = StringUtil.month(Integer.valueOf(mIndexYear),
                        Integer.valueOf(newValue + 1));
                days = new String[dayCount];
                initDays();
                for (int i = 0; i < dayCount; i++) {
                    days[i] = i + 1 + "日";
                }
                int data_old_item = wv_age2.getCurrentItem();
                SelectYearWheelAdapter adapter2 = new SelectYearWheelAdapter(
                        mContext, days);
                wv_age2.setViewAdapter(adapter2);
                wv_age2.setCyclic(true);
                if (data_old_item > (dayCount - 1)) {
                    wv_age2.setCurrentItem(dayCount - 1);
                } else {
                    wv_age2.setCurrentItem(data_old_item);
                }
                wv_age2.postInvalidate();
                wv_age2.refreshDrawableState();
            }
        });
        wv_age2.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                Log.e("", dayCount + "");
                days = new String[dayCount];
                initDays();
                for (int i = 0; i < dayCount; i++) {
                    days[i] = i + 1 + "日";
                }
                SelectYearWheelAdapter adapter2 = new SelectYearWheelAdapter(
                        mContext, days);
                wheel.setViewAdapter(adapter2);
                wheel.setCyclic(true);
                // wv_age2.postInvalidate();
            }
        });
        llMain.startAnimation(mAnim_open);
        show(Gravity.BOTTOM);
    }

    /**
     * 选择提现账号
     */
    public void chooseWalletWithDrawal(int payType, String nick, OnChooseWithDrawalListener onChooseWithDrawalListener) {
        View popView = View.inflate(mContext, R.layout.pop_wallet_withdrawal, null);
        mPopupWindow.setContentView(popView);
        TextView tv_nick = (TextView) popView.findViewById(R.id.tv_nick);
        TextView tv_account = (TextView) popView.findViewById(R.id.tv_account);
        LinearLayout layout_withdrawal = (LinearLayout) popView.findViewById(R.id.layout_withdrawal);
        LinearLayout layout_change = (LinearLayout) popView.findViewById(R.id.layout_change);
        LinearLayout layout_anim = (LinearLayout) popView.findViewById(R.id.layout_anim);
        String account = "";
        switch (payType) {
            case 0://微信
                account = mContext.getString(R.string.wallet_accounts_wx);
                break;
            case 1://支付宝
                account = mContext.getString(R.string.wallet_accounts_ali);
                break;
        }
        tv_account.setText(account);
        tv_nick.setText(nick);
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });

        popView.findViewById(R.id.layout_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsAnimming)
                    layout_anim.startAnimation(mAnim_close);
            }
        });

        layout_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onChooseWithDrawalListener) {
                    onChooseWithDrawalListener.withDrawal(payType, nick);
                }
            }
        });

        layout_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onChooseWithDrawalListener) {
                    onChooseWithDrawalListener.change(payType, nick);
                }
            }
        });

        if (!mIsAnimming) {
            layout_anim.startAnimation(mAnim_open);
            show(Gravity.CENTER);
        }
    }

    public void showUserOfferRecordLeft(View view, int selectPosition, OnPopListSelectListener onPopListSelectListener) {
        View popView = View.inflate(mContext, R.layout.pop_user_offer_record_top_title1, null);
        mPopupWindow.setContentView(popView);
        mPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(DeviceUtil.getScreenHeightPx(mContext) - DeviceUtil.dip2px(mContext, 120));
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.translate_black)));
        TextView tv_item1 = (TextView) popView.findViewById(R.id.tv_item1);
        TextView tv_item2 = (TextView) popView.findViewById(R.id.tv_item2);
        View v_item1 = popView.findViewById(R.id.v_item1);
        View v_item2 = popView.findViewById(R.id.v_item2);
        switch (selectPosition) {
            case 0:
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_red));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                v_item1.setVisibility(View.VISIBLE);
                v_item2.setVisibility(View.GONE);
                break;
            case 1:
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_red));
                v_item1.setVisibility(View.GONE);
                v_item2.setVisibility(View.VISIBLE);
                break;
        }
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        popView.findViewById(R.id.layout_item1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_red));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                v_item1.setVisibility(View.VISIBLE);
                v_item2.setVisibility(View.GONE);
                dismiss();
                if (null != onPopListSelectListener) {
                    onPopListSelectListener.select(0);
                }
            }
        });
        popView.findViewById(R.id.layout_item2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_red));
                v_item1.setVisibility(View.GONE);
                v_item2.setVisibility(View.VISIBLE);
                dismiss();
                if (null != onPopListSelectListener) {
                    onPopListSelectListener.select(1);
                }
            }
        });
        if (isCanShow())
            mPopupWindow.showAsDropDown(view);
    }

    public void showUserOfferRecordRight(View view, int selectPosition, OnPopListSelectListener onPopListSelectListener) {
        View popView = View.inflate(mContext, R.layout.pop_user_offer_record_top_title2, null);
        mPopupWindow.setContentView(popView);
        mPopupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mPopupWindow.setHeight(DeviceUtil.getScreenHeightPx(mContext) - DeviceUtil.dip2px(mContext, 120));
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.translate_black)));
        TextView tv_item1 = (TextView) popView.findViewById(R.id.tv_item1);
        TextView tv_item2 = (TextView) popView.findViewById(R.id.tv_item2);
        View v_item1 = popView.findViewById(R.id.v_item1);
        View v_item2 = popView.findViewById(R.id.v_item2);
        switch (selectPosition) {
            case 0:
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_red));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                v_item1.setVisibility(View.VISIBLE);
                v_item2.setVisibility(View.GONE);
                break;
            case 1:
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_red));
                v_item1.setVisibility(View.GONE);
                v_item2.setVisibility(View.VISIBLE);
                break;
        }
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        popView.findViewById(R.id.layout_item1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_red));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                v_item1.setVisibility(View.VISIBLE);
                v_item2.setVisibility(View.GONE);
                if (null != onPopListSelectListener) {
                    onPopListSelectListener.select(0);
                }
            }
        });
        popView.findViewById(R.id.layout_item2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_item1.setTextColor(mContext.getResources().getColor(R.color.common_text1));
                tv_item2.setTextColor(mContext.getResources().getColor(R.color.common_red));
                v_item1.setVisibility(View.GONE);
                v_item2.setVisibility(View.VISIBLE);
                if (null != onPopListSelectListener) {
                    onPopListSelectListener.select(1);
                }
            }
        });
        mPopupWindow.showAsDropDown(view);
    }

    /**
     * 输入提现密码
     * 提现
     *
     * @param onInputPwdListener 监听
     */
    public void showPwdInput(OnInputPwdListener onInputPwdListener) {
        View popView = View.inflate(mContext, R.layout.pop_wallet_withdrawal_pwd, null);
        mPopupWindow.setContentView(popView);
        List<TextView> mTvPwds = new ArrayList<>();
        LinearLayout layout_pwd = (LinearLayout) popView.findViewById(R.id.layout_pwd);
        Pwd6KeyBorad pwd6KeyBorad = (Pwd6KeyBorad) popView.findViewById(R.id.pwd6KeyBoard);
        TextView tv_modify_pwd = (TextView) popView.findViewById(R.id.tv_modify_pwd);
//        pwd6KeyBorad.setKeyBoard(DeviceUtil.getScreenWidthPx(mContext), DeviceUtil.getScreenWidthPx(mContext) * 2 / 3);
        int width = DeviceUtil.getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 80);
        int height = (width - 5 * DeviceUtil.dip2px(mContext, 4)) / 6;
        layout_pwd.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        mTvPwds.add((TextView) popView.findViewById(R.id.tv_pwd1));
        mTvPwds.add((TextView) popView.findViewById(R.id.tv_pwd2));
        mTvPwds.add((TextView) popView.findViewById(R.id.tv_pwd3));
        mTvPwds.add((TextView) popView.findViewById(R.id.tv_pwd4));
        mTvPwds.add((TextView) popView.findViewById(R.id.tv_pwd5));
        mTvPwds.add((TextView) popView.findViewById(R.id.tv_pwd6));
        pwd6KeyBorad.show();
        for (TextView view : mTvPwds) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pwd6KeyBorad.show();
                }
            });
        }
        pwd6KeyBorad.setListener(new Pwd6KeyBorad.OnPwd6SelectListener() {

            @Override
            public void pwd(String pwd) {
            }

            @Override
            public void input(List<String> pwds) {
                showPwd(mTvPwds, pwds);
            }

            @Override
            public void delete(List<String> pwds, String pwd) {
                showPwd(mTvPwds, pwds);
            }
        });
        List<View> btnCancels = new ArrayList<>();
        btnCancels.add(popView.findViewById(btn_cancel));
        btnCancels.add(popView.findViewById(R.id.btn_delete));
        if (null != onInputPwdListener) {
            for (View view : btnCancels) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
            }
            popView.findViewById(btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onInputPwdListener.ok(pwd6KeyBorad.getPwd());
                    dismiss();
                }
            });
            tv_modify_pwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onInputPwdListener.modifyPwd();
                    dismiss();
                }
            });
        }
        show(Gravity.CENTER);
    }

    private void showPwd(List<TextView> tvPwds, List<String> pwds) {
        int size = pwds.size();
        for (int i = 0; i < 6; i++) {
            tvPwds.get(i).setText(size > i ? pwds.get(i) : "");
        }
    }

    /**
     * 通用列表选择弹出框
     *
     * @param title 标题
     * @param items 列表项
     */
    public void showPopListView(String title, List<String> items, OnItemClickListener onItemClickListener) {
        View popView = View.inflate(mContext, R.layout.pop_listview, null);
        ListView listView = (ListView) popView.findViewById(R.id.listView);
        TextView tv_title = (TextView) popView.findViewById(R.id.tv_title);
        Button btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        } else {
            tv_title.setVisibility(View.GONE);
        }
        CommonPopListAdapter adapter = new CommonPopListAdapter(mContext);
        adapter.setList(items);
        listView.setAdapter(adapter);
        mPopupWindow.setContentView(popView);
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popView.findViewById(R.id.layout_anim).startAnimation(mAnim_close);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(i, adapter.getItem(i));
                }
                popView.findViewById(R.id.layout_anim).startAnimation(mAnim_close);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popView.findViewById(R.id.layout_anim).startAnimation(mAnim_close);
            }
        });
        popView.findViewById(R.id.layout_anim).startAnimation(mAnim_open);
        show(Gravity.CENTER);
    }

    /**
     * 设置订单备注
     */
    public void showOrderRemark(OnOrderRemarkInputListener onOrderRemarkInputListener) {
        View popView = View.inflate(mContext, R.layout.pop_order_remark, null);
        EditText editText = (EditText) popView.findViewById(R.id.edit_remark);
        Button btn_cancel = (Button) popView.findViewById(R.id.btn_cancel);
        Button btn_ok = (Button) popView.findViewById(R.id.btn_ok);
        mPopupWindow.setContentView(popView);
        popView.findViewById(R.id.layout_touch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popView.findViewById(R.id.layout_anim).startAnimation(mAnim_close);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (null != onOrderRemarkInputListener) {
                    String remark = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(remark)) {
                        ToastUtil.show(R.string.order_detail_remark_null_tip);
                    } else {
                        onOrderRemarkInputListener.ok(remark);
                    }
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });
        show(Gravity.CENTER);
    }

    public void show(int gravity) {
        try {
            if (isCanShow()) {
                LogUtil.e(TAG, "显示");
                mPopupWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView(), gravity, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        try {
            if (!((Activity) mContext).isFinishing() && null != mPopupWindow && mPopupWindow.isShowing()) {
                LogUtil.e(TAG, "隐藏");
                mPopupWindow.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnPopConfirmctListener {
        void ok();

        void cancel();
    }

    public interface OnCarCitySelectListener {
        void select(String text);

        void opened();
    }

    public interface OnCarNumberSelectListener {
        void input(String number);

        void delete();

        void opened();
    }

    public interface OnPwd6SelectListener {
        void input(String pwd);

        void delete();
    }

    public interface OnCitySelectListener {
        void search(String keywords);

        void select(String city);
    }

    public interface OnSelectDateListener {
        void select(String year, String month, String day);
    }

    public interface OnOrderSendCodeListener {
        void submit(String code);

        void changeMobile();
    }

    public interface OnOrderChangeMobileListener {
        void sendCode(CountButton countButton, String mobile);

        void submit(String mobile, String code);

        void callHotline();
    }

    public interface OnChooseWithDrawalListener {
        void withDrawal(int payType, String name);

        void change(int payType, String curName);
    }

    public interface OnPopListSelectListener {
        void select(int position);
    }

    public interface OnInputPwdListener {
        void ok(String pwd);

        void modifyPwd();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, String item);
    }

    public interface OnOrderRemarkInputListener {
        void ok(String remark);
    }
}

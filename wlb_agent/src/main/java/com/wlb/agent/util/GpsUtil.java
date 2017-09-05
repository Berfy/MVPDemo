package com.wlb.agent.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.util.log.LogUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Arrays;

public class GpsUtil {

    private Context mContext;
    // 百度的一些api 获取经纬度
    private LocationClientOption option = new LocationClientOption();
    private MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;
    private OnGpsListener mOnGpsListener;
    private String[] mCitys = new String[]{"北京", "上海", "天津", "重庆"};
    private boolean mIsGetData = false;
    private long mTimeOut = 10000;
    private final String TAG = "GpsUtil";

    public GpsUtil(Context context) {
        mContext = context;
        Arrays.sort(mCitys);
    }

    public void setListener(OnGpsListener onGpsListener) {
        mOnGpsListener = onGpsListener;
    }

    /**
     * 定位
     */
    public void startGps() {
        LogUtil.e(TAG, "定位开始");
        mIsGetData = false;
        if (null == mLocClient) {
            mLocClient = new LocationClient(mContext);
            option.setIsNeedAddress(true);
            option.setCoorType("gcj02");
            mLocClient.setLocOption(option);
            mLocClient.registerLocationListener(myListener);
        }
        mLocClient.start();
        mHandler.sendEmptyMessageDelayed(0, mTimeOut);
    }

    public void stopGps() {
        if (null != mLocClient) {
            LogUtil.e(TAG, "定位停止");
            mLocClient.stop();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    stopGps();
                    if (!mIsGetData) {
                        if (null != mOnGpsListener) {
                            mOnGpsListener.onError();
                        }
                    }
                    break;
                case 1:
                    mIsGetData = true;
                    if (null != mOnGpsListener) {
                        BDLocation location = (BDLocation) msg.obj;
                        String province = location.getProvince();
                        String city = location.getCity();
                        LogUtil.e(TAG, "定位地址" + province + "__" + city);
                        mOnGpsListener.onReceiveLocation(location.getLatitude(),
                                location.getLongitude(), TextUtils
                                        .isEmpty(province) ? "" : province,
                                TextUtils.isEmpty(city) ? "" : city, TextUtils
                                        .isEmpty(location.getDistrict()) ? ""
                                        : location.getDistrict(), TextUtils
                                        .isEmpty(location.getAddrStr()) ? ""
                                        : location.getAddrStr());
                    }
                    stopGps();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 监听获取经纬度
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null == location) {
                mHandler.sendEmptyMessage(0);
                return;
            } else {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                LogUtil.e(TAG, "定位" + longitude + "__" + latitude);
                if (String.valueOf(longitude).contains("E")
                        || String.valueOf(latitude).contains("E")) {
                    mHandler.sendEmptyMessage(0);
                    return;
                }
                if (null != mOnGpsListener) {
                    String province = location.getProvince();
                    String city = location.getCity();
                    if (!TextUtils.isEmpty(province)) {// 如果是直辖市或者省市一样，删除省
                        if (Arrays.binarySearch(mCitys, province) >= 0
                                || province.equals(location.getCity())) {
                            province = "";
                            city = city.replace("市", "");
                        }
                    } else {// 获取城市失败
                        mHandler.sendEmptyMessage(0);
                        return;
                    }
                    Message message = new Message();
                    message.what = 1;
                    message.obj = location;
                    mHandler.sendMessage(message);
                    mHandler.removeMessages(0);
                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    public interface OnGpsListener {
        void onReceiveLocation(double lat, double lng, String province,
                               String city, String distrct, String address);

        void onError();
    }
}

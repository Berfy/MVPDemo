package com.wlb.agent.util;

import android.content.Context;

import com.android.util.log.LogUtil;
import com.wlb.agent.core.data.common.CarCityBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析省市
 *
 * @author Administrator
 */
public class DataParseUtil {

    private static DataParseUtil mCityUtil;
    private Context mContext;

    public static DataParseUtil getInstance(Context context) {
        if (null == mCityUtil) {
            mCityUtil = new DataParseUtil(context);
        }
        return mCityUtil;
    }

    private DataParseUtil(Context context) {
        mContext = context;
    }

    public List<String> getCitys() {
        List<String> provinceList = new ArrayList<>();
        // TODO Auto-generated method stub
        try {
            InputStream inputStream;
            inputStream = mContext.getAssets().open("address");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputReader.close();
            inputStream.close();
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            provinceList = parseJsonToBean(jsonArray);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return provinceList;
    }

    public List<CarCityBean> getCarCitys() {
        List<CarCityBean> carCitys = new ArrayList<>();
        // TODO Auto-generated method stub
        try {
            InputStream inputStream;
            inputStream = mContext.getAssets().open("car_short_num");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputReader.close();
            inputStream.close();
            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            carCitys = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CarCityBean carCityBean = new CarCityBean();
                carCityBean.setCarNum(jsonObject.optString("carNum"));
                carCityBean.setProvince(jsonObject.optString("province"));
                carCitys.add(carCityBean);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return carCitys;
    }

    private ArrayList<String> parseJsonToBean(JSONArray json) {
        ArrayList<String> pList = new ArrayList<String>();
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                JSONObject provinceJsonObject = json
                        .optJSONObject(i);
                if (provinceJsonObject != null) {
                    String provinceName = provinceJsonObject.optString("name");
                    LogUtil.e("筛选前省", provinceName);
                    if (provinceName.lastIndexOf("区") == -1 && provinceName.lastIndexOf("县") == -1
                            && provinceName.lastIndexOf("自治州") == -1
                            && provinceName.lastIndexOf("其他") == -1
                            && provinceName.lastIndexOf("省") == -1) {//去掉自治区 自治州 区 县 只要市
                        if (provinceName.lastIndexOf("市") == -1) {
                            provinceName += "市";
                        }
                        pList.add(provinceName);
                        LogUtil.e("省", provinceName);
                    }
                    JSONArray cityJsonArray = provinceJsonObject
                            .optJSONArray("sub");
                    if (cityJsonArray != null) {
                        for (int j = 0; j < cityJsonArray.length(); j++) {
                            JSONObject cityJsonObject = cityJsonArray
                                    .optJSONObject(j);
                            if (cityJsonObject != null) {
                                String cityName = cityJsonObject
                                        .optString("name");
                                LogUtil.e("筛选前城市", cityName);
                                if (cityName.lastIndexOf("区") == -1 && cityName.lastIndexOf("县") == -1
                                        && cityName.lastIndexOf("自治州") == -1
                                        && cityName.lastIndexOf("其他") == -1
                                        && cityName.lastIndexOf("省") == -1) {//去掉自治区 自治州 区 县 只要市
                                    if (cityName.lastIndexOf("市") == -1) {
                                        cityName += "市";
                                    }
                                    LogUtil.e("城市", cityName);
                                    pList.add(cityName);
                                }
                            }
                        }
                    }
                }
            }
        }
        return pList;
    }

    public List<String> getCarNumber() {
        List<String> carNums = new ArrayList<>();
        try {
            InputStream inputStream;
            inputStream = mContext.getAssets().open("car_number");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputReader.close();
            inputStream.close();
            for (String number : stringBuilder.toString().split(",")) {
                carNums.add(number);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return carNums;
    }

    public List<String> getPwdKeyBoradDatas() {
        List<String> carNums = new ArrayList<>();
        try {
            InputStream inputStream;
            inputStream = mContext.getAssets().open("pwd_keyboard");
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            inputReader.close();
            inputStream.close();
            for (String number : stringBuilder.toString().split(",")) {
                carNums.add(number);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return carNums;
    }
}

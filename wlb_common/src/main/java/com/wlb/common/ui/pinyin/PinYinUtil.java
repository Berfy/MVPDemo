package com.wlb.common.ui.pinyin;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Berfy on 2017/7/26.
 */

public class PinYinUtil {

    private Context mContext;
    private static PinYinUtil mPinYinUtil;
    /**
     * 汉字转换成拼音的类
     */
    CharacterParser mCharacterParser;// 实例化汉字转拼音类;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    PinyinComparator mPinyinComparator = new PinyinComparator();

    public static PinYinUtil getInstance(Context context) {
        if (null == mPinYinUtil) {
            mPinYinUtil = new PinYinUtil(context);
        }
        return mPinYinUtil;
    }

    private PinYinUtil(Context context) {
        mContext = context;
        mCharacterParser = CharacterParser.getInstance();// 实例化汉字转拼音类;
    }

    /**
     * 按照拼音排序
     *
     * @param rawData 原始数据 必须是字符数组
     */
    public List<SortModel> getPinYinData(List<String> rawData) {
        return filledData(rawData);
    }

    public String getSortLetter(String text) {
        String pinyin = mCharacterParser.getSelling(text);
        String sortString = pinyin.substring(0, 1).toUpperCase();

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            return sortString;
        } else {
            return "#";
        }
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(List<String> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i));
            // 汉字转换成拼音
            String pinyin = mCharacterParser.getSelling(date.get(i));
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        Collections.sort(mSortList, mPinyinComparator);
        return mSortList;
    }
}

package com.wlb.agent.ui.user.util;

import android.text.TextUtils;

import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.text.RegUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.IvehicleModel;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户模块操作工具类
 *
 * @author 张全
 */
public final class UserUtil {
    // 解锁剩余时间
    public static final String CODE_TIME = "CODE_TIME";
    // 是否允许获取验证码
    private static final String USER_ALLOW_GETCODE = "USER_ALLOW_GETCODE";
    //账号：字母开头，其他允许5-15个字符，允许字母数字下划线
    private static final String REG_USERNMAE = "^[a-zA-Z][a-zA-Z0-9_]{5,15}$";

    /**
     * 是否为合法的账号
     *
     * @param userName
     * @return
     */
    public static boolean isValidateUserName(String userName) {
        return RegUtil.match(REG_USERNMAE, userName);
    }

    /**
     * 是否是手机号码
     *
     * @param phoneNum
     * @return
     */
    public static boolean isPhoneNum(final String phoneNum) {
        return RegUtil.isPhoneNum(phoneNum);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return
     */
    public static boolean checkPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            ToastUtil.show("密码不能为空");
            return false;
        }
        if (RegUtil.isPassword(password)) {
            ToastUtil.show("密码只支持6-16位数字或字母组合");
            return false;
        }
        return true;
    }

    public static boolean checkLicenceNo(String licenceNo) {
        if (TextUtils.isEmpty(licenceNo)) {
            ToastUtil.show("车牌号不能为空");
            return false;
        }

        if (licenceNo.length() != 7) {
            ToastUtil.show("车牌号为7位");
            return false;
        }

        Pattern pattern = Pattern
                .compile("^[京|津|沪|渝|川|新|湘|赣|鄂|粤|浙|苏|皖|鲁|辽|闽|吉|贵|豫|甘|云|桂|蒙|青|冀|藏|黑|琼|晋|宁|陕][A-Z][A-Z0-9]{5}");
        Matcher matcher = pattern.matcher(licenceNo.toUpperCase(Locale.getDefault()));
        boolean match = matcher.find();
        if (!match) {
            ToastUtil.show("车牌号输入错误");
        }
        return match;
    }

    public static boolean checkVin(String vinStr) {
        if (TextUtils.isEmpty(vinStr)) {
            ToastUtil.show("车架号不能为空");
            return false;
        }
        boolean matche = vinStr.matches(RegUtil.REG_LETTER_NUM);
        if ((vinStr.length() != 17) || !matche) {
            ToastUtil.show("请输入17位数字字母组成的车架号。");
            return false;
        }
        return true;
    }

    public static boolean checkEnginNo(String enginNo) {
        if (TextUtils.isEmpty(enginNo)) {
            ToastUtil.show("发动机号不能为空");
            return false;
        }
        if (enginNo.length() < 6) {
            ToastUtil.show("发动机号最少6位，且不能同车架号");
            return false;
        }
        return true;
    }

    /**
     * 检测手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNum(String phoneNumber) {

        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtil.show("手机号码不能为空");
            return false;
        }

        boolean isMobile = RegUtil.isPhoneNum(phoneNumber);
        if (!isMobile) {
            ToastUtil.show("请输入正确的手机号码");
            return false;
        }

        return true;
    }

    public static boolean isAllowedToGetCode() {
        return SPUtil.getBoolean(USER_ALLOW_GETCODE, true);
    }

    /**
     * 设置下次解锁时间
     *
     * @param time
     */
    public static void setNextTime(long time) {
        SPUtil.setLong(CODE_TIME, time);
    }

    /**
     * 获取下次解锁时间
     *
     * @return
     */
    public static long getNextTime() {
        return SPUtil.getLong(CODE_TIME, -1);
    }

    public static boolean checkShowName(String name) {
        if (TextUtils.isEmpty(name)) {
            ToastUtil.show("姓名不能为空");
            return false;
        }
        if (name.length() == 1) {
            ToastUtil.show("姓名长度不够");
            return false;
        }

        if (!StringUtil.isChinese(name)) {
            ToastUtil.show("姓名应为中文");
            return false;
        }

        return true;
    }

    public static boolean checkShowCardNo(String cardNo) {
        if (TextUtils.isEmpty(cardNo)) {
            ToastUtil.show("身份证号不能为空");
            return false;
        }
        cardNo = cardNo.toUpperCase();
        if (cardNo.length() != 18) {
            ToastUtil.show("您的身份证位数有误，应为18位");
            return false;
        }
        boolean rightNo = checkCardNo(cardNo);
        if (!rightNo) {
            ToastUtil.show("您输入的身份证号码有误");
            return false;
        }
        return true;
    }

    /**
     * 检测工行卡号
     *
     * @return
     */
//	public static boolean checkIcbcNo(String icbcNo) {
//		if (TextUtils.isEmpty(icbcNo)) {
//			ToastUtil.show("请输入工行卡号");
//			return false;
//		}
//		if (icbcNo.length() != 19) {
//			ToastUtil.show("您的银行卡位数有误，应为19位");
//			return false;
//		}
//		try {
//			Long.valueOf(icbcNo);
//		} catch (Exception e) {
//			ToastUtil.show("工行卡号必须由数字组成");
//			return false;
//		}
//		return true;
//	}
    public static boolean checkIcbcName(String icbcName) {
        if (TextUtils.isEmpty(icbcName)) {
            ToastUtil.show("请输入工行卡的开户姓名");
            return false;
        }
        if (icbcName.length() == 1) {
            ToastUtil.show("工行卡的开户姓名长度不够");
            return false;
        }

        if (!StringUtil.isChinese(icbcName)) {
            ToastUtil.show("工行卡的开户姓名应为中文");
            return false;
        }

        return true;
    }

    public static boolean checkPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.show(R.string.tip_pwd_null);
            return false;
        }
        if (pwd.length() < 6 || pwd.length() > 16) {
            ToastUtil.show(R.string.pass_tip);
            return false;
        }
        return true;
    }

    /**
     * 获取处理过的身份证号或银行卡号
     *
     * @param str
     * @return
     */
    public static String getEncodedCard(String str) {
        if (TextUtils.isEmpty(str) || str.length() < 10) {
            return str;
        }
        String middle = str.substring(4, str.length() - 4);
//        StringBuffer stringBuffer = new StringBuffer();
//        for (int i = 0; i < middle.length(); i++) {
//            stringBuffer.append("*");
//        }
//		str = str.replaceAll(middle, stringBuffer.toString());
        str = str.replaceAll(middle, "******");
        return str;
    }

    /**
     * 获取处理过的手机号
     *
     * @param str
     * @return
     */
    public static String getEncodedPhone(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        String middle = str.substring(3, str.length() - 3);
        str = str.replaceAll(middle, "*****");
        return str;
    }

    /**
     * 判断邮箱
     *
     * @param email
     */
    public static boolean isEmail(String email) {
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        boolean isMatched = matcher.matches();
        return isMatched;
    }


    /**
     * 检测身份证号码
     *
     * @param cardNo
     * @return
     */
    public static boolean checkCardNo(String cardNo) {
        return RegUtil.checkCardNo(cardNo);
    }

    public static List<IvehicleModel> carModels;
}

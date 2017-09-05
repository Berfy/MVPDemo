package com.wlb.agent.core.data.user.response;

import com.google.gson.annotations.SerializedName;
import com.wlb.agent.core.data.base.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */

public class CouponListResponse extends BaseResponse{

    private int count;
    private List<CouponEntity> list;

    public List<CouponEntity> getList() {
        if(null==list)list=new ArrayList<>();
        return list;
    }

    public void setList(List<CouponEntity> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "CouponListResponse{" +
                "count=" + count +
                ", list=" + list +
                '}';
    }

    public static class CouponEntity implements Serializable {
        /**
         * coupon_id : 6
         * coupon_no : CPN-1001-fasfdasdi-FASDFASFD
         * coupon_name : 车险优惠券
         * desc : 车险优100元惠券
         * status : 0
         * denomination : ￥100
         * start_time_use : 1480521600000
         * end_time_use : 1485878400000
         * grant_time : 1480521600000
         */

        private int coupon_id;
        private String coupon_no;
        private String coupon_name;
        private String desc;
        @SerializedName("status")
        private int statusX;
        private String denomination;
        private long start_time_use;
        private long end_time_use;
        private long grant_time;
        private int check_flag;

        public int getCoupon_id() {
            return coupon_id;
        }

        public void setCoupon_id(int coupon_id) {
            this.coupon_id = coupon_id;
        }

        public String getCoupon_no() {
            return coupon_no;
        }

        public void setCoupon_no(String coupon_no) {
            this.coupon_no = coupon_no;
        }

        public String getCoupon_name() {
            return coupon_name;
        }

        public void setCoupon_name(String coupon_name) {
            this.coupon_name = coupon_name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public int getStatusX() {
            return statusX;
        }

        public void setStatusX(int statusX) {
            this.statusX = statusX;
        }

        public String getDenomination() {
            return denomination;
        }

        public void setDenomination(String denomination) {
            this.denomination = denomination;
        }

        public long getStart_time_use() {
            return start_time_use;
        }

        public void setStart_time_use(long start_time_use) {
            this.start_time_use = start_time_use;
        }

        public long getEnd_time_use() {
            return end_time_use;
        }

        public void setEnd_time_use(long end_time_use) {
            this.end_time_use = end_time_use;
        }

        public long getGrant_time() {
            return grant_time;
        }

        public void setGrant_time(long grant_time) {
            this.grant_time = grant_time;
        }

        public int getCheck_flag() {
            return check_flag;
        }

        public void setCheck_flag(int check_flag) {
            this.check_flag = check_flag;
        }

        @Override
        public String toString() {
            return "CouponEntity{" +
                    "coupon_id=" + coupon_id +
                    ", coupon_no='" + coupon_no + '\'' +
                    ", coupon_name='" + coupon_name + '\'' +
                    ", desc='" + desc + '\'' +
                    ", statusX=" + statusX +
                    ", denomination='" + denomination + '\'' +
                    ", start_time_use=" + start_time_use +
                    ", end_time_use=" + end_time_use +
                    ", grant_time=" + grant_time +
                    ", check_flag=" + check_flag +
                    '}';
        }
    }
}

package com.wlb.common;

import android.app.Activity;

import com.android.util.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Berfy
 * @category Activity堆栈管理
 */
public class ActivityManager {

    private Stack<ActEntity> activityStack;
    public static ActivityManager instance;

    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    public Integer getActivityNum() {
        if (null != activityStack) {
            return activityStack.size();
        }
        return 0;
    }

    public void popActivity() {
        ActEntity actEntity = activityStack.lastElement();
        if (null != actEntity) {
            LogUtil.d("退出Activity", actEntity.getActivity().getClass() + "");
            actEntity.getActivity().finish();
        }
    }

    public void popActivity(Activity activity) {
        LogUtil.d("退出Activity", activity.getClass() + "");
        List<ActEntity> actEntities = new ArrayList<>();
        if (null != activity) {
            for (ActEntity actEntity : activityStack) {
                if (actEntity.getActivity() == activity) {
                    actEntities.add(actEntity);
                    actEntity.getActivity().finish();
                }
            }
            for (ActEntity actEntity : actEntities) {
                activityStack.remove(actEntity);
            }
        }
    }

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement().getActivity();
        return activity;
    }

    public void pushActivity(Activity activity) {
        if (null == activityStack) {
            activityStack = new Stack<ActEntity>();
        }
        LogUtil.d("跳转Activity", activity.getClass().getName());
        ActEntity actEntity = new ActEntity("", activity);
        activityStack.add(actEntity);
    }

    public void pushActivity(String tag, Activity activity) {
        if (null == activityStack) {
            activityStack = new Stack<ActEntity>();
        }
        LogUtil.d("跳转Activity", activity.getClass().getName());
        ActEntity actEntity = new ActEntity(tag, activity);
        activityStack.add(actEntity);
    }

    public void popAllActivityExceptOne(Class<?> cls) {
        LogUtil.d("退出Activity", "除了" + cls);
        while (null != activityStack && activityStack.size() > 0) {
            Activity activity = currentActivity();
            if (null == activity) {
                break;
            }
            if (null != cls) {
                if (activity.getClass().equals(cls)) {
                    break;
                }
            }
            popActivity(activity);
        }
        System.gc();
    }

    public void popActivity(Class<?> cls) {
        LogUtil.d("退出Activity", cls + "");
        if (null != activityStack) {
            for (int i = 0; i < activityStack.size(); i++) {
                ActEntity actEntity = activityStack.elementAt(i);
                if (actEntity.getActivity().getClass().equals(cls)) {
                    LogUtil.e("关闭Activity", cls + "");
                    popActivity(actEntity.getActivity());
                }
            }
        }
    }

    public void popActivityWithTag(String tag) {
        LogUtil.d("退出Activity 根据Tag", tag + "");
        if (null != activityStack) {
            for (int i = 0; i < activityStack.size(); i++) {
                ActEntity actEntity = activityStack.elementAt(i);
                if (actEntity.getTag().getClass().equals(tag)) {
                    LogUtil.e("关闭Activity", actEntity.getActivity().getClass() + "");
                    popActivity(actEntity.getActivity());
                }
            }
        }
    }

    public Activity getActivity(int position) {
        if (null != activityStack && activityStack.size() > position) {
            return activityStack.get(position).getActivity();
        }
        return null;
    }

    class ActEntity {
        private Activity activity;
        private String tag;//标记Activity分组，用于整组退出销毁页面

        public ActEntity(String tag, Activity activity) {
            this.activity = activity;
            this.tag = tag;
        }

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}

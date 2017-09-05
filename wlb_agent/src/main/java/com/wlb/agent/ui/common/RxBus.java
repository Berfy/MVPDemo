package com.wlb.agent.ui.common;

import java.util.HashMap;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

public class RxBus {
    private final Subject<Object, Object> _bus;
    private HashMap<String, CompositeSubscription> mSubscriptionMap = new HashMap<>();
    private static RxBus instance = null;

    /**
     * Subject同时充当了Observer和Observable的角色，Subject是非线程安全的
     * PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者。
     * SerializedSubject，并发时只允许一个线程调用onnext等方法！
     */
    private RxBus() {
        //SerializedSubject将线程非安全的PublishSubject包装成线程安全的Subject
        _bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        if (null == instance) {
            synchronized (RxBus.class) {
                if (null == instance) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    public void post(Object o) {
        _bus.onNext(o);
    }

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        return _bus.ofType(eventType);////filter + cast
    }

    public boolean hasObservers() {
        return _bus.hasObservers();
    }

    public <T> Subscription doSubscribe(final Class<T> type, Observer<T> observer) {
        return toObserverable(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    public <T> void addSubscribe(Object key,final Class<T> type, Observer<T> observer) {
        Subscription subscribe = toObserverable(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
        addSubscription(key,subscribe);
    }

    public <T> Subscription doSubscribe(final Class<T> type, Action1<T> action) {
        return toObserverable(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
    }
    public <T> void  addSubscribe(Object key,final Class<T> type, Action1<T> action) {
        Subscription subscribe = toObserverable(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action);
        addSubscription(key,subscribe);
    }

    public <T> Subscription doSubscribe(final Class<T> type, Action1<T> action, Action1<Throwable> errorAction) {
        return toObserverable(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, errorAction);
    }
    public <T> void addSubscribe(Object key,final Class<T> type, Action1<T> action, Action1<Throwable> errorAction) {
        Subscription subscribe = toObserverable(type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, errorAction);
        addSubscription(key,subscribe);
    }


    /**
     * 保存订阅后的subscription
     *
     * @param keyObj
     * @param subscription
     */
    public void addSubscription(Object keyObj, Subscription subscription) {
        String key = keyObj.getClass().getName();
        CompositeSubscription compositeSubscription = mSubscriptionMap.get(key);
        if (null == compositeSubscription) {
            compositeSubscription = new CompositeSubscription();
            mSubscriptionMap.put(key, compositeSubscription);
        }
        compositeSubscription.add(subscription);
    }

    /**
     * 取消订阅
     *
     * @param o
     */
    public void unSubscribe(Object o) {

        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)) {
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).unsubscribe();
        }

        mSubscriptionMap.remove(key);
    }

}
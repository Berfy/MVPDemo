package com.android.util.http.callback;

import com.android.util.http.exception.NetException;

/**
 * 加载回调接口
 * <p>
 * <ul>
 * <li>1、start:开始</li>
 * <li>2、success(Object data)执行成功回调 并将执行结果返回</li>
 * <li>3、failure(NetExpetion e) 执行失败回调，客户端可根据NetExpetion处理不同的异常:
 * 比如：
 * <p>
 * <pre class="prettyprint">
 * switch (e.getErrorCode()) {
 * case Network_NoConnected:
 * // 当前无网络
 * break;
 * case Network_Error:
 * // 网络请求失败
 * break;
 * case Parse_Error:
 * // 数据解析异常
 * break;
 * case NO_DATA:
 * // 无数据
 * break;
 * }
 * </pre>
 * <p>
 * </li>
 * <li>4、end:执行完成，无论执行成功还是失败都会回调此方法</li>
 * <ul>
 * </p>
 *
 * @author 张全
 */
public interface ICallback<T> {

    /**
     * 开始
     */
    public void start();

    /**
     * 加载成功
     *
     * @param data 响应数据
     */
    public void success(T data);

    /**
     * 加载失败
     *
     * @param e
     */
    public void failure(NetException e);

    /**
     * 结束，不管失败或成功都会回调此方法。
     */
    public void end();
}


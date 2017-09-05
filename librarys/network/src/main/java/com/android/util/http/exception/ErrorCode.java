package com.android.util.http.exception;

/**
 * 网络请求错误码
 * 
 * @author 张全
 */
public enum ErrorCode {
	/**
	 * 网络无连接
	 */
	NoConnection(100, "网络无连接"),
	/**
	 * 网络异常
	 */
	NetworkError(101, "网络异常"),
	/**
	 * 网络超时
	 */
	TimeOut(102, "网络超时"),
	/**
	 * 解析错误
	 */
	ParseError(103, "解析错误"),
	/**
	 * 授权失败
	 */
	AuthFailureError(104, "授权失败"),

	/**
	 * 服务器错误
	 */
	ServerError(105, "服务器错误"),
	/**
	 * 请求已取消
	 */
	RequestCanceled(106, "请求已取消");

	public int code;
	public String msg;

	private ErrorCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}

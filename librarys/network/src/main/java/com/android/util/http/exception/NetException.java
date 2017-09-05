package com.android.util.http.exception;

/**
 * 网络异常
 * @author zhangquan
 */
public class NetException extends Exception {

	private static final long serialVersionUID = -188142280285540429L;

	public ErrorCode errorCode;

	public NetException() {
	}

	public NetException(Throwable cause) {
		super(cause);
	}

	public NetException(String exceptionMessage) {
		super(exceptionMessage);
	}

	public NetException(ErrorCode errorCode){
		this(errorCode.msg);
		this.errorCode=errorCode;
	}
	public NetException(ErrorCode errorCode, Throwable reason) {
		this(reason);
		this.errorCode = errorCode;
	}

	public NetException(String exceptionMessage, Throwable reason) {
		super(exceptionMessage, reason);
	}

}

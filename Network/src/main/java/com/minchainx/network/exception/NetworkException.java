package com.minchainx.network.exception;

public class NetworkException extends RuntimeException {

    private int mStatusCode;
    private ErrorType mErrorType;
    private String mMessage;

    public NetworkException(int statusCode, String responseMessage) {
        super(responseMessage);
        this.mStatusCode = statusCode;
        this.mErrorType = ErrorType.SERVER;
        this.mMessage = responseMessage;
    }

    public NetworkException(ErrorType errorType, String detailMessage) {
        super(detailMessage);
        this.mErrorType = errorType;
        this.mMessage = detailMessage;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    public ErrorType getErrorType() {
        return mErrorType;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[statusCode=" + mStatusCode + ",errorType=" + mErrorType
                + ",message=" + mMessage + "]";
    }

    public enum ErrorType {
        //人为错误
        MANUAL,
        //没有网络链接
        NO_NETWORK_CONNECTED,
        //服务器错误
        SERVER,
        //JSON解析错误
        JSON
    }
}

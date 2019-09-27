package com.minchainx.network.utils;

import com.minchainx.network.exception.NetworkException;
import com.minchainx.network.exception.SessionTimeoutException;

import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;

public class ErrorMessageUtils {

    public static String createErrorMessage(Exception exception) {
        String message = "网络请求异常";
        if (exception != null) {
            if (exception instanceof NetworkException) {
                NetworkException e = (NetworkException) exception;
                if (NetworkException.ErrorType.NO_NETWORK_CONNECTED == e.getErrorType()) {
                    message = "U-1009网络连接失败";
                } else if (NetworkException.ErrorType.SERVER == e.getErrorType()) {
                    message = "I0网络请求异常";
                } else if (NetworkException.ErrorType.JSON == e.getErrorType()) {
                    message = "I1数据返回异常";
                }
            } else if (exception instanceof SessionTimeoutException) {
//                    message = "登录状态已过期,请重新登录";
                message = "";
            } else if (exception instanceof SocketTimeoutException) {
                message = "U-1001网络超时，请稍后再试";
            } else {
                int statusCode = -1;
                if (exception.getClass().isAssignableFrom(HttpException.class)) {
                    try {
                        statusCode = ((HttpException) exception).code();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (statusCode != -1) {
                    message = "H" + statusCode + "网络请求异常";
                }
            }
        }
        return message;
    }
}

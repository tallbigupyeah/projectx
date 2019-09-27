package com.minchainx.base.clazz.subscriber;

import android.text.TextUtils;

import com.minchainx.network.entity.ResponseEntity;
import com.minchainx.network.exception.SessionTimeoutException;
import com.minchainx.network.utils.ErrorMessageUtils;

import rx.Subscriber;

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private boolean ifShowErrorMessage = false;

    public BaseSubscriber() {
        this(false);
    }

    public BaseSubscriber(boolean ifShowErrorMessage) {
        this.ifShowErrorMessage = ifShowErrorMessage;
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        showErrorMessage(e);
    }

    @Override
    public void onNext(T t) {
        if (t != null && t instanceof ResponseEntity) {
            ResponseEntity response = (ResponseEntity) t;
            if (response != null && ResponseEntity.FLAG_NONE == response.flag && !TextUtils.isEmpty(response.errorCode)) {
                if (ResponseEntity.ERROR_CODE_SUCCESS.equalsIgnoreCase(response.errorCode)) {
                    response.flag = ResponseEntity.FLAG_SUCCESS;
                }
                response.msg = response.errorMessage;
            }
        }
        onSuccess(t);
    }

    public abstract void onSuccess(T response);

    public abstract void logout();

    public abstract void showErrorMessage(String errorMessage);

    public abstract void startLoginActivity();

    private void showErrorMessage(Throwable throwable) {
        try {
            if (throwable instanceof Exception) {
                Exception exception = (Exception) throwable;
                if (ifShowErrorMessage) {
                    String errorMessage = ErrorMessageUtils.createErrorMessage(exception);
                    if (!TextUtils.isEmpty(errorMessage)) {
                        showErrorMessage(errorMessage);
                    }
                }
                if (exception instanceof SessionTimeoutException) {
                    logout();
                }
            }
            throwable.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

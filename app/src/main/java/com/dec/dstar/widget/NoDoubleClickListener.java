package com.dec.dstar.widget;

import android.view.View;

import java.util.Calendar;

/**
 * 作者：luoxiaohui
 * 日期:2017/4/21 18:07
 * 文件描述: 避免控件设置onClickListener事件重复
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(View v);

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }
}

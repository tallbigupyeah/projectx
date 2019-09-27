package com.dec.dstar.widget;

import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;

/**
 * 作者：luoxiaohui
 * 日期:2017/4/21 18:19
 * 文件描述:
 */
public abstract class NoDoubleItemClickListener implements AdapterView.OnItemClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(AdapterView<?> adapterView, View view, int i, long l);

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(adapterView, view, i, l);
        }
    }
}

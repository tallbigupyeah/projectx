package com.dec.dstar.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.dec.dstar.DstarApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/15 20:06
 * 文件描述:
 */
public final class Utils {

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {

        return DstarApplication.getInstance().getApplicationContext();
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate() {
        long timeStamp = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeStamp);
        return simpleDateFormat.format(date);
    }

    /**
     * 复制文本内容到系统剪贴板
     */
    public static void copy2Clipboard(String text) {

        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) DstarApplication.getInstance().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    /**
     * 日期格式字符串转换成时间戳
     * @return
     */
    public static String date2TimeStamp(String date_str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String date = sdf.format(new Date(Integer.parseInt(date_str) * 1000L));
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

package com.dec.dstar.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dec.dstar.R;

public class CommonToast {


    /**
     * 揭示结果的toast
     * 顶部显示，padding左右32，展示时间2秒，动画由上而下
     */
    public static void showMessage(CharSequence msg) {
        showMessage(msg, 0);
    }


    /**
     * 揭示结果的toast(带图标)
     * 顶部显示，padding左右32，展示时间2秒，动画由上而下
     */
    public static void showMessage(CharSequence msg, int resIcon) {
        View inflate = LayoutInflater.from(Utils.getContext()).inflate(R.layout.common_toast, null);
        ImageView ivIcon = (ImageView) inflate.findViewById(R.id.iv_icon);
        TextView tvMessage = (TextView) inflate.findViewById(R.id.tv_content);
        if (resIcon != 0) {
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setImageResource(resIcon);
        }
        if (!StringUtils.isEmpty(msg)) {
            tvMessage.setText(msg);
        }
        ToastCustomUtils.setView(inflate);
        ToastCustomUtils.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 36);
        ToastCustomUtils.setMargin(0.0f, 0.0f);
        ToastCustomUtils.setAnimation(R.style.Animation_ToastInfo);
        ToastCustomUtils.showShortSafe("");
    }

    /**
     * 承载报错提示的toast
     * 居中显示，padding左右32，展示时间2秒，动画由下居中
     */
    public static void showErrorMessage(CharSequence msg) {
        View inflate = LayoutInflater.from(Utils.getContext()).inflate(R.layout.common_toast, null);
        TextView tvMessage = (TextView) inflate.findViewById(R.id.tv_content);
        if (!StringUtils.isEmpty(msg)) {
            tvMessage.setText(msg);
        }
        ToastCustomUtils.setView(inflate);
        ToastCustomUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastCustomUtils.setMargin(0.0f, 0.0f);
        ToastCustomUtils.setAnimation(R.style.Animation_ToastError);
        ToastCustomUtils.showShortSafe("");
    }

}

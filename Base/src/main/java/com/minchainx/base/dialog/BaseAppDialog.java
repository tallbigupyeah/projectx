package com.minchainx.base.dialog;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAppDialog extends BaseDialog {

    /**
     * 页面黑名单（在其中的页面不显示当前弹窗）
     */
    private List<String> mPageBlackList;

    public BaseAppDialog(Context context) {
        super(context);
        initialize(context);
    }

    /**
     * 初始化弹窗
     *
     * @param context
     */
    protected abstract void initialize(Context context);

    /**
     * 检查是否显示弹窗
     *
     * @param activity
     * @param callback
     * @return
     */
    protected void checkIfShow(Activity activity, IDialogCallback callback) {
    }

    /**
     * 当APP从后台再次打开
     *
     * @param activity
     */
    protected abstract void onAppReopenFromBackground(Activity activity);

    /**
     * 获取页面黑名单列表
     *
     * @return
     */
    protected List<String> getPageBlackList() {
        if (mPageBlackList == null) {
            mPageBlackList = new ArrayList<String>();
        }
        return mPageBlackList;
    }

    /**
     * 设置页面黑名单，其中的页面不检测本弹窗
     *
     * @param list
     */
    protected void setPageBlackList(List<String> list) {
        getPageBlackList().clear();
        if (list != null && list.size() > 0) {
            getPageBlackList().addAll(list);
        }
    }

    /**
     * 判断activity是否在黑名单内
     *
     * @param activity
     * @return
     */
    protected boolean isInPageBlackList(Activity activity) {
        return AppDialogManager.judgePatternMatch(activity, getPageBlackList());
    }

    protected static void i(String msg) {
        AppDialogManager.i(msg);
    }

    protected static void w(String msg) {
        AppDialogManager.w(msg);
    }

    protected static void e(String msg) {
        AppDialogManager.e(msg);
    }

    /**
     * 弹窗回调接口
     */
    public interface IDialogCallback {

        /**
         * 加载完成
         *
         * @param enableLoadNext 是否能加载下个弹窗
         */
        void onCompleted(boolean enableLoadNext);
    }
}

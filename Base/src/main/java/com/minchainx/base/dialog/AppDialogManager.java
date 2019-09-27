package com.minchainx.base.dialog;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.minchainx.network.utils.LogUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppDialogManager implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "AppDialog";

    /**
     * 弹窗管理类对象
     */
    private static AppDialogManager sManager;
    /**
     * 是否开启日志
     */
    private static boolean sLogEnable = false;

    /**
     * 应用
     */
    private Application mApplication;
    /**
     * 公共黑名单（在其中的页面不进行弹窗显示检测）
     */
    private List<String> mCommonBlackList;
    /**
     * 弹窗信息列表
     */
    private List<DialogInfo> mDialogInfoList = new ArrayList<DialogInfo>();
    /**
     * 弹窗Class与弹窗信息列表索引对应集合
     */
    private Map<Class, Integer> mIndexMap = new HashMap<Class, Integer>();
    /**
     * 当前显示弹窗索引
     */
    private int mShowIndex = 0;
    /**
     * 是否正在检测弹窗
     */
    private boolean mIsChecking = false;
    /**
     * 当前运行的Activity数
     */
    private int mActiveCount;

    private AppDialogManager(Application application) {
        this.mApplication = application;
        application.registerActivityLifecycleCallbacks(this);
    }

    public static AppDialogManager getInstance() {
        return sManager;
    }

    public static void init(Application application) {
        sManager = new AppDialogManager(application);
    }

    /**
     * 设置是否输出日志
     *
     * @param enable
     */
    public static void setLogEnable(boolean enable) {
        sLogEnable = enable;
    }

    private synchronized List<DialogInfo> getDialogInfoList() {
        return mDialogInfoList;
    }

    public synchronized void addDialog(Class clazz) {
        if (clazz != null) {
            mIndexMap.put(clazz, mDialogInfoList.size());
            mDialogInfoList.add(new DialogInfo(clazz, true));
        }
    }

    public synchronized void execute(Activity activity) {
        i("DialogManager...execute...activity=" + activity + ",mIsChecking=" + mIsChecking);
        if (mIsChecking) {
            return;
        }
        mIsChecking = true;
        traverseExecute(activity);
    }

    private synchronized void traverseExecute(final Activity activity) {
        i("traverseExecute...activity=" + activity + ",mIsChecking=" + mIsChecking);
        List<DialogInfo> dialogInfoList = getDialogInfoList();
        if (dialogInfoList == null) {
            reset();
            return;
        }
        int dialogInfoListSize = dialogInfoList.size();
        i("dialogInfoListSize=" + dialogInfoListSize + ",mShowIndex=" + mShowIndex);
        if (dialogInfoListSize <= mShowIndex) {
            reset();
            return;
        }
        DialogInfo dialogInfo = dialogInfoList.get(mShowIndex);
        i("dialogInfo=" + dialogInfo);
        if (dialogInfo == null) {
            loadNext(activity);
            return;
        }
        i("dialogInfo.enable=" + dialogInfo.enable);
        if (!dialogInfo.enable) {
            loadNext(activity);
            return;
        }
        BaseAppDialog dialog = getDialogByReflect(activity, dialogInfo.clazz);
        i("dialog=" + dialog);
        if (dialog == null) {
            loadNext(activity);
            return;
        }
        dialogInfo.dialog = dialog;
        i("dialog.isInPageBlackList=" + dialog.isInPageBlackList(activity));
        if (dialog.isInPageBlackList(activity)) {
            loadNext(activity);
            return;
        }
        dialog.checkIfShow(activity, new BaseAppDialog.IDialogCallback() {
            @Override
            public void onCompleted(boolean enableLoadNext) {
                i("IDialogCallback.onCompleted....enableLoadNext=" + enableLoadNext);
                if (enableLoadNext) {
                    mShowIndex++;
                }
                i("mShowIndex=" + mShowIndex);
                traverseExecute(activity);
            }
        });
    }

    public synchronized void checkDialog(Activity activity, Class clazz) {
        i("checkDialog...clazz=" + clazz);
        int index = -1;
        if (mIndexMap != null && mIndexMap.containsKey(clazz)) {
            index = mIndexMap.get(clazz);
        }
        final BaseAppDialog dialog;
        if (index > -1) {
            List<DialogInfo> dialogInfoList = getDialogInfoList();
            if (dialogInfoList == null) {
                return;
            }
            int dialogInfoListSize = dialogInfoList.size();
            i("dialogInfoListSize=" + dialogInfoListSize + ",index=" + index);
            if (dialogInfoListSize <= index) {
                return;
            }
            DialogInfo dialogInfo = dialogInfoList.get(index);
            i("dialogInfo=" + dialogInfo);
            if (dialogInfo == null) {
                return;
            }
            i("dialogInfo.enable=" + dialogInfo.enable);
            if (!dialogInfo.enable) {
                return;
            }
            dialog = getDialogByReflect(activity, dialogInfo.clazz);
            if (dialog != null) {
                dialogInfo.dialog = dialog;
            }
        } else {
            dialog = getDialogByReflect(activity, clazz);
        }
        i("dialog=" + dialog);
        if (dialog == null) {
            return;
        }
        i("dialog.isInPageBlackList=" + dialog.isInPageBlackList(activity));
        if (dialog.isInPageBlackList(activity)) {
            return;
        }
        dialog.checkIfShow(activity, null);
    }

    private void loadNext(Activity activity) {
        mShowIndex++;
        traverseExecute(activity);
    }

    /**
     * 遍历调用各弹窗onAppReopenFromBackground方法
     *
     * @param activity
     */
    private void traverseOnAppReopenFromBackground(Activity activity) {
        i("traverseOnAppReopenFromBackground....activity=" + activity);
        if (mDialogInfoList != null) {
            for (DialogInfo dialogInfo : mDialogInfoList) {
                i("dialogInfo=" + dialogInfo);
                if (dialogInfo != null && dialogInfo.dialog != null) {
                    dialogInfo.dialog.onAppReopenFromBackground(activity);
                }
            }
        }
    }

    private BaseAppDialog getDialogByReflect(Activity activity, Class clazz) {
        if (clazz != null) {
            try {
                Constructor constructor = clazz.getConstructor(new Class[]{Context.class});
                BaseAppDialog dialog = (BaseAppDialog) constructor.newInstance(activity);
                return dialog;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取公共黑名单
     *
     * @return
     */
    public List<String> getCommonBlackList() {
        if (mCommonBlackList == null) {
            mCommonBlackList = new ArrayList<String>();
        }
        return mCommonBlackList;
    }

    /**
     * 设置公共黑名单，其中的页面所有弹窗均不检测
     *
     * @param list
     */
    public void setCommonBlackList(List<String> list) {
        getCommonBlackList().clear();
        if (list != null && list.size() > 0) {
            getCommonBlackList().addAll(list);
        }
    }

    /**
     * 判断activity是否在黑名单内
     *
     * @param activity
     * @return
     */
    private boolean isInCommonBlackList(Activity activity) {
        return judgePatternMatch(activity, getCommonBlackList());
    }

    /**
     * 判断activity是否匹配给定列表中的某一规则
     *
     * @param activity
     * @param patternList
     * @return
     */
    public static boolean judgePatternMatch(Activity activity, List<String> patternList) {
        if (activity == null) {
            return false;
        }
        String currentClassName = activity.getClass().getName();
        if (TextUtils.isEmpty(currentClassName)) {
            return false;
        }
        boolean matchResult = false;
        if (patternList != null && patternList.size() > 0) {
            for (String pattern : patternList) {
                if (!TextUtils.isEmpty(pattern) && currentClassName.matches(pattern)) {
                    matchResult = true;
                    break;
                }
            }
        }
        return matchResult;
    }

    private void reset() {
        mIsChecking = false;
        mShowIndex = 0;
        i("reset...mIsChecking=" + mIsChecking + ",mShowIndex=" + mShowIndex);
    }

    public static void i(String msg) {
        if (sLogEnable) {
            LogUtils.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (sLogEnable) {
            LogUtils.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (sLogEnable) {
            LogUtils.e(TAG, msg);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        i("onActivityCreated....activity=" + activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        i("onActivityStarted....activity=" + activity + ",mActiveCount=" + mActiveCount);
        if (mActiveCount == 0) {
            traverseOnAppReopenFromBackground(activity);
        }
        mActiveCount++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
//        i("onActivityResumed....activity=" + activity);
        i("isInCommonBlackList=" + isInCommonBlackList(activity));
        if (!isInCommonBlackList(activity)) {
            execute(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
//        i("onActivityPaused....activity=" + activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
//        i("onActivityStopped....activity=" + activity);
        mActiveCount--;
//        if (mActiveCount == 0) {
//        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//        i("onActivitySaveInstanceState....activity=" + activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        i("onActivityDestroyed....activity=" + activity);
        if (mActiveCount == 0) {
            mDialogInfoList.clear();
        }
    }

    public static class DialogInfo {

        /**
         * 弹窗类文件
         */
        public Class clazz;
        /**
         * 弹窗是否可用
         */
        public boolean enable;
        /**
         * 弹窗对象
         */
        public BaseAppDialog dialog;

        public DialogInfo(Class clazz, boolean enable) {
            this.clazz = clazz;
            this.enable = enable;
        }

        @Override
        public String toString() {
            return "DialogInfo{" +
                    "clazz=" + clazz +
                    ", enable=" + enable +
                    ", dialog=" + dialog +
                    '}';
        }
    }
}

package com.dec.dstar.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/15 19:59
 * 文件描述:
 */
public class ActivityCollector {

    /**
     * 存储Activity集合
     */
    private static List<Activity> activityList;

    /**
     * 对象实例
     */
    private static ActivityCollector activityCollector;

    /**
     * 私有化构造方法
     */
    private ActivityCollector() {

    }

    /**
     * 实例化对象
     *
     * @return
     */
    public static ActivityCollector getInstance() {
        if (activityCollector == null) {
            activityCollector = new ActivityCollector();
        }
        return activityCollector;
    }

    /**
     * 添加Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (!getActivityList().contains(activity)) {
            activityList.add(activity);
        }
    }

    public Activity getPrevActivity(Activity activity) {
        final int index = activityList.indexOf(activity);
        if (index > 0) {
            return activityList.get(index - 1);
        }
        return null;
    }

    public int getActivitySize(){
       return getActivityList().size();
    }

    /**
     * 移除Activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        getActivityList().remove(activity);
    }


    /**
     * 移除Activity
     *
     * @param activityClass
     */
    public void removeActivity(Class<? extends Activity> activityClass) {
        for (Activity activity : getActivityList()) {
            if (activity.getClass().equals(activityClass)) {
                activity.finish();
            }
        }
    }


    /**
     * 关闭所有Activity
     */
    public void finishAll() {
        for (Activity activity : getActivityList()) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList = null;
    }

    /**
     * 关闭所有Activity Except
     */
    public <T> void finishAllExcept(Class<T> clazz) {
        for (Activity activity : getActivityList()) {
            if (activity != null && clazz.isInstance(activity)) {
                continue;
            }
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        activityList = null;
    }


    public List<Activity> getActivityList() {
        if (activityList == null) {
            activityList = new WeakList<>();
        }
        return activityList;
    }

    /**
     * 退出出应用
     */
    public void exitApp() {
        finishAll();
    }

    private class WeakList<E> extends AbstractList<E> {

        private LinkedList<WeakReference<E>> items;


        public WeakList() {
            items = new LinkedList<>();
        }

        public WeakList(Collection<E> c) {
            items = new LinkedList<>();
            addAll(0, c);
        }

        public void add(int index, E element) {
            items.add(index, new WeakReference<>(element));
        }

        public Iterator<E> iterator() {
            return new WeakListIterator();
        }

        public int size() {
            removeReleased();
            return items.size();
        }

        public E get(int index) {
            return (items.get(index)).get();
        }

        @Override
        public boolean remove(Object object) {
            for (int i = 0; i < items.size(); i++) {
                WeakReference<E> ref = items.get(i);
                if (ref.get() == object) {
                    return items.remove(ref);
                }
            }
            return false;
        }

        private void removeReleased() {
            for (int i = 0; i < items.size(); i++) {
                WeakReference<E> ref = items.get(i);
                if (ref.get() == null) {
                    items.remove(ref);
                    --i;
                }
            }
//            for (Iterator<WeakReference<E>> it = items.iterator(); it.hasNext(); ) {
//                WeakReference<E> ref = it.next();
//                if (ref.get() == null) items.remove(ref);
//            }
        }

        private class WeakListIterator implements Iterator<E> {

            private int n;
            private int i;

            public WeakListIterator() {
                n = size();
                i = 0;
            }

            public boolean hasNext() {
                return i < n;
            }

            public E next() {
                return get(i++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

        }

    }
}


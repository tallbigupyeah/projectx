package com.minchainx.base.clazz.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListViewAdapter<E> extends BaseAdapter {

    protected List<E> mData; // 数据源
    protected Context mContext;
    protected int mLayoutId;

    public BaseListViewAdapter(Context context, int layoutId) {
        this.mContext = context;
        this.mLayoutId = layoutId;
    }

    public BaseListViewAdapter(Context context, List<E> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        this.mLayoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public E getItem(int position) {
        return mData == null ? null : mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.getViewHolder(mContext, convertView, mLayoutId, parent);
        holder.mPosition = position;

        convert(holder, getItem(position));
        return holder.getConvertView();
    }

    public void setData(List<E> data) {
        this.mData = data;
    }

    public void setDataAndUpdate(List<E> data) {
        if (data != null) {
            setData(data);
            notifyDataSetChanged();
        }
    }

    public void addDataAndUpdate(List<E> data) {
        if (data != null) {
            if (this.mData == null) {
                this.mData = new ArrayList<E>();
            }
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (this.mData != null) {
            this.mData.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 设置item中各控件显示的内容
     *
     * @param holder
     * @param e
     */
    public abstract void convert(ViewHolder holder, E e);

    /**
     * 持有者类（内部类）
     *
     * @author Evan
     */
    public static class ViewHolder {

        private View mConvertView;
        private SparseArray<View> mViews;
        private int mPosition; // 当前holder对应的View的下标

        public ViewHolder(Context context, int layoutId, ViewGroup parent) {
            this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            mConvertView.setTag(this);

            mViews = new SparseArray<View>();
        }

        public static ViewHolder getViewHolder(Context context, View convertView, int layoutId, ViewGroup parent) {
            if (convertView == null) {
                return new ViewHolder(context, layoutId, parent);
            } else {
                return (ViewHolder) convertView.getTag();
            }
        }

        /**
         * 通过View的id获取控件
         */
        public <T extends View> T getViewById(int resId) {
            View view = mViews.get(resId);
            if (view == null) {
                view = mConvertView.findViewById(resId);
                mViews.put(resId, view);
            }
            return (T) view;
        }

        /**
         * 为Item中的TextView设置数据
         */
        public ViewHolder setText(int viewId, CharSequence text) {
            ((TextView) getViewById(viewId)).setText(text);
            return this;
        }
        /**
         * 为Item中的TextView设置显隐性
         */
        public ViewHolder setTextVisible(int viewId,int i) {
            ((TextView) getViewById(viewId)).setVisibility(i);
            return this;
        }
        /**
         * 为Item中的TextView设置字体大小
         */
        public ViewHolder setTextSize(int viewId, int size) {
            ((TextView) getViewById(viewId)).setTextSize(size);
            return this;
        }

        /**
         * 为Item中的ImageView设置数据
         */
        public ViewHolder setImageResource(int viewId, int imgId) {
            ((ImageView) getViewById(viewId)).setImageResource(imgId);
            return this;
        }

        /**
         * 为Item中的ImageView设置数据
         */
        public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
            ((ImageView) getViewById(viewId)).setImageBitmap(bm);
            return this;
        }
        /**
         * 为Item中的SeekBar设置进度
         */
        public ViewHolder setProgress(int viewId, int progress) {
            ((ProgressBar) getViewById(viewId)).setProgress(progress);
            return this;
        }

        /**
         * 获取当前ViewHolder对应的View对象
         */
        public View getConvertView() {
            return mConvertView;
        }

        public int getPostion() {
            return mPosition;
        }
    }
}
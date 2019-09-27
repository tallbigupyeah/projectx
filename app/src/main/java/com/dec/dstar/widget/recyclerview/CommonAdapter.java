package com.dec.dstar.widget.recyclerview;

import android.content.Context;

import com.alibaba.android.vlayout.LayoutHelper;

import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2017/5/8 20:13
 * 文件描述: 通用的ViewHolder
 */
public abstract class CommonAdapter<T> extends MultiItemTypeAdapter<T> {

    public CommonAdapter(Context context, final int layoutId, List<T> datas, int viewType, boolean isItemClick, LayoutHelper layoutHelper) {
        super(context, datas, isItemClick, layoutHelper);

        addItemViewDelegate(viewType, new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(CommonViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    public CommonAdapter(Context context, final int layoutId, List<T> datas, boolean isItemClick, LayoutHelper layoutHelper) {
        super(context, datas, isItemClick, layoutHelper);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(CommonViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    public CommonAdapter(Context context, final int layoutId, List<T> datas, LayoutHelper layoutHelper) {
        super(context, datas, true, layoutHelper);

        addItemViewDelegate(new ItemViewDelegate<T>() {
            @Override
            public int getItemViewLayoutId() {
                return layoutId;
            }

            @Override
            public boolean isForViewType(T item, int position) {
                return true;
            }

            @Override
            public void convert(CommonViewHolder holder, T t, int position) {
                CommonAdapter.this.convert(holder, t, position);
            }
        });
    }

    protected abstract void convert(CommonViewHolder holder, T t, int position);


}

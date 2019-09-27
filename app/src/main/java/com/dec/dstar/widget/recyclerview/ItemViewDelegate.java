package com.dec.dstar.widget.recyclerview;

/**
 * 作者：luoxiaohui
 * 日期:2017/5/8 20:54
 * 文件描述: recyclerView抽离出来的接口
 */
public interface ItemViewDelegate<T>
{

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(CommonViewHolder holder, T t, int position);

}

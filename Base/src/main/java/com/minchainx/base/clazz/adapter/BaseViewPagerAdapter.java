package com.minchainx.base.clazz.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class BaseViewPagerAdapter<T> extends PagerAdapter {

    protected List<T> mData;

    public void setData(List<T> data) {
        this.mData = data;
    }

    public List<T> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}

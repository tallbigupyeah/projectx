package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.wallet.ethereum.ui.fragment.TransferRecordFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/11 17:19
 * 文件描述: 转账记录列表页面
 */
public class TransferRecordsActivity extends BaseActivity {

    @BindView(R.id.transferrecords_tv_collect)
    TextView mTvCollect;
    @BindView(R.id.transferrecords_tv_transfer)
    TextView mTvTransfer;
    private TabLayout tabLayout;
    private ViewPager vp;

    private List<String> tabs;
    private List<TransferRecordFragment> fragments;


    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_transferrecords;
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        tabLayout = findViewById(R.id.transferrecords_tl);
        vp = findViewById(R.id.transferrecords_vp);

        initDatas();
        initViewPager();
        initTabLayout();
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText(getIntent().getStringExtra("tokenName"));
    }

    private void initTabLayout() {
        //关联tabLayout和ViewPager,两者的选择和滑动状态会相互影响
        tabLayout.setupWithViewPager(vp);
        //自定义标签布局
        for (int i = 0; i < tabs.size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.tabview_main, tabLayout, false);
            tv.setText(tabs.get(i));
            tab.setCustomView(tv);
        }
        ((TextView) tabLayout.getTabAt(0).getCustomView()).setTextColor(getResources().getColor(R.color.theme_color));
        vp.setCurrentItem(0);
        fragments.get(0).requestNetwork();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //在这里可以设置选中状态下  tab字体显示样式
                vp.setCurrentItem(tab.getPosition());
                View view = tab.getCustomView();
                if (null != view && view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(mContext, R.color.theme_color));
                }
                fragments.get(tab.getPosition()).requestNetwork();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                View view = tab.getCustomView();
                if (null != view && view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(mContext, R.color.black));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initViewPager() {
        vp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
    }

    private void initDatas() {
        tabs = new ArrayList<>();
        tabs.add("全部");
        tabs.add("转出");
        tabs.add("转入");
        tabs.add("失败");

        fragments = new ArrayList<>();
        for (int i = 0; i < tabs.size(); i++) {
            fragments.add(TransferRecordFragment.newInstance(getIntent().getStringExtra("tokenName"), i));
        }
    }

    @OnClick({R.id.transferrecords_tv_collect, R.id.transferrecords_tv_transfer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.transferrecords_tv_collect:

                Intent colletIntent = new Intent(mContext, WalletAddrActivity.class);
                colletIntent.putExtra("tokenName", getIntent().getStringExtra("tokenName"));
                colletIntent.putExtra("tokenCount", getIntent().getStringExtra("tokenCount"));
                startActivity(colletIntent);
                break;
            case R.id.transferrecords_tv_transfer:

                Intent intent = new Intent(mContext, TransferActivity.class);
                intent.putExtra("tokenName", getIntent().getStringExtra("tokenName"));
                intent.putExtra("tokenCount", getIntent().getStringExtra("tokenCount"));
                startActivity(intent);
                break;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * 如果不是自定义标签布局，需要重写该方法
         */
//        @Nullable
//        @Override
//        public CharSequence getPageTitabLayoute(int position) {
//            return tabs.get(position);
//        }
    }
}

package com.jcedar.sdahyoruba.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afolayan Oluwaseyi on 07/01/2017.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragmentTitleList = new ArrayList<>();
    private Context context;

    public ViewPagerAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.context = mContext;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public int getItemPosition(Object object) {
         super.getItemPosition(object);
        return mFragmentList.indexOf(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "1-100";
            case 1:
                return "101-200";
            case 2:
                return "201-300";
            case 3:
                return "301-400";
            case 4:
                return "401-500";
            case 5:
                return "501-600";
            case 6:
                return "601-621";

        }
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }


}
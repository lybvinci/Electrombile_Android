package com.xunce.electrombile.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by heyukun on 2015/5/14.
 */
public class XCFragmentAdapter extends FragmentPagerAdapter {

    List<Fragment> list;

    public XCFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public XCFragmentAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list=list;
    }
    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
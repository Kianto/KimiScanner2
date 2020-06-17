package com.app.kimiscanner.account;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.authview.ForgotFragment;
import com.app.kimiscanner.account.authview.LoginFragment;
import com.app.kimiscanner.account.authview.RegisterFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2,  R.string.tab_text_3};
    private final Context mContext;
    private final AccountFragment mFatherFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm, AccountFragment fatherFragment) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        mFatherFragment = fatherFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return RegisterFragment.newInstance(mFatherFragment, position);
            case 2:
                return ForgotFragment.newInstance(mFatherFragment, position);
            default:
                return LoginFragment.newInstance(mFatherFragment, position);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 3;
    }
}
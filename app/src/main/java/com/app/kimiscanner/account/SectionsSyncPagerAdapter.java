package com.app.kimiscanner.account;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.authview.ForgotFragment;
import com.app.kimiscanner.account.authview.LoginFragment;
import com.app.kimiscanner.account.authview.RegisterFragment;
import com.app.kimiscanner.account.cloudview.BackupFragment;
import com.app.kimiscanner.account.cloudview.RestoreFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsSyncPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_sync_1, R.string.tab_sync_2};
    private final Context mContext;
    private final SyncDataFragment mFatherFragment;

    public SectionsSyncPagerAdapter(SyncDataFragment contextFragment, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = contextFragment.getContext();
        mFatherFragment = contextFragment;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return RestoreFragment.newInstance(mFatherFragment, position);
        }
        return BackupFragment.newInstance(mFatherFragment, position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 2;
    }
}
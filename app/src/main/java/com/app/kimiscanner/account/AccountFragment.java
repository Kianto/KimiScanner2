package com.app.kimiscanner.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.kimiscanner.R;
import com.google.android.material.tabs.TabLayout;


/**
 * A placeholder fragment containing a simple view.
 */
public class AccountFragment extends Fragment {

    private IFragmentInteractionListener activityListener;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

//        viewHolder = new ViewHolder();
//        viewHolder.load(view);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager(), this);
        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentInteractionListener) {
            activityListener = (IFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement IFragmentInteractionListener");
        }
    }

    public void registerAccount(String email, String password) {
        activityListener.onRegisterFragmentInteraction(email, password);
    }

    public void loginAccount(String email, String password) {
        activityListener.onSignInFragmentInteraction(email, password);
    }

    public void forgotPassword(String email) {
        activityListener.onForgotFragmentInteraction(email);
    }

    public void loginWithGoogleAccount() {
        activityListener.onGoogleSignInFragmentInteraction();
    }

    public interface IFragmentInteractionListener {
        void onGoogleSignInFragmentInteraction();
        void onRegisterFragmentInteraction(String email, String password);
        void onSignInFragmentInteraction(String email, String password);
        void onForgotFragmentInteraction(String email);
        void onLogoutFragmentInteraction();
    }


}

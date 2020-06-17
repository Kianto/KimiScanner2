package com.app.kimiscanner;

import android.view.View;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BasePresenter {
    protected BaseView.BaseFragment fragmentView;
    protected BaseView.BaseActivity activityView;

    public BasePresenter(BaseView.BaseFragment fragmentView) {
        this.fragmentView = fragmentView;
    }
    public BasePresenter(BaseView.BaseActivity activityView) {
        this.activityView = activityView;
    }

    public void process (String codeState, Object input) {
        Object output = getResult(codeState, input);
        if (null != output) {
            if (null != fragmentView) fragmentView.changeView(codeState, output);
            if (null != activityView) activityView.changeView(codeState, output);
        }
    }

    public abstract void setUp(View view);

    public abstract Object getResult(String codeState, Object input);


}

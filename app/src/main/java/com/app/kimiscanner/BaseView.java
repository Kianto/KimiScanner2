package com.app.kimiscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public interface BaseView {
    void changeView(String codeState, Object output);
    void setPresenter(BasePresenter presenter);

    public abstract class BaseFragment extends Fragment implements BaseView {
        protected BasePresenter presenter;

        @Override
        public void setPresenter(BasePresenter presenter) {
            this.presenter = presenter;
        }
    }

    public abstract class BaseActivity extends AppCompatActivity implements BaseView {
        protected BasePresenter presenter;

        public abstract void changeView(String codeState, Object output);

        @Override
        public void setPresenter(BasePresenter presenter) {
            this.presenter = presenter;
        }
    }
}



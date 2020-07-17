package com.app.kimiscanner;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.kimiscanner.account.DataTransferManager;
import com.app.util.LanguageManager;

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

    public abstract class BaseActivity extends AppCompatActivity implements BaseView, DataTransferManager.DataListener {
        protected BasePresenter presenter;

        public abstract void changeView(String codeState, Object output);

        @Override
        public void setPresenter(BasePresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onUpdateProgress(String folderName, String action, double percent) {

        }

        public BaseActivity() {
            DataTransferManager.getInstance().registerListen(this);
        }

        @Override
        public void onDone(String folderName, String action) {
            Toast.makeText(this, folderName + " " + action + " " + LanguageManager.getInstance().getString(R.string.completed), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFail(String folderName, String action) {
            Toast.makeText(this, folderName + " " + action + " " + LanguageManager.getInstance().getString(R.string.failed), Toast.LENGTH_LONG).show();
        }
    }
}



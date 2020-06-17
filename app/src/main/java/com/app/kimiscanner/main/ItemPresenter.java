package com.app.kimiscanner.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.app.kimiscanner.BaseView.BaseFragment;
import com.app.kimiscanner.BasePresenter;
import com.app.kimiscanner.R;
import com.app.widget.LoadingRunner;


public class ItemPresenter extends BasePresenter {
    private int mColumnCount = 1;
    private LoadingRunner runner;

    public ItemPresenter(BaseFragment fragmentView) {
        super(fragmentView);
    }

    @Override
    public Object getResult(String codeState, Object input) {
        if (codeState.equals(ItemFragment.RUN_SHOW_LIST_CODE)) {
            if (null != runner) runner.execute();
        }
        // do nothing else
        return null;
    }

    @Override
    public void setUp(View view) {
        if (fragmentView.getArguments() != null) {
            mColumnCount = fragmentView.getArguments().getInt(ItemFragment.ARG_COLUMN_COUNT);
        }

        ShowOptionListener listener = new ShowOptionListener(fragmentView);
        view.findViewById(R.id.list_linear).setOnClickListener(listener);
        view.findViewById(R.id.list_grid).setOnClickListener(listener);

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        setLoadingThread(progressBar);
    }

    protected class ShowOptionListener implements View.OnClickListener {
        private boolean isList = true;
        private Fragment fragment;

        ShowOptionListener(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.list_linear:
                    if (isList) return;
                    mColumnCount = 1;
                    isList = true;
                    break;

                case R.id.list_grid:
                    if (!isList) return;
                    mColumnCount = ItemFragment.GRID_COLUMN;
                    isList = false;
                    break;
            }
            if (null != runner) runner.execute();

            Bundle args = new Bundle();
            args.putInt(ItemFragment.ARG_COLUMN_COUNT, mColumnCount);
            fragment.setArguments(args);
        }
    }


    // === Thread Loading ===
    private void setLoadingThread(ProgressBar progressBar) {
        runner = new ListLoadingTask(progressBar, list -> {
            if (mColumnCount <= 1) {
                fragmentView.changeView(ItemFragment.LINEAR_LIST_SHOW_CODE, list);
            } else {
                fragmentView.changeView(ItemFragment.GRID_LIST_SHOW_CODE, list);
            }
        });
    }

    private static class ListLoadingTask extends LoadingRunner {
        ListLoadingTask(ProgressBar progressBar, LoadingRunner.LoadingCallback callback) {
            super(progressBar, callback);
        }

        @Override
        protected void doInBackground() {
            returnValue = FolderCollector.getLocalFolders();
        }

    } // end class ListLoadingTask

}

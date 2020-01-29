package com.app.kimiscanner.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.kimiscanner.R;
import com.app.kimiscanner.main.adapter.GridItemAdapter;
import com.app.kimiscanner.main.adapter.LinearItemAdapter;
import com.app.kimiscanner.model.FolderInfo;
import com.app.widget.LoadingRunner;

import java.util.List;

public class ItemFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final int GRID_COLUMN = 2;

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    private RecyclerView listLayout;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBar;

    private LoadingRunner runner;

    public ItemFragment() {
    }

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        return fragment;
    }

    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        setListManager(view);
        setShowOptionListener(view);
        setLoadingThread(progressBar);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(FolderInfo item);

        void onLongListFragmentInteraction(FolderInfo item);
    }

    public void changedNotify() {
        listLayout.setAdapter(new LinearItemAdapter(FolderCollector.getLocalFolders(), mListener));
    }

    @Override
    public void onStart() {
        super.onStart();
        runner.execute();
    }

    private void setListManager(View view) {
        listLayout = view.findViewById(R.id.list);

        Context context = getContext();
        linearLayoutManager = new LinearLayoutManager(context);
        gridLayoutManager = new GridLayoutManager(context, ItemFragment.GRID_COLUMN);

        // do set adapter in onStart
    }

    private void setShowOptionListener(View view) {
        ShowOptionListener listener = new ShowOptionListener(this);
        view.findViewById(R.id.list_linear).setOnClickListener(listener);
        view.findViewById(R.id.list_grid).setOnClickListener(listener);
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
            runner.execute();

            Bundle args = new Bundle();
            args.putInt(ARG_COLUMN_COUNT, mColumnCount);
            fragment.setArguments(args);
        }
    }

    // === Thread Loading ===
    private void setLoadingThread(ProgressBar progressBar) {
        runner = new ListLoadingTask(progressBar, list -> {
            List<FolderInfo> fileList = (List<FolderInfo>) list;
            if (mColumnCount <= 1) {
                listLayout.setLayoutManager(linearLayoutManager);
                listLayout.setAdapter(new LinearItemAdapter(fileList, mListener));
            } else {
                listLayout.setLayoutManager(gridLayoutManager);
                listLayout.setAdapter(new GridItemAdapter(fileList, mListener));
            }
        });
    }

    private class ListLoadingTask extends LoadingRunner {
        ListLoadingTask(ProgressBar progressBar, LoadingRunner.LoadingCallback callback) {
            super(progressBar, callback);
        }

        @Override
        protected void doInBackground() {
            returnValue = FolderCollector.getLocalFolders();
        }

    } // end class ListLoadingTask

}

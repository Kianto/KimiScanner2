package com.app.kimiscanner.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.kimiscanner.BaseView;
import com.app.kimiscanner.R;
import com.app.kimiscanner.main.adapter.GridItemAdapter;
import com.app.kimiscanner.main.adapter.LinearItemAdapter;
import com.app.kimiscanner.model.FolderInfo;

import java.util.List;

public class ItemFragment extends BaseView.BaseFragment {
    static final String ARG_COLUMN_COUNT = "column-count";
    static final int GRID_COLUMN = 2;
    static final String RUN_SHOW_LIST_CODE = "show-list-code";
    static final String LINEAR_LIST_SHOW_CODE = "linear-list-code";
    static final String GRID_LIST_SHOW_CODE = "grid-list-code";

    private OnListFragmentInteractionListener mListener;

    private RecyclerView listLayout;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;


    public ItemFragment() {
        super.setPresenter(new ItemPresenter(this));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        setListManager(view);
        presenter.setUp(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) presenter.process(RUN_SHOW_LIST_CODE, null);
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

    @Override
    public void changeView(String codeState, Object output) {
        List<FolderInfo> fileList = (List<FolderInfo>) output;

        if (codeState.equals(LINEAR_LIST_SHOW_CODE)) {
            listLayout.setLayoutManager(linearLayoutManager);
            listLayout.setAdapter(new LinearItemAdapter(fileList, mListener));
        } else if (codeState.equals(GRID_LIST_SHOW_CODE)) {
            listLayout.setLayoutManager(gridLayoutManager);
            listLayout.setAdapter(new GridItemAdapter(fileList, mListener));
        }
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

    private void setListManager(View view) {
        listLayout = view.findViewById(R.id.list);

        Context context = getContext();
        linearLayoutManager = new LinearLayoutManager(context);
        gridLayoutManager = new GridLayoutManager(context, ItemFragment.GRID_COLUMN);

        // do set adapter in onStart
    }



}

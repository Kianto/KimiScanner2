package com.app.kimiscanner.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.kimiscanner.R;
import com.app.kimiscanner.main.ItemFragment.OnListFragmentInteractionListener;
import com.app.kimiscanner.model.FolderInfo;

import java.util.List;


public class GridItemAdapter extends ItemAdapter {

    public GridItemAdapter(List<FolderInfo> items, OnListFragmentInteractionListener listener) {
        super(items, listener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_grid_item, parent, false);

        return new ViewHolder(view);
    }

}

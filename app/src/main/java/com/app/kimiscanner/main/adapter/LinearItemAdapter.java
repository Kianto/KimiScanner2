package com.app.kimiscanner.main.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.main.ItemFragment.OnListFragmentInteractionListener;
import com.app.kimiscanner.model.FolderInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class LinearItemAdapter extends ItemAdapter {

    public LinearItemAdapter(List<FolderInfo> items, OnListFragmentInteractionListener listener) {
        super(items, listener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_linear_item, parent, false);
        return new ViewHolder(view);
    }

}

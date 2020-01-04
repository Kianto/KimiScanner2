package com.app.kimiscanner.main.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.main.ItemFragment;
import com.app.kimiscanner.model.FolderInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public abstract class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder>  {

    protected final List<FolderInfo> mValues;
    protected final ItemFragment.OnListFragmentInteractionListener mListener;

    public ItemAdapter(List<FolderInfo> items, ItemFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItemView(mValues.get(position));

        holder.mView.setOnClickListener(view -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(holder.folderInfo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImage;
        public final TextView mName;
        public final TextView mSize;
        public final TextView mDate;
        public final TextView mPage;
        public FolderInfo folderInfo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.item_image);
            mName = (TextView) view.findViewById(R.id.item_name);
            mSize = (TextView) view.findViewById(R.id.item_size);
            mDate = (TextView) view.findViewById(R.id.item_date);
            mPage = (TextView) view.findViewById(R.id.item_page);
        }

        public void setItemView(FolderInfo folderInfo) {
            this.folderInfo = folderInfo;

            Glide.with(mView.getContext()).load(folderInfo.getCoverImagePath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .into(mImage);

            mName.setText(folderInfo.folderName);
            if (null != mPage) mPage.setText("Page: " + folderInfo.pageNumber);
            if (null != mDate) mDate.setText("Modified: " + folderInfo.lastModified);
            if (null != mSize) mSize.setText("Size: " + folderInfo.folderSize);
        }

    }



}

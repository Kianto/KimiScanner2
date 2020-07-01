package com.app.kimiscanner.account.cloudview;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.CloudFolderInfo;
import com.app.kimiscanner.account.StorageConnector;
import com.app.kimiscanner.model.FolderInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class RestoreFolderAdapter extends RecyclerView.Adapter<RestoreFolderAdapter.ViewHolder>  {

    protected final List<CloudFolderInfo> mValues;
    protected final SyncFragment.OnListFragmentInteractionListener mListener;

    public RestoreFolderAdapter(List<CloudFolderInfo> items, SyncFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_grid_sync_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItemView(mValues.get(position));

        holder.mView.setOnClickListener(view -> {
            if (null != mListener) {
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
        public final ImageButton mAction;
        public final ProgressBar mProgressBar;
        public final TextView mName;
        public final TextView mPage;
        public CloudFolderInfo folderInfo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.item_image);
            mAction = (ImageButton)view.findViewById(R.id.item_cloud_action_button);
            mProgressBar = (ProgressBar)view.findViewById(R.id.item_cloud_loading);
            mName = (TextView) view.findViewById(R.id.item_name);
            mPage = (TextView) view.findViewById(R.id.item_page);
        }

        public void setItemView(CloudFolderInfo cloudFolderInfo) {
            this.folderInfo = cloudFolderInfo;

            mName.setText(folderInfo.folderName);

            StorageConnector.getInstance().getImageList(folderInfo, new StorageConnector.OnImageListSuccessListener() {
                @Override
                public void onSuccess(CloudFolderInfo folder) {
                    folderInfo = folder;

                    if (null != mPage) mPage.setText("Page: " + folderInfo.pageNumber);

                    StorageConnector.getInstance().getImage(folderInfo.imageRefs.get(0)
                            , new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(mView.getContext()).load(uri)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .skipMemoryCache(true)
                                            .into(mImage);
                                    mProgressBar.setVisibility(View.GONE);
                                }
                            }
                            , new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }
            });

            mAction.setBackgroundResource(R.drawable.button_shape);
            mAction.setImageResource(R.drawable.ic_cloud_download);
            mAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteractionRestore(folderInfo);
                }
            });
        }

    }



}

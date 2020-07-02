package com.app.kimiscanner.account.cloudview;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
    protected final List<FolderInfo> mExamples;
    protected final SyncFragment.OnListFragmentInteractionListener mListener;
    final int FADE_COLOR = 0xA3303030;

    public RestoreFolderAdapter(
            List<CloudFolderInfo> items,
            List<FolderInfo> examples,
            SyncFragment.OnListFragmentInteractionListener listener
    ) {
        mValues = items;
        mExamples = examples;
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
        holder.mView.setOnClickListener(view -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.folderInfo);
            }
        });
        holder.setItemView(mValues.get(position));
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
        public final CardView mCardView;
        public CloudFolderInfo folderInfo;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.item_image);
            mAction = (ImageButton)view.findViewById(R.id.item_cloud_action_button);
            mProgressBar = (ProgressBar)view.findViewById(R.id.item_cloud_loading);
            mName = (TextView) view.findViewById(R.id.item_name);
            mPage = (TextView) view.findViewById(R.id.item_page);
            mCardView = view.findViewById(R.id.item_card);
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
            if (checkExist(folderInfo, mExamples)) {
                mAction.setImageResource(R.drawable.ic_cloud_off);
                mCardView.setForeground(new ColorDrawable(FADE_COLOR));
                mView.setOnClickListener(null);
            } else /* allow backup action */{
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

    boolean checkExist(CloudFolderInfo cloudFolder, List<FolderInfo> folderList) {
        for (FolderInfo folder : folderList) {
            if (cloudFolder.folderName.equals(folder.folderName))
                return true;
        }
        return false;
    }

}

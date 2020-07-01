package com.app.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.account.CloudFolderInfo;
import com.app.kimiscanner.account.StorageConnector;
import com.app.kimiscanner.main.FolderCollector;
import com.app.kimiscanner.model.FolderInfo;
import com.app.kimiscanner.model.FolderInfoChecker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ListImageDialog extends Dialog {

    private FolderInfo folderInfo;
    private CloudFolderInfo cloudFolderInfo;
    private boolean isCloudFolder = false;

    OnBackupAction backupAction;
    OnRestoreAction restoreAction;

    // Init with image links
    public ListImageDialog(Context context, Callback callback) {
        super(context, callback);
    }
    public ListImageDialog(Context context, FolderInfo folderInfo, OnBackupAction callAction) {
        super(context, null);
        this.folderInfo = folderInfo;
        this.backupAction = callAction;

        this.isCloudFolder = false;
    }
    public ListImageDialog(Context context, CloudFolderInfo cloudFolderInfo, OnRestoreAction callAction) {
        super(context, null);
        this.cloudFolderInfo = cloudFolderInfo;
        this.restoreAction = callAction;

        this.isCloudFolder = true;
    }


    public ListImageDialog setList(FolderInfo folderInfo) {
        this.folderInfo = folderInfo;

        this.isCloudFolder = false;
        return this;
    }

    public ListImageDialog setList(CloudFolderInfo cloudFolderInfo) {
        this.cloudFolderInfo = cloudFolderInfo;

        this.isCloudFolder = true;
        return this;
    }

    // === Callback === //
    public interface OnBackupAction {
        void backupFolder(FolderInfo folder);
    }
    public interface OnRestoreAction {
        void restoreCloud(CloudFolderInfo cloudFolder);
    }
    // === Callback === //

    @Override
    public void show() {
        if ((null == folderInfo || folderInfo.filePaths.isEmpty()) && (null == cloudFolderInfo || cloudFolderInfo.imageRefs.isEmpty())) {
            Toast toast = Toast.makeText(context, "There is no image available!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        final View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_list_folder, null);

        // Set list
        final RecyclerView recyclerView = inflate.findViewById(R.id.dialog_list_folder);
        recyclerView.setLayoutManager(new GridLayoutManager(inflate.getContext(), 3));
        recyclerView.setAdapter(
                isCloudFolder
                    ? new ImageShowAdapter(cloudFolderInfo.imageRefs, true)
                    : new ImageShowAdapter(folderInfo.filePaths)
        );

        // Set title folder name
        String folderName = isCloudFolder ? cloudFolderInfo.folderName : folderInfo.folderName;

        TextView title = inflate.findViewById(R.id.dialog_select_all);
        title.setText(folderName);
        title.setVisibility(View.GONE);

        new AlertDialog
                .Builder(context)
                .setTitle(folderName)
                .setView(inflate)
                .setPositiveButton(
                        context.getString(isCloudFolder ? R.string.action_restore : R.string.action_backup)
                        , (dialog, which) -> {
                            if (isCloudFolder) {
                                restoreAction.restoreCloud(cloudFolderInfo);
                            } else {
                                backupAction.backupFolder(folderInfo);
                            }
                            dialog.dismiss();
                        }
                )
                .setNegativeButton(context.getString(R.string.action_close), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    protected static class ImageShowAdapter extends RecyclerView.Adapter<ImageShowAdapter.ViewHolder> {

        private boolean mIsCloudFolder = false;
        private final List<String> mValues;
        private final List<StorageReference> mRefValues;

        ImageShowAdapter(List<String> links) {
            mRefValues = null;
            mValues = links;
            mIsCloudFolder = false;
        }

        ImageShowAdapter(List<StorageReference> links, boolean isCloudFolder) {
            mRefValues = links;
            mValues = null;
            mIsCloudFolder = true;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (mIsCloudFolder) {
                holder.setItemView(position + 1, mRefValues.get(position));
            } else {
                holder.setItemView(position + 1, mValues.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mIsCloudFolder) {
                return mRefValues.size();
            } else {
                return mValues.size();
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final ImageView mImage;
            private final TextView mPage;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImage = (ImageView) view.findViewById(R.id.file_image);
                mPage = (TextView) view.findViewById(R.id.file_page);
            }

            public void setItemView(int index, String uriPath) {
                mPage.setText(String.valueOf(index));
                Glide.with(mView.getContext()).load(uriPath)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true)
                        .into(mImage);
            }

            public void setItemView(int index, StorageReference uriRef) {
                mPage.setText(String.valueOf(index));
                StorageConnector.getInstance().getImage(
                        uriRef
                        , new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(mView.getContext()).load(uri)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .skipMemoryCache(true)
                                        .into(mImage);
                            }
                        }
                        , new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }

        }
    }

}

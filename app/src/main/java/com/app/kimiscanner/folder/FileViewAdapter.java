package com.app.kimiscanner.folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.app.widget.dialog.FolderNameDialog;
import com.app.widget.dialog.OCRTextDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class FileViewAdapter extends RecyclerView.Adapter<FileViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private FolderFragment.OnListFragmentInteractionListener<String> mListener;

    public FileViewAdapter(List<String> itemPaths, FolderFragment.OnListFragmentInteractionListener<String> listener) {
        mValues = itemPaths;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItemView(mValues.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImage;
        public final TextView mNumber;
        public final ImageButton mOCR;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.file_image);
            mNumber = (TextView) view.findViewById(R.id.file_page);
            mOCR = (ImageButton) view.findViewById(R.id.file_ocr_text);
        }

        public void setItemView(String imagePath, int index) {
            Glide.with(mView.getContext()).load(imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(true)
                    .into(mImage);

            mNumber.setText(String.valueOf(index + 1));

            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mListener.onLongListFragmentInteraction(mValues.get(index));
                    return true;
                }
            });

            mOCR.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reviewORCText(mView.getContext(), mValues.get(index));
                }
            });

        }

        private boolean isLoading = false;
        private void reviewORCText(Context context, String imagePath) {
            if (isLoading) return;
            /*else*/ isLoading = true;

            OCRTextDialog renameDialog = new OCRTextDialog(context, new FolderNameDialog.Callback() {
                @Override
                public void onSucceed(Object... newName) {
                    // do nothing
                }

                @Override
                public void onFailure(String error) {
                    // do nothing
                }
            });
            OCRHelper.runTextRecognition(imagePath, new OCRHelper.OnTextRecognizedListener() {
                @Override
                public void onResult(String content) {
                    renameDialog.setContent(content);
                    renameDialog.show();
                    isLoading = false;
                }
            });
        }

    }
}

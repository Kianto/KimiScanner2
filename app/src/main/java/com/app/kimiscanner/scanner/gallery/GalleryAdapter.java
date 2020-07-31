package com.app.kimiscanner.scanner.gallery;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.kimiscanner.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private final List<String> mValues;
    private final GalleryFragment.OnListFragmentInteractionListener mListener;

    public GalleryAdapter(List<String> itemPaths, GalleryFragment.OnListFragmentInteractionListener listener) {
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
        holder.setItemView(mValues.get(position), position + 1);
        holder.mView.setOnClickListener(view -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onListFragmentInteraction(position);
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
        public final TextView mNumber;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImage = (ImageView) view.findViewById(R.id.file_image);
            mNumber = (TextView) view.findViewById(R.id.file_page);
            view.findViewById(R.id.file_ocr_text).setVisibility(View.GONE);
        }

        public void setItemView(String image, int index) {
//            mImage.setImageBitmap(BitmapFactory.decodeFile(image));
//            mImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(mView.getContext()).load(image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(mImage);
            mNumber.setText(String.valueOf(index));
        }

    }
}

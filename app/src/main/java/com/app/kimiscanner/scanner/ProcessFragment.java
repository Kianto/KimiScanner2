package com.app.kimiscanner.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.app.kimiscanner.R;
import com.app.util.Corners;
import com.app.util.FileHelper;
import com.app.util.PhotoProcessor;
import com.app.widget.LoadingRunner;
import com.bumptech.glide.Glide;

import java.util.List;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class ProcessFragment extends ScanFragment {

    private PhotoProcessor photoProcessor;
    private ViewHolder viewHolder;

    boolean isRunning = false;
    private LoadingRunner saveRunner;

    public ProcessFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process, container, false);

        viewHolder = new ViewHolder();
        viewHolder.load(view);

        saveRunner = new SaveLoadingTask(view.findViewById(R.id.process_loading), object -> {
            isRunning = false;
            activityListener.onDoneAllWorkInteraction();
        });

        viewHolder.showImage(photoProcessor.getOriginal());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Bitmap newBitmap = PhotoStore.getInstance().getProcessingBitmap();
        Corners cropPoints = PhotoStore.getInstance().getProcessingCorners();
        if (null == newBitmap) {
            activityListener.onCloseFragmentInteraction(ProcessFragment.this);
            return;
        }
        photoProcessor = new PhotoProcessor(newBitmap, cropPoints);
    }

    private class ViewHolder {
        ImageView addBtn, doneBtn,
                deleteBtn, rotateBtn, cropBtn,
                blackwhiteBtn, grayscaleBtn, colorBtn;
        //ImageView previewLayout;
        ImageViewTouch previewLayout;
        ProgressBar loadingBar;

        public void load(View view) {
            addBtn = view.findViewById(R.id.process_add);
            doneBtn = view.findViewById(R.id.process_done);
            deleteBtn = view.findViewById(R.id.process_delete);
            rotateBtn = view.findViewById(R.id.process_rotate);
            cropBtn = view.findViewById(R.id.process_crop);
            blackwhiteBtn = view.findViewById(R.id.process_blackwhite);
            grayscaleBtn = view.findViewById(R.id.process_grayscale);
            colorBtn = view.findViewById(R.id.process_color);
            previewLayout = view.findViewById(R.id.process_image_view);
            loadingBar = view.findViewById(R.id.process_loading);

            setListener();
        }

        private void setListener() {
            View.OnClickListener listener = getViewListener();
            addBtn.setOnClickListener(listener);
            doneBtn.setOnClickListener(listener);
            deleteBtn.setOnClickListener(listener);
            rotateBtn.setOnClickListener(listener);
            cropBtn.setOnClickListener(listener);
            blackwhiteBtn.setOnClickListener(listener);
            grayscaleBtn.setOnClickListener(listener);
            colorBtn.setOnClickListener(listener);
        }

        public void showImage(Bitmap bitmap) {
            /*Bitmap scale = resize(bitmap, 1920, 1920);
            bitmap.recycle();*/
            previewLayout.setImageDrawable(getBitmapDrawable(bitmap), previewLayout.getDisplayMatrix(), -1.0f, -1.0f);
           /* Glide.with(ProcessFragment.this)
                    .load(bitmap)
                    .into(previewLayout);*/
        }

        private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
            if (maxHeight > 0 && maxWidth > 0) {
                int width = image.getWidth();
                int height = image.getHeight();
                float ratioBitmap = (float) width / (float) height;
                float ratioMax = (float) maxWidth / (float) maxHeight;

                int finalWidth = maxWidth;
                int finalHeight = maxHeight;
                if (ratioMax > ratioBitmap) {
                    finalWidth = (int) ((float)maxHeight * ratioBitmap);
                } else {
                    finalHeight = (int) ((float)maxWidth / ratioBitmap);
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            }
            return image;
        }


        private BitmapDrawable getBitmapDrawable(Bitmap bitmap) {
            return new BitmapDrawable(getResources(), bitmap);
        }

        public Bitmap getShowBitmap() {
            return ((BitmapDrawable) previewLayout.getDrawable()).getBitmap();
        }
    }

    @Override
    protected View.OnClickListener getViewListener() {
        return this::onClick;
    }

    private void onClick(View view) {
        if (isRunning) return;

        switch (view.getId()) {
            case R.id.process_add:
                PhotoStore.getInstance().saveProcessing(viewHolder.getShowBitmap());
                activityListener.onCloseFragmentInteraction(ProcessFragment.this);
                break;

            case R.id.process_done:
                PhotoStore.getInstance().saveProcessing(viewHolder.getShowBitmap());
                isRunning = true;
                saveRunner.execute();
//                activityListener.onDoneAllWorkInteraction();
                break;

            case R.id.process_rotate:
                viewHolder.showImage(photoProcessor.getRotate(viewHolder.getShowBitmap()));
                break;

            case R.id.process_crop:
                activityListener.onProcessFragmentInteraction(false);
                break;

            case R.id.process_delete:
                PhotoStore.getInstance().deleteProcessing();
                activityListener.onCloseFragmentInteraction(ProcessFragment.this);
                break;

            case R.id.process_blackwhite:
                viewHolder.showImage(photoProcessor.getBlackWhite());
                break;

            case R.id.process_grayscale:
                viewHolder.showImage(photoProcessor.getGrayScale());
                break;

            case R.id.process_color:
                viewHolder.showImage(photoProcessor.getOriginal());
                break;
        }
    }

    private void saveAllPhotos() {
        List<Bitmap> fileList = PhotoStore.getInstance().getProcessedPhotos();

        FileHelper.saveBitmapFiles(fileList);

        // Close store and clean work
        PhotoStore.getInstance().clear();
    }

    // === Thread Loading ===
    private class SaveLoadingTask extends LoadingRunner {
        SaveLoadingTask(ProgressBar progressBar, LoadingRunner.LoadingCallback callback) {
            super(progressBar, callback);
        }

        @Override
        protected void doInBackground() {
            saveAllPhotos();
        }

    } // end class SaveLoadingTask

}

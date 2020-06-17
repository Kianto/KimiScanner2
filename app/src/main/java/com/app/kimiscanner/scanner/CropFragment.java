package com.app.kimiscanner.scanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.app.kimiscanner.R;
import com.app.util.Corners;
import com.app.util.PhotoProcessor;
import com.app.widget.CropImageView;

public class CropFragment extends ScanFragment {

    private Bitmap newBitmap;
    private Corners cropPoints;

    private CropImageView cropImageView;
    private PhotoProcessor photoProcessor;
    private ViewHolder viewHolder;

    boolean isCropOn = true;
    boolean isRunning = true;

    public CropFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop, container, false);

        viewHolder = new ViewHolder();
        viewHolder.load(view);

        int[] srcData = cropPoints.cornersToArray();

        showImage(PhotoProcessor.getResizedBitmap(newBitmap, cropPoints.layoutWidth, cropPoints.layoutHeight),
                new Point(srcData[0], srcData[1]),
                new Point(srcData[2], srcData[3]),
                new Point(srcData[4], srcData[5]),
                new Point(srcData[6], srcData[7])
        );

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        newBitmap = PhotoStore.getInstance().getProcessingBitmap();
        cropPoints = PhotoStore.getInstance().getProcessingCorners();
        if (null == newBitmap) {
            activityListener.onCloseFragmentInteraction(CropFragment.this);
            return;
        }

        // TODO: photoCropper
//        photoProcessor = new PhotoProcessor(newBitmap, cropPoints);
    }

    private class ViewHolder {
        ImageView switchBtn, doneBtn, rotateBtn;
        RelativeLayout previewLayout;
        ProgressBar loadingBar;

        public void load(View view) {
            switchBtn = view.findViewById(R.id.crop_switch);
            doneBtn = view.findViewById(R.id.crop_done);
            rotateBtn = view.findViewById(R.id.crop_rotate);
            previewLayout = view.findViewById(R.id.crop_image_layout);
            loadingBar = view.findViewById(R.id.crop_loading);

            setListener();
        }

        private void setListener() {
            View.OnClickListener listener = getViewListener();
            switchBtn.setOnClickListener(listener);
            doneBtn.setOnClickListener(listener);
            rotateBtn.setOnClickListener(listener);
        }

    }

    @Override
    protected View.OnClickListener getViewListener() {
        return this::onClick;
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.crop_switch:
                if (isCropOn) {
                    ((ImageView) view).setImageResource(R.drawable.ic_crop_on);
                    cropImageView.initPoints();
                    isCropOn = false;
                } else {
                    ((ImageView) view).setImageResource(R.drawable.ic_crop_off);
                    cropImageView.updatePoints();
                    isCropOn = true;
                }
                break;

            case R.id.crop_done:
                // TODO: Update crop and degree in newest photo
                PhotoStore.getInstance().updateProcessing(newBitmap, cropImageView.getPoints());
                activityListener.onCloseFragmentInteraction(CropFragment.this);
                break;

            case R.id.crop_rotate:
                break;
        }
    }

    private void showImage(Bitmap showBitmap, Point tl, Point tr, Point br, Point bl) {
        cropImageView = new CropImageView(getContext(), showBitmap, tl, tr, br, bl);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        viewHolder.previewLayout.removeAllViewsInLayout();
        viewHolder.previewLayout.addView(cropImageView, layoutParams);
    }



}

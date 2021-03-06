package com.app.kimiscanner.scanner.gallery;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.app.kimiscanner.R;
import com.app.kimiscanner.scanner.PhotoStore;
import com.app.kimiscanner.scanner.ScanFragment;
import com.app.kimiscanner.scanner.ScanProcessor;
import com.app.util.Corners;
import com.app.util.FileHelper;
import com.app.util.PhotoProcessor;
import com.app.widget.LoadingRunner;


/**
 * A placeholder fragment containing a simple view.
 */
public class GalleryFragment extends ScanFragment {

    private GalleryFragment.OnListFragmentInteractionListener mListener;
    private final int GRID_COLUMN = 3;

    private RecyclerView mRecyclerView;

    boolean isRunning = false;
    private LoadingRunner mSaveRunner;

    public GalleryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRecyclerView = view.findViewById(R.id.gallery_album);

        view.findViewById(R.id.gallery_done_all).setOnClickListener(view1 -> {
            // check if there is no bitmap to save
            if (PhotoStore.getInstance().getCapturedPhotos().isEmpty()) {
                activityListener.onDoneAllWorkInteraction();
                return;
            }
            isRunning = true;
            mSaveRunner.execute();
        });

        mSaveRunner = new SaveLoadingTask(view.findViewById(R.id.gallery_loading), object -> {
            isRunning = false;
            activityListener.onDoneAllWorkInteraction();
        });


        detectEdgeThenCropImages(mRecyclerView);
        displaySelectedBitmaps(mRecyclerView);

        // if there is only one image then process it
        if (0 == PhotoStore.getInstance().size()) {
            preHandle(0);
        }

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GalleryFragment.OnListFragmentInteractionListener) {
            mListener = (GalleryFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    protected View.OnClickListener getViewListener() {
        return null;
    }

    private void detectEdgeThenCropImages(RecyclerView viewLayout) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int delta = 32*2 + 56 + 56;
        int height = displayMetrics.heightPixels - delta;
        int width = displayMetrics.widthPixels;

        for (int i = 0; i < PhotoStore.getInstance().getCapturedPhotos().size(); i++) {
            Bitmap bitmap = PhotoStore.getInstance().getBitmap(i);

            Corners corners = ScanProcessor.getCorners(bitmap);
            double scale = (1.0 * bitmap.getWidth()) / bitmap.getHeight();
            /*
             *         bitmap.getWidth()    viewLayout.getWidth()
             * scale = ------------------ ? ----------------------
             *         bitmap.getHeight()   viewLayout.getHeight()
             * */
            if (scale * height <= width) {
                corners.layoutWidth = (float) (scale * height);
                corners.layoutHeight = height;
                corners.scalePoints(1.0 * height / bitmap.getHeight());
            } else {
                corners.layoutWidth = width;
                corners.layoutHeight = (float) (width / scale);
                corners.scalePoints(1.0 * width / bitmap.getWidth());
            }

            PhotoStore.getInstance().addCorners(corners);
            PhotoStore.getInstance().saveProcessing(new PhotoProcessor(bitmap, corners).getOriginal());

            bitmap.recycle();
        }
    }

    private void displaySelectedBitmaps(RecyclerView recyclerView) {
        Context context = getContext();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, GRID_COLUMN);
        GalleryAdapter adapter = new GalleryAdapter(
                PhotoStore.getInstance().getProcessedPhotos(),
                this::preHandle
        );

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void preHandle(int index) {
        PhotoStore.getInstance().setProcessingIndex(index);

        Corners corners = PhotoStore.getInstance().getProcessingCorners();
        if (0 == corners.layoutHeight || 0 == corners.layoutWidth) {

            Bitmap bitmap = PhotoStore.getInstance().getBitmap(index);
            double scale = (1.0 * bitmap.getWidth()) / bitmap.getHeight();

            /*
             *         bitmap.getWidth()    viewLayout.getWidth()
             * scale = ------------------ ? ----------------------
             *         bitmap.getHeight()   viewLayout.getHeight()
             * */
            if (scale * mRecyclerView.getHeight() <= mRecyclerView.getWidth()) {
                corners.layoutWidth = (float) (scale * mRecyclerView.getHeight());
                corners.layoutHeight = mRecyclerView.getHeight();
                corners.scalePoints(1.0 * mRecyclerView.getHeight() / bitmap.getHeight());
            } else {
                corners.layoutWidth = mRecyclerView.getWidth();
                corners.layoutHeight = (float) (mRecyclerView.getWidth() / scale);
                corners.scalePoints(1.0 * mRecyclerView.getWidth() / bitmap.getWidth());
            }
        }
        mListener.onListFragmentInteraction(index);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(int index);
    }

    public void update() {
        if (PhotoStore.getInstance().getCapturedPhotos().isEmpty()) {
            activityListener.onDoneAllWorkInteraction();
            return;
        }

        mRecyclerView.setAdapter(new GalleryAdapter(
                PhotoStore.getInstance().getProcessedPhotos(),
                mListener
        ));
    }

    // <=== Saving photos ===>
    private void saveAllPhotos() {
        FileHelper.saveAllPhotos(PhotoStore.getInstance().getProcessedPhotos());

        // Close store and clean work
        PhotoStore.getInstance().clear();
    }
    private class SaveLoadingTask extends LoadingRunner {
        SaveLoadingTask(ProgressBar progressBar, LoadingRunner.LoadingCallback callback) {
            super(progressBar, callback);
        }

        @Override
        protected void doInBackground() {
            saveAllPhotos();
        }

    } // end class SaveLoadingTask

    // </=== Saving photos ===/>

}


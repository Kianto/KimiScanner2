package com.app.kimiscanner.scanner.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.kimiscanner.R;
import com.app.kimiscanner.scanner.PhotoStore;
import com.app.kimiscanner.scanner.ScanFragment;
import com.app.kimiscanner.scanner.SomeDetector;
import com.app.util.Corners;

import org.opencv.core.Mat;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraFragment extends ScanFragment {

    private ViewHolder viewHolder;
    private ScanCamera scanCamera;
    private boolean isFlashOn = false;

    public CameraFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        viewHolder = new ViewHolder();
        viewHolder.load(view);

        loadCamera(getContext(), viewHolder.previewLayout);
        loadImageStore();

        return view;
    }

    public void reset() {
        isFinish = false;
        viewHolder.previewLayout.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cameraWidth = displayMetrics.widthPixels;
        cameraHeight = displayMetrics.heightPixels;

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        mCamera = getCameraInstance();
        resumePreview();

        loadImageStore();
    }

    private void loadImageStore() {
        if (PhotoStore.getInstance().hasPhoto()) {
            viewHolder.showImageStore(PhotoStore.getInstance().getProcessingBitmap(), PhotoStore.getInstance().size());
        } else {
            viewHolder.showImageStore(null, 0);
        }
    }

    private class ViewHolder {
        ImageView shootBtn, flashBtn;
        RelativeLayout previewLayout;
        RelativeLayout previewHolder;
        RelativeLayout storeLayout;
        ImageView storeImage;
        TextView storeNumber;

        public void load(View view) {
            shootBtn = view.findViewById(R.id.camera_shoot);
            flashBtn = view.findViewById(R.id.camera_flash);
            previewLayout = view.findViewById(R.id.camera_preview_layout);
            previewHolder = view.findViewById(R.id.camera_preview_holder);
            storeImage = view.findViewById(R.id.camera_store_image);
            storeNumber = view.findViewById(R.id.camera_store_number);
            storeLayout = view.findViewById(R.id.camera_store_layout);

            setListener();
        }

        private void setListener() {
            View.OnClickListener listener = getViewListener();
            shootBtn.setOnClickListener(listener);
            flashBtn.setOnClickListener(listener);
            storeImage.setOnClickListener(listener);
        }

        public void showImageStore(Bitmap image, int number) {
            if (null == image) {
                storeLayout.setVisibility(View.INVISIBLE);
            } else {
                storeImage.setImageBitmap(image);
                storeNumber.setText(String.valueOf(number).concat("x"));

                storeLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected View.OnClickListener getViewListener() {
        return this::onClick;
    }

    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_shoot:
                takePicture();
                break;

            case R.id.camera_flash:
                if (isFlashOn) {
                    ((ImageView) view).setImageResource(R.drawable.ic_flash_off);
                    isFlashOn = false;
                } else {
                    ((ImageView) view).setImageResource(R.drawable.ic_flash_on);
                    isFlashOn = true;
                }
                break;

            case R.id.camera_store_image:
                activityListener.onCameraFragmentInteraction();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        isFinish = false;
        viewHolder.previewLayout.removeAllViews();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        cameraWidth = displayMetrics.widthPixels;
        cameraHeight = displayMetrics.heightPixels;

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        mCamera = getCameraInstance();
        resumePreview();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mCamera) {
            this.mCamera = null;
        }
//        setResult(Activity.RESULT_OK, new Intent(this, MainActivity.class));
    }

    // <@=== Camera handle ===@>
    private int cameraWidth;
    private int cameraHeight;
    private int maxPWidth, maxPHeight;
    private Camera mCamera;
    private boolean isFinish = false;
    private CameraView mPreview;

    private void loadCamera(Context context, ViewGroup viewLayout) {
//        scanCamera = new ScanCamera(context, viewLayout);
//        scanCamera.start();
        // TODO:
    }

    private Camera getCameraInstance() {
        Camera camera;
        try {
            camera = Camera.open();
            if (null == camera) {
                camera = Camera.open(Camera.getNumberOfCameras() - 1);
            }
            setMaxPicSize(camera);
            return camera;

        } catch (Exception e) {
            getActivity().finish();
            return null;
        }
    }

    private void setMaxPicSize(Camera camera) {
        List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
        maxPWidth = 0;
        maxPHeight = 0;
        for (Camera.Size size : sizes) {
            if (maxPWidth < size.width) maxPWidth = size.width;
            if (maxPHeight < size.height) maxPHeight = size.height;
        }
    }

    private void resumePreview() {
        if (!isFinish) {
            mPreview = new CameraView(getActivity(), mCamera, viewHolder.previewLayout, cameraWidth, cameraHeight);

            Camera.Parameters camParameters = mCamera.getParameters();
            if (camParameters.getSupportedFocusModes().contains("auto")) {
                if (camParameters.getSupportedFocusModes().contains("auto")) {
                    camParameters.setFocusMode("auto");
                }

                camParameters.setPictureSize(maxPWidth, maxPHeight);
                mCamera.setParameters(camParameters);
                mPreview.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            Camera.Parameters parameters = mCamera.getParameters();
                            if (parameters.getSupportedFocusModes().contains("auto")) {
                                parameters.setFocusMode("auto");
                            }
                            List supportedFlashModes = parameters.getSupportedFlashModes();
                            if (supportedFlashModes != null) {
                                if (isFlashOn) {
                                    if (supportedFlashModes.contains("on")) {
                                        parameters.setFlashMode("on");
                                    }
                                } else if (supportedFlashModes.contains("off")) {
                                    parameters.setFlashMode("off");
                                }
                            }

                            mCamera.setParameters(parameters);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            viewHolder.shootBtn.setEnabled(true);
            viewHolder.shootBtn.setClickable(true);
        } else {
            if (mCamera != null) {
                mCamera.release();
            }
            mCamera = null;
        }
    }

    private void takePicture() {
        if (mCamera != null) {
            openFlash();

            // TODO: check taking photo one or many times at once

            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        camera.takePicture(null, null, jpegCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        viewHolder.shootBtn.setEnabled(true);
    }

    private void openFlash() {
        Camera.Parameters parameters = mCamera.getParameters();
        List supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes != null) {
            if (this.isFlashOn) {
                if (supportedFlashModes.contains("on")) {
                    parameters.setFlashMode("on");
                }
            } else if (supportedFlashModes.contains("off")) {
                parameters.setFlashMode("off");
            }
        }

        mCamera.setParameters(parameters);
    }

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] bArr, Camera camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();

            getCropImage(bArr);
        }
    };

    public void getCropImage(final byte[] bArr) {
        new Thread(() -> {
            Bitmap nowBitmap = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);

            if (nowBitmap.getWidth() > nowBitmap.getHeight()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90.0f);

                nowBitmap = Bitmap.createBitmap(nowBitmap, 0, 0, nowBitmap.getWidth(), nowBitmap.getHeight(), matrix, true);
            }

            // Put extras
            Corners corners = mPreview.getDetectedCorners();
            if (null == corners) {
                Mat orig = new Mat();
                org.opencv.android.Utils.bitmapToMat(nowBitmap, orig);
                corners = new Corners(SomeDetector.getEdgePoints(orig), orig.size());
            }

            corners.layoutHeight = viewHolder.previewHolder.getHeight();
            corners.layoutWidth = viewHolder.previewHolder.getWidth();

            PhotoStore.getInstance().addBitmap(nowBitmap);
            PhotoStore.getInstance().addCorners(corners);

            activityListener.onCameraFragmentInteraction();
        }).start();
    }


}

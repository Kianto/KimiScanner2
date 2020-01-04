package com.app.kimiscanner.camera;

import android.content.Context;
import android.graphics.Matrix;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.app.kimiscanner.R;

public class ScanCamera implements LifecycleOwner {
    private LifecycleRegistry mLifecycleRegistry;

    public void doOnResume() {
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }

    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }

    private Context context;
    private ViewGroup mPreviewLayout;
    private TextureView textureView;
    Preview preview;
    ImageCapture imageCapture;
    ImageCaptureConfig imageCaptureConfig;

    public ScanCamera(Context context, ViewGroup layout) {
        this.context = context;
        this.mPreviewLayout = layout;


        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);



        textureView = mPreviewLayout.findViewById(R.id.camera_texture);
        textureView.post(this::start);
    }

    private void setView() {

    }

    public void start() {
        PreviewConfig previewConfig =
                new PreviewConfig.Builder()
                        .setLensFacing(CameraX.LensFacing.BACK)
                        .build();
        preview = new Preview(previewConfig);


        ImageAnalysisConfig config =
                new ImageAnalysisConfig.Builder()
                        .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis(config);


        imageCaptureConfig = new ImageCaptureConfig.Builder().build();
        imageCapture = new ImageCapture(imageCaptureConfig);


        preview.setOnPreviewOutputUpdateListener(
                output -> {
                    mPreviewLayout.removeView(textureView);
                    mPreviewLayout.addView(textureView, 0);
                    textureView.setSurfaceTexture(output.getSurfaceTexture());
                    updateTransform();
                }
        );

        CameraX.bindToLifecycle((LifecycleOwner) this, preview, imageCapture, imageAnalysis);

    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        // Compute the center of the view finder
        float centerX = textureView.getWidth() / 2f;
        float centerY = textureView.getHeight() / 2f;

        // Correct preview output to account for display rotation

        int rotationDegrees = (int) textureView.getRotation();
        matrix.postRotate(-rotationDegrees, centerX, centerY);

        // Finally, apply transformations to our TextureView
        textureView.setTransform(matrix);
    }

    public void close() {

    }

}

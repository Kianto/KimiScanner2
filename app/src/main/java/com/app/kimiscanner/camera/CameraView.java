package com.app.kimiscanner.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.util.Corners;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.app.kimiscanner.camera.ImageProcessor.*;

public class CameraView extends SurfaceView implements Callback {

    private Camera mCamera;
    private int displayWidth, displayHeight;
    private Activity activity;
    private SurfaceHolder holder;
    PaperRectangle paperRectangle;
    SurfaceHolder mSurfaceHolder;
    private boolean busy = false;
    private ViewGroup mPreviewLayout;
    private Corners detectedCorners;

    public CameraView(Activity context, Camera camera, ViewGroup layout, Integer displayWidth, Integer displayHeight) {
        super(context);
        this.activity = context;
        this.mCamera = camera;
        this.mPreviewLayout = layout;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        this.holder = null;
        this.holder = getHolder();
        this.holder.setType(3);
        this.holder.addCallback(this);

        paperRectangle = new PaperRectangle(activity);
        SurfaceView surfaceView = new SurfaceView(activity);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        mPreviewLayout.addView(this, layoutParams);
        mPreviewLayout.addView(surfaceView, layoutParams);
        mPreviewLayout.addView(paperRectangle, layoutParams);

        this.mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                initCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                updateCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (null != mCamera) {
                    mCamera.stopPreview();
                    mCamera.setPreviewCallback(null);
                    mCamera.release();
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            initCamera();
            mCamera.setPreviewDisplay(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (null == mCamera) {
            return;
        }
//        try {
//            mCamera.startPreview();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            mCamera.setPreviewCallback(getCameraPreviewCallback());

            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void initCamera() {
        int iWid;
        int iHei;

        Camera.Parameters parameters = this.mCamera.getParameters();
        List supportedPreviewSizes = this.mCamera.getParameters().getSupportedPreviewSizes();
        List supportedPictureSizes = this.mCamera.getParameters().getSupportedPictureSizes();

        int size = supportedPictureSizes.size();
        ArrayList arrayList = new ArrayList();
        for (int i5 = 0; i5 < size; i5++) {
            arrayList.add(supportedPictureSizes.get(i5));
        }
        if (arrayList.size() > 0) {
            Collections.sort(arrayList, new Comparator<Camera.Size>() {
                public int compare(Camera.Size size1, Camera.Size size2) {
                    return (size1.width * size1.height) - (size2.width * size2.height);
                }
            });
            iWid = ((Camera.Size) arrayList.get(arrayList.size() - 1)).width;
            iHei = ((Camera.Size) arrayList.get(arrayList.size() - 1)).height;
        } else {
            iWid = 0;
            iHei = 0;
        }

        if (!(iWid == 0 || iHei == 0)) {
            parameters.setPictureSize(iWid, iHei);
            displayWidth = displayWidth;
            displayHeight = (displayWidth * iWid) / iHei;
        }

        int optWid;
        int optHei;
        if (supportedPreviewSizes != null) {
            Camera.Size optimalPreviewSize = getOptimalPreviewSize(supportedPreviewSizes, displayHeight, displayWidth);
            optWid = optimalPreviewSize.width;
            optHei = optimalPreviewSize.height;
        } else {
            optWid = 0;
            optHei = 0;
        }
        if (!(optWid == 0 || optHei == 0)) {
            parameters.setPreviewSize(optWid, optHei);

            mPreviewLayout.setLayoutParams(new RelativeLayout.LayoutParams(displayWidth, displayHeight));
            this.setLayoutParams(new RelativeLayout.LayoutParams(displayWidth, displayHeight));
        }

        parameters.setPictureFormat(256);
        List supportedFocusModes = parameters.getSupportedFocusModes();
        if (supportedFocusModes.contains("auto")) {
            parameters.setFocusMode("auto");
        }
        if (supportedFocusModes.contains("continuous-picture")) {
            parameters.setFocusMode("continuous-picture");
        }

        int cameraId = Camera.getNumberOfCameras() - 1;
        parameters.setRotation(setCameraDisplayOrientation(activity, cameraId, mCamera));
        mCamera.setParameters(parameters);
    }

    private void updateCamera() {
        if (null == mCamera) {
            return;
        }

        mCamera.stopPreview();
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mCamera.setPreviewCallback(getCameraPreviewCallback());
        mCamera.startPreview();
    }

    private Camera.PreviewCallback getCameraPreviewCallback() {
        return new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, final Camera camera) {
                if (busy) {
                    return;
                }
                Log.i("PreviewCam", "on process start");
                busy = true;

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Scheduler proxySchedule = Schedulers.from(executor);
                Observable.just(data)
                        .observeOn(proxySchedule)
                        .subscribe(new Observer<byte[]>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.i("PreviewCam", "start prepare paper");
                                Camera.Parameters parameters = camera.getParameters();
                                int width = parameters.getPreviewSize().width; // default 1080
                                int height = parameters.getPreviewSize().height; // default 1920
                                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
                                byte[] bytes = out.toByteArray();
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                Mat img = new Mat();
                                Utils.bitmapToMat(bitmap, img);
                                bitmap.recycle();
                                Core.rotate(img, img, Core.ROTATE_90_CLOCKWISE);
                                try {
                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                final Mat finalImg = img;
                                Observable
                                        .create(new ObservableOnSubscribe<Corners>() {
                                            @Override
                                            public void subscribe(ObservableEmitter<Corners> emitter) {
                                                Corners corner = processPicture(finalImg);
                                                busy = false;
                                                if (null != corner) {
                                                    detectedCorners = corner;
                                                    emitter.onNext(corner);
                                                } else {
                                                    emitter.onError(new Throwable("paper not detected"));
                                                }
                                            }
                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Corners>() {
                                            @Override
                                            public void accept(Corners corners) throws Exception {
                                                Log.d("Scan", "Get edges");
                                                paperRectangle.onCornersDetected(corners);
                                            }

                                        }, (new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                Log.d("Scan", "Error get Edges");
                                                paperRectangle.onCornersNotDetected();
                                            }
                                        }));
                            }

                            @Override
                            public void onNext(byte[] bytes) {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        };
    }

    private int setCameraDisplayOrientation(Activity activity, int id, Camera camera) {

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(id, cameraInfo);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            case Surface.ROTATION_0:
                break;
        }

        int orientation;
        if (cameraInfo.facing == 1) {
            orientation = (360 - ((cameraInfo.orientation + degree) % 360)) % 360;
        } else {
            orientation = ((cameraInfo.orientation - degree) + 360) % 360;
        }
        camera.setDisplayOrientation(orientation);
        return orientation;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> list, int height, int width) {
        double dH = (double) height;
        double dW = (double) width;

        double d3 = dH / dW;
        Camera.Size size = null;
        if (list == null) {
            return null;
        }
        double d4 = Double.MAX_VALUE;
        double d5 = Double.MAX_VALUE;
        for (Camera.Size size2 : list) {
            double d6 = (double) size2.width;
            double d7 = (double) size2.height;

            if (Math.abs((d6 / d7) - d3) <= 0.1d && ((double) Math.abs(size2.height - height)) < d5) {
                d5 = (double) Math.abs(size2.height - height);
                size = size2;
            }
        }

        if (size == null) {
            for (Camera.Size size3 : list) {
                if (((double) Math.abs(size3.height - height)) < d4) {
                    d4 = (double) Math.abs(size3.height - height);
                    size = size3;
                }
            }
        }
        return size;
    }

    public Corners getDetectedCorners() {
        return detectedCorners;
    }
}

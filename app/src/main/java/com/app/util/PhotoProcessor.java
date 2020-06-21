package com.app.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.Arrays;
import java.util.List;

public class PhotoProcessor {
    private Bitmap srcImage;
    private Bitmap croppedImage;
    private Corners corners;

    public PhotoProcessor(Bitmap source, Corners corners) {
        this.srcImage = source;
        this.corners = corners;

        this.croppedImage = cropBitmap(source, corners.cornersToArray());

        Log.e("adf", "Adf");
    }

    public Bitmap getOriginal() {
        return croppedImage;
    }

    public Bitmap getGrayScale() {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(croppedImage, srcMat);
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2GRAY);

        Bitmap showBitmap = Bitmap.createBitmap(srcMat.width(), srcMat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcMat, showBitmap);
        srcMat.release();

        return showBitmap;
    }

    public Bitmap getBlackWhite() {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(croppedImage, srcMat);
        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.adaptiveThreshold(srcMat, srcMat, 255.0, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 15.0);

        Bitmap showBitmap = Bitmap.createBitmap(srcMat.width(), srcMat.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(srcMat, showBitmap, true);
        srcMat.release();

        return showBitmap;
    }

    public Bitmap getRotate(Bitmap showBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);
        croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);

        showBitmap = Bitmap.createBitmap(showBitmap, 0, 0, showBitmap.getWidth(), showBitmap.getHeight(), matrix, true);
        return showBitmap;
    }

    public static Bitmap rotate(Bitmap showBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90.0f);

        showBitmap = Bitmap.createBitmap(showBitmap, 0, 0, showBitmap.getWidth(), showBitmap.getHeight(), matrix, true);
        return showBitmap;
    }

    public static Corners rotate(Corners corners) {
//        cornerPoints.

        return corners;
    }

    // <@== Support method ==@>
    private Bitmap cropBitmap(Bitmap bitmap, int[] iArr) {
        float layoutWidth = corners.layoutWidth;
        float layoutHeight = corners.layoutHeight;
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();
        float scale = 0;

        if (imgHeight > layoutHeight || imgWidth > layoutWidth) {
            scale = layoutHeight / imgHeight;
            if (imgWidth * scale > layoutWidth) {
                scale = layoutWidth / imgWidth;
            }
        }
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = (int) (iArr[i] / scale);
        }

        Point point1 = new Point(iArr[0], iArr[1]);
        Point point2 = new Point(iArr[2], iArr[3]);
        Point point3 = new Point(iArr[4], iArr[5]);
        Point point4 = new Point(iArr[6], iArr[7]);
        double minY;
        double maxY;
        double minX;
        double maxX;

        if (point1.y > point2.y) {
            maxY = point1.y; /*>*/
            minY = point2.y;
        } else {
            minY = point1.y; /*<*/
            maxY = point2.y;
        }
        if (minY > point3.y) {
            minY = point3.y;
        }
        if (minY > point4.y) {
            minY = point4.y;
        }
        if (maxY < point3.y) {
            maxY = point3.y;
        }
        if (maxY < point4.y) {
            maxY = point4.y;
        }

        if (point1.x > point2.x) {
            minX = point2.x; /*>*/
            maxX = point1.x;
        } else {
            minX = point1.x; /*<*/
            maxX = point2.x;
        }
        if (minX > point3.x) {
            minX = point3.x;
        }
        if (minX > point4.x) {
            minX = point4.x;
        }
        if (maxX < point3.x) {
            maxX = point3.x;
        }
        if (maxX < point4.x) {
            maxX = point4.x;
        }

        point1 = new Point((double) Math.round((float) iArr[0]), (double) Math.round((float) iArr[1]));
        point2 = new Point((double) Math.round((float) iArr[2]), (double) Math.round((float) iArr[3]));
        point3 = new Point((double) Math.round((float) iArr[4]), (double) Math.round((float) iArr[5]));
        point4 = new Point((double) Math.round((float) iArr[6]), (double) Math.round((float) iArr[7]));

        List<Point> arrayList = Arrays.asList(point1, point2, point3, point4);
        Mat frame = Converters.vector_Point2f_to_Mat(arrayList);

        double deltaX = maxX - minX;
        double deltaY = maxY - minY;
        Point perspectiveTL = new Point(0.0d, 0.0d);
        Point perspectiveTR = new Point(Math.abs(deltaX), 0.0d);
        Point perspectiveBR = new Point(Math.abs(deltaX), Math.abs(deltaY));
        Point perspectiveBL = new Point(0.0d, Math.abs(deltaY));

        List<Point> transPoints = Arrays.asList(perspectiveTL, perspectiveTR, perspectiveBR, perspectiveBL);
        Mat perspective = Imgproc.getPerspectiveTransform(frame, Converters.vector_Point2f_to_Mat(transPoints));

        Mat frameMat = new Mat(bitmap.getHeight(), bitmap.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitmap, frameMat);

        Mat perspectiveMat = new Mat(new Size(Math.abs(deltaX), Math.abs(deltaY)), CvType.CV_8UC4);
        Imgproc.warpPerspective(frameMat, perspectiveMat, perspective, new Size(Math.abs(deltaX), Math.abs(deltaY)));

        Bitmap croppedBitmap = Bitmap.createBitmap(perspectiveMat.cols(), perspectiveMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(perspectiveMat, croppedBitmap);
        return croppedBitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bitmap, float layoutWidth, float layoutHeight){
        int imgWidth = bitmap.getWidth();
        int imgHeight = bitmap.getHeight();

        if (imgHeight > layoutHeight || imgWidth > layoutWidth) {
            float scale = layoutHeight / imgHeight;
            if (imgWidth * scale > layoutWidth) {
                scale = layoutWidth / imgWidth;
            }

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            try {
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, Math.max(1, imgWidth), Math.max(1, imgHeight), matrix, true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return bitmap;
        } else {
            return bitmap;
        }
    }
    // </== Support method ==/>

}

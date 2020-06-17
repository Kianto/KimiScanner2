package com.app.kimiscanner.scanner;

import android.graphics.Bitmap;

import com.app.util.ImageUtils;
import com.app.util.MathUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SomeDetector {

    private static final int THRESHOLD_LEVEL = 2;
    private static final double AREA_LOWER_THRESHOLD = 0.2;
    private static final double AREA_UPPER_THRESHOLD = 0.98;
    private static final double DOWNSCALE_IMAGE_SIZE = 600f;

   /* public Bitmap getScannedBitmap(Bitmap bitmap, float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        PerspectiveTransformation perspective = new PerspectiveTransformation();
        MatOfPoint2f rectangle = new MatOfPoint2f();
        rectangle.fromArray(new Point(x1, y1), new Point(x2, y2), new Point(x3, y3), new Point(x4, y4));
        Mat dstMat = perspective.transform(ImageUtils.bitmapToMat(bitmap), rectangle);
        return ImageUtils.matToBitmap(dstMat);
    }*/

    public static List<Point> getEdgePoints(Mat src) {
        List<Point> points = null;
        try {
            points = getContourEdgePoints(src);

            if(points != null && points.size() == 4){
                points = sortPoints(points);
            }
            else {
                points = createDefaultOutline(src);
            }

        }
        catch (Exception e){
            points = createDefaultOutline(src);
        }
        return points;
    }

    private static List<Point> createDefaultOutline(Mat src){
        List<Point> defaultPoints = new ArrayList<>();
        defaultPoints.add(new Point(0, 0));
        defaultPoints.add(new Point(src.width(), 0));
        defaultPoints.add(new Point(src.width(), src.height()));
        defaultPoints.add(new Point(0, src.height()));

        return defaultPoints;
    }

    public static List<Point> getEdgePoints(Bitmap bitmap) {
        Mat orig = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, orig);
        return getEdgePoints(orig);
    }
    public static List<Point> getContourEdgePoints(Mat src) {
        MatOfPoint2f point2f = getPoint(src);
        if (point2f == null)
            point2f = new MatOfPoint2f();
        List<Point> points = Arrays.asList(point2f.toArray());
        List<Point> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            result.add(new Point(((float) points.get(i).x), ((float) points.get(i).y)));
        }

        return result;
    }

    private static Comparator<MatOfPoint2f> AreaDescendingComparator = new Comparator<MatOfPoint2f>() {
        public int compare(MatOfPoint2f m1, MatOfPoint2f m2) {
            double area1 = Imgproc.contourArea(m1);
            double area2 = Imgproc.contourArea(m2);
            return (int) Math.ceil(area2 - area1);
        }
    };


    public static MatOfPoint2f getPoint(Mat src) {

        // Downscale image for better performance.
        double ratio = DOWNSCALE_IMAGE_SIZE / Math.max(src.width(), src.height());
        Size downscaledSize = new Size(src.width() * ratio, src.height() * ratio);
        Mat downscaled = new Mat(downscaledSize, src.type());
        Imgproc.resize(src, downscaled, downscaledSize);

        List<MatOfPoint2f> rectangles = getPoints(downscaled);
        if (rectangles.size() == 0) {
            return null;
        }
        Collections.sort(rectangles, AreaDescendingComparator);
        MatOfPoint2f largestRectangle = rectangles.get(0);
        MatOfPoint2f result = MathUtils.scaleRectangle(largestRectangle, 1f / ratio);
        return result;
    }

    public static MatOfPoint2f getPoint(Bitmap bitmap) {

        Mat src = ImageUtils.bitmapToMat(bitmap);

        return getPoint(src);
    }

    //public native float[] getPoints(Bitmap bitmap);
    public static List<MatOfPoint2f> getPoints(Mat src) {

        // Blur the image to filter out the noise.
        Mat blurred = new Mat();
        Imgproc.medianBlur(src, blurred, 9);

        // Set up images to use.
        Mat gray0 = new Mat(blurred.size(), CvType.CV_8U);
        Mat gray = new Mat();

        // For Core.mixChannels.
        List<MatOfPoint> contours = new ArrayList<>();
        List<MatOfPoint2f> rectangles = new ArrayList<>();

        List<Mat> sources = new ArrayList<>();
        sources.add(blurred);
        List<Mat> destinations = new ArrayList<>();
        destinations.add(gray0);

        // To filter rectangles by their areas.
        int srcArea = src.rows() * src.cols();

        // Find squares in every color plane of the image.
        for (int c = 0; c < 3; c++) {
            int[] ch = {c, 0};
            MatOfInt fromTo = new MatOfInt(ch);

            Core.mixChannels(sources, destinations, fromTo);

            // Try several threshold levels.
            for (int l = 0; l < THRESHOLD_LEVEL; l++) {
                if (l == 0) {
                    // HACK: Use Canny instead of zero threshold level.
                    // Canny helps to catch squares with gradient shading.
                    // NOTE: No kernel size parameters on Java API.
                    Imgproc.Canny(gray0, gray, 10, 20);

                    // Dilate Canny output to remove potential holes between edge segments.
                    Imgproc.dilate(gray, gray, Mat.ones(new Size(3, 3), 0));
                } else {
                    int threshold = (l + 1) * 255 / THRESHOLD_LEVEL;
                    Imgproc.threshold(gray0, gray, threshold, 255, Imgproc.THRESH_BINARY);
                }

                // Find contours and store them all as a list.
                Imgproc.findContours(gray, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

                for (MatOfPoint contour : contours) {
                    MatOfPoint2f contourFloat = MathUtils.toMatOfPointFloat(contour);
                    double arcLen = Imgproc.arcLength(contourFloat, true) * 0.02;

                    // Approximate polygonal curves.
                    MatOfPoint2f approx = new MatOfPoint2f();
                    Imgproc.approxPolyDP(contourFloat, approx, arcLen, true);

                    if (isRectangle(approx, srcArea)) {
                        rectangles.add(approx);
                    }
                }
            }
        }

        return rectangles;

    }

    private static boolean isRectangle(MatOfPoint2f polygon, int srcArea) {
        MatOfPoint polygonInt = MathUtils.toMatOfPointInt(polygon);

        if (polygon.rows() != 4) {
            return false;
        }

        double area = Math.abs(Imgproc.contourArea(polygon));
        if (area < srcArea * AREA_LOWER_THRESHOLD || area > srcArea * AREA_UPPER_THRESHOLD) {
            return false;
        }

        if (!Imgproc.isContourConvex(polygonInt)) {
            return false;
        }

        // Check if the all angles are more than 72.54 degrees (cos 0.3).
        double maxCosine = 0;
        Point[] approxPoints = polygon.toArray();

        for (int i = 2; i < 5; i++) {
            double cosine = Math.abs(MathUtils.angle(approxPoints[i % 4], approxPoints[i - 2], approxPoints[i - 1]));
            maxCosine = Math.max(cosine, maxCosine);
        }

        return !(maxCosine >= 0.3);
    }

  /*  public static Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap)  {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private static List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        MatOfPoint2f point2f = SomeDetector.getPoint(tempBitmap);
        if (point2f == null)
            point2f = new MatOfPoint2f();
        List<Point> points = Arrays.asList(point2f.toArray());
        List<PointF> result = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            result.add(new PointF(((float) points.get(i).x), ((float) points.get(i).y)));
        }

        return result;

    }*/

    /*private static Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }*/

   /* private static Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = getOrderedPoints(pointFs);
        if (!isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    public static Map<Integer, PointF> getOrderedPoints(List<PointF> points) {

        PointF centerPoint = new PointF();
        int size = points.size();
        for (PointF pointF : points) {
            centerPoint.x += pointF.x / size;
            centerPoint.y += pointF.y / size;
        }
        Map<Integer, PointF> orderedPoints = new HashMap<>();
        for (PointF pointF : points) {
            int index = -1;
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0;
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1;
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2;
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3;
            }
            orderedPoints.put(index, pointF);
        }
        return orderedPoints;
    }

    public static boolean isValidShape(Map<Integer, PointF> pointFMap) {
        return pointFMap.size() == 4;
    }*/

    /*static List<Point> sortPoint(List<Point> points) {
        ArrayList<Double> sumCoordinates = new ArrayList<>();
        ArrayList<Double> minusCoordinates = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            sumCoordinates.add(points.get(i).x + points.get(i).y);
            minusCoordinates.add(points.get(i).x - points.get(i).y);
        }

        Point p0 = points.get(sumCoordinates.indexOf(Collections.min(sumCoordinates)));
        Point p1 = points.get(minusCoordinates.indexOf(Collections.max(minusCoordinates)));
        Point p2 = points.get(sumCoordinates.indexOf(Collections.max(sumCoordinates)));
        Point p3 = points.get(minusCoordinates.indexOf(Collections.min(minusCoordinates)));

        return Arrays.asList(p0, p1, p2, p3);
    }*/

    static public List<Point> sortPoints(List<Point> points) {

        Point centerPoint = new Point();
        int size = points.size();
        for (Point pointF : points) {
            centerPoint.x += pointF.x / size;
            centerPoint.y += pointF.y / size;
        }
        List<Point> orderedPoints = new ArrayList<>();
        Point point0 = null, point1 = null, point2 = null, point3 = null;

        for (Point point : points) {
            if (point.x < centerPoint.x && point.y < centerPoint.y) {
                point0 = point.clone();
            } else if (point.x > centerPoint.x && point.y < centerPoint.y) {
                point1 = point.clone();
            } else if (point.x < centerPoint.x && point.y > centerPoint.y) {
                point3 = point.clone();
            } else if (point.x > centerPoint.x && point.y > centerPoint.y) {
                point2 = point.clone();
            }
            else {
                return null;
            }
        }
        orderedPoints.add(point0);
        orderedPoints.add(point1);
        orderedPoints.add(point2);
        orderedPoints.add(point3);
        return orderedPoints;
    }

}

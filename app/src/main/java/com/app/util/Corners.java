package com.app.util;

import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

public class Corners {
    public List<Point> cornerPoints;
    public Size size;
    public float layoutHeight;
    public float layoutWidth;

    public Corners(List<Point> corners, Size size) {
        this.cornerPoints = corners;
        this.size = size;
    }

    public int[] cornersToArray() {
        if (null == cornerPoints)
            return new int[]{
                    0, 0,
                    (int) layoutWidth, 0,
                    (int) layoutWidth, (int) layoutHeight,
                    0, (int) layoutHeight,
            };
        return new int[]{
                (int) cornerPoints.get(0).x, (int) cornerPoints.get(0).y,
                (int) cornerPoints.get(1).x, (int) cornerPoints.get(1).y,
                (int) cornerPoints.get(2).x, (int) cornerPoints.get(2).y,
                (int) cornerPoints.get(3).x, (int) cornerPoints.get(3).y,
        };
    }

    public void setCornersByArray(int[] arr) {
        cornerPoints = new ArrayList<>();
        cornerPoints.add(new Point(arr[0], arr[1]));
        cornerPoints.add(new Point(arr[2], arr[3]));
        cornerPoints.add(new Point(arr[4], arr[5]));
        cornerPoints.add(new Point(arr[6], arr[7]));
    }

    public void scalePoints(double scale) {
        if (null == cornerPoints) return;
        for (Point point : cornerPoints) {
            point.x = point.x * scale;
            point.y = point.y * scale;
        }
    }

}

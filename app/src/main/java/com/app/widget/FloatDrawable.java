package com.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;

public class FloatDrawable extends Drawable {
    private static final int FACTOR = 3;
    private int RADIUS = 80;
    private int sRADIUS = 25;

    Bitmap bitmap;
    
    private Point top;
    private Point bottom;
    private Point left;
    private Point right;
    private int mCurrentX;
    private int mCurrentY;

    private Paint paint = new Paint();
    private Paint mLinePaint = new Paint();
    private Path mPath = new Path();
    private Matrix matrix = new Matrix();

    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public void setAlpha(int i) {
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public FloatDrawable(Context context, Point point, Point point2, Point point3, Point point4, Bitmap bitmap) {
        this.mLinePaint.setARGB(200, 50, 50, 50);
        this.mLinePaint.setStrokeWidth(1.0f);
        this.mLinePaint.setStyle(Style.STROKE);
        this.mLinePaint.setAntiAlias(true);
        this.mLinePaint.setColor(-1);
        this.mCurrentY = -1;
        this.mCurrentX = -1;
        this.left = point;
        this.right = point3;
        this.top = point2;
        this.bottom = point4;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int min = Math.min(windowManager.getDefaultDisplay().getWidth(), windowManager.getDefaultDisplay().getHeight());
        
        this.RADIUS = min / 10;
        this.sRADIUS = this.RADIUS / 2;
        
        this.bitmap = bitmap;
        this.mPath.addCircle((float) this.RADIUS, (float) this.RADIUS, (float) this.RADIUS, Direction.CW);
        this.matrix.setScale(3.0f, 3.0f);
        this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paint.setStrokeWidth(3.0f);
        this.paint.setStrokeCap(Cap.ROUND);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.STROKE);
        updatePoint();
    }

    public void setPoints(Point point, Point point2, Point point3, Point point4) {
        this.left = point;
        this.right = point3;
        this.top = point2;
        this.bottom = point4;
    }

    public void draw(Canvas canvas) {
        Path path = new Path();
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setStrokeCap(Cap.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(30, 35, 66));
        paint.setStrokeWidth(3.0f);
        
        canvas.drawColor(-2009910477);
        canvas.save();
        
        Path path2 = new Path();
        path2.moveTo((float) this.left.x, (float) this.left.y);
        path2.lineTo((float) this.top.x, (float) this.top.y);
        path2.lineTo((float) this.right.x, (float) this.right.y);
        path2.lineTo((float) this.bottom.x, (float) this.bottom.y);
        path2.lineTo((float) this.left.x, (float) this.left.y);
        path2.close();
        
        canvas.clipPath(path2);
        canvas.drawBitmap(this.bitmap, new Matrix(), null);
        canvas.restore();
        
        paint.setStyle(Style.FILL);
        paint.setColor(Color.rgb(94, 123, 250));
        paint.setAntiAlias(true);
        
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
        canvas.drawCircle((float) this.left.x, (float) this.left.y, (float) this.sRADIUS, paint);
        canvas.drawCircle((float) this.top.x, (float) this.top.y, (float) this.sRADIUS, paint);
        canvas.drawCircle((float) this.bottom.x, (float) this.bottom.y, (float) this.sRADIUS, paint);
        canvas.drawCircle((float) this.right.x, (float) this.right.y, (float) this.sRADIUS, paint);
        
        path.moveTo((float) this.left.x, (float) this.left.y);
        path.lineTo((float) this.top.x, (float) this.top.y);
        path.lineTo((float) this.right.x, (float) this.right.y);
        path.lineTo((float) this.bottom.x, (float) this.bottom.y);
        path.close();
        paint.setStyle(Style.STROKE);
        
        canvas.drawPath(path, paint);
        if (this.mCurrentY != -1 || this.mCurrentX != -1) {
            canvas.save();
            if (this.mCurrentX >= this.bitmap.getWidth() / 2 || this.mCurrentY >= this.bitmap.getWidth() / 2) {
                canvas.translate(10.0f, 10.0f);
            } else {
                canvas.translate((float) ((this.bitmap.getWidth() - (this.RADIUS * 2)) - 10), 10.0f);
            }
            this.paint.setColor(-1);
            this.paint.setStrokeWidth(5.0f);
            canvas.drawPath(this.mPath, this.paint);
            this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.paint.setStrokeWidth(3.0f);
            canvas.clipPath(this.mPath);
            canvas.translate((float) (this.RADIUS - (this.mCurrentX * 3)), (float) (this.RADIUS - (this.mCurrentY * 3)));
            canvas.drawBitmap(this.bitmap, this.matrix, null);
            canvas.restore();
            canvas.save();
            if (this.mCurrentX >= this.bitmap.getWidth() / 2 || this.mCurrentY >= this.bitmap.getWidth() / 2) {
                canvas.translate(10.0f, 10.0f);
            } else {
                canvas.translate((float) ((this.bitmap.getWidth() - (this.RADIUS * 2)) - 10), 10.0f);
            }
            this.paint.setColor(Color.rgb(94, 123, 250));
            this.paint.setStrokeWidth(5.0f);
            canvas.drawPath(this.mPath, this.paint);
            this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.paint.setStrokeWidth(2.0f);
            canvas.restore();
            if (this.mCurrentX >= this.bitmap.getWidth() / 2 || this.mCurrentY >= this.bitmap.getWidth() / 2) {
                Canvas canvas2 = canvas;
                canvas2.drawLine((float) ((this.RADIUS - 15) + 10), (float) (this.RADIUS + 10), (float) (this.RADIUS + 15 + 10), (float) (this.RADIUS + 10), this.paint);
                canvas2.drawLine((float) (this.RADIUS + 10), (float) ((this.RADIUS - 15) + 10), (float) (this.RADIUS + 10), (float) (this.RADIUS + 15 + 10), this.paint);
                return;
            }
            
            canvas.drawLine((float) (((this.bitmap.getWidth() - this.RADIUS) - 10) - 15), (float) (this.RADIUS + 10), (float) (((this.bitmap.getWidth() - this.RADIUS) - 10) + 15), (float) (this.RADIUS + 10), this.paint);
            canvas.drawLine((float) ((this.bitmap.getWidth() - this.RADIUS) - 10), (float) ((this.RADIUS - 15) + 10), (float) ((this.bitmap.getWidth() - this.RADIUS) - 10), (float) (this.RADIUS + 15 + 10), this.paint);
        }
    }

    public void setcurry(int i, int i2) {
        this.mCurrentX = i;
        this.mCurrentY = i2;
    }

    public Point getLeft() {
        return this.left;
    }

    public void setLeft(Point point) {
        this.left = point;
    }

    public Point getTop() {
        return this.top;
    }

    public void setTop(Point point) {
        this.top = point;
    }

    public Point getRight() {
        return this.right;
    }

    public void setRight(Point point) {
        this.right = point;
    }

    public Point getBottom() {
        return this.bottom;
    }

    public void setBottom(Point point) {
        this.bottom = point;
    }

    public void updatePoint() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.left);
        arrayList.add(this.top);
        arrayList.add(this.bottom);
        arrayList.add(this.right);
        
        Point point = (Point) arrayList.get(0);
        int i = 0;
        for (int i2 = 1; i2 < 4; i2++) {
            if (point.y > ((Point) arrayList.get(i2)).y) {
                point = (Point) arrayList.get(i2);
                i = i2;
            }
        }
        arrayList.remove(i);
        Point point2 = (Point) arrayList.get(0);
        int i3 = 0;
        for (int i4 = 1; i4 < 3; i4++) {
            if (point2.y > ((Point) arrayList.get(i4)).y) {
                point2 = (Point) arrayList.get(i4);
                i3 = i4;
            }
        }
        arrayList.remove(i3);
        Point point3 = (Point) arrayList.get(0);
        Point point4 = (Point) arrayList.get(1);
        if (point.x < point2.x) {
            this.left = point;
            this.top = point2;
        } else {
            this.left = point2;
            this.top = point;
        }
        if (point3.x < point4.x) {
            this.bottom = point3;
            this.right = point4;
            return;
        }
        this.bottom = point4;
        this.right = point3;
    }

    public void updatePoint1() {
        new Point();
        if (hasSame(this.left, this.top, this.bottom, this.right) && hasSame(this.bottom, this.right, this.left, this.top)) {
            Point point = this.bottom;
            this.bottom = this.left;
            this.left = point;
        } else if (hasSame(this.left, this.bottom, this.top, this.right) && hasSame(this.top, this.right, this.left, this.bottom)) {
            Point point2 = this.top;
            this.top = this.left;
            this.left = point2;
        }
    }

    public boolean hasSame(Point point, Point point2, Point point3, Point point4) {
        if (point.x == point2.x && point.y == point2.y) {
            return false;
        }
        if (point.x == point2.x) {
            if ((point3.x - point.x) * (point4.x - point.x) >= 0) {
                return false;
            }
            return true;
        } else if (point.y == point2.y) {
            if ((point3.y - point.y) * (point4.y - point.y) >= 0) {
                return false;
            }
            return true;
        } else if (((float) ((((((point2.y - point.y) * point3.x) + (point2.x * point.y)) - (point.x * point2.y)) / (point2.x - point.x)) - point3.y)) * ((float) ((((((point2.y - point.y) * point4.x) + (point2.x * point.y)) - (point.x * point2.y)) / (point2.x - point.x)) - point4.y)) >= 0.0f) {
            return false;
        } else {
            return true;
        }
    }
}

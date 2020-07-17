package com.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CropImageView extends View {
    
    final int EDGE_LT = 1;
    final int EDGE_RT = 2;
    final int EDGE_LB = 3;
    final int EDGE_RB = 4;
    final int EDGE_MOVE_OUT = 6;
    final int EDGE_NONE = 7;

    private int MaxHeight;
    private int MaxWidth;
    private final int STATUS_TOUCH_SINGLE = 1;
    private final int STATUS_TOUCH_MULTI_START = 2;
    private final int STATUS_TOUCH_MULTI_TOUCHING = 3;
    
    private Bitmap bitmap;
    private int currentEdge = 7;
    private Context mContext;
    private FloatDrawable mFloatDrawable;
    private int mStatus = 1;
    private float oldX = 0.0f;
    private float oldY = 0.0f;
    private float oriRationWH = 0.0f;

    private Point left;
    private Point right;
    private Point top;
    private Point bottom;

    public CropImageView(Context context) {
        super(context);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    public CropImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context);
    }

    public CropImageView(Context context, Bitmap bitmap2, Point point, Point point2, Point point3, Point point4) {
        super(context);
        if (bitmap2 != null) {
            init(context);
            this.bitmap = bitmap2;
            if (point.x < 0) {
                point.x = 0;
            } else if (point.x > bitmap2.getWidth()) {
                point.x = bitmap2.getWidth();
            }
            if (point.y < 0) {
                point.y = 0;
            } else if (point.y > bitmap2.getHeight()) {
                point.y = bitmap2.getHeight();
            }
            if (point2.x < 0) {
                point2.x = 0;
            } else if (point2.x > bitmap2.getWidth()) {
                point2.x = bitmap2.getWidth();
            }
            if (point2.y < 0) {
                point2.y = 0;
            } else if (point2.y > bitmap2.getHeight()) {
                point2.y = bitmap2.getHeight();
            }
            if (point3.x < 0) {
                point3.x = 0;
            } else if (point3.x > bitmap2.getWidth()) {
                point3.x = bitmap2.getWidth();
            }
            if (point3.y < 0) {
                point3.y = 0;
            } else if (point3.y > bitmap2.getHeight()) {
                point3.y = bitmap2.getHeight();
            }
            if (point4.x < 0) {
                point4.x = 0;
            } else if (point4.x > bitmap2.getWidth()) {
                point4.x = bitmap2.getWidth();
            }
            if (point4.y < 0) {
                point4.y = 0;
            } else if (point4.y > bitmap2.getHeight()) {
                point4.y = bitmap2.getHeight();
            }
            setLayerType(View.LAYER_TYPE_SOFTWARE/*1*/, null);
            this.left = point;
            this.right = point3;
            this.top = point2;
            this.bottom = point4;
            this.mFloatDrawable = new FloatDrawable(this.mContext, point, point2, point3, point4, bitmap2);
            invalidate();
        }
    }

    private void init(Context context) {
        this.mContext = context;
    }

    public void clear() {
        if (this.bitmap != null && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }
        this.bitmap = null;
        this.mFloatDrawable = null;
    }

    public void setBitmap(Bitmap bitmap2, Point point, Point point2, Point point3, Point point4) {
        this.bitmap = bitmap2;
        this.left = point;
        this.right = point3;
        this.top = point2;
        this.bottom = point4;
        this.mFloatDrawable = new FloatDrawable(this.mContext, point, point2, point3, point4, bitmap2);
        invalidate();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() <= 1) {
            if (this.mStatus == 2 || this.mStatus == 3) {
                this.oldX = motionEvent.getX();
                this.oldY = motionEvent.getY();
            }
            this.mStatus = 1;
        } else if (this.mStatus == 1) {
            this.mStatus = 2;
        } else if (this.mStatus == 2) {
            this.mStatus = 3;
        }
        if (this.mFloatDrawable != null) {
            this.mFloatDrawable.setcurry(-1, -1);
        }
        int action = motionEvent.getAction();
        if (action != 6) {
            switch (action) {
                case 0:
                    this.oldX = motionEvent.getX();
                    this.oldY = motionEvent.getY();
                    this.currentEdge = getTouchEdge((int) this.oldX, (int) this.oldY);
                    break;
                case 1:
                    this.mFloatDrawable.setcurry(-1, -1);
                    this.mFloatDrawable.updatePoint();
                    invalidate();
                    break;
                case 2:
                    if (this.mStatus == 1) {
                        int x = (int) (motionEvent.getX() - this.oldX);
                        int y = (int) (motionEvent.getY() - this.oldY);
                        this.oldX = motionEvent.getX();
                        this.oldY = motionEvent.getY();
                        int width = this.bitmap.getWidth();
                        int height = this.bitmap.getHeight();
                        if (!(x == 0 && y == 0)) {
                            switch (this.currentEdge) {
                                case EDGE_LT:
                                    int i = this.mFloatDrawable.getLeft().x + x;
                                    int i2 = this.mFloatDrawable.getLeft().y + y;
                                    int min = Math.min(width, Math.max(0, i));
                                    int min2 = Math.min(height, Math.max(0, i2));
                                    this.mFloatDrawable.setLeft(new Point(min, min2));
                                    this.mFloatDrawable.setcurry(min, min2);
                                    break;
                                case EDGE_RT:
                                    int i3 = this.mFloatDrawable.getTop().x + x;
                                    int i4 = this.mFloatDrawable.getTop().y + y;
                                    int min3 = Math.min(width, Math.max(0, i3));
                                    int min4 = Math.min(height, Math.max(0, i4));
                                    this.mFloatDrawable.setTop(new Point(min3, min4));
                                    this.mFloatDrawable.setcurry(min3, min4);
                                    break;
                                case EDGE_LB:
                                    int i5 = this.mFloatDrawable.getBottom().x + x;
                                    int i6 = this.mFloatDrawable.getBottom().y + y;
                                    int min5 = Math.min(width, Math.max(0, i5));
                                    int min6 = Math.min(height, Math.max(0, i6));
                                    this.mFloatDrawable.setBottom(new Point(min5, min6));
                                    this.mFloatDrawable.setcurry(min5, min6);
                                    break;
                                case EDGE_RB:
                                    int i7 = this.mFloatDrawable.getRight().x + x;
                                    int i8 = this.mFloatDrawable.getRight().y + y;
                                    int min7 = Math.min(width, Math.max(0, i7));
                                    int min8 = Math.min(height, Math.max(0, i8));
                                    this.mFloatDrawable.setRight(new Point(min7, min8));
                                    this.mFloatDrawable.setcurry(min7, min8);
                                    break;
                            }
                            invalidate();
                            break;
                        }
                    }
            }
        } else {
            this.currentEdge = EDGE_NONE;
            this.mFloatDrawable.setcurry(-1, -1);
            this.mFloatDrawable.updatePoint();
            invalidate();
        }
        return true;
    }

    public int getTouchEdge(int i, int i2) {
        Point point = new Point(i, i2);
        int pointToPoint = getPointToPoint(point, this.mFloatDrawable.getLeft());
        int pointToPoint2 = getPointToPoint(point, this.mFloatDrawable.getTop());
        int pointToPoint3 = getPointToPoint(point, this.mFloatDrawable.getRight());
        int pointToPoint4 = getPointToPoint(point, this.mFloatDrawable.getBottom());

        if (pointToPoint <= pointToPoint2 && pointToPoint <= pointToPoint3 && pointToPoint <= pointToPoint4) {
            return 1;
        }
        if (pointToPoint2 <= pointToPoint && pointToPoint2 <= pointToPoint3 && pointToPoint2 <= pointToPoint4) {
            return 2;
        }
        if (pointToPoint3 > pointToPoint2 || pointToPoint3 > pointToPoint || pointToPoint3 > pointToPoint4) {
            return 3;
        }
        return 4;
    }

    @Override
    public  void onDraw(Canvas canvas) {
        if (this.bitmap != null) {
            try {
                canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, null);
                this.MaxWidth = this.bitmap.getWidth();
                this.MaxHeight = this.bitmap.getHeight();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.mFloatDrawable.draw(canvas);
        }
    }

    public int getPointToPoint(Point point1, Point point2) {
        int abs = Math.abs(point1.x - point2.x);
        int abs2 = Math.abs(point1.y - point2.y);
        return (int) Math.sqrt((double) ((abs * abs) + (abs2 * abs2)));
    }

    @Override
    public void onMeasure(int i, int i2) {
        try {
            if (this.bitmap != null) {
                setMeasuredDimension(this.bitmap.getWidth(), this.bitmap.getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int[] getPoints() {
        this.mFloatDrawable.updatePoint();
        return new int[]{this.mFloatDrawable.getLeft().x, this.mFloatDrawable.getLeft().y, this.mFloatDrawable.getTop().x, this.mFloatDrawable.getTop().y, this.mFloatDrawable.getRight().x, this.mFloatDrawable.getRight().y, this.mFloatDrawable.getBottom().x, this.mFloatDrawable.getBottom().y};
    }

    public void initPoints() {
        this.mFloatDrawable.setLeft(new Point(0, 0));
        this.mFloatDrawable.setTop(new Point(this.bitmap.getWidth(), 0));
        this.mFloatDrawable.setRight(new Point(this.bitmap.getWidth(), this.bitmap.getHeight()));
        this.mFloatDrawable.setBottom(new Point(0, this.bitmap.getHeight()));
        invalidate();
    }

    public void updatePoints() {
        this.mFloatDrawable.setLeft(this.left);
        this.mFloatDrawable.setTop(this.top);
        this.mFloatDrawable.setRight(this.right);
        this.mFloatDrawable.setBottom(this.bottom);
        this.mFloatDrawable.updatePoint();
        invalidate();
    }

    public void setPoint(Point tf, Point tr, Point br, Point bf) {
        this.mFloatDrawable.setLeft(tf);
        this.mFloatDrawable.setTop(tr);
        this.mFloatDrawable.setRight(br);
        this.mFloatDrawable.setBottom(bf);
        invalidate();
    }

    public void refresh() {
        invalidate();
    }

    public double isRect(Point point, Point point2, Point point3) {
        int i = point.x;
        int i2 = point.y;
        int i3 = point2.x;
        int i4 = point2.y;
        int i5 = i - i3;
        int i6 = i2 - i4;
        int i7 = point3.x - i3;
        int i8 = point3.y - i4;
        double d = (double) ((i5 * i7) + (i6 * i8));
        double sqrt = Math.sqrt((double) ((i5 * i5) + (i6 * i6))) * Math.sqrt((double) ((i7 * i7) + (i8 * i8)));
        
        return (Math.acos(d / sqrt) * 180.0d) / Math.PI;
    }

    public ArrayList<Integer> getMax() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(this.MaxWidth);
        arrayList.add(this.MaxHeight);
        return arrayList;
    }
}

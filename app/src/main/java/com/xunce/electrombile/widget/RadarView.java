package com.xunce.electrombile.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by lybvinci on 2015/9/17.
 */
public class RadarView extends FrameLayout {

    private int viewSize = 500;
    private Paint mPaintLine;
    private Paint mPaintSector;
    private boolean isStart = false;
    private ScanThread mThread;
    private int start = 0;

    private final int paintWidth = 10;

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        initPaint();
        // mThread = new ScanThread();
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void initPaint() {
        mPaintLine = new Paint();
        mPaintLine.setStrokeWidth(paintWidth);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStyle(Style.STROKE);
        mPaintLine.setColor(0xffff0000);

        mPaintSector = new Paint();
        mPaintSector.setColor(0x9D00ff00);
        mPaintSector.setAntiAlias(true);
    }

    public void setViewSize(int size) {
        this.viewSize = size;
        mShader = new SweepGradient(viewSize >> 1, viewSize >> 1, Color.TRANSPARENT, Color.GREEN);
        setMeasuredDimension(viewSize, viewSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(viewSize, viewSize);
    }


    public void start() {
        if (mThread == null) {
            mThread = new ScanThread();
            mThread.start();
        }
        mThread.PAUSE = false;
        isStart = true;

    }

    public void destoryThread() {
        if (isStart) {
            isStart = false;
            Thread.interrupted();
        }
    }

    public void stop() {
        if (isStart) {
            mThread.PAUSE = true;
            //isStart = false;
            // Thread.interrupted();
        }
    }

    private Shader mShader = new SweepGradient(viewSize >> 1, viewSize >> 1, Color.TRANSPARENT, Color.GREEN);
    private Matrix matrix = new Matrix();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int halfRadaSize = viewSize >> 1;
        canvas.drawCircle(halfRadaSize, halfRadaSize, halfRadaSize >> 1, mPaintLine);
        canvas.drawCircle(halfRadaSize, halfRadaSize, halfRadaSize - (paintWidth >> 1), mPaintLine);
        canvas.drawLine(halfRadaSize, 0, halfRadaSize, viewSize, mPaintLine);
        canvas.drawLine(0, halfRadaSize, viewSize, halfRadaSize, mPaintLine);
        mPaintSector.setShader(mShader);
        canvas.concat(matrix);
        canvas.drawCircle(halfRadaSize, halfRadaSize, halfRadaSize - paintWidth, mPaintSector);
    }

    protected class ScanThread extends Thread {

        int halfRadaSize = viewSize >> 1;
        public boolean PAUSE = false;

        @Override
        public void run() {
            while (isStart) {
                while (PAUSE) {
                }
                start = start + 2;
                matrix.reset();
                matrix.postRotate(start, halfRadaSize, halfRadaSize);
                postInvalidate();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

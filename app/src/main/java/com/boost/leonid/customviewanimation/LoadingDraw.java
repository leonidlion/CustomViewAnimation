package com.boost.leonid.customviewanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class LoadingDraw extends View {
    private static final String TAG = "LoadingDraw";

    private static final int CIRCLE = 0;
    private static final int SQUARE = 1;
    private static final int BATMAN = 2;

    private static final int DEFAULT_LINE_COLOR = Color.GREEN;
    private static final int DEFAULT_LINE_WIDTH = 2;
    private static final int DEFAULT_ANIMATION_SPEED = 1000;
    private static final int DEFAULT_POINT_FIGURE = SQUARE;
    private static final int DEFAULT_BOX_SIZE = 100;
    private static final int DEFAULT_POIT_SIZE = 10;
    private static final int DEFAULT_POIT_COLOR = Color.GREEN;

    private int mPointColor;
    private int mPointFigure;
    private int mPointSize;
    private int mLineColor;
    private int mLineWidth;
    private int mBoxSize;

    private Paint mFigurePaint;
    private Paint mLinePaint;

    private float[] centerFigureCoord;
    private float[] vertexLeftTop, vertexLeftBottom, vertexRightTop, vertexRightBottom;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initFigure();
    }

    public LoadingDraw(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingDraw, 0, 0);
            setPointColor(a.getInt(R.styleable.LoadingDraw_pointColor, DEFAULT_POIT_COLOR));
            setPointFigure(a.getInt(R.styleable.LoadingDraw_pointFigure, DEFAULT_POINT_FIGURE));
            setPointSize(a.getInt(R.styleable.LoadingDraw_pointSize, DEFAULT_POIT_SIZE));

            setLineColor(a.getInt(R.styleable.LoadingDraw_lineColor, DEFAULT_LINE_COLOR));
            setLineWidth(a.getInt(R.styleable.LoadingDraw_lineWidth, DEFAULT_LINE_WIDTH));
            setBoxSize(a.getInt(R.styleable.LoadingDraw_lineSize, DEFAULT_BOX_SIZE));

            a.recycle();
        }

        mFigurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFigurePaint.setColor(mPointColor);
        mFigurePaint.setStrokeWidth(mPointSize);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);
    }

    private void initFigure(){


        centerFigureCoord = new float[]{
                getWidth() / 2 - mPointSize,  //lx
                getHeight() / 2 - mPointSize, //ly
                getWidth() / 2 + mPointSize , //bx
                getHeight() / 2 + mPointSize}; //by
        vertexLeftTop = new float[]{
                0,
                0,
                mPointSize,
                mPointSize
        };
        vertexLeftBottom = new float[]{
                0,
                getHeight() - mPointSize,
                mPointSize,
                getHeight()
        };
        vertexRightTop = new float[]{
                getWidth() - mPointSize,
                0,
                getWidth(),
                mPointSize
        };
        vertexRightBottom = new float[]{
                getWidth() - mPointSize,
                getHeight() - mPointSize,
                getWidth(),
                getHeight()
        };

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");

        drawFigure(canvas);


        canvas.drawLine(vertexLeftTop[2], vertexLeftTop[3], vertexRightBottom[0], vertexRightBottom[1], mLinePaint);
        canvas.drawLine(vertexLeftBottom[2], vertexLeftBottom[3] - mPointSize, vertexRightTop[0], vertexRightTop[1] + mPointSize, mLinePaint);

        canvas.drawLine(vertexLeftTop[2] - mPointSize / 2, vertexLeftTop[3], vertexLeftBottom[0] + mPointSize /2, vertexLeftBottom[1], mLinePaint);
        canvas.drawLine(vertexLeftTop[0] + mPointSize, vertexLeftTop[1] + mPointSize / 2, vertexRightTop[0], vertexRightTop[1] + mPointSize / 2, mLinePaint);
        canvas.drawLine(vertexRightTop[2] - mPointSize / 2, vertexRightTop[3], vertexRightBottom[0] + mPointSize / 2, vertexRightBottom[1], mLinePaint);
        canvas.drawLine(vertexLeftBottom[2], vertexLeftBottom[3] - mPointSize / 2, vertexRightBottom[0], vertexRightBottom[1] + mPointSize / 2, mLinePaint);

    }

    private void drawFigure(Canvas canvas) {
        switch (mPointFigure){
            case SQUARE:
                canvas.drawRect(centerFigureCoord[0], centerFigureCoord[1],centerFigureCoord[2],centerFigureCoord[3], mFigurePaint);
                canvas.drawRect(vertexLeftTop[0], vertexLeftTop[1],vertexLeftTop[2],vertexLeftTop[3],mFigurePaint);
                canvas.drawRect(vertexLeftBottom[0], vertexLeftBottom[1],vertexLeftBottom[2],vertexLeftBottom[3],mFigurePaint);
                canvas.drawRect(vertexRightTop[0], vertexRightTop[1],vertexRightTop[2],vertexRightTop[3],mFigurePaint);
                canvas.drawRect(vertexRightBottom[0], vertexRightBottom[1],vertexRightBottom[2],vertexRightBottom[3],mFigurePaint);
                break;
            case CIRCLE:
                break;
            case BATMAN:
                break;
        }
    }

    public void setPointColor(int pointColor) {
        mPointColor = pointColor;
        invalidate();
        requestLayout();
    }

    public void setPointFigure(int pointFigure) {
        mPointFigure = pointFigure;
    }

    public void setPointSize(int pointSize) {
        mPointSize = pointSize;
        invalidate();
        requestLayout();
    }

    public void setLineColor(int lineColor) {
        mLineColor = lineColor;
        invalidate();
        requestLayout();
    }

    public void setLineWidth(int lineWidth) {
        mLineWidth = lineWidth;
        invalidate();
        requestLayout();
    }

    public void setBoxSize(int boxSize) {
        mBoxSize = boxSize;
        invalidate();
        requestLayout();
    }
}

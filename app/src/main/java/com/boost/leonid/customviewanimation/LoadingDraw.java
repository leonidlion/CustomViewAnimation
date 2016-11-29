package com.boost.leonid.customviewanimation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

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

    private List<VertexHolder> mVertexHolders;
    private VertexHolder mCenter;


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mVertexHolders = initFigure();
        initPaint();
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
    }

    private void initPaint(){
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);

        mFigurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFigurePaint.setColor(mPointColor);
        mFigurePaint.setStrokeWidth(mPointSize);

    }



    private List<VertexHolder> initFigure(){
        List<VertexHolder> vertexHolderList = new ArrayList<>();

        VertexHolder vertexHolder;

        float halfFigureSize = mPointSize / 2;
        float halfWidth = getWidth() / 2;
        float halfHeight = getHeight() / 2;

        mCenter = new VertexHolder(halfWidth - mPointSize, halfHeight - mPointSize, mPointSize * 2, mPointSize * 2);

        //Left top
        vertexHolder = new VertexHolder(0,0,mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),      //start position
                new Pair<>(vertexHolder.getLeft(), halfHeight - halfFigureSize),//2-st position in half
                new Pair<>(halfWidth, halfHeight));                             //end position in center
        vertexHolderList.add(vertexHolder);

        //Right top
        vertexHolder = new VertexHolder(getWidth() - mPointSize, 0, mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new Pair<>(vertexHolder.getLeft(), halfHeight - halfFigureSize),//2-st position in half
                new Pair<>(halfWidth, halfHeight));//end position in center
        vertexHolderList.add(vertexHolder);

        //Left bottom
        vertexHolder = new VertexHolder(0, getHeight() - mPointSize,mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new Pair<>(halfWidth - halfFigureSize, vertexHolder.getTop()),//2-st position in half
                new Pair<>(halfWidth, halfHeight));//end position in center
        vertexHolderList.add(vertexHolder);

        //Right bottom
        vertexHolder = new VertexHolder(getWidth() - mPointSize, getHeight() - mPointSize,mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new Pair<>(vertexHolder.getLeft(), halfHeight - halfFigureSize),//2-st position in half
                new Pair<>(halfWidth, halfHeight));//end position in center
        vertexHolderList.add(vertexHolder);

        return vertexHolderList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");

        for (int i = 0; i < mVertexHolders.size(); i++){
            VertexHolder vertexHolder = mVertexHolders.get(i);
            for (int j = i + 1; j < mVertexHolders.size(); j++){
                VertexHolder vertexHolderTo = mVertexHolders.get(j);
                canvas.drawLine(vertexHolder.getCenterX(), vertexHolder.getCenterY(), vertexHolderTo.getCenterX(), vertexHolderTo.getCenterY(), mLinePaint);
            }
            drawFigure(canvas, vertexHolder);
        }
        drawFigure(canvas, mCenter);
    }

    private void drawFigure(Canvas canvas, VertexHolder x) {
        switch (mPointFigure){
            case SQUARE:
                canvas.drawRect(x.getLeft(), x.getTop(), x.getRight(), x.getBottom(), mFigurePaint);
                break;
            case CIRCLE:
                canvas.drawCircle(x.getCenterX(), x.getCenterY(), x.getWidth() / 2, mFigurePaint);
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

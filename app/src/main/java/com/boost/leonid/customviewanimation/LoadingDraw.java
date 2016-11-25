package com.boost.leonid.customviewanimation;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class LoadingDraw extends View {
    private static final int CIRCLE = 0;
    private static final int SQUARE = 1;
    private static final int BATMAN = 2;

    private int lineSize;
    private int lineWidth;
    private int pointSize;

    private Paint mPaintLine;
    private Paint mPaintFigure;
    private Paint mPaintCenterFigure;
    private Rect mRect;



    public LoadingDraw(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingDraw, 0, 0);

        try {
            lineSize = a.getInt(R.styleable.LoadingDraw_lineSize, 0);
            lineWidth = a.getInt(R.styleable.LoadingDraw_lineWidth, 0);

            mPaintLine = new Paint();
            mPaintLine.setColor(a.getColor(R.styleable.LoadingDraw_lineColor, 0));
            mPaintLine.setStrokeWidth(lineWidth);

            pointSize = a.getInt(R.styleable.LoadingDraw_pointSize, 10);
            switch (a.getInt(R.styleable.LoadingDraw_pointFigure, SQUARE)){
                case CIRCLE:
                    break;
                case SQUARE:
                    mRect = new Rect(0, 0, pointSize, pointSize);
                    break;
                case BATMAN:
                    break;
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(mRect, mPaintLine);
    }
}

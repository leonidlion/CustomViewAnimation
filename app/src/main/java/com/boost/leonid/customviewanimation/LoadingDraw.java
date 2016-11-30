package com.boost.leonid.customviewanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class LoadingDraw extends View {
    private static final String TAG = "LoadingDraw";

    private static final int CIRCLE = 0;
    private static final int SQUARE = 1;
    private static final int BITMAP = 2;

    private static final int CENTER = 0;
    private static final int LINES = 1;

    private static final int DEFAULT_LINE_COLOR = Color.BLACK;
    private static final int DEFAULT_LINE_WIDTH = 5;
    private static final int DEFAULT_ANIMATION_SPEED = 900;
    private static final int DEFAULT_POINT_FIGURE = SQUARE;
    private static final int DEFAULT_POINT_SIZE = 20;
    private static final int DEFAULT_POINT_COLOR = Color.GREEN;
    private static final int DEFAULT_BLINK_ELEMENT = CENTER;

    private int mPointColor;
    private int mPointFigure;
    private int mPointSize;
    private int mLineColor;
    private int mLineWidth;
    private int mBlinkElement;

    private Paint mFigurePaint;
    private Paint mLinePaint;

    private Paint mCenterPaint;
    private List<VertexHolder> mVertexHolders;

    private VertexHolder mCenter;
    private AnimationHelper mAnimationHelper;
    private int mDuration;
    private Bitmap mBitmap;

    public LoadingDraw(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingDraw, 0, 0);
            setPointColor(a.getInt(R.styleable.LoadingDraw_pointColor, DEFAULT_POINT_COLOR));
            setPointFigure(a.getInt(R.styleable.LoadingDraw_pointFigure, DEFAULT_POINT_FIGURE));
            setPointSize(a.getInt(R.styleable.LoadingDraw_pointSize, DEFAULT_POINT_SIZE));
            setDuration(a.getInt(R.styleable.LoadingDraw_animationSpeed, DEFAULT_ANIMATION_SPEED));
            setLineColor(a.getInt(R.styleable.LoadingDraw_lineColor, DEFAULT_LINE_COLOR));
            setLineWidth(a.getInt(R.styleable.LoadingDraw_lineWidth, DEFAULT_LINE_WIDTH));
            setBlinkElement(a.getInt(R.styleable.LoadingDraw_setBlinkElement, DEFAULT_BLINK_ELEMENT));
            a.recycle();
        }
    }
    private Paint getBlinkPaint(){
        switch (mBlinkElement){
            case CENTER:
                return mCenterPaint;
            case LINES:
                return mLinePaint;
            default: return mCenterPaint;
        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        initPaint();

        mCenter = new VertexHolder(
                getWidth() / 2 - mPointSize, getHeight() / 2 - mPointSize,
                mPointSize * 2, mPointSize * 2);

        ObjectAnimator alphaAnimation = ObjectAnimator.ofInt(getBlinkPaint(), "alpha", 100, 200);
        alphaAnimation.setDuration(mDuration / 2);
        alphaAnimation.setRepeatCount(6);
        alphaAnimation.setRepeatMode(ValueAnimator.REVERSE);

        mVertexHolders = getVertexList();

        List<AnimatorSet> animationTo = new ArrayList<>();
        List<AnimatorSet> animationFrom = new ArrayList<>();

        for (VertexHolder x : mVertexHolders) {
            animationTo.add(buildVertexPositionForward(x));
            animationFrom.add(buildVertexPositionBackward(x));
        }

        if (mAnimationHelper != null){
            mAnimationHelper.stopAnimation();
        }
        mAnimationHelper = new AnimationHelper(animationFrom, animationTo, alphaAnimation);
        mAnimationHelper.startAnimation();
    }
    private void initPaint(){
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);

        mFigurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFigurePaint.setColor(mPointColor);
        mFigurePaint.setStrokeWidth(mPointSize);

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(mPointColor);
        mCenterPaint.setStrokeWidth(mPointSize);
    }
    private AnimatorSet buildVertexPositionForward(VertexHolder vertex) {
        Log.d(TAG, "buildForward");
        AnimatorSet result = new AnimatorSet();

        List<ObjectAnimator> traceX = new ArrayList<>();
        List<ObjectAnimator> traceY = new ArrayList<>();

        Pair<Float, Float> pos0 = vertex.getNextVertexPosition().get(0);

        for (int i = 1; i < vertex.getNextVertexPosition().size(); i++) {
            Pair<Float, Float> pos = vertex.getNextVertexPosition().get(i);

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(vertex, "left", pos0.first, pos.first);
            animatorX.setDuration(mDuration);

            ObjectAnimator animatorY = ObjectAnimator.ofFloat(vertex, "top", pos0.second, pos.second);
            animatorY.setDuration(mDuration);

            traceX.add(animatorX);
            traceY.add(animatorY);
            pos0 = pos;
        }
        result.play(traceX.get(0)).with(traceY.get(0));
        for (int i = 1; i < traceX.size(); i++) {
            result.play(traceX.get(i)).with(traceY.get(i));
            result.play(traceX.get(i)).after(traceX.get(i - 1));
        }
        return result;
    }

    private AnimatorSet buildVertexPositionBackward(VertexHolder vertex) {
        Log.d(TAG, "buildBack");
        AnimatorSet result = new AnimatorSet();

        List<ObjectAnimator> traceX = new ArrayList<>();
        List<ObjectAnimator> traceY = new ArrayList<>();

        List<Pair<Float, Float>> trackPoints = vertex.getNextVertexPosition();

        Pair<Float, Float> posN = trackPoints.get(trackPoints.size() - 1);

        for (int i = trackPoints.size() - 2; i >= 0; i--) {
            Pair<Float, Float> pos = trackPoints.get(i);

            ObjectAnimator animatorX = ObjectAnimator.ofFloat(vertex, "left", posN.first, pos.first);
            animatorX.setDuration(mDuration);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(vertex, "top", posN.second, pos.second);
            animatorY.setDuration(mDuration);

            traceX.add(animatorX);
            traceY.add(animatorY);
            posN = pos;
        }

        result.play(traceX.get(0)).with(traceY.get(0));
        for (int i = 1; i < traceX.size(); i++) {
            result.play(traceX.get(i)).with(traceY.get(i));
            result.play(traceX.get(i)).after(traceX.get(i - 1));
        }
        return result;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        if (mAnimationHelper.isCollapsed) {
            VertexHolder vertexHolder = mVertexHolders.get(0);
            drawFigure(canvas, vertexHolder, mFigurePaint);
        } else {
            for (int i = 0; i < mVertexHolders.size(); i++) {
                VertexHolder vertexHolderFrom = mVertexHolders.get(i); // save vertex from write line
                for (int j = i + 1; j < mVertexHolders.size(); j++) {
                    VertexHolder vertexHolderTo = mVertexHolders.get(j);// save vertex line end
                    canvas.drawLine(
                            vertexHolderFrom.getCenterX(), vertexHolderFrom.getCenterY(),
                            vertexHolderTo.getCenterX(), vertexHolderTo.getCenterY(),
                            mLinePaint);
                }
                drawFigure(canvas, vertexHolderFrom, mFigurePaint);
            }
            drawFigure(canvas, mCenter, mCenterPaint);
        }
    }
    private void drawFigure(Canvas canvas, VertexHolder x, Paint paint) {
        switch (mPointFigure){
            case SQUARE:
                canvas.drawRect(x.getLeft(), x.getTop(), x.getRight(), x.getBottom(), paint);
                break;
            case CIRCLE:
                canvas.drawCircle(x.getCenterX(), x.getCenterY(), x.getWidth() / 2, paint);
                break;
            case BITMAP:
                canvas.drawBitmap(
                        mBitmap,
                        null,
                        new Rect(
                                (int)x.getLeft(),
                                (int)x.getTop(),
                                (int)x.getRight(),
                                (int)x.getBottom()),
                        paint);
                break;
        }
    }
    private List<VertexHolder> getVertexList(){
        List<VertexHolder> vertexHolderList = new ArrayList<>();

        VertexHolder vertexHolder;

        float halfFigureSize = mPointSize / 2;
        float halfWidth = getWidth() / 2;
        float halfHeight = getHeight() / 2;

        //Left top
        vertexHolder = new VertexHolder(0,0,mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),      //start position
                new Pair<>(vertexHolder.getLeft(), halfHeight - halfFigureSize),//2-st position in half
                new Pair<>(halfWidth - mPointSize /2, halfHeight - mPointSize/2));//end position in center
        vertexHolderList.add(vertexHolder);

        //Right top
        vertexHolder = new VertexHolder(getWidth() - mPointSize, 0, mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new Pair<>(vertexHolder.getLeft(), halfHeight - halfFigureSize),//2-st position in half
                new Pair<>(halfWidth - mPointSize /2, halfHeight - mPointSize/2));//end position in center
        vertexHolderList.add(vertexHolder);

        //Left bottom
        vertexHolder = new VertexHolder(0, getHeight() - mPointSize,mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new Pair<>(halfWidth - halfFigureSize, vertexHolder.getTop()),//2-st position in half
                new Pair<>(halfWidth - mPointSize /2, halfHeight - mPointSize/2));//end position in center
        vertexHolderList.add(vertexHolder);

        //Right bottom
        vertexHolder = new VertexHolder(getWidth() - mPointSize, getHeight() - mPointSize,mPointSize, mPointSize);
        vertexHolder.addVertexPosition(
                new Pair<>(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new Pair<>(vertexHolder.getLeft(), halfHeight - halfFigureSize),//2-st position in half
                new Pair<>(halfWidth - mPointSize /2, halfHeight - mPointSize/2));//end position in center
        vertexHolderList.add(vertexHolder);

        return vertexHolderList;
    }

    private class AnimationHelper {
        private final Handler mHandler = new Handler();
        private final Runnable invalidateRunnable;
        private List<AnimatorSet> mAnimationFrom, mAnimationTo;
        private ObjectAnimator mObjectAnimator;
        private Paint mAlphaPaint;
        private boolean isCollapsed, isStart;

        AnimationHelper(List<AnimatorSet> animationFrom, List<AnimatorSet> animationTo, ObjectAnimator objectAnimator) {
            mAnimationFrom = animationFrom;
            mAnimationTo = animationTo;
            mObjectAnimator = objectAnimator;
            mAlphaPaint = (Paint) mObjectAnimator.getTarget();

            mObjectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isStart) {
                        if (isCollapsed) {
                            isCollapsed = false;
                            startAnimationFrom(mDuration / 2);
                        } else {
                            startAnimationTo(mDuration / 2);
                        }
                    }
                }
            });
            invalidateRunnable = new Runnable() {
                @Override
                public void run() {
                    invalidate();
                    mHandler.postDelayed(this, 10);
                }
            };
        }


        private void startAnimationTo(int delay) {
            mAlphaPaint.setAlpha(0);
            for (int i = 0; i < mAnimationTo.size(); i++) {
                AnimatorSet anim = mAnimationTo.get(i);
                anim.setStartDelay(i*delay);
                anim.start();
            }

            mAnimationTo.get(mAnimationTo.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isStart) {
                        isCollapsed = true;
                        mObjectAnimator.start();
                    }
                }
            });
        }

        private void startAnimationFrom(int delay) {
            mAlphaPaint.setAlpha(0);
            for (int i = 0; i < mAnimationFrom.size(); i++) {
                AnimatorSet anim = mAnimationFrom.get(i);
                anim.setStartDelay(i*delay);
                anim.start();
            }

            mAnimationFrom.get(mAnimationFrom.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isStart) {
                        isCollapsed = false;
                        mObjectAnimator.start();
                    }
                }
            });
        }
        void startAnimation() {
            invalidateRunnable.run();
            mObjectAnimator.start();
            isStart = true;
        }

        void stopAnimation(){
            isStart = false;
            mHandler.removeCallbacks(invalidateRunnable);
            for (AnimatorSet anim: mAnimationTo) {
                anim.cancel();
            }
            for (AnimatorSet anim: mAnimationFrom) {
                anim.cancel();
            }
            mObjectAnimator.cancel();
        }
    }
    /**
     *
     *  Getters and Setters
     */
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
    public void setDuration(int duration) {
        mDuration = duration;
        invalidate();
        requestLayout();
    }
    public void setBitmap(int bitmap) {
        mBitmap = BitmapFactory.decodeResource(getResources(), bitmap);
        setPointFigure(BITMAP);
    }
    public void setBlinkElement(int blinkElement) {
        mBlinkElement = blinkElement;
        invalidate();
        requestLayout();
    }
}
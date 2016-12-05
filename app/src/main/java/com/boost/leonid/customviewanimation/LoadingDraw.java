package com.boost.leonid.customviewanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingDraw extends View {
    private static final String TAG = "LoadingDraw";

    private static final int FIGURE_CIRCLE = 0;
    private static final int FIGURE_SQUARE = 1;
    private static final int FIGURE_BITMAP = 2;

    private static final int DEFAULT_LINE_COLOR = Color.BLACK;
    private static final int DEFAULT_LINE_WIDTH = 5;
    private static final int DEFAULT_ANIMATION_SPEED = 900;
    private static final int DEFAULT_POINT_FIGURE = FIGURE_SQUARE;
    private static final int DEFAULT_POINT_COLOR = Color.GREEN;
    private static final int MIN_SIZE_VIEW = 200;

    private int mPointColor;
    private int mPointFigure;
    private int mLineColor;
    private int mLineWidth;
    private int mDuration;

    private float mItemWidth;
    private float mItemHeight;

    private Paint mFigurePaint;
    private Paint mLinePaint;
    private Paint mCenterPaint;

    private List<VertexHolder> mVertexHolders;
    private VertexHolder mCenter;
    private AnimationHelper mAnimationHelper;
    private Bitmap mBitmap;
    private AnimationCompleteListener mCallback;

    /**
     * Listener for complete
     */
    public interface AnimationCompleteListener {
        void onComplete(boolean complete);
    }

    /**
     * Getters and Setters
     */
    public void setAnimationListener(AnimationCompleteListener listener){
        mCallback = listener;
    }
    public void pause(){
        Log.d(TAG, "pause");
        mAnimationHelper.pauseAnimation();
    }
    public void resume(){
        Log.d(TAG, "resume");
        mAnimationHelper.resumeAnimation();
    }
    public void setPointColor(int pointColor) {
        mPointColor = pointColor;
        invalidate();
        requestLayout();
    }
    public void setPointFigure(int pointFigure) {
        mPointFigure = pointFigure;
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
        initAnimation();
    }
    public void setBitmap(int bitmap) {
        mBitmap = BitmapFactory.decodeResource(getResources(), bitmap);
        setPointFigure(FIGURE_BITMAP);
    }
    public void setItemWidth(float itemWidth) {
        mItemWidth = itemWidth;
        invalidate();
        requestLayout();
    }
    public void setItemHeight(float itemHeight) {
        mItemHeight = itemHeight;
        invalidate();
        requestLayout();
    }
    public int getDuration() {
        return mDuration;
    }
    public float getItemWidth() {
        return mItemWidth;
    }
    public float getItemHeight() {
        return mItemHeight;
    }

    public LoadingDraw(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingDraw, 0, 0);
            setPointColor(a.getInt(R.styleable.LoadingDraw_pointColor, DEFAULT_POINT_COLOR));
            setPointFigure(a.getInt(R.styleable.LoadingDraw_pointFigure, DEFAULT_POINT_FIGURE));
            setDuration(a.getInt(R.styleable.LoadingDraw_animationSpeed, DEFAULT_ANIMATION_SPEED));
            setLineColor(a.getInt(R.styleable.LoadingDraw_lineColor, DEFAULT_LINE_COLOR));
            setLineWidth(a.getInt(R.styleable.LoadingDraw_lineWidth, DEFAULT_LINE_WIDTH));
            setItemWidth(a.getDimension(R.styleable.LoadingDraw_itemWidth,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())));
            setItemHeight(a.getDimension(R.styleable.LoadingDraw_itemHeight,
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics())));
            a.recycle();
        }
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = resolveSize(MIN_SIZE_VIEW, widthMeasureSpec);
        int h = resolveSize(MIN_SIZE_VIEW, heightMeasureSpec);

        int size = w > h ? h : w;

        setMeasuredDimension(size, size);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initAnimation();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(mLineWidth);

        mFigurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFigurePaint.setColor(mPointColor);
        mFigurePaint.setStrokeWidth(5);

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(mPointColor);
        mCenterPaint.setStrokeWidth(5);
    }

    private void initAnimation() {
        mCenter = new VertexHolder(
                getWidth() / 2 - mItemWidth * 2, getHeight() / 2 - mItemHeight * 2,
                mItemWidth * 4, mItemHeight * 4);

        ObjectAnimator alphaAnimation = ObjectAnimator.ofInt(mCenterPaint, "alpha", 100, 200);
        alphaAnimation.setDuration(mDuration / 2);
        alphaAnimation.setRepeatCount(5);
        alphaAnimation.setRepeatMode(ValueAnimator.REVERSE);

        mVertexHolders = getVertexList();

        List<ValueAnimator> animationTo = new ArrayList<>();
        List<ValueAnimator> animationFrom = new ArrayList<>();

        for (VertexHolder x : mVertexHolders) {
            animationTo.add(buildVertexPositionForward(x));
            animationFrom.add(buildVertexPositionBackward(x));
        }

        if (mAnimationHelper != null) {
            mAnimationHelper.stopAnimation();
        }
        mAnimationHelper = new AnimationHelper(animationFrom, animationTo, alphaAnimation);
        mAnimationHelper.startAnimation();
    }

    private ValueAnimator buildVertexPositionForward(final VertexHolder vertex) {
        List<PointF> pointTracks = vertex.getNextVertexPosition();

        final Path path = new Path();

        path.moveTo(pointTracks.get(0).x, pointTracks.get(0).y); // goto first position
        for (int i = 1; i < pointTracks.size(); i++) {
            path.lineTo(pointTracks.get(i).x, pointTracks.get(i).y);
        }

        ValueAnimator pathAnimator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pathAnimator = ObjectAnimator.ofFloat(vertex, "left","top", path);
            pathAnimator.setDuration(mDuration);
        }else {
            pathAnimator = ValueAnimator.ofFloat(0.0f,1.0f).setDuration(mDuration);
            pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float[] point = new float[2];
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = animation.getAnimatedFraction();
                    PathMeasure pathMeasure = new PathMeasure(path, false);
                    pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);
                    vertex.setLeft(point[0]);
                    vertex.setTop(point[1]);
                }
            });
        }

        return pathAnimator;
    }
    private ValueAnimator buildVertexPositionBackward(final VertexHolder vertex) {
        List<PointF> pointTracks = vertex.getNextVertexPosition();

        final Path path = new Path();

        path.moveTo(pointTracks.get(pointTracks.size() - 1).x, pointTracks.get(pointTracks.size() - 1).y); // goto last position
        for (int i = pointTracks.size() - 2; i >= 0; i--) {
            path.lineTo(pointTracks.get(i).x, pointTracks.get(i).y);
        }

        ValueAnimator pathAnimator;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pathAnimator = ObjectAnimator.ofFloat(vertex, "left","top", path);
            pathAnimator.setDuration(mDuration);
        }else {
            pathAnimator = ValueAnimator.ofFloat(0.0f,1.0f).setDuration(mDuration);
            pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                float[] point = new float[2];
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = animation.getAnimatedFraction();
                    PathMeasure pathMeasure = new PathMeasure(path, false);
                    pathMeasure.getPosTan(pathMeasure.getLength() * val, point, null);
                    vertex.setLeft(point[0]);
                    vertex.setTop(point[1]);
                }
            });
        }
        return pathAnimator;
    }

    private List<VertexHolder> getVertexList() {
        List<VertexHolder> vertexHolderList = new ArrayList<>();

        VertexHolder vertexHolder;

        float halfFigureWidth = mItemWidth / 2;
        float halfFigureHeight = mItemHeight / 2;
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        //Left top
        vertexHolder = new VertexHolder(0, 0, mItemWidth, mItemHeight);
        vertexHolder.addVertexPosition(
                new PointF(vertexHolder.getLeft(), vertexHolder.getTop()),      //start position
                new PointF(vertexHolder.getLeft(), centerY - halfFigureHeight),//2-st position in half
                new PointF(centerX - halfFigureWidth, centerY - halfFigureHeight));//end position in center
        vertexHolderList.add(vertexHolder);

        //Left bottom
        vertexHolder = new VertexHolder(0, getHeight() - mItemHeight, mItemWidth, mItemHeight);
        vertexHolder.addVertexPosition(
                new PointF(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new PointF(centerX - halfFigureWidth, vertexHolder.getTop()),//2-st position in half
                new PointF(centerX - halfFigureWidth, centerY - halfFigureHeight));//end position in center
        vertexHolderList.add(vertexHolder);

        //Right bottom
        vertexHolder = new VertexHolder(getWidth() - mItemWidth, getHeight() - mItemHeight, mItemWidth, mItemHeight);
        vertexHolder.addVertexPosition(
                new PointF(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new PointF(vertexHolder.getLeft(), centerY - halfFigureHeight),//2-st position in half
                new PointF(centerX - halfFigureWidth, centerY - halfFigureWidth));//end position in center
        vertexHolderList.add(vertexHolder);

        //Right top
        vertexHolder = new VertexHolder(getWidth() - mItemWidth, 0, mItemWidth, mItemHeight);
        vertexHolder.addVertexPosition(
                new PointF(vertexHolder.getLeft(), vertexHolder.getTop()),//start position
                new PointF(centerX - halfFigureWidth, vertexHolder.getTop()),//2-st position in half
                new PointF(centerX - halfFigureWidth, centerY - halfFigureHeight));//end position in center
        vertexHolderList.add(vertexHolder);

        return vertexHolderList;
    }

    private void drawFigure(Canvas canvas, VertexHolder x, Paint paint) {
        switch (mPointFigure) {
            case FIGURE_SQUARE:
                canvas.drawRect(x.getLeft(), x.getTop(), x.getRight(), x.getBottom(), paint);
                break;
            case FIGURE_CIRCLE:
                canvas.drawCircle(x.getCenterX(), x.getCenterY(), x.getWidth() / 2, paint);
                break;
            case FIGURE_BITMAP:
                canvas.drawBitmap(
                        mBitmap,
                        null,
                        new Rect(
                                (int) x.getLeft(),
                                (int) x.getTop(),
                                (int) x.getRight(),
                                (int) x.getBottom()),
                        paint);
                break;
        }
    }

    /**
     * Vertex Holder class
     */
    private class VertexHolder {
        private float mTop, mLeft, mWidth, mHeigth;
        private List<PointF> mNextVertexPosition = new ArrayList<>();

        public VertexHolder(float mLeft, float mTop, float mWidth, float mHeigth) {
            this.mTop = mTop;
            this.mLeft = mLeft;
            this.mWidth = mWidth;
            this.mHeigth = mHeigth;

        }

        public void addVertexPosition(PointF... position) {
            mNextVertexPosition = Arrays.asList(position);
        }

        public float getTop() {
            return mTop;
        }

        public void setTop(float top) {
            mTop = top;
        }

        public float getLeft() {
            return mLeft;
        }

        public void setLeft(float left) {
            mLeft = left;
        }

        public float getWidth() {
            return mWidth;
        }

        public void setWidth(float width) {
            mWidth = width;
        }

        public float getHeigth() {
            return mHeigth;
        }

        public void setHeigth(float heigth) {
            mHeigth = heigth;
        }

        public float getCenterX() {
            return mLeft + mWidth / 2;
        }

        public float getCenterY() {
            return mTop + mHeigth / 2;
        }

        public float getRight() {
            return mLeft + mWidth;
        }

        public float getBottom() {
            return mTop + mHeigth;
        }

        public List<PointF> getNextVertexPosition() {
            return mNextVertexPosition;
        }
    }

    /**
     * Animation Helper class
     */
    private class AnimationHelper {
        private  Handler mHandler;
        private List<ValueAnimator> mAnimationFrom;
        private List<ValueAnimator> mAnimationTo;
        private ObjectAnimator mObjectAnimator;
        private Paint mAlphaPaint;
        private int mDelayAnimation = mDuration / 2;
        private boolean isCollapsed, isStart;
        private static final int EMPTY_MESSAGE_WHAT = 0;
        private static final int DELAY_FPS = 10; // 30 fps

        AnimationHelper(List<ValueAnimator> animationFrom, List<ValueAnimator> animationTo, ObjectAnimator objectAnimator) {
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
                            startAnimationFrom(mDelayAnimation);
                        } else {
                            startAnimationTo(mDelayAnimation);
                        }
                    }
                }
            });

            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what){
                        case EMPTY_MESSAGE_WHAT:
                            invalidate();
                            sendEmptyMessageDelayed(EMPTY_MESSAGE_WHAT, DELAY_FPS);
                            break;
                        case 1:
                            Log.d(TAG, "PAUSE IN HANDLER");
                            mCallback = null;
                            break;
                        case 2:
                            Log.d(TAG, "RESUME IN HANDLER");
                            break;
                    }
                }
            };
        }


        private void startAnimationTo(int delay) {
            mAlphaPaint.setAlpha(0);
            for (int i = 0; i < mAnimationTo.size(); i++) {
                ValueAnimator anim = mAnimationTo.get(i);
                anim.setStartDelay(i * delay);
                anim.start();
            }
            mAnimationTo.get(mAnimationTo.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isStart) {
                        isCollapsed = true;
                        if (mCallback != null){
                            mCallback.onComplete(false);
                        }
                        mObjectAnimator.start();
                    }
                }
            });
        }

        private void startAnimationFrom(int delay) {
            mAlphaPaint.setAlpha(0);
            for (int i = 0; i < mAnimationFrom.size(); i++) {
                ValueAnimator anim = mAnimationFrom.get(i);
                anim.setStartDelay(i * delay);
                anim.start();
            }
            mAnimationFrom.get(mAnimationFrom.size() - 1).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isStart) {
                        isCollapsed = false;
                        if (mCallback != null){
                            mCallback.onComplete(true);
                        }
                        mObjectAnimator.start();
                    }
                }
            });
        }

        void startAnimation() {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessage(0);
            mObjectAnimator.start();
            isStart = true;
        }

        void stopAnimation() {
            isStart = false;

            mHandler.removeCallbacksAndMessages(null);
            for (ValueAnimator anim : mAnimationTo) {
                anim.cancel();
            }
            for (ValueAnimator anim : mAnimationFrom) {
                anim.cancel();
            }
            mObjectAnimator.cancel();
        }
        void pauseAnimation(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (ValueAnimator anim : mAnimationTo) {
                    anim.pause();
                }
                for (ValueAnimator anim : mAnimationFrom) {
                    anim.pause();
                }
            }else {
                mHandler.removeCallbacksAndMessages(null);
            }
        }
        void resumeAnimation(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (ValueAnimator anim : mAnimationTo) {
                    anim.resume();
                }
                for (ValueAnimator anim : mAnimationFrom) {
                    anim.resume();
                }
            }else {
                mHandler.sendEmptyMessage(EMPTY_MESSAGE_WHAT);
            }

        }
    }
}
package com.boost.leonid.customviewanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity{
    private LoadingDraw customLoad3;
    private static final String TAG = "MainActivity";
    private Button mBtnPause, mBtnResume, mBtnPcolor, mBtnLineColor, mBtnLineWidth, mBtnFigure, mBtnDuration, mBtnPsize;
    private Random mRandom = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        customLoad3.setAnimationListener(new LoadingDraw.AnimationCompleteListener() {
            @Override
            public void onComplete(boolean complete) {
                Log.d(TAG, complete ? "Complete" : "Incomplete");
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.pause();
            }
        });

        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.resume();
            }
        });
        mBtnLineColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.setLineColor(mRandom.nextInt());
                Log.d(TAG, "Line color: " + customLoad3.getLineColor());
            }
        });

        mBtnLineWidth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.setLineWidth(mRandom.nextInt(10) + 1);
                Log.d(TAG, "Line width: " + customLoad3.getLineWidth());
            }
        });

        mBtnFigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.setPointFigure(mRandom.nextInt() % 2 == 0 ? 0 : 1);
                Log.d(TAG, "Figure: " + customLoad3.getPointFigure());
            }
        });

        mBtnPsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rndSize = mRandom.nextInt(50) + 1;
                customLoad3.setItemWidth(rndSize);
                customLoad3.setItemHeight(rndSize);
                Log.d(TAG, "Item size: " +
                        customLoad3.getItemHeight() + ":" +
                        customLoad3.getItemWidth());
            }
        });

        mBtnPcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.setPointColor(mRandom.nextInt());
                Log.d(TAG, "Point color: " + customLoad3.getPointColor());
            }
        });
        mBtnDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.setDuration(mRandom.nextInt(1000) + 1);
                Log.d(TAG, "Duration: " + customLoad3.getDuration());
            }
        });
    }

    private void initViews() {
        customLoad3 = (LoadingDraw) findViewById(R.id.customLoad3);
        mBtnPause = (Button) findViewById(R.id.btn_pause);
        mBtnResume = (Button) findViewById(R.id.btn_resume);
        mBtnDuration = (Button) findViewById(R.id.btn_duration);
        mBtnFigure = (Button) findViewById(R.id.btn_point_figure);
        mBtnLineWidth = (Button) findViewById(R.id.btn_line_width);
        mBtnLineColor = (Button) findViewById(R.id.btn_line_color);
        mBtnPcolor = (Button) findViewById(R.id.btn_point_color);
        mBtnPsize = (Button) findViewById(R.id.btn_point_size);
    }
}

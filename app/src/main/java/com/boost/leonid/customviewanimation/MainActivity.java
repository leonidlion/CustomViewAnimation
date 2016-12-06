package com.boost.leonid.customviewanimation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity{
    private LoadingDraw customLoad3;
    private static final String TAG = "MainActivity";
    private Button mBtnPause, mBtnResume;
    Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.customLoad1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lineColor = r.nextInt();
                Log.d(TAG, "onClick() set custom Load color: " + lineColor);
                // todo apply ALL changes in runtime
                customLoad3.setLineColor(lineColor);
                customLoad3.setLineWidth(2);
            }
        });
        customLoad3 = (LoadingDraw) findViewById(R.id.customLoad3);
        customLoad3.setLineColor(Color.GRAY);
        customLoad3.setLineWidth(6);
        customLoad3.setBitmap(R.mipmap.ic_launcher);

        customLoad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MAIN", String.valueOf(customLoad3.getDuration()));
                customLoad3.setDuration(customLoad3.getDuration() - 100);
            }
        });
        customLoad3.setAnimationListener(new LoadingDraw.AnimationCompleteListener() {
            @Override
            public void onComplete(boolean complete) {
                if (complete){
                    Log.d(TAG, "Complete");
                }else {
                    Log.d(TAG, "Incomplete");
                }
            }
        });

        mBtnPause = (Button) findViewById(R.id.btn_pause);
        mBtnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.pause();
            }
        });

        mBtnResume = (Button) findViewById(R.id.btn_resume);
        mBtnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customLoad3.resume();
            }
        });
    }
}

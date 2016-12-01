package com.boost.leonid.customviewanimation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private LoadingDraw customLoad3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.customLoad1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int duration = 10 + 50 * r.nextInt(10);
                ((LoadingDraw)v).setDuration(duration);
                Log.d("TEST", "onClick: duration: " + duration);

                customLoad3.setLineColor(r.nextInt());
            }
        });
        customLoad3 = (LoadingDraw) findViewById(R.id.customLoad3);
        customLoad3.setLineColor(Color.GRAY);
        customLoad3.setPointSize(35);
        customLoad3.setLineWidth(6);
        customLoad3.setBitmap(R.mipmap.ic_launcher);
        // todo what mean blinkElement 1?
        customLoad3.setBlinkElement(1);
    }
}

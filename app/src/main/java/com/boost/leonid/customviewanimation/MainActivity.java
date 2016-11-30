package com.boost.leonid.customviewanimation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LoadingDraw customLoad3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customLoad3 = (LoadingDraw) findViewById(R.id.customLoad3);
        customLoad3.setLineColor(Color.GRAY);
        customLoad3.setPointSize(35);
        customLoad3.setLineWidth(6);
        customLoad3.setBitmap(R.mipmap.ic_launcher);
        customLoad3.setBlinkElement(1);
    }
}

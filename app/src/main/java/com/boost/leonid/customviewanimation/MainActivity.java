package com.boost.leonid.customviewanimation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    private LoadingDraw customLoad3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customLoad3 = (LoadingDraw) findViewById(R.id.customLoad3);
        customLoad3.setLineColor(Color.GRAY);
        customLoad3.setLineWidth(6);
        customLoad3.setBitmap(R.mipmap.ic_launcher);

        customLoad3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("MAIN", String.valueOf(customLoad3.getDuration()));
                customLoad3.setDuration(customLoad3.getDuration() - 100);
            }
        });
        customLoad3.setAnimationListener(new LoadingDraw.AnimationCompleteListener() {
            @Override
            public void isComplete(boolean complete) {
                if (complete){
                    Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Incomplete", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

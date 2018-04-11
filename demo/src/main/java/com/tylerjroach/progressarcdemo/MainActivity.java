package com.tylerjroach.progressarcdemo;

import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.tylerjroach.progressarc.ProgressArc;

public class MainActivity extends AppCompatActivity {

    ProgressArc progressArc;
    SeekBar progressSeekBar;
    SeekBar sweepAngleSeekBar;
    View innerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressArc = findViewById(R.id.progressArc);
        innerView = findViewById(R.id.innerView);

        progressSeekBar = findViewById(R.id.progressSeekBar);
        progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressArc.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sweepAngleSeekBar = findViewById(R.id.sweepAngleSeekBar);
        sweepAngleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressArc.setSweepAngle((int) (180 + ((progress / 100f) * 180)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        innerView.post(new Runnable() {
            @Override
            public void run() {
                progressArc.setOnInnerRectangleSizeChangedListener(new ProgressArc.OnInnerRectangleBoundsChangedListener() {
                    @Override
                    public void onInnerRectangleSizeChanged(Rect rect) {
                        ((ConstraintLayout.LayoutParams)innerView.getLayoutParams()).width = rect.width();
                        ((ConstraintLayout.LayoutParams)innerView.getLayoutParams()).height = rect.height();
                        ((ConstraintLayout.LayoutParams)innerView.getLayoutParams()).setMargins((progressArc.getWidth() - rect.width()) / 2, (progressArc.getWidth() - rect.width()) / 2, 0, 0);
                        innerView.requestLayout();
                    }
                });
            }
        });

        progressSeekBar.setProgress(50);
        sweepAngleSeekBar.setProgress((int) (progressArc.getSweepAngle() / 360f * 100));
    }
}
package com.ar.ipsum.ipsumapp.view;

/**
 * Created by tiago_000 on 30/07/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;

public class seekDirectionView extends Activity {

    LinearLayout ll;
    SeekBar sBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ll = new LinearLayout(this);
        sBar = new SeekBar(this);
        SeekBar.OnSeekBarChangeListener abc = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //Executed when progress is changed
                System.out.println(progress);
            }
        };
        sBar.setOnSeekBarChangeListener(abc);
        LayoutParams sBarLayParams=new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        ll.addView(sBar,sBarLayParams);
        setContentView(ll);
    }
}

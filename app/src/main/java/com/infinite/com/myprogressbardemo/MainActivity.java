package com.infinite.com.myprogressbardemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.infinite.com.myprogressbardemo.view.MyProgressView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MyProgressView myProgressView;
    int p = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myProgressView = (MyProgressView) findViewById(R.id.pv);

        List<Integer> m = new ArrayList<>();
        m.add(5);
        m.add(7);
        m.add(7);
        m.add(13);
        m.add(13);
        m.add(53);
        m.add(25);
        m.add(25);
        m.add(25);
        myProgressView.setProgressDots(m);
        myProgressView.setProgress(50);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessageDelayed(1, 30);
//                myProgressView.setProgress(0, 0);
            }
        }, 1000);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (p > 66) {
                return;
            }
//            myProgressView.setProgress(p);
            myProgressView.setProgress(5, p);
            p++;
            mHandler.sendEmptyMessageDelayed(1, 50);
        }
    };
}

package com.tuojin.videoplayerutils;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DemoExoActivity extends AppCompatActivity {

    @BindView(R.id.btn_test)
    Button mBtnTest;
    @BindView(R.id.activity_demo_exo)
    LinearLayout mActivityDemoExo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_exo);
        ButterKnife.bind(this);


    }
}

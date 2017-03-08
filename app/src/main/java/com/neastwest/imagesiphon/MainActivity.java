package com.neastwest.imagesiphon;

import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout mResultsRegion;
    protected EditText mImagesToDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImagesToDownload = (EditText) findViewById(R.id.urls_to_download);
        mResultsRegion = (LinearLayout)findViewById(R.id.results_region);
    }

    public void onButtonClick (View view) {
        Log.i("MESSAGE", "Button Clicked");


    }

}

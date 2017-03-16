package com.neastwest.imagesiphon;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout imagesLayout;
    protected EditText urlTextBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning variables to views
        urlTextBox = (EditText) findViewById(R.id.urls_to_download);
        imagesLayout = (LinearLayout)findViewById(R.id.results_region);
    }

    public void onButtonClick (View view) {
        Log.i("MESSAGE", "Button Clicked");

        //retrieving urls from text box, split by new line whitespace
        String[] images = urlTextBox.getText().toString().split("\\s+");

        //send the array of urls to execute
        executeImages(images);
    }

    protected void executeImages(String[] images) {
        Log.i("MESSAGE", "reached for loop");

        //enhanced for loop to execute asynctask on each image in array
        for (String image1: images) {
            new ImageDownloader().execute(image1);
            Log.i("MESSAGE", image1);
        }

    }

    //AsyncTask to download URL and return and assign a Bitmap to an ImageView
    private class ImageDownloader extends AsyncTask <String, Void, Bitmap> {

        private String newImageUrl = "";

        protected Bitmap doInBackground(String... imagesURL) {

            newImageUrl = imagesURL[0];

            Log.i("MESSAGE", newImageUrl);
            Bitmap newImage = null;

            //send new image URL to retrieveImage function
            try {
                newImage = ImageSiphon.retrieveImage(newImageUrl);
                Log.i("MESSAGE", "retreive Image worked?");
                //Catch exception
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newImage;
        }

        //Create a new image view and add it to results layout onPostExecute
        protected void onPostExecute(Bitmap image) {
            ImageView iv = new ImageView(MainActivity.this);
            iv.setImageBitmap(image);
            imagesLayout.addView(iv);
        }


    }

}

/*
 * Need to add file reader and writer.
 * load urls from file 0r EditText
 * write to file a log of whats been downloaded previously?
 * maybe add a URL validator.
 */
package com.neastwest.imagesiphon;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout imagesLayout;
    protected EditText urlTextBox;
    protected ProgressBar progBar;
    protected String[] images;
    private List<Downed> downedList = new ArrayList<>();
    private int totalLinks = 0;
    private int completedImages = 0;
    private DatabaseHandler db = new DatabaseHandler(this);
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning variables to views
        urlTextBox = (EditText) findViewById(R.id.urls_to_download);
        imagesLayout = (LinearLayout)findViewById(R.id.results_region);

        //Progress bar settings
        progBar = (ProgressBar)findViewById(R.id.progressBar3);
        progBar.setMax(100);

    }

    public void onFileButtonClick(View view) {
        new FileReader().execute();
    }

    //Clear button clears the image results from the LinearLayout
    public void onClearButtonClick(View view) {
        if(db.getDownedCount() > 0) {
            clearALL();
        }
    }

    public void onButtonClick (View view) {
        Log.i("MESSAGE", "Button Clicked");
        //retrieving urls from text box, split by new line whitespace
        images = urlTextBox.getText().toString().split("\\s+");
        startDownloading(images);
    }

    protected void executeImages(String[] images) {
        Log.i("MESSAGE", "reached for loop");

        /* enhanced for loop to execute AsyncTask on each image in array
         * creating a new Wrapper object to pass the URL and the MainActivity Context
         * Uses the apache UrlValidator library to determine if the entered URL is in
         * the correct format to be stored as a URL object.
         */
        UrlValidator urlValidator = new UrlValidator();
        for (String image1: images) {
            if(urlValidator.isValid(valueOf(image1))) {
                totalLinks++;
                new ImageDownloader().execute(image1);
                Log.i("MESSAGE", "assigned to object");
            } else {
                TextView newTxtView = (TextView) ImageSiphon.createErrorTextView(MainActivity.this);
                imagesLayout.addView(newTxtView);
            }
        }
    }

    private class FileReader extends AsyncTask <String[], Void, String[]> {

        protected String[] doInBackground(String[]... StringArray) {
            String[] newStringArray = new String[4];

            try {
                newStringArray = FileSiphon.readFileToStringArray(MainActivity.this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newStringArray;
        }

        protected void onPostExecute(String[] newStringArray) {
            Log.d("MESSAGE", newStringArray[0]);
            images = newStringArray;
            startDownloading(images);
        }
    }

    //AsyncTask to check a URL and return a View.
    private class ImageDownloader extends AsyncTask <String, Void, View> {
        private String newImageUrl = "";

        protected View doInBackground(String... imagesURL) {
            Wrapper ww = new Wrapper(imagesURL[0]);
            Log.i("MESSAGE", newImageUrl);

            //send new Wrapper object to viewCreator function
            try {
                view = ImageSiphon.viewCreator(ww);
                Log.i("MESSAGE", "retrieve Image worked?");
                //Catch exception
            } catch (IOException e) {
                e.printStackTrace();
            }

            return view;
        }

        /* Compares counters, and disable progress bar if finished all images.
         * Adds ImageView to the results LinearLayout, and the corresponding URI to the
         * thumbnailURIs ArrayList.
         */
        protected void onPostExecute(View view) {
            completedImages++;
            View newView = ImageSiphon.createImageViewFromURI
                    (Uri.parse(db.getDowned(completedImages).getThumbnail()), MainActivity.this);
            imagesLayout.addView(newView);

            if(view != null) {
                Downed downed = db.getDowned(1);
                Log.d("dbTest", downed.getURL());
                Log.d("dbTest", downed.getThumbnail());
            }
            if (totalLinks == completedImages) {
                progBar.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        db.close();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Retrieve list of URIs from bundle. Re-add URIs to list and redisplay ImageViews
        DatabaseHandler db = new DatabaseHandler(this);
        downedList = db.getAllDowned();
        int count = db.getDownedCount();
        for (int i = 0; i < count; i++) {
            Uri uri;
            uri = Uri.parse(downedList.get(i).getThumbnail());
            Log.d("MESSAGE", uri.toString());
            View tempImage = ImageSiphon.createImageViewFromURI(uri, MainActivity.this);
            imagesLayout.addView(tempImage);
            db.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //If user is force closing app, clear downloaded and displayed data
        if(isFinishing()) {
            clearALL();
        }
    }

    /*
     * Deletes currently saved images from storage.
     * Clears the ImageViews/LinearLayouts.
     * Resets counters for progress bar.
     * Clears the entered Links in text box.
     * Displays Toast Message on completion.
     */
    public void clearALL() {
        Log.d("MESSAGE", valueOf(db.getDownedCount()));
        deleteStoredImages();
        db.deleteAll();
        imagesLayout.removeAllViews();
        imagesLayout.invalidate();
        completedImages = 0;
        totalLinks = 0;
        urlTextBox.setText("");
        Toast.makeText(MainActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
    }

    private void startDownloading(String[] images) {
        //send the array of urls to execute
        executeImages(images);

        progBar.setVisibility(View.VISIBLE);
        progBar.setProgress(0);
    }

    private void deleteStoredImages() {
        downedList = db.getAllDowned();
        int count = db.getDownedCount();
        for (int i = 0; i < count; i++) {
            File fileDelete = new File(downedList.get(i).getThumbnail());
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    Log.d("MESSAGE", "file deleted" + downedList.get(i).getThumbnail());
                } else {
                    Log.d("MESSAGE", "file not deleted" + downedList.get(i).getThumbnail());
                }
            }
        }
    }

    //Wrapper holds a link as a String, and the MainActivity Context
    class Wrapper {
        final Context context = MainActivity.this;
        String imageName = "";

        Wrapper(String url) {
            this.imageName = url;
        }
        String getString() {
            return imageName;
        }
        Context getContext() {
            return context;
        }
    }

}


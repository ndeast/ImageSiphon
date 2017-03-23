package com.neastwest.imagesiphon;


//Image to Storage feature branch
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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout imagesLayout;
    protected EditText urlTextBox;
    protected ProgressBar progBar;
    protected String[] images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning variables to views
        urlTextBox = (EditText) findViewById(R.id.urls_to_download);
        imagesLayout = (LinearLayout)findViewById(R.id.results_region);


        //Progress bar settings
        progBar = (ProgressBar)findViewById(R.id.progressBar3);
        progBar.setMax(20);
    }

    public void onFileButtonClick(View view) {

    }

    //Clear button clears the image results from the LinearLayout
    public void onClearButtonClick(View view) {
        if(imagesLayout.getChildCount() > 0) {
            imagesLayout.removeAllViews();
            imagesLayout.invalidate();
        }
    }

    public void onButtonClick (View view) {
        Log.i("MESSAGE", "Button Clicked");

        //retrieving urls from text box, split by new line whitespace
        images = urlTextBox.getText().toString().split("\\s+");

        //send the array of urls to execute
        executeImages(images);
    }

    protected void executeImages(String[] images) {
        Log.i("MESSAGE", "reached for loop");

        //enhanced for loop to execute AsyncTask on each image in array
        //creating a new Wrapper object to pass the URL and the MainActivity Context
        //Uses the apache UrlValidator library to determine if the entered URL is in
        //the correct format to be stored as a URL object.
        UrlValidator urlValidator = new UrlValidator();
        for (String image1: images) {
            if(urlValidator.isValid(String.valueOf(image1))) {
                Wrapper w = new Wrapper();
                w.setString(image1);
                new ImageDownloader().execute(w);
                Log.i("MESSAGE", "assigned to object");
            } else {
                TextView newTxtView = (TextView) ImageSiphon.createErrorTextView(MainActivity.this);
                imagesLayout.addView(newTxtView);
            }
        }
    }

    //AsyncTask to check a URL and return a View.
    //Returns an ImageView for a valid link and a TextView in case of errors.
    private class ImageDownloader extends AsyncTask <Wrapper, Void, ImageDL> {
        private String newImageUrl = "";
        Wrapper ww = new Wrapper();

        //PreExecute will start a progress bar
        protected void onPreExecute() {
            progBar.setVisibility(View.VISIBLE);
            progBar.setProgress(0);
        }

        protected ImageDL doInBackground(Wrapper... imagesURL) {
            ww = imagesURL[0];
            Log.i("MESSAGE", newImageUrl);
            ImageDL dl = new MainActivity.ImageDL();

            //send new Wrapper object to viewCreator function
            try {
                dl = ImageSiphon.viewCreator(ww);
                Log.i("MESSAGE", "retreive Image worked?");
                //Catch exception
            } catch (IOException e) {
                e.printStackTrace();
            }
            return dl;
        }

        //Post Execute will hide the progress bar and
        //Add the View created in the doInBackground process
        //to the imagesLayout LinearLayout
        protected void onPostExecute(ImageDL dl) {
            imagesLayout.addView(dl.getView());
            progBar.setVisibility(View.GONE);
        }
    }

    //Wrapper class. This class holds the Context and URL variables. This was created in order
    //to be able to pass multiple variables through an AsyncTask. Normally I would not have
    //used an AsyncTask due to our need of passing multiple variables (context)
    //however for the purposes of demoing threads, this started out to demo AsyncTask, and
    //I just stuck with it. I would probably redo this with an ExecutorService.
    class Wrapper {
        final Context context = MainActivity.this;
        String imageName = "";
        String packageName = getApplicationContext().getPackageName();

        public void setString(String newName) {
            imageName = newName;
        }
        public String getString() {
            return imageName;
        }
        Context getContext() {
            return context;
        }
        String getPackageName() {
            return packageName;
        }
    }
    static class ImageDL {
        Uri thumb;
        Uri fullSize;
        View view;
        boolean imgOrTxt;

        public ImageDL(){}

        void setThumb(Uri uri) {
            thumb = uri;
        }
        void setFullSize(Uri uri) {
            fullSize = uri;
        }
        void setView(View newView) {
            view = newView;
        }
        void setImgOrTxt(boolean typeOfView) {
            imgOrTxt = typeOfView;
        }
        Uri getThumb() {
            return thumb;
        }
        Uri getFullSize() {
            return fullSize;
        }
        View getView() {
            return view;
        }
        boolean getImgOrText() {
            return imgOrTxt;
        }
    }
}


package com.neastwest.imagesiphon;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
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

import static android.view.View.GONE;
import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    protected LinearLayout imagesLayout;
    protected EditText urlTextBox;
    protected ProgressBar progBar;
    protected String[] images;
    ArrayList<Uri> thumbnailURIs = new ArrayList<Uri>();
    boolean doneLoop = false;
    int totalLinks = 0;
    int completedImages = 0;

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

    }

    //Clear button clears the image results from the LinearLayout
    public void onClearButtonClick(View view) {
        if(imagesLayout.getChildCount() > 0) {
            clearALL();
        }
    }

    public void onButtonClick (View view) {
        Log.i("MESSAGE", "Button Clicked");

        //retrieving urls from text box, split by new line whitespace
        images = urlTextBox.getText().toString().split("\\s+");

        //send the array of urls to execute
        executeImages(images);

        progBar.setVisibility(View.VISIBLE);
        progBar.setProgress(0);

    }

    protected void executeImages(String[] images) {
        Log.i("MESSAGE", "reached for loop");

        //enhanced for loop to execute AsyncTask on each image in array
        //creating a new Wrapper object to pass the URL and the MainActivity Context
        //Uses the apache UrlValidator library to determine if the entered URL is in
        //the correct format to be stored as a URL object.
        UrlValidator urlValidator = new UrlValidator();
        for (String image1: images) {
            if(urlValidator.isValid(valueOf(image1))) {
                totalLinks++;
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
            completedImages++;
            imagesLayout.addView(dl.getView());
            if(dl.getThumb() != null) {
                thumbnailURIs.add(dl.getThumb());
                Log.i("MESSAGE", valueOf(thumbnailURIs.size()));
                Log.d("MESSAGE", dl.getThumb().toString());
                Log.d("MESSAGE", "totalLinks is: " + valueOf(totalLinks) + " completedImages is: " + valueOf(completedImages));
            }
            if (totalLinks == completedImages) {
                progBar.setVisibility(GONE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("KEY_URIS", thumbnailURIs);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ArrayList<Parcelable> uris = savedInstanceState.getParcelableArrayList("KEY_URIS");
        if(savedInstanceState != null) {
            for (Parcelable p: uris) {
                Uri uri = (Uri) p;
                Log.d("MESSAGE", uri.toString());
                thumbnailURIs.add(uri);
                View tempImage = ImageSiphon.createImageViewURI(uri, MainActivity.this);
                imagesLayout.addView(tempImage);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isFinishing()) {
            clearALL();
        }
    }

    public void clearALL() {
        for (Uri u: thumbnailURIs) {
            File fileDelete = new File(u.getPath());
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    Log.d("MESSAGE", "file deleted" + u.getPath());
                } else {
                    Log.d("MESSAGE", "file not deleted" + u.getPath());
                }
            }
        }
        thumbnailURIs.clear();
        imagesLayout.removeAllViews();
        imagesLayout.invalidate();
        completedImages = 0;
        totalLinks = 0;
        Toast.makeText(MainActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
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


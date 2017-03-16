package com.neastwest.imagesiphon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageSiphon {

    //Method to download a bitmap from a given URL
    public static Bitmap retrieveImage(URL newURL) throws MalformedURLException, IOException {

        //Opens a URL connection
        HttpURLConnection urlConnection = (HttpURLConnection) newURL.openConnection();
        Bitmap image;
        InputStream newInputStream = null;
        try {
            //Attempts to send the input stream from the opened URL connection
            //and send it to the BitmapFactory stream decoder
            newInputStream = urlConnection.getInputStream();
            image = BitmapFactory.decodeStream(newInputStream);
        } finally {
            //If the InputStream was used and is finished, disconnect.
            if(newInputStream != null) {
                urlConnection.disconnect();
                newInputStream.close();
            }
        }
        return image;
    }

    public static View viewCreator(MainActivity.Wrapper w) throws MalformedURLException, IOException {
        Context context = w.getContext();
        URL url = new URL(w.getString());
        Bitmap newImage;
        String newString = "invalid url";
        //if the URL returns a good HTTP response code, retrieve the
        //image from the URL and then turn it into an ImageView
        if (testURL(url)) {
            Log.d("MESSAGE", "good URL: ");
            newImage = retrieveImage(url);
            ImageView newImgView = new ImageView(context);
            newImgView.setImageBitmap(newImage);
            newImgView.setPadding(5, 5, 5, 5);
            return newImgView;
            //Otherwise, if the response code is bad, create a
            //TextView for an Invalid URL
        } else {
            Log.d("MESSAGE", "bad URL: ");
            TextView newTxtView = new TextView(context);
            newTxtView.setText(newString);
            newTxtView.setPadding(5, 5, 5, 5);
            return newTxtView;
        }
    }
    //Method to get the HTTP Response Code from a URL and return true if it is a 200
    public static boolean testURL(URL url) throws IOException {
        Log.d("MESSAGE", "passed validator");
            try {
                //Open URLConnection and assign the HTTP Response code to int responseCode
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                //Disconnect from url
                urlConnection.disconnect();
                if (responseCode != 200) {
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        //Return true if responseCode is 200
       return true;
    }

}

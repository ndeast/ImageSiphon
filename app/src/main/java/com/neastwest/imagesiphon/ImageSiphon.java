package com.neastwest.imagesiphon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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
        Bitmap thumb;
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

        thumb = createThumb(image);
        image.recycle();
        return thumb;
    }

    public static View viewCreator(MainActivity.Wrapper w) throws MalformedURLException, IOException {
        Context context = w.getContext();
        URL url = new URL(w.getString());
        Bitmap newImage;
        //if the URL returns a good HTTP response code, retrieve the
        //image from the URL and then turn it into an ImageView
        if (testURL(url)) {
            Log.d("MESSAGE", "good URL: ");
            newImage = retrieveImage(url);
            View imgView = createImageView(newImage, context);
            return imgView;
            //Else return an error TextView
        } else {
            Log.d("MESSAGE", "bad URL: ");
            View txtView = createErrorTextView(context);
            return txtView;
        }
    }
    //Method to get the HTTP Response Code from a URL and return true if it is a 200
    private static boolean testURL(URL url) throws IOException {
        Log.d("MESSAGE", "passed validator");
        HttpURLConnection urlConnection = null;
            try {
                //Open URLConnection and assign the HTTP Response code to int responseCode
                urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    return false;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

        //Return true if responseCode is 200
       return true;
    }
    private static Bitmap createThumb(Bitmap image) {
       // int dimension = getSquareCropDimensionForBitmap(image);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(image, 750, 750);
        image.recycle();
        return bitmap;

    }
    public static int getSquareCropDimensionForBitmap(Bitmap bitmap)
    {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

    public static View createErrorTextView(Context context) {
        TextView newTxtView = new TextView(context);
        newTxtView.setText(R.string.error_text);
        newTxtView.setPadding(5, 5, 5, 5);
        return newTxtView;
    }

    public static View createImageView(Bitmap image, Context context) {
        ImageView newImgView = new ImageView(context);
        newImgView.setImageBitmap(image);
        newImgView.setPadding(5, 5, 5, 5);
        return newImgView;
    }
}

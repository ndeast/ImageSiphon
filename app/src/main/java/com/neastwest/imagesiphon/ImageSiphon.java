// dbBranch

package com.neastwest.imagesiphon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageSiphon {

    //Downloads a Bitmap from a URL, and returns a thumbnail
    private static Bitmap retrieveImage(URL newURL) throws IOException {
        Bitmap image;
        Bitmap thumb;
        HttpURLConnection urlConnection = (HttpURLConnection) newURL.openConnection();
        InputStream newInputStream = null;

        try {
            newInputStream = urlConnection.getInputStream();
            image = BitmapFactory.decodeStream(newInputStream);
        } finally {
            if(newInputStream != null) {
                urlConnection.disconnect();
                newInputStream.close();
            }
        }
        thumb = createThumb(image);
        if(image != null) {
            image.recycle();
        }
        return thumb;
    }

    // Test the link, good URLs get turned into ImageViews, Bad links return an error TextView
    static View viewCreator(MainActivity.Wrapper w) throws IOException {
        Context context = w.getContext();
        URL url = new URL(w.getString());
        View view;

        if (testURL(url)) {
             view = goodURL(url, context);
            return view;
        } else {
            view = badURL(context);
            return view;
        }
    }

    //Method to get the HTTP Response Code from a URL and return true if it is a 200
    private static boolean testURL(URL url) throws IOException {
        Log.d("MESSAGE", "passed validator");
        HttpURLConnection urlConnection;
        urlConnection = (HttpURLConnection) url.openConnection();
            try {
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200) {
                    return false;
                }
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

    //Method to run if URL fails tests
    private static View badURL(Context context) {
        Log.d("MESSAGE", "bad URL: ");

        return createErrorTextView(context);
    }

    //Good links get downloaded, a thumbnail generated, saved, and assigned to an ImageDL object
    private static View goodURL(URL url, Context context)
            throws IOException {
        DatabaseHandler db = new DatabaseHandler(context);
        Bitmap newImage;
        Log.d("MESSAGE", "good URL: ");
        newImage = retrieveImage(url);
        Log.d("MESSAGE", "calling save image function");
        File newThumbFile = FileSiphon.getOutputMediaFile(context);
        FileSiphon.saveThumbToFile(newImage, newThumbFile);
        View imgView = createImageViewFromFile(newThumbFile, context);

        if(newThumbFile != null) {
            Downed downed = new Downed(url.toString(), newThumbFile.toString());
            db.addDowned(downed);
        }
        return imgView;
    }
    private static Bitmap createThumb(Bitmap image) {
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(image, 1500, 1500);
        if(image != null) {
            image.recycle();
        }
        return bitmap;
    }

    //Create and return TextView with error text
     static View createErrorTextView(Context context) {
        TextView newTxtView = new TextView(context);
        newTxtView.setText(R.string.error_text);
        newTxtView.setPadding(5, 5, 5, 5);
        return newTxtView;
    }

    //create and return an ImageView from a File
    private static View createImageViewFromFile(File image, Context context) {
        Uri uri = Uri.fromFile(image);
        ImageView newImgView = new ImageView(context);
        newImgView.setImageURI(uri);
        newImgView.setAdjustViewBounds(true);
        newImgView.setPadding(5, 5, 5, 5);
        return newImgView;
    }

    //Create and return an ImageView from a URI
    static View createImageViewFromURI(Uri uri, Context context) {
        ImageView newImgView = new ImageView(context);
        newImgView.setImageURI(uri);
        newImgView.setAdjustViewBounds(true);
        newImgView.setPadding(5, 5, 5, 5);
        return newImgView;
    }
}

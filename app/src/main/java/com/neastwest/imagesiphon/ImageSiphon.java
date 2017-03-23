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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.content.ContentValues.TAG;

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
        if(image != null) {
            image.recycle();
        }
        return thumb;
    }

    public static MainActivity.ImageDL viewCreator(MainActivity.Wrapper w) throws IOException {
        MainActivity.ImageDL dl = new MainActivity.ImageDL();
        Context context = w.getContext();
        URL url = new URL(w.getString());
        Bitmap newImage;
        //if the URL returns a good HTTP response code, retrieve the
        //image from the URL and then turn it into an ImageView
        if (testURL(url)) {
            dl = goodURL(url, context, dl);
            return dl;
            //Else return an error TextView
        } else {
            dl = badURL(context, dl);
            return dl;
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

    //Method to run if URL fails tests
    private static MainActivity.ImageDL badURL(Context context, MainActivity.ImageDL dl) {
        Log.d("MESSAGE", "bad URL: ");
        View txtView = createErrorTextView(context);
        dl.setView(txtView);
        dl.setImgOrTxt(false);

        return dl;
    }

    //Method to run if URL passes tests.
    private static MainActivity.ImageDL goodURL(URL url, Context context, MainActivity.ImageDL dl)
            throws IOException {
        Bitmap newImage;
        Log.d("MESSAGE", "good URL: ");
        newImage = retrieveImage(url);
        Log.d("MESSAGE", "calling save image function");
        File newThumbFile = getOutputMediaFile(context);
        dl.setThumb(Uri.fromFile(newThumbFile));
        saveThumbToFile(newImage, context, newThumbFile);
        View imgView = createImageView(newThumbFile, context);
        dl.setView(imgView);

        return dl;
    }
    private static Bitmap createThumb(Bitmap image) {
       // int dimension = getSquareCropDimensionForBitmap(image);
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(image, 750, 750);
        if(image != null) {
            image.recycle();
        }
        return bitmap;
    }
    public static int getSquareCropDimensionForBitmap(Bitmap bitmap)
    {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

    //Create and return TextView with error text
    public static View createErrorTextView(Context context) {
        TextView newTxtView = new TextView(context);
        newTxtView.setText(R.string.error_text);
        newTxtView.setPadding(5, 5, 5, 5);
        return newTxtView;
    }

    //create and return an ImageView from a File
    public static View createImageView(File image, Context context) {
        Uri uri = Uri.fromFile(image);
        ImageView newImgView = new ImageView(context);
        newImgView.setImageURI(uri);
        newImgView.setPadding(5, 5, 5, 5);
        return newImgView;
    }

    //Create and return an ImageView from a URI
    public static View createImageViewURI(Uri uri, Context context) {
        ImageView newImgView = new ImageView(context);
        newImgView.setImageURI(uri);
        newImgView.setPadding(5, 5, 5, 5);
        return newImgView;
    }

    //converts an thumbnail bitmap into a thumbnail File
    public static void saveThumbToFile(Bitmap image, Context context, File pictureFile) {
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(Context context){
        Log.d("MESSAGE", "reached output media file");
        File mediaStorageDir = new File(context.getFilesDir().getPath() + "/THUMBS/");
        Log.d("MESSAGE", "saved storage dir");
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MESSAGE", "returning null on mediaStorageExists");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.US).format(new Date());
        File mediaFile;
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String mImageName="MI_"+ timeStamp + uuid + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        Log.d("MESSAGE", mediaFile.getPath());
        return mediaFile;
    }
}

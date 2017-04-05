package com.neastwest.imagesiphon;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class FileSiphon {
    //converts an thumbnail bitmap into a thumbnail File
    public static void saveThumbToFile(Bitmap image, File pictureFile) {
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(Context context){
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

    /** Reads in strings from a text file*/
    public static String[] readFileToStringArray(Context context) throws IOException {
        String[] urlArray = new String[4];
        BufferedReader br = new BufferedReader
                (new InputStreamReader(context.getAssets().open("links.txt")));
        try {
            String mLine = br.readLine();
            while (mLine != null) {
                for(int i=0;i<4;i++) {
                    urlArray[i] = mLine;
                    mLine = br.readLine();
                }
            }
        } catch (IOException e) {
            Log.d("MESSAGE", e.toString());
            Toast.makeText(context, "Could not open file", Toast.LENGTH_SHORT).show();
        } finally {
            br.close();
        }
        return urlArray;
    }
}

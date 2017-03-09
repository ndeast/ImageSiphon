package com.neastwest.imagesiphon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageSiphon {

    public static Bitmap retrieveImage(String newURL) throws MalformedURLException, IOException {
        URL url = new URL(newURL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Bitmap image;
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
        return image;
    }


}

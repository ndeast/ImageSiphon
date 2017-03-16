package com.neastwest.imagesiphon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.validator.routines.UrlValidator;

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


    public static void confirmURL(String newURL) throws MalformedURLException{
        URL url = new URL(newURL);
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(String.valueOf(url))) {
            System.out.println("url is valid");
        } else {
            System.out.println("url is invalid");
        }

    }


    public void urlImageViewCreator(Bitmap newImage) {
        //View imgView = new View();




    }

}
/*
 * Need to add ability to create views from returned bitmap
 * and return error messages on exception.
 * TextViews and ImageViews should be returned based on what is retrieved from URL
 */

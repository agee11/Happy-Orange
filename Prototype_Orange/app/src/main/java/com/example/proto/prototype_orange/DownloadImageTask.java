package com.example.proto.prototype_orange;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by orang on 7/10/2016.
 */
public class DownloadImageTask extends AsyncTask<Uri, Void, Void> {
    Bitmap bmp;
    ImageView imageView;

    DownloadImageTask(ImageView image){
        imageView = image;
    }
    @Override
    protected Void doInBackground(Uri... params) {
        try {
            URL url = new URL(params[0].toString());
            System.out.println("Storage URL: " + url);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        imageView.setImageBitmap(bmp);
    }
}
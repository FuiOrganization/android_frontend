package br.com.fui.fuiapplication.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import br.com.fui.fuiapplication.cache.MemoryCache;

/**
 * Created by guilherme on 14/10/17.
 */

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    String imageUrl = "";
    ImageView imageView = null;
    AppBarLayout appBar = null;

    public LoadImageTask(String imageUrl, ImageView imageView) {
        this.imageUrl = imageUrl;
        this.imageView = imageView;
    }

    public LoadImageTask(String imageUrl, AppBarLayout appBar) {
        this.imageUrl = imageUrl;
        this.appBar = appBar;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            //try to get bitmap from memory cache
            bitmap = MemoryCache.getBitmapFromMemCache(this.imageUrl);
            //if null, load from stream
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(this.imageUrl).getContent());
                //add to memory cache
                MemoryCache.addBitmapToMemoryCache(this.imageUrl, bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(final Bitmap image) {
        if (imageView != null) {
            imageView.setImageBitmap(image);
        } else if (appBar != null) {
            Drawable drawableImage = new BitmapDrawable(image);
            if (Build.VERSION.SDK_INT >= 16) {
                appBar.setBackground(drawableImage);
            } else {
                appBar.setBackgroundDrawable(drawableImage);
            }

        }
    }

    @Override
    protected void onCancelled() {
    }


}
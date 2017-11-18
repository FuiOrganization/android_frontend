package br.com.fui.fuiapplication.tasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import br.com.fui.fuiapplication.cache.MemoryCache;
import br.com.fui.fuiapplication.data.Data;

/**
 * Created by guilherme on 14/10/17.
 */

public class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
    String imageUrl = "";
    ImageView imageView = null;
    AppBarLayout appBar = null;
    Context context = null;
    boolean usePicasso;

    public LoadImageTask(String imageUrl, ImageView imageView, Context context, boolean usePicasso) {
        this.imageUrl = imageUrl;
        this.imageView = imageView;
        this.context = context;
        this.usePicasso = usePicasso;
    }

    public LoadImageTask(String imageUrl, AppBarLayout appBar, Context context, boolean usePicasso) {
        this.imageUrl = imageUrl;
        this.appBar = appBar;
        this.context = context;
        this.usePicasso = usePicasso;
    }

    private Bitmap getFromPicasso(){
        Bitmap bitmap = null;
        try {
            bitmap = Picasso.with(context)
                    .load(this.imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .get();
            if(bitmap == null && Data.hasConnection){
                Picasso.with(context)
                        .load(this.imageUrl)
                        .get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        //use picasso first
        Bitmap bitmap = this.usePicasso ? getFromPicasso() : null;
        try {
            if(bitmap == null){
                //try to get bitmap from memory cache
                bitmap = MemoryCache.getBitmapFromMemCache(this.imageUrl);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(this.imageUrl).getContent());
                    //add to memory cache
                    MemoryCache.addBitmapToMemoryCache(this.imageUrl, bitmap);
                }
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
            Drawable drawableImage = new BitmapDrawable(context.getResources(), image);
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
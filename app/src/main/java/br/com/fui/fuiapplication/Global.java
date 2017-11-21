package br.com.fui.fuiapplication;

import android.app.Application;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import br.com.fui.fuiapplication.cache.MemoryCache;

public class Global extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Picasso
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);

        //start memory cache: (not picasso)
        MemoryCache.start();
    }
}
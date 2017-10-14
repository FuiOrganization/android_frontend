package br.com.fui.fuiapplication.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import br.com.fui.fuiapplication.R;

/**
 * Created by guilherme on 19/09/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Experience experiences[] = {};

    public ImageAdapter(Context c, Experience recommendations[]) {
        mContext = c;
        this.experiences = recommendations;
    }

    public int getCount() {
        return experiences.length;
    }

    public Object getItem(int position) {
        return experiences[position];
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new Layout containing ImageView and Text View for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout;
        LinearLayout shadowingLayout;
        RelativeLayout relativeLayout;
        ImageView experienceImage;
        TextView experienceTitle;
        TextView sponsorship;
        //if convertview isn't created yet
        if (convertView == null) {
            linearLayout = new LinearLayout(mContext);

            linearLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

            //fix padding according to position

            if (position % 2 == 0) {
                //left, top, right, bottom
                linearLayout.setPadding(10, 10, 5, 10);
            } else {
                //left, top, right, bottom
                linearLayout.setPadding(5, 10, 10, 10);
            }

            //increase top padding for two first ones

            if (position <= 1) {
                linearLayout.setPadding(
                        linearLayout.getPaddingLeft(),
                        linearLayout.getPaddingTop() + 10,
                        linearLayout.getPaddingRight(),
                        linearLayout.getPaddingBottom()
                );
            }

            //to shadow be able to work
            linearLayout.setClipToPadding(false);

            //for shadowing purpose
            shadowingLayout = new LinearLayout(mContext);

            //shadowing works from sdk 21
            if (Build.VERSION.SDK_INT >= 21) {
                shadowingLayout.setElevation(5);
                shadowingLayout.setBackgroundColor(Color.parseColor("white"));
            }

            shadowingLayout.setOrientation(LinearLayout.VERTICAL);
            shadowingLayout.setGravity(Gravity.CENTER);
            shadowingLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

            relativeLayout = new RelativeLayout(mContext);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(320, 320));
            RelativeLayout.LayoutParams relativeLayoutParams;

            // if it's not recycled, initialize some attributes
            experienceImage = new ImageView(mContext);
            experienceImage.setLayoutParams(new GridView.LayoutParams(320, 320));
            experienceImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //left, top, right, bottom
            experienceImage.setPadding(0, 0, 0, 0);

            //add to relativelayout
            relativeLayout.addView(experienceImage);

            relativeLayoutParams = new RelativeLayout.LayoutParams
                    (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

            //create sponsorship label
            sponsorship = new TextView(mContext);
            sponsorship.setBackgroundColor(Color.parseColor("#FFC000"));
            sponsorship.setWidth(150);
            sponsorship.setHeight(60);
            sponsorship.setTextColor(Color.parseColor("#ffffff"));
            sponsorship.setGravity(Gravity.CENTER);
            sponsorship.setLayoutParams(relativeLayoutParams);
            if (!experiences[position].isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }
            relativeLayout.addView(sponsorship);

            shadowingLayout.addView(relativeLayout);

            //experience name
            experienceTitle = new TextView(mContext);
            experienceTitle.setGravity(Gravity.CENTER);
            experienceTitle.setPadding(0, 10, 0, 10);
            shadowingLayout.addView(experienceTitle);
            linearLayout.addView(shadowingLayout);

        } else {
            //get data
            linearLayout = (LinearLayout) convertView;
            shadowingLayout = (LinearLayout) linearLayout.getChildAt(0);
            relativeLayout = (RelativeLayout) shadowingLayout.getChildAt(0);
            experienceTitle = (TextView) shadowingLayout.getChildAt(1);
            experienceImage = (ImageView) relativeLayout.getChildAt(0);
            sponsorship = (TextView) relativeLayout.getChildAt(1);
            if (!experiences[position].isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }
        }

        //set image and title on asynctask
        LoadImage loadImageTask = new LoadImage(experiences[position].getImage(), experienceImage);
        loadImageTask.execute((Void) null);
        experienceTitle.setText(experiences[position].getTitle());
        //if is sponsored, add label
        if (experiences[position].isSponsored()) {
            sponsorship.setText(mContext.getString(R.string.sponsored_label));
        }

        return linearLayout;
    }

    public class LoadImage extends AsyncTask<Void, Void, Bitmap> {
        String imageUrl = "";
        ImageView imageView;

        LoadImage(String imageUrl, ImageView imageView) {
            this.imageUrl = imageUrl;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            //get recommendations
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(this.imageUrl).getContent());
                Log.d("bitmap", bitmap.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(final Bitmap image) {
            imageView.setImageBitmap(image);
        }

        @Override
        protected void onCancelled() {
        }


    }


}

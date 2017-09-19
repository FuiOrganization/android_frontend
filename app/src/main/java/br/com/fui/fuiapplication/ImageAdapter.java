package br.com.fui.fuiapplication;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by guilherme on 19/09/17.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
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
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        ImageView imageView;
        TextView textView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(3, 20, 3, 3);

            //add to layout
            layout.addView(imageView);

            textView = new TextView(mContext);
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);
        } else {
            layout = (LinearLayout) convertView;
            imageView = (ImageView) layout.getChildAt(0);
            textView = (TextView) layout.getChildAt(1);
        }

        imageView.setImageResource(experiences[position].getImageId());
        textView.setText(experiences[position].getTitle());

        return layout;
    }

    // references to our experiences
    private Experience experiences[] = {
        new Experience("Ibirapuera", "lorem ipsum dolor sit amet", R.drawable.ibirapuera),
            new Experience("Mercado Municipal", "lorem ipsum dolor sit amet", R.drawable.mercadao),
            new Experience("Santos", "lorem ipsum dolor sit amet", R.drawable.santos),
            new Experience("Sí Señor!", "lorem ipsum dolor sit amet", R.drawable.si_senor),
    };
}

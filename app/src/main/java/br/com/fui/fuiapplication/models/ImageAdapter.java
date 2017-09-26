package br.com.fui.fuiapplication.models;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.fui.fuiapplication.R;

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
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        RelativeLayout relativeLayout;
        ImageView experienceImage;
        TextView experienceTitle;
        TextView sponsorship;

        if (convertView == null) {

            relativeLayout = new RelativeLayout(mContext);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(320, 320));
            RelativeLayout.LayoutParams relativeLayoutParams;

            // if it's not recycled, initialize some attributes
            experienceImage = new ImageView(mContext);
            experienceImage.setLayoutParams(new GridView.LayoutParams(320, 320));
            experienceImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            experienceImage.setPadding(3, 20, 3, 3);

            //add to linearLayout
            relativeLayout.addView(experienceImage);

            relativeLayoutParams = new RelativeLayout.LayoutParams
                        (RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

            sponsorship = new TextView(mContext);
            sponsorship.setBackgroundColor(Color.parseColor("#d40000"));
            sponsorship.setWidth(150);
            sponsorship.setHeight(40);
            sponsorship.setTextColor(Color.parseColor("#ffffff"));
            sponsorship.setGravity(Gravity.CENTER);
            sponsorship.setLayoutParams(relativeLayoutParams);
            if(!experiences[position].isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }
            relativeLayout.addView(sponsorship);

            linearLayout.addView(relativeLayout);


            experienceTitle = new TextView(mContext);
            experienceTitle.setGravity(Gravity.CENTER);
            linearLayout.addView(experienceTitle);

        } else {
            linearLayout = (LinearLayout) convertView;
            relativeLayout = (RelativeLayout) linearLayout.getChildAt(0);
            experienceTitle = (TextView) linearLayout.getChildAt(1);
            experienceImage = (ImageView) relativeLayout.getChildAt(0);
            sponsorship = (TextView) relativeLayout.getChildAt(1);
            if(!experiences[position].isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }
        }

        experienceImage.setImageResource(experiences[position].getImageId());
        experienceTitle.setText(experiences[position].getTitle());
        if(experiences[position].isSponsored()) {
            sponsorship.setText(mContext.getString(R.string.sponsored_label));
        }

        return linearLayout;
    }

    // references to our experiences
    private Experience experiences[] = {
        new Experience("Ibirapuera", "lorem ipsum dolor sit amet", R.drawable.ibirapuera, false),
            new Experience("Mercado Municipal", "lorem ipsum dolor sit amet", R.drawable.mercadao, true),
            new Experience("Santos", "lorem ipsum dolor sit amet", R.drawable.santos, false),
            new Experience("Sí Señor!", "lorem ipsum dolor sit amet", R.drawable.si_senor, true),
    };
}

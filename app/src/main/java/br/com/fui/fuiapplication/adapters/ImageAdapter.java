package br.com.fui.fuiapplication.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.tasks.LoadImageTask;

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
            linearLayout = parent.findViewById(R.id.experience_box);

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

            experienceImage = parent.findViewById(R.id.experience_box_image);

            sponsorship = parent.findViewById(R.id.experience_box_sponsorship);

            if (!experiences[position].isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }

            experienceTitle = parent.findViewById(R.id.experience_box_title);

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

            //if it's the same source
            if (experienceTitle.getText().equals(experiences[position].getTitle())) {
                return linearLayout;
            }
        }

        //set image and title on asynctask
        LoadImageTask loadImageTaskTask = new LoadImageTask(experiences[position].getImage(), experienceImage);
        loadImageTaskTask.execute((Void) null);
        experienceTitle.setText(experiences[position].getTitle());
        //if is sponsored, add label
        if (experiences[position].isSponsored()) {
            sponsorship.setText(mContext.getString(R.string.sponsored_label));
        }

        return linearLayout;
    }


}

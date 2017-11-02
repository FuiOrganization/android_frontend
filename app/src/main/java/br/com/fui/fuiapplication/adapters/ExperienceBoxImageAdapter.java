package br.com.fui.fuiapplication.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.helpers.ResolutionHelper;
import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.tasks.LoadImageTask;

/**
 * Created by guilherme on 19/09/17.
 */

public class ExperienceBoxImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Experience> experiences;

    public ExperienceBoxImageAdapter(Context c, ArrayList<Experience> recommendations) {
        mContext = c;
        this.experiences = recommendations;
    }

    public int getCount() {
        return experiences.size();
    }

    public Object getItem(int position) {
        return experiences.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new Layout containing ImageView and Text View for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout;
        LinearLayout shadowingLayout;
        LinearLayout titleLayout;
        RelativeLayout relativeLayout;
        ImageView experienceImage;
        TextView experienceTitle;
        ImageView pinIcon;
        TextView sponsorship;

        //if convertView isn't created yet
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.experience_box, null);
            linearLayout = convertView.findViewById(R.id.experience_box);

            //Adjust size for multiple screens
            int leftOnesTop = ResolutionHelper.getAdjustedPixels(25, ResolutionHelper.WIDTH);
            int rightOnesTop = ResolutionHelper.getAdjustedPixels(15, ResolutionHelper.WIDTH);
            int imageSize = ResolutionHelper.getAdjustedPixels(320, ResolutionHelper.WIDTH);
            int singleHeight = ResolutionHelper.getAdjustedPixels(10, ResolutionHelper.HEIGHT);
            int doubleHeight = ResolutionHelper.getAdjustedPixels(20, ResolutionHelper.HEIGHT);

            //fix padding according to position
            //left ones
            if (position % 2 == 0) {
                //left, top, right, bottom
                linearLayout.setPadding(leftOnesTop, singleHeight, 0, singleHeight);
            } else {
                //right ones
                linearLayout.setPadding(rightOnesTop, singleHeight, 0, singleHeight);
            }

            //increase top padding for two first ones
            if (position <= 1) {
                linearLayout.setPadding(
                        linearLayout.getPaddingLeft(),
                        linearLayout.getPaddingTop() + doubleHeight,
                        linearLayout.getPaddingRight(),
                        linearLayout.getPaddingBottom()
                );
            }

            relativeLayout = convertView.findViewById(R.id.experience_box_relative_layout);
            relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));

            experienceImage = convertView.findViewById(R.id.experience_box_image);
            experienceImage.setLayoutParams(new RelativeLayout.LayoutParams(imageSize, imageSize));

            sponsorship = convertView.findViewById(R.id.experience_box_sponsorship);

            if (!experiences.get(position).isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }

            experienceTitle = convertView.findViewById(R.id.experience_box_title);
            pinIcon = convertView.findViewById(R.id.experience_box_ic_pin);

        } else {
            //get data
            linearLayout = (LinearLayout) convertView;
            shadowingLayout = (LinearLayout) linearLayout.getChildAt(0);
            relativeLayout = (RelativeLayout) shadowingLayout.getChildAt(0);
            titleLayout = (LinearLayout) shadowingLayout.getChildAt(1);
            pinIcon = (ImageView) titleLayout.getChildAt(0);
            experienceTitle = (TextView) titleLayout.getChildAt(1);
            experienceImage = (ImageView) relativeLayout.getChildAt(0);
            sponsorship = (TextView) relativeLayout.getChildAt(1);
            if (!experiences.get(position).isSponsored()) {
                sponsorship.setVisibility(View.INVISIBLE);
            }

            //if it's the same source
            if (experienceTitle.getText().equals(experiences.get(position).getTitle())) {
                return linearLayout;
            }
        }

        //set image and title on asynctask
        LoadImageTask loadImageTaskTask = new LoadImageTask(experiences.get(position).getImage(), experienceImage);
        loadImageTaskTask.execute((Void) null);
        experienceTitle.setText(experiences.get(position).getTitle());

        //if it's sponsored, add label
        if (experiences.get(position).isSponsored()) {
            sponsorship.setText(mContext.getString(R.string.sponsored_label));
        }

        //if user has already visited experience locale, change design
        if (experiences.get(position).hasUserVisited()) {
            pinIcon.setVisibility(View.VISIBLE);
            experienceTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        return linearLayout;
    }


}

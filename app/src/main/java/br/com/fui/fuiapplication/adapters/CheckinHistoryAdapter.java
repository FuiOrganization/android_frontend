package br.com.fui.fuiapplication.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.helpers.ResolutionHelper;
import br.com.fui.fuiapplication.models.Checkin;
import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.tasks.LoadImageTask;

/**
 * Created by guilherme on 19/09/17.
 */

public class CheckinHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Checkin> history;

    public CheckinHistoryAdapter(Context c, ArrayList<Checkin> history) {
        mContext = c;
        this.history = history;
    }

    public int getCount() {
        return history.size();
    }

    public Object getItem(int position) {
        return history.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new Layout containing ImageView and Text View for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout linearLayout;
        LinearLayout shadowingLayout;
        LinearLayout titleLayout;
        RelativeLayout relativeLayout;
        final ImageView experienceImage;
        TextView experienceTitle;
        TextView checkinDate;

        //if convertView isn't created yet
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.checkin_history, null);
            linearLayout = convertView.findViewById(R.id.checkin_history);

            //Adjust size for multiple screens
            int imageSize = ResolutionHelper.getAdjustedPixels(120, ResolutionHelper.WIDTH);
            int singleHeight = ResolutionHelper.getAdjustedPixels(10, ResolutionHelper.HEIGHT);
            int doubleHeight = ResolutionHelper.getAdjustedPixels(20, ResolutionHelper.HEIGHT);
            linearLayout.setPadding(singleHeight, 0, singleHeight, 0);

            //increase top padding for first one
            if (position == 0) {
                linearLayout.setPadding(
                        linearLayout.getPaddingLeft(),
                        linearLayout.getPaddingTop() + doubleHeight,
                        linearLayout.getPaddingRight(),
                        linearLayout.getPaddingBottom()
                );
            }

            relativeLayout = convertView.findViewById(R.id.checkin_history_relative_layout);
            relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));

            experienceImage = convertView.findViewById(R.id.checkin_history_image);
            experienceImage.setLayoutParams(new RelativeLayout.LayoutParams(imageSize, imageSize));

            experienceTitle = convertView.findViewById(R.id.checkin_history_title);

            checkinDate = convertView.findViewById(R.id.checkin_history_date);

        } else {
            //get data
            linearLayout = (LinearLayout) convertView;
            shadowingLayout = (LinearLayout) linearLayout.getChildAt(0);
            relativeLayout = (RelativeLayout) shadowingLayout.getChildAt(0);
            experienceImage = (ImageView) relativeLayout.getChildAt(0);
            titleLayout = (LinearLayout) shadowingLayout.getChildAt(1);
            experienceTitle = (TextView) titleLayout.getChildAt(0);
            checkinDate = (TextView) titleLayout.getChildAt(1);


            //if it's the same source
            if (experienceTitle.getText().equals(history.get(position).getExperience().getTitle())) {
                return linearLayout;
            }
        }

        //set image and title
        //CACHE
        Picasso.with(mContext)
                .load(this.history.get(position).getExperience().getImage())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(experienceImage, new Callback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {
                        //NETWORK
                        if(Data.hasConnection){
                            Picasso.with(mContext)
                                    .load(history.get(position).getExperience().getImage())
                                    .into(experienceImage, new Callback() {

                                        @Override
                                        public void onSuccess() {}

                                        @Override
                                        public void onError() {
                                            //CUSTOM MEMORY CACHE
                                            LoadImageTask imageTask = new LoadImageTask(history.get(position).getExperience().getImage(), experienceImage, mContext, false);
                                            imageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        }
                                    });
                        }
                    }
                });

        experienceTitle.setText(history.get(position).getExperience().getTitle());
        checkinDate.setText(history.get(position).getFormattedDate().toString());

        return linearLayout;
    }


}

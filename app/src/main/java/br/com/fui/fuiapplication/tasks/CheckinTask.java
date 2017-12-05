package br.com.fui.fuiapplication.tasks;


import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.BaseAdapter;

import java.util.Date;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.activities.MainActivity;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.models.Checkin;
import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 01/11/17.
 */

public class CheckinTask extends AsyncTask<Void, Void, Boolean> {
    private Experience experience;
    private View view;
    private boolean realExperienceObject = false;

    public CheckinTask(Experience experience, View view) {
        this.experience = experience;
        this.view = view;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        //do checkin request
        return ExperienceConnector.checkin(CheckinTask.this.experience.getId());
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (result) {
            //if user had not visited until now
            if (!this.experience.hasUserVisited()) {
                //exchange current experience for real experience
                this.experience = this.getRealExperienceObject();
                //change visited param
                this.experience.setUserVisited(true);
                //notify adapter
                ((BaseAdapter) MainActivity.gridRecommendations.getAdapter()).notifyDataSetChanged();
            }

            //add to history
            Data.addNewCheckinToHistory(this.experience);

            //notify adapter
            ((BaseAdapter) MainActivity.gridHistory.getAdapter()).notifyDataSetChanged();

            //show result to user
            Snackbar.make(view, R.string.fui_success_message, Snackbar.LENGTH_LONG)
                    .setAction("fui_link_action", null).show();
        } else {
            Snackbar.make(view, R.string.fui_failure_message, Snackbar.LENGTH_LONG)
                    .setAction("fui_link_action", null).show();
        }
    }

    /**
     * the class' experience object is a clone of the real object because of serialization between activities
     * therefore if you intent to change the object, you must get the real one reference first
     *
     * @return real Experience object
     */
    private Experience getRealExperienceObject() {
        if (this.realExperienceObject) return this.experience;
        for (Experience e2 : Data.recommendations) {
            if (this.experience.getId() == e2.getId()) {
                this.realExperienceObject = true;
                return e2;
            }
        }
        return null;
    }
}

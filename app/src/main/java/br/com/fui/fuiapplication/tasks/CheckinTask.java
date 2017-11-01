package br.com.fui.fuiapplication.tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 01/11/17.
 */

public class CheckinTask extends AsyncTask<Void, Void, Boolean> {
    Experience experience;
    View view;

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
            Snackbar.make(view, R.string.fui_success_message, Snackbar.LENGTH_LONG)
                    .setAction("fui_link_action", null).show();
        } else {
            Snackbar.make(view, R.string.fui_failure_message, Snackbar.LENGTH_LONG)
                    .setAction("fui_link_action", null).show();
        }
    }
}

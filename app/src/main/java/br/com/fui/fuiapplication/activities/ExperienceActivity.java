package br.com.fui.fuiapplication.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.tasks.LoadImageTask;

public class ExperienceActivity extends AppCompatActivity {

    private Experience experience;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);

        Intent i = getIntent();
        experience = (Experience) i.getSerializableExtra("experience");

        FloatingActionButton favorite_button = (FloatingActionButton) findViewById(R.id.favorite_button);
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fui_button = (FloatingActionButton) findViewById(R.id.fui_button);
        fui_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton share_button = (FloatingActionButton) findViewById(R.id.share_button);
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.share_experience_success_message, Snackbar.LENGTH_LONG)
                        .setAction("share_link_action", null).show();
                String URL = getString(R.string.share_experience_checkout_message) +
                        " " +
                        "https://www.fuiapp.com/" +
                        experience.getTitle().replace(' ', '_');
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", URL);
                clipboard.setPrimaryClip(clip);
            }
        });

        //enable display home
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //load description
        TextView description = (TextView) findViewById(R.id.experience_description);
        description.setText(experience.getDescription());

        //set support action bar title
        setTitle(experience.getTitle());

        //update appbar image
        LoadImageTask loadImageTaskTask = new LoadImageTask(experience.getImage(), appBar);
        loadImageTaskTask.execute((Void) null);
    }
}

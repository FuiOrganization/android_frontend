package br.com.fui.fuiapplication.activities;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.ClipboardManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.models.Experience;

public class ExperienceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBar = (AppBarLayout) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

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

        FloatingActionButton ShareButton = (FloatingActionButton) findViewById(R.id.ShareButton);
        ShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Link de compartilhamento copiado com sucesso!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                String URL = "Confira esse Lugar! Fui! https://www.FuiApp.com/AlgumLocalDahora";
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", URL);
                clipboard.setPrimaryClip(clip);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        Experience experience = (Experience) i.getSerializableExtra("experience");

        TextView description = (TextView) findViewById(R.id.experience_description);
        description.setText(experience.getDescription());

        setTitle(experience.getTitle());

        if (Build.VERSION.SDK_INT >= 16){
            appBar.setBackground(ContextCompat.getDrawable(this, experience.getImageId()));
        }else{
            appBar.setBackgroundDrawable(ContextCompat.getDrawable(this, experience.getImageId()));
        }
    }
}

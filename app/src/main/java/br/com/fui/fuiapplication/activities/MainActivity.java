package br.com.fui.fuiapplication.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;


import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.models.ImageAdapter;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private GridView gridRecommendations;
    private Intent experienceIntent;
    private Experience[] recommendations = {};
    private GetRecommendations getRecommendationsTask;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setVisibility(View.INVISIBLE);
                    gridRecommendations.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_favorites:
                    mTextMessage.setVisibility(View.VISIBLE);
                    gridRecommendations.setVisibility(View.INVISIBLE);
                    mTextMessage.setText(R.string.title_favorites);
                    return true;
                case R.id.navigation_history:
                    mTextMessage.setVisibility(View.VISIBLE);
                    gridRecommendations.setVisibility(View.INVISIBLE);
                    mTextMessage.setText(R.string.title_history);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //close loginActivity
        setResult(RESULT_OK);

        ActionBar actionBar = getSupportActionBar();
        //custom action bar with logo
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        //intent for experience
        experienceIntent = new Intent(this, ExperienceActivity.class);

        mTextMessage = (TextView) findViewById(R.id.message);
        mTextMessage.setVisibility(View.INVISIBLE);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getRecommendationsTask = new GetRecommendations();
        getRecommendationsTask.execute((Void) null);

        gridRecommendations= (GridView) findViewById(R.id.grid_recommendations);

        gridRecommendations.setAdapter(new ImageAdapter(this, recommendations));

        gridRecommendations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                experienceIntent.putExtra("experience", (Experience) gridRecommendations.getAdapter().getItem(position));
                startActivity(experienceIntent);
            }
        });
    }

    public class GetRecommendations extends AsyncTask<Void, Void, Experience[]> {

        GetRecommendations(){
        }

        @Override
        protected Experience[] doInBackground(Void... voids) {
            //get recommendations
            Experience recommendations[] = ExperienceConnector.getRecommendations();
            return recommendations;
        }

        @Override
        protected void onPostExecute(final Experience[] recommendations){
            MainActivity.this.recommendations = recommendations;
            MainActivity.this.gridRecommendations.setAdapter(new ImageAdapter(MainActivity.this, MainActivity.this.recommendations));
        }

        @Override
        protected void onCancelled(){}


    }

}

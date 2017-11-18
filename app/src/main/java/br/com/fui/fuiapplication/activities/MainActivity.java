package br.com.fui.fuiapplication.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.adapters.ExperienceBoxImageAdapter;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.data.CustomSharedPreferences;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.helpers.ResolutionHelper;
import br.com.fui.fuiapplication.models.Experience;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    public static  GridView gridRecommendations;
    private Intent experienceIntent;
    private GetRecommendations getRecommendationsTask;
    private SwipeRefreshLayout experienceSwipeRefresh;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setVisibility(View.GONE);
                    gridRecommendations.setVisibility(View.VISIBLE);
                    experienceSwipeRefresh.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_favorites:
                    mTextMessage.setText(R.string.title_favorites);
                    mTextMessage.setVisibility(View.VISIBLE);
                    gridRecommendations.setVisibility(View.GONE);
                    experienceSwipeRefresh.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_history:
                    mTextMessage.setText(R.string.title_history);
                    mTextMessage.setVisibility(View.VISIBLE);
                    gridRecommendations.setVisibility(View.GONE);
                    experienceSwipeRefresh.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Log.d("main_activity", "Creating main activity");
            setContentView(R.layout.activity_main);

            //start memory cache: deprecated
            //MemoryCache.start();

            //start resolution helper
            ResolutionHelper.start(this);

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

            gridRecommendations = (GridView) findViewById(R.id.grid_recommendations);

            getRecommendationsTask = new GetRecommendations();
            getRecommendationsTask.execute((Void) null);


            gridRecommendations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    experienceIntent.putExtra("experience", (Experience) gridRecommendations.getAdapter().getItem(position));
                    startActivity(experienceIntent);
                }
            });

            experienceSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.experience_swipe_refresh);

            //if recommendations are not loaded yet, start animation
            if (Data.recommendations == null || Data.recommendations.size() == 0) {
                experienceSwipeRefresh.setRefreshing(true);
            }

            //on swipe to refresh
            experienceSwipeRefresh.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            updateExperiences();
                        }
                    }
            );

        }
    }

    /*
     * On swipe action, call this function
     * it must reload the experiences, on grid recommendations task it must cancel the animation
     */
    private void updateExperiences() {
        //hide text message
        mTextMessage.setVisibility(View.GONE);
        //nullify current experiences
        Data.recommendations = null;
        //execute new task
        getRecommendationsTask = new GetRecommendations();
        getRecommendationsTask.execute((Void) null);
    }

    private void showNoMessage(){
        mTextMessage.setVisibility(View.GONE);
    }

    private void showNoData(){
        mTextMessage.setText(R.string.message_error_retrieve_data);
        mTextMessage.setVisibility(View.VISIBLE);
    }

    private void showNoConnectionError(){
        mTextMessage.setText(R.string.no_connection);
        mTextMessage.setVisibility(View.VISIBLE);
    }

    public class GetRecommendations extends AsyncTask<Void, Void, ArrayList<Experience>> {

        GetRecommendations() {
        }

        @Override
        protected ArrayList<Experience> doInBackground(Void... voids) {
            //get recommendations if current data is null
            if (Data.recommendations == null) {
                Data.recommendations = ExperienceConnector.getRecommendations();
                return Data.recommendations;
            }
            return Data.recommendations;
        }

        @Override
        protected void onPostExecute(final ArrayList<Experience> recommendations) {

            //if it couldn't retrieve any data
            if (Data.recommendations == null || Data.recommendations.size() == 0) {
                if(!Data.hasConnection){
                    showNoConnectionError();
                    ArrayList<Experience> cachedExperiences = CustomSharedPreferences.getRecommendations();
                    if(cachedExperiences != null){
                        Data.recommendations = cachedExperiences;
                        MainActivity.this.gridRecommendations.setAdapter(new ExperienceBoxImageAdapter(MainActivity.this, Data.recommendations));
                    }
                }else{
                    showNoData();
                    MainActivity.this.gridRecommendations.setVisibility(View.GONE);
                }
            } else {
                showNoMessage();
                MainActivity.this.gridRecommendations.setVisibility(View.VISIBLE);
                MainActivity.this.gridRecommendations.setAdapter(new ExperienceBoxImageAdapter(MainActivity.this, Data.recommendations));
            }

            //cancel animation
            experienceSwipeRefresh.setRefreshing(false);

        }

        @Override
        protected void onCancelled() {
        }
    }
}

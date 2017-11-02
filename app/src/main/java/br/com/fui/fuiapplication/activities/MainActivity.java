package br.com.fui.fuiapplication.activities;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.adapters.ExperienceBoxImageAdapter;
import br.com.fui.fuiapplication.cache.MemoryCache;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.helpers.ResolutionHelper;
import br.com.fui.fuiapplication.models.Experience;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

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
                    mTextMessage.setVisibility(View.INVISIBLE);
                    gridRecommendations.setVisibility(View.VISIBLE);
                    experienceSwipeRefresh.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_favorites:
                    mTextMessage.setText(R.string.title_favorites);
                    mTextMessage.setVisibility(View.VISIBLE);
                    gridRecommendations.setVisibility(View.INVISIBLE);
                    experienceSwipeRefresh.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_history:
                    mTextMessage.setText(R.string.title_history);
                    mTextMessage.setVisibility(View.VISIBLE);
                    gridRecommendations.setVisibility(View.INVISIBLE);
                    experienceSwipeRefresh.setVisibility(View.INVISIBLE);
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

            //start memory cache
            MemoryCache.start();

            //start resolution helper
            ResolutionHelper.start(this);

            /*ActionBar actionBar = getSupportActionBar();
            //custom action bar with logo
            actionBar.setCustomView(R.layout.action_bar);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            */

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * On swipe action, call this function
     * it must reload the experiences, on grid recommendations task it must cancel the animation
     */
    private void updateExperiences() {
        //hide text message
        mTextMessage.setVisibility(View.INVISIBLE);
        //nullify current experiences
        Data.recommendations = null;
        //execute new task
        getRecommendationsTask = new GetRecommendations();
        getRecommendationsTask.execute((Void) null);
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
                mTextMessage.setText(R.string.message_error_retrieve_data);
                mTextMessage.setVisibility(View.VISIBLE);
                MainActivity.this.gridRecommendations.setVisibility(View.INVISIBLE);
            } else {
                mTextMessage.setVisibility(View.INVISIBLE);
                MainActivity.this.gridRecommendations.setVisibility(View.VISIBLE);
            }

            MainActivity.this.gridRecommendations.setAdapter(new ExperienceBoxImageAdapter(MainActivity.this, Data.recommendations));

            //cancel animation
            experienceSwipeRefresh.setRefreshing(false);

        }

        @Override
        protected void onCancelled() {
        }
    }
}

package br.com.fui.fuiapplication.activities;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;

import java.util.ArrayList;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.adapters.ExperienceBoxImageAdapter;
import br.com.fui.fuiapplication.cache.MemoryCache;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.dialogs.ConfirmationDialog;
import br.com.fui.fuiapplication.helpers.AbstractTimer;
import br.com.fui.fuiapplication.helpers.ResolutionHelper;
import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.tasks.LoadImageTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Intent loginIntent;
    private TextView mTextMessage;
    public static  GridView gridRecommendations;
    private Intent experienceIntent;
    private GetRecommendations getRecommendationsTask;
    private SwipeRefreshLayout experienceSwipeRefresh;
    private View headerLayout;

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
            loginIntent = new Intent(this, LoginActivity.class);
            Log.d("main_activity", "Creating main activity");
            setContentView(R.layout.activity_main);

            //start memory cache
            MemoryCache.start();

            //start resolution helper
            ResolutionHelper.start(this);

            //Add toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //Exchange title for logo
            View logo = getLayoutInflater().inflate(R.layout.logo, null);
            toolbar.setTitle("");
            toolbar.addView(logo);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            //get header layout
            headerLayout = navigationView.getHeaderView(0);

            NavigationHeaderTimer facebookDataTimer = new NavigationHeaderTimer(this);
            facebookDataTimer.run();

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

        if (id == R.id.nav_log_off){

            //confirmation action
            DialogInterface.OnClickListener confirmationAction = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //nullify facebook access token
                    AccessToken.setCurrentAccessToken(null);
                    startActivity(loginIntent);
                    finish();
                }
            };

            //create confirmation dialog
            ConfirmationDialog.create(getResources().getString(R.string.fui_confirmation_title),
                    getResources().getString(R.string.log_out_confirmation_message),
                    this,
                    confirmationAction,
                    null
                    );
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

    public class NavigationHeaderTimer extends AbstractTimer{

        public NavigationHeaderTimer(Activity activity) {
            super(activity);
        }

        @Override
        protected boolean onVerification() {
            //action is executed if name is not equal to empty string
            return !Data.name.equals("");
        }

        @Override
        protected void doInBackground() {
            //change nav header main data
            ImageView navigationProfileImage = headerLayout.findViewById(R.id.nav_header_main_profile_image);
            LoadImageTask profileImageTask = new LoadImageTask(Data.profilePic, navigationProfileImage);
            profileImageTask.execute((Void) null);
            TextView navigationProfileName = headerLayout.findViewById(R.id.nav_header_main_name_text);
            navigationProfileName.setText(Data.name);
            TextView navigationProfileDescription = headerLayout.findViewById(R.id.nav_header_main_name_description);
            navigationProfileDescription.setText(Data.email);
        }
    }
}

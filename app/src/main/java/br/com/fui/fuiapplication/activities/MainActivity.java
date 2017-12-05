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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.adapters.ExperienceBoxImageAdapter;
import br.com.fui.fuiapplication.cache.DiskCache;
import br.com.fui.fuiapplication.connection.ExperienceConnector;
import br.com.fui.fuiapplication.data.CustomSharedPreferences;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.dialogs.ConfirmationDialog;
import br.com.fui.fuiapplication.helpers.AbstractTimer;
import br.com.fui.fuiapplication.helpers.ResolutionHelper;
import br.com.fui.fuiapplication.models.Checkin;
import br.com.fui.fuiapplication.models.Experience;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Intent loginIntent;
    private TextView mTextMessage;
    public static  GridView gridRecommendations;
    private Intent experienceIntent;
    private GetRecommendations getRecommendationsTask;
    private GetHistory getHistoryTask;
    private SwipeRefreshLayout experienceSwipeRefresh;
    private View headerLayout;

    //NAVIGATION ITEMS
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showMainScreen();
                    return true;
                case R.id.navigation_favorites:
                    showMessage(R.string.title_favorites);
                    hideMainScreen();
                    return true;
                case R.id.navigation_history:
                    showMessage(R.string.title_history);
                    hideMainScreen();
                    checkHistory();
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
            //INTENTS
            loginIntent = new Intent(this, LoginActivity.class);
            experienceIntent = new Intent(this, ExperienceActivity.class);

            Log.d("main_activity", "Creating main activity");
            setContentView(R.layout.activity_main);

            //start resolution helper
            ResolutionHelper.start(this);

            //TOOLBAR
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //Exchange title for logo
            View logo = getLayoutInflater().inflate(R.layout.logo, null);
            toolbar.setTitle("");
            toolbar.addView(logo);
            setSupportActionBar(toolbar);

            //NAVIGATION
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

            //NAVIGATION USER PROFILE
            headerLayout = navigationView.getHeaderView(0);
            NavigationHeaderTimer facebookDataTimer = new NavigationHeaderTimer(this);
            facebookDataTimer.run();

            //MESSAGE
            mTextMessage = (TextView) findViewById(R.id.message);
            showNoMessage();

            //RECOMMENDATIONS
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

            //SWIPE REFRESH
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

    /*========================================MESSAGES========================================*/

    private void showMainScreen(){
        showNoMessage();
        gridRecommendations.setVisibility(View.VISIBLE);
        experienceSwipeRefresh.setVisibility(View.VISIBLE);
        mTextMessage.setTextColor(getResources().getColor(R.color.black));
    }

    private void hideMainScreen(){
        gridRecommendations.setVisibility(View.GONE);
        experienceSwipeRefresh.setVisibility(View.GONE);
    }

    private void showMessage(int messageId){
        mTextMessage.setText(messageId);
        mTextMessage.setVisibility(View.VISIBLE);
        mTextMessage.setTextColor(getResources().getColor(R.color.black));
    }

    private void showNoMessage(){
        mTextMessage.setVisibility(View.GONE);
        mTextMessage.setText("");
    }

    private void showNoDataErrorMessage(){
        mTextMessage.setText(R.string.message_error_retrieve_data);
        mTextMessage.setVisibility(View.VISIBLE);
        mTextMessage.setTextColor(getResources().getColor(R.color.alert));
    }

    private void showNoConnectionError(){
        mTextMessage.setText(R.string.no_connection);
        mTextMessage.setVisibility(View.VISIBLE);
        mTextMessage.setTextColor(getResources().getColor(R.color.alert));
    }

    /*========================================NAVIGATION========================================*/

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
                    //clear sharedPreferences
                    CustomSharedPreferences.clear();
                    //delete app disk cache
                    DiskCache.deleteDirectoryTree(MainActivity.this.getCacheDir());
                    //nullify facebook access token
                    AccessToken.setCurrentAccessToken(null);
                    //go to login screen
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

    /**
     * Class used to update navigation user profile
     */
    private class NavigationHeaderTimer extends AbstractTimer{

        private NavigationHeaderTimer(Activity activity) {
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
            final ImageView navigationProfileImage = headerLayout.findViewById(R.id.nav_header_main_profile_image);
            //deprecated
            //LoadImageTask profileImageTask = new LoadImageTask(Data.profilePic, navigationProfileImage, MainActivity.this, false);
            //profileImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //set profile picture
            //CACHE
            Picasso.with(MainActivity.this)
                    .load(Data.profilePic)
                    //deprecated, for some reason, picasso can't cache this image
                    //.networkPolicy(NetworkPolicy.OFFLINE)
                    .into(navigationProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {}

                        @Override
                        public void onError() {
                            //NETWORK
                            //deprecated
                            /*
                            if(Data.hasConnection){
                                Picasso.with(MainActivity.this)
                                        .load(Data.profilePic)
                                        .into(navigationProfileImage);
                            }
                            */
                        }
                    });
            TextView navigationProfileName = headerLayout.findViewById(R.id.nav_header_main_name_text);
            navigationProfileName.setText(Data.name);
            TextView navigationProfileDescription = headerLayout.findViewById(R.id.nav_header_main_name_description);
            navigationProfileDescription.setText(Data.email);
        }
    }

    /*========================================EXPERIENCES========================================*/

    /*
     * On swipe action, call this function
     * it must reload the experiences, on get recommendations task it must cancel the animation
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

    /**
     * Class used to update recommendations screen
     */

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
                        MainActivity.gridRecommendations.setAdapter(new ExperienceBoxImageAdapter(MainActivity.this, Data.recommendations));
                    }
                }else{
                    showNoDataErrorMessage();
                    MainActivity.gridRecommendations.setVisibility(View.GONE);
                }
            } else {
                showNoMessage();
                MainActivity.gridRecommendations.setVisibility(View.VISIBLE);
                MainActivity.gridRecommendations.setAdapter(new ExperienceBoxImageAdapter(MainActivity.this, Data.recommendations));
            }

            //cancel animation
            experienceSwipeRefresh.setRefreshing(false);

        }

        @Override
        protected void onCancelled() {
        }
    }

    /*========================================HISTORY========================================*/
    public void checkHistory(){
        if(Data.history == null){
            updateHistory();
        }
    }

    /*
     * On swipe action, call this function
     * it must reload history, on get history task it must cancel the animation
     */
    private void updateHistory() {
        //hide text message
        mTextMessage.setVisibility(View.GONE);
        //nullify current experiences
        Data.history = null;
        //execute new task
        getHistoryTask = new GetHistory();
        getHistoryTask.execute((Void) null);
    }

    /**
     * Class used to update history screen
     */

    public class GetHistory extends AsyncTask<Void, Void, ArrayList<Checkin>> {

        GetHistory() {
        }

        @Override
        protected ArrayList<Checkin> doInBackground(Void... voids) {
            //get recommendations if current data is null
            if (Data.history == null) {
                Data.history = ExperienceConnector.getHistory();
                return Data.history;
            }
            return Data.history;
        }

        @Override
        protected void onPostExecute(final ArrayList<Checkin> history) {

            //if it couldn't retrieve any data
            if (Data.history == null || Data.history.size() == 0) {
                if(!Data.hasConnection){
                    showNoConnectionError();
                    ArrayList<Checkin> cachedHistory = CustomSharedPreferences.getHistory();
                    if(cachedHistory != null){
                        Data.history = cachedHistory;
                        //TODO gridHistory update
                    }
                }else{
                    showNoDataErrorMessage();
                    //TODO gridHistory GONE
                }
            } else {
                showNoMessage();
                //TODO gridHistory Update

                for(Checkin c : history){
                    Log.d("checkin", c.getExperienceId()+" "+c.getExperienceName()+" "+c.getCreatedAt());
                }
            }

            //cancel animation
            experienceSwipeRefresh.setRefreshing(false);

        }

        @Override
        protected void onCancelled() {
        }
    }

}

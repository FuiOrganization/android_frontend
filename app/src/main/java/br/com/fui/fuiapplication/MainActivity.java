package br.com.fui.fuiapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private GridView gridRecommendations;

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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        gridRecommendations= (GridView) findViewById(R.id.grid_recommendations);
        gridRecommendations.setAdapter(new ImageAdapter(this));

        gridRecommendations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //
            }
        });
    }

}

package de.nicograef.sudokutrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MODE = "de.nicograef.sudokutrainer.MODE";
    public static final String EXTRA_LEVEL = "de.nicograef.sudokutrainer.LEVEL";
    public static final String EXTRA_SUDOKU_OF_THE_WEEK = "de.nicograef.sudokutrainer.SUDOKU_OF_THE_WEEK";
    public static final String EXTRA_SUDOKU_OF_THE_WEEK_TIME = "de.nicograef.sudokutrainer.SUDOKU_OF_THE_WEEK_TIME";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private SharedPreferences mSharedPref;

    private Button btnNew;
    private Button btnLoad;
    private Button btnHighscore;
    private FloatingActionButton fabChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar myToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);

        btnNew = findViewById(R.id.btnNew);
        btnLoad = findViewById(R.id.btnLoad);
        btnHighscore = findViewById(R.id.btnHighscore);
        fabChallenge = findViewById(R.id.fabChallenge);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
        boolean solved = mSharedPref.getBoolean(getString(R.string.sudoku_of_the_week_solved_flag_key), false);
        if (!solved) fabChallenge.show();

        boolean canLoad = mSharedPref.getBoolean(getString(R.string.can_load_key), false);
        btnLoad.setEnabled(canLoad);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFirebaseRemoteConfig.activate();

                    int week_counter = (int) mFirebaseRemoteConfig.getLong("week_counter");
                    String sudoku_of_the_week = mFirebaseRemoteConfig.getString("sudoku_of_the_week");
                    int week_counter_old = mSharedPref.getInt(getString(R.string.week_counter_key), 0);

                    Log.i("MAINACTIVITY", "old counter: " + week_counter_old + ", new counter: " + week_counter);

                    if (week_counter > week_counter_old) {
                        SharedPreferences.Editor editor = mSharedPref.edit();
                        editor.putInt(getString(R.string.week_counter_key), week_counter);
                        editor.putString(getString(R.string.sudoku_of_the_week_key), sudoku_of_the_week);
                        editor.putBoolean(getString(R.string.sudoku_of_the_week_solved_flag_key), false);
                        editor.apply();
                        fabChallenge.show();
                    }

                    Toast.makeText(MainActivity.this, "Updated successfully",  Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Network problems occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
                startActivity(intent);
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SudokuActivity.class);
                intent.putExtra(EXTRA_MODE, "load");
                startActivity(intent);
            }
        });

        btnHighscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HighscoreActivity.class);
                intent.putExtra(EXTRA_LEVEL, "easy");
                startActivity(intent);
            }
        });

        fabChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sudokuOfTheWeek = mSharedPref.getString(getString(R.string.sudoku_of_the_week_key), null);
                if (sudokuOfTheWeek == null) {
                    sudokuOfTheWeek = mFirebaseRemoteConfig.getString("sudoku_of_the_week");
                }
                Intent intent = new Intent(getApplicationContext(), SudokuOfTheWeekActivity.class);
                intent.putExtra(EXTRA_SUDOKU_OF_THE_WEEK, sudokuOfTheWeek);
                startActivity(intent);
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean solved = mSharedPref.getBoolean(getString(R.string.sudoku_of_the_week_solved_flag_key), false);
        if (solved) fabChallenge.hide();

        boolean canLoad = mSharedPref.getBoolean(getString(R.string.can_load_key), false);
        btnLoad.setEnabled(canLoad);
    }
}

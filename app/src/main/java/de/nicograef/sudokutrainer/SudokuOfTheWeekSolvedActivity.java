package de.nicograef.sudokutrainer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static de.nicograef.sudokutrainer.MainActivity.EXTRA_SUDOKU_OF_THE_WEEK_TIME;

public class SudokuOfTheWeekSolvedActivity extends AppCompatActivity {

    private Score mUserTime;
    private DatabaseReference mDatabase;

    private TextView lblAverageTime;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku_of_the_week_solved);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int weekCounter = sharedPref.getInt(getString(R.string.week_counter_key), 1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("sudoku_of_the_week").child("times_week_" + weekCounter).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setAverageTime(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        int userTime = (int) getIntent().getLongExtra(EXTRA_SUDOKU_OF_THE_WEEK_TIME, 0);
        mUserTime = new Score(userTime);

        mDatabase.child("sudoku_of_the_week").child("times_week_" + weekCounter).push().setValue(mUserTime.getScore());

        TextView lblTime = findViewById(R.id.lblUserTime);
        Button btnOkay = findViewById(R.id.btnOkay);
        spinner = findViewById(R.id.spinner);
        lblAverageTime = findViewById(R.id.lblAverageTime);

        lblTime.setText(mUserTime.toString());
        btnOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (isNetworkAvailable()) lblAverageTime.setText("");
        else spinner.setVisibility(View.GONE);
    }

    private void setAverageTime(DataSnapshot dataSnapshot) {
        int counter = 0;
        long sum = 0;
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            sum += (Long) child.getValue();
            counter++;
        }
        Score avgTime = new Score( (int) (sum / counter));

        lblAverageTime.setText(getString(R.string.lblAverageTime) + " " + avgTime.toString());
        spinner.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

package de.nicograef.sudokutrainer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static de.nicograef.sudokutrainer.MainActivity.EXTRA_LEVEL;
import static de.nicograef.sudokutrainer.MainActivity.EXTRA_MODE;

public class LevelActivity extends AppCompatActivity {

    private Button btnEasy;
    private Button btnMedium;
    private Button btnHard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnEasy = (Button) findViewById(R.id.btnEasy);
        btnMedium = (Button) findViewById(R.id.btnMedium);
        btnHard = (Button) findViewById(R.id.btnHard);

        btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SudokuActivity.class);
                intent.putExtra(EXTRA_MODE, "new");
                intent.putExtra(EXTRA_LEVEL, "easy");
                startActivity(intent);
                finish();
            }
        });

        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SudokuActivity.class);
                intent.putExtra(EXTRA_MODE, "new");
                intent.putExtra(EXTRA_LEVEL, "medium");
                startActivity(intent);
                finish();
            }
        });

        btnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SudokuActivity.class);
                intent.putExtra(EXTRA_MODE, "new");
                intent.putExtra(EXTRA_LEVEL, "hard");
                startActivity(intent);
                finish();
            }
        });
    }
}

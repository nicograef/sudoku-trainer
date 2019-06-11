package de.nicograef.sudokutrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import de.nicograef.sudokutrainer.sudoku.Field;

import static de.nicograef.sudokutrainer.MainActivity.EXTRA_LEVEL;

public class SudokuActivity extends AppCompatActivity implements ResetDialogFragment.ResetDialogListener{

    private static final int FIELDSIZE = 9;
    private static final int SUDOKU_COUNT = 25;
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static String level;

    private static final boolean DEBUG = false;

    private InputMethodManager mInputManager;
    private SharedPreferences mSharedPref;

    private EditText[] cells;
    private String[] lastState;
    private char[] currentSudoku;
    private char[] currentSolution;

    private boolean paused = false;
    private boolean generatingSudoku = false;
    private int unsolvedCells = 81;
    private boolean solved = false;

    private Button btnHint;
    private Button btnReset;
    private Button btnCheck;
    private Button btnClean;
    private ImageButton btnPauseResume;
    private TextView lblTimer;
    private TextView lblNumbersLeft;

    private long startTime = 0;
    private long millis;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (paused) { startTime = System.currentTimeMillis() - millis; }
            millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            lblTimer.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mInputManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(getString(R.string.can_load_key), true);
        editor.apply(); // or .commit();

        storeCells();
        lastState = new String[FIELDSIZE * FIELDSIZE];

        lblTimer = (TextView)findViewById(R.id.lblTimer);
        lblNumbersLeft = (TextView)findViewById(R.id.lblNumbersLeft);
        btnHint = (Button) findViewById(R.id.btnHint);
        btnReset = (Button) findViewById(R.id.btnReset);
        btnCheck = (Button) findViewById(R.id.btnCheck);
        btnClean = (Button) findViewById(R.id.btnClean);
        btnPauseResume = (ImageButton)findViewById(R.id.btnPause);

        btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveHint();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetDialog();
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSudoku();
            }
        });
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
            }
        });
        btnPauseResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paused = !paused;
                if (paused) btnPauseResume.setImageResource(R.drawable.ic_timer_black_36dp);
                else btnPauseResume.setImageResource(R.drawable.ic_timer_off_black_36dp);
            }
        });

        if (DEBUG) newSudokuDirectly(getString(R.string.sudoku_of_the_week));
        else {
            String mode = getIntent().getStringExtra(MainActivity.EXTRA_MODE);
            if (mode.equals("load")) {
                loadSudoku();
            } else {
                level = getIntent().getStringExtra(EXTRA_LEVEL);
                newSudoku(level);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
    }

    @Override
    protected void onPause() {
        paused = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        paused = true;
        if (!solved) saveSudoku();
        else {

        }
        super.onDestroy();
    }

    /** solves the sudoku completely */
    public static int[] solveSudoku(int[] sudoku) {

        Field field = new Field(FIELDSIZE, sudoku);
        while (!field.solved) {
            field.computeOptions();
            if (!field.checkForSolvedCells()) {
                break;
            }
        }
        return field.getSolution();
    }

    /** loads a new sudoku based on the level into the field */
    private void newSudoku(String level) {
        Random rand = new Random();
        int randomSudoku = rand.nextInt(SUDOKU_COUNT) + 1;
        char[] sudoku;

        switch(level) {
            case "easy":
                sudoku = getResources().getStringArray(R.array.easy)[randomSudoku].toCharArray();
                break;
            case "medium":
                sudoku = getResources().getStringArray(R.array.medium)[randomSudoku].toCharArray();
                break;
            default:
                sudoku = getResources().getStringArray(R.array.hard)[randomSudoku].toCharArray();
                break;
        }

        currentSudoku = mutateSudoku(sudoku); // mutate sudoku
        currentSolution = intToChar(solveSudoku(charToInt(currentSudoku))); // compute solution

        generatingSudoku = true;
        unsolvedCells = 0;
        // fill grid with the numbers
        for (int i = 0; i < cells.length; ++i) {
            if (currentSudoku[i] != '0') {
                cells[i].setText(currentSudoku[i] + "");
                cells[i].setTextColor(Color.BLACK);
                cells[i].setEnabled(false);
            } else {
                cells[i].setText("");
                cells[i].setTextColor(Color.WHITE);
                cells[i].setEnabled(true);
                ++unsolvedCells;
            }
            lastState[i] = (currentSudoku[i] == '0' ? "" : currentSudoku[i] + "");
        }
        generatingSudoku = false;

        // start timer
        paused = false;
        btnPauseResume.setImageResource(R.drawable.ic_timer_off_black_36dp);
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        // show amount of numbers that are left to solve
        lblNumbersLeft.setText(unsolvedCells + NEW_LINE + getString(R.string.lblNumbersLeftText));

        highlightNumbers("");
    }

    /** loads a new sudoku based on the level into the field */
    private void newSudokuDirectly(String sudoku) {
        currentSudoku = sudoku.toCharArray();
        currentSolution = intToChar(solveSudoku(charToInt(currentSudoku))); // compute solution

        generatingSudoku = true;
        unsolvedCells = 0;
        // fill grid with the numbers
        for (int i = 0; i < cells.length; ++i) {
            if (currentSudoku[i] != '0') {
                cells[i].setText(currentSudoku[i] + "");
                cells[i].setTextColor(Color.BLACK);
                cells[i].setEnabled(false);
            } else {
                cells[i].setText("");
                cells[i].setTextColor(Color.WHITE);
                cells[i].setEnabled(true);
                ++unsolvedCells;
            }
            lastState[i] = (currentSudoku[i] == '0' ? "" : currentSudoku[i] + "");
        }
        generatingSudoku = false;

        // start timer
        paused = false;
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        // show amount of numbers that are left to solve
        lblNumbersLeft.setText(unsolvedCells + NEW_LINE + getString(R.string.lblNumbersLeftText));

        highlightNumbers("");
    }

    /** mutates a sudoku randomly in a way that the result is still a solveable sudoku */
    private char[] mutateSudoku(char[] sudoku) {

        // store the three big rows as parts
        char[][] parts = new char[(int)Math.sqrt(FIELDSIZE)][FIELDSIZE*(int)Math.sqrt(FIELDSIZE)];
        for (int i = 0; i < Math.sqrt(FIELDSIZE); ++i) {
            for (int j = 0; j < FIELDSIZE*(int)Math.sqrt(FIELDSIZE); ++j) {
                parts[i][j] = sudoku[j + (i * 27)];
            }
        }

        // randomly interconvert the rows
        Random rand = new Random();
        int firstRow = rand.nextInt(3);
        int secondRow = rand.nextInt(3);

        char[] temp = parts[firstRow];
        parts[firstRow] = parts[secondRow];
        parts[secondRow] = temp;

        // store the mutated sudoku back to the result
        char[] result = new char[sudoku.length]; // FIELDSIZE * FIELDSIZE
        for (int i = 0; i < Math.sqrt(FIELDSIZE); ++i) {
            for (int j = 0; j < FIELDSIZE*(int)Math.sqrt(FIELDSIZE); ++j) {
                result[j + (i * 27)] = parts[i][j];
            }
        }

        return result;
    }

    /** loads the sudoku saved in the savefile to display */
    public void loadSudoku() {
        char[] loadedSudoku;
        char[] loadedInit;
        long loadedTime;

        String defaultSudoku = getResources().getStringArray(R.array.easy)[0];
        String defaultInit = "";
        for (int i = 0; i < FIELDSIZE * FIELDSIZE; ++i) {
            defaultInit += defaultSudoku.toCharArray()[i] ==  '0' ? '0' : '1';
        }

        loadedTime = mSharedPref.getLong(getString(R.string.save_time_key), 0);
        loadedInit = mSharedPref.getString(getString(R.string.save_init_key), defaultInit).toCharArray();
        loadedSudoku = mSharedPref.getString(getString(R.string.save_sudoku_key), defaultSudoku).toCharArray();
        level = mSharedPref.getString(getString(R.string.save_level_key), getString(R.string.lvlEasy));

        char[] initSudoku = new char[loadedSudoku.length];

        for (int i = 0; i < loadedSudoku.length; ++i) {
            if (loadedInit[i] == '1') initSudoku[i] = loadedSudoku[i];
            else initSudoku[i] = '0';
        }

        currentSudoku = initSudoku;
        currentSolution = intToChar(solveSudoku(charToInt(currentSudoku)));

        generatingSudoku = true;
        unsolvedCells = 0;
        // fill grid with the numbers
        for (int i = 0; i < cells.length; ++i) {
            if (loadedSudoku[i] != '0' && loadedInit[i] == '1') {
                cells[i].setText(loadedSudoku[i] + "");
                cells[i].setTextColor(Color.BLACK);
                cells[i].setEnabled(false);
            } else if (loadedSudoku[i] != '0' && loadedInit[i] == '0') {
                cells[i].setText(loadedSudoku[i] + "");
                cells[i].setTextColor(Color.WHITE);
                cells[i].setEnabled(true);
            } else if (loadedSudoku[i] != '0' && loadedInit[i] == '2') {
                cells[i].setText(loadedSudoku[i] + "");
                cells[i].setTextColor(Color.RED);
                cells[i].setEnabled(true);
            } else {
                cells[i].setText("");
                cells[i].setTextColor(Color.WHITE);
                cells[i].setEnabled(true);
                ++unsolvedCells;
            }
            lastState[i] = (loadedSudoku[i] == '0' ? "" : loadedSudoku[i] + "");
        }
        generatingSudoku = false;

        highlightNumbers("");
        lblNumbersLeft.setText(unsolvedCells + NEW_LINE + getString(R.string.lblNumbersLeftText));

        // start timer
        paused = false;
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis() - loadedTime;
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /** saves the currently displayed sudoku on the savefile */
    public void saveSudoku() {
        String sudoku = "";
        String init = "";
        for (EditText cell : cells) {
            sudoku += (cell.getText().toString().equals("") ? "0" : cell.getText().toString());
            init += (cell.getCurrentTextColor() == Color.BLACK ? "1" : (cell.getCurrentTextColor() == Color.RED ? "2" : "0"));
        }

        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(getString(R.string.save_sudoku_key), sudoku);
        editor.putString(getString(R.string.save_init_key), init);
        editor.putString(getString(R.string.save_level_key), level);
        editor.putLong(getString(R.string.save_time_key), millis);
        editor.apply();

        Toast.makeText(this, getString(R.string.sudoku_has_been_saved), Toast.LENGTH_SHORT).show();
    }

    /** Checks the sudoku for false numbers */
    public void checkSudoku() {
        boolean correct = true;
        boolean freeCellsLeft = false;

        for (int i = 0; i < cells.length; ++i) {
            if (currentSudoku[i] != '0') { continue; }
            if (cells[i].getText().toString().equals("")) { freeCellsLeft = true; continue; }
            if (!cells[i].getText().toString().equals(currentSolution[i] + "")) {
                cells[i].setTextColor(Color.RED);
                correct = false;
            }
        }

        highlightNumbers("");

        final String message = correct ? getString(R.string.checkCorrectText) : getString(R.string.checkNotCorrectText);

        if (correct && !freeCellsLeft) {
            solved = true;
            Toast.makeText(SudokuActivity.this, "SOLVED!", Toast.LENGTH_SHORT).show();
            saveScore();
            showHighscore();
            return;
        }

        for (int i = 0; i < cells.length; ++i) { lastState[i] = cells[i].getText().toString(); }

        Snackbar.make(findViewById(R.id.activity_sudoku), message, Snackbar.LENGTH_SHORT).show();
    }

    /** deletes all wrong numbers */
    public void clean() {
        for (int i = 0; i < cells.length; ++i) {
            if (currentSudoku[i] != '0' || cells[i].getText().toString().equals("")) { continue; }
            if (!cells[i].getText().toString().equals(currentSolution[i] + "")) { cells[i].setText(""); }
        }

        /*
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });
        if (mInterstitialAd.isLoaded()) { mInterstitialAd.show(); }
        */
    }

    /** reveals a randomly selected number from the solution of the sudoku */
    public void giveHint() {

        boolean solved = true;
        for (EditText cell : cells) { if (cell.getText().toString().equals("")) solved = false; }
        if (solved) return;

        Random rand = new Random();
        int index = rand.nextInt(81);
        while (!cells[index].getText().toString().equals("") && cells[index].getCurrentTextColor() != Color.RED) { index = rand.nextInt(81); }
        cells[index].setText(currentSolution[index] + "");
        cells[index].setTextColor(Color.WHITE);

        highlightNumbers("");

    }

    /** resets the sudoku to the original unsolved sudoku */
    @Override
    public void resetFromDialog() {
        for (EditText cell : cells) {
            if (cell.getCurrentTextColor() == Color.BLACK) continue;
            if (!cell.getText().toString().equals("")) cell.setText(""); // check for appropriate number of unsolved cells
            cell.setTextColor(Color.WHITE);
            cell.setEnabled(true);
        }

        highlightNumbers("");

        // start timer
        paused = false;
        btnPauseResume.setImageResource(R.drawable.ic_timer_off_black_36dp);
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        // show amount of numbers that are left to solve
        lblNumbersLeft.setText(unsolvedCells + NEW_LINE + getString(R.string.lblNumbersLeftText));
    }

    private void oneStepBack() {
        for (int i = 0; i < cells.length; ++i) {
            cells[i].setText(lastState[i]);
        }
    }

    /** Hilights all numbers equal to the user input by decreasing the alpha value of the other cells. */
    private void highlightNumbers(String value) {
        for (EditText cell : cells) {
            cell.setAlpha(0.7F);
            if (value.equals("")) continue;
            if (cell.getText().toString().equals(value)) {
                cell.setAlpha(1F);
            }
        }
    }

    public static int[] charToInt(char[] input) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; ++i) { output[i] = Character.getNumericValue(input[i]); }
        return output;
    }

    public static char[] intToChar(int[] input) {
        char[] output = new char[input.length];
        for (int i = 0; i < input.length; ++i) { output[i] = (char)(input[i] + 48); }
        return output;
    }

    /** storing all textviews in an array */
    private void storeCells() {
        cells = new EditText[FIELDSIZE * FIELDSIZE];

        cells[0] = (EditText)findViewById(R.id.cell0);
        cells[1] = (EditText)findViewById(R.id.cell1);
        cells[2] = (EditText)findViewById(R.id.cell2);
        cells[3] = (EditText)findViewById(R.id.cell3);
        cells[4] = (EditText)findViewById(R.id.cell4);
        cells[5] = (EditText)findViewById(R.id.cell5);
        cells[6] = (EditText)findViewById(R.id.cell6);
        cells[7] = (EditText)findViewById(R.id.cell7);
        cells[8] = (EditText)findViewById(R.id.cell8);
        cells[9] = (EditText)findViewById(R.id.cell9);

        cells[10] = (EditText)findViewById(R.id.cell10);
        cells[11] = (EditText)findViewById(R.id.cell11);
        cells[12] = (EditText)findViewById(R.id.cell12);
        cells[13] = (EditText)findViewById(R.id.cell13);
        cells[14] = (EditText)findViewById(R.id.cell14);
        cells[15] = (EditText)findViewById(R.id.cell15);
        cells[16] = (EditText)findViewById(R.id.cell16);
        cells[17] = (EditText)findViewById(R.id.cell17);
        cells[18] = (EditText)findViewById(R.id.cell18);
        cells[19] = (EditText)findViewById(R.id.cell19);

        cells[20] = (EditText)findViewById(R.id.cell20);
        cells[21] = (EditText)findViewById(R.id.cell21);
        cells[22] = (EditText)findViewById(R.id.cell22);
        cells[23] = (EditText)findViewById(R.id.cell23);
        cells[24] = (EditText)findViewById(R.id.cell24);
        cells[25] = (EditText)findViewById(R.id.cell25);
        cells[26] = (EditText)findViewById(R.id.cell26);
        cells[27] = (EditText)findViewById(R.id.cell27);
        cells[28] = (EditText)findViewById(R.id.cell28);
        cells[29] = (EditText)findViewById(R.id.cell29);

        cells[30] = (EditText)findViewById(R.id.cell30);
        cells[31] = (EditText)findViewById(R.id.cell31);
        cells[32] = (EditText)findViewById(R.id.cell32);
        cells[33] = (EditText)findViewById(R.id.cell33);
        cells[34] = (EditText)findViewById(R.id.cell34);
        cells[35] = (EditText)findViewById(R.id.cell35);
        cells[36] = (EditText)findViewById(R.id.cell36);
        cells[37] = (EditText)findViewById(R.id.cell37);
        cells[38] = (EditText)findViewById(R.id.cell38);
        cells[39] = (EditText)findViewById(R.id.cell39);

        cells[40] = (EditText)findViewById(R.id.cell40);
        cells[41] = (EditText)findViewById(R.id.cell41);
        cells[42] = (EditText)findViewById(R.id.cell42);
        cells[43] = (EditText)findViewById(R.id.cell43);
        cells[44] = (EditText)findViewById(R.id.cell44);
        cells[45] = (EditText)findViewById(R.id.cell45);
        cells[46] = (EditText)findViewById(R.id.cell46);
        cells[47] = (EditText)findViewById(R.id.cell47);
        cells[48] = (EditText)findViewById(R.id.cell48);
        cells[49] = (EditText)findViewById(R.id.cell49);

        cells[50] = (EditText)findViewById(R.id.cell50);
        cells[51] = (EditText)findViewById(R.id.cell51);
        cells[52] = (EditText)findViewById(R.id.cell52);
        cells[53] = (EditText)findViewById(R.id.cell53);
        cells[54] = (EditText)findViewById(R.id.cell54);
        cells[55] = (EditText)findViewById(R.id.cell55);
        cells[56] = (EditText)findViewById(R.id.cell56);
        cells[57] = (EditText)findViewById(R.id.cell57);
        cells[58] = (EditText)findViewById(R.id.cell58);
        cells[59] = (EditText)findViewById(R.id.cell59);

        cells[60] = (EditText)findViewById(R.id.cell60);
        cells[61] = (EditText)findViewById(R.id.cell61);
        cells[62] = (EditText)findViewById(R.id.cell62);
        cells[63] = (EditText)findViewById(R.id.cell63);
        cells[64] = (EditText)findViewById(R.id.cell64);
        cells[65] = (EditText)findViewById(R.id.cell65);
        cells[66] = (EditText)findViewById(R.id.cell66);
        cells[67] = (EditText)findViewById(R.id.cell67);
        cells[68] = (EditText)findViewById(R.id.cell68);
        cells[69] = (EditText)findViewById(R.id.cell69);

        cells[70] = (EditText)findViewById(R.id.cell70);
        cells[71] = (EditText)findViewById(R.id.cell71);
        cells[72] = (EditText)findViewById(R.id.cell72);
        cells[73] = (EditText)findViewById(R.id.cell73);
        cells[74] = (EditText)findViewById(R.id.cell74);
        cells[75] = (EditText)findViewById(R.id.cell75);
        cells[76] = (EditText)findViewById(R.id.cell76);
        cells[77] = (EditText)findViewById(R.id.cell77);
        cells[78] = (EditText)findViewById(R.id.cell78);
        cells[79] = (EditText)findViewById(R.id.cell79);

        cells[80] = (EditText)findViewById(R.id.cell80);

        for (EditText cell : cells) {
            // highlighting numbers and closing keyboard
            cell.addTextChangedListener(new SudokuTextWatcher(cell));
        }
    }

    public void showHighscore() {
        Intent intent = new Intent(this, HighscoreActivity.class);
        intent.putExtra(EXTRA_LEVEL, level);
        paused = true;
        startActivity(intent);
        finish();
    }

    /**
     * Show a dialog that asks user to either cancel reset or accept to reset the highscore.
     */
    public void showResetDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = ResetDialogFragment.newInstance("sudoku");
        dialog.show(getSupportFragmentManager(), "ResetDialogFragment");
    }

    private void saveScore() {
        ArrayList<Score> scores = new ArrayList<>();
        SharedPreferences.Editor editor = mSharedPref.edit();
        Log.d(level, "");

        if (level.equals("easy")) {

            scores.add(new Score(mSharedPref.getInt(getString(R.string.easy_key_1), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.easy_key_2), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.easy_key_3), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.easy_key_4), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.easy_key_5), 0)));
            scores.add(new Score((int) (millis / 1000)));
            Collections.sort(scores);

            editor.putInt(getString(R.string.easy_key_1), scores.get(0).getScore());
            editor.putInt(getString(R.string.easy_key_2), scores.get(1).getScore());
            editor.putInt(getString(R.string.easy_key_3), scores.get(2).getScore());
            editor.putInt(getString(R.string.easy_key_4), scores.get(3).getScore());
            editor.putInt(getString(R.string.easy_key_5), scores.get(4).getScore());
        }

        else if (level.equals("medium")) {

            scores.add(new Score(mSharedPref.getInt(getString(R.string.medium_key_1), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.medium_key_2), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.medium_key_3), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.medium_key_4), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.medium_key_5), 0)));
            scores.add(new Score((int) (millis / 1000)));
            Collections.sort(scores);

            editor.putInt(getString(R.string.medium_key_1), scores.get(0).getScore());
            editor.putInt(getString(R.string.medium_key_2), scores.get(1).getScore());
            editor.putInt(getString(R.string.medium_key_3), scores.get(2).getScore());
            editor.putInt(getString(R.string.medium_key_4), scores.get(3).getScore());
            editor.putInt(getString(R.string.medium_key_5), scores.get(4).getScore());
        }

        else if (level.equals("hard")) {

            scores.add(new Score(mSharedPref.getInt(getString(R.string.hard_key_1), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.hard_key_2), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.hard_key_3), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.hard_key_4), 0)));
            scores.add(new Score(mSharedPref.getInt(getString(R.string.hard_key_5), 0)));
            scores.add(new Score((int) (millis / 1000)));
            Collections.sort(scores);

            editor.putInt(getString(R.string.hard_key_1), scores.get(0).getScore());
            editor.putInt(getString(R.string.hard_key_2), scores.get(1).getScore());
            editor.putInt(getString(R.string.hard_key_3), scores.get(2).getScore());
            editor.putInt(getString(R.string.hard_key_4), scores.get(3).getScore());
            editor.putInt(getString(R.string.hard_key_5), scores.get(4).getScore());
        }

        editor.putBoolean(getString(R.string.can_load_key), false);
        editor.apply(); // or .commit();
    }

    private class SudokuTextWatcher implements TextWatcher {
        private EditText mEditText;

        public SudokuTextWatcher(EditText e) {
            mEditText = e;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!generatingSudoku) highlightNumbers(s.toString());
            if (mEditText.getCurrentTextColor() == Color.RED)
                mEditText.setTextColor(Color.WHITE);
        }

        public void afterTextChanged(Editable s) {
            if (!generatingSudoku) {
                // update counter for unsolved cells only when not generating a new sudoku
                if (s.toString().equals("")) ++unsolvedCells;
                else --unsolvedCells;
                lblNumbersLeft.setText(unsolvedCells + NEW_LINE + getString(R.string.lblNumbersLeftText));
            }

            // close Keyboard after changing text
            mInputManager.hideSoftInputFromWindow(cells[0].getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

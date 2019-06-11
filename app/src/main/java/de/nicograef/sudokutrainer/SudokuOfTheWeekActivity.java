package de.nicograef.sudokutrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import static de.nicograef.sudokutrainer.MainActivity.EXTRA_SUDOKU_OF_THE_WEEK_TIME;

public class SudokuOfTheWeekActivity extends AppCompatActivity {

    private static final int FIELDSIZE = 9;

    private char[] currentSudoku;
    private char[] currentSolution;

    private SharedPreferences mSharedPref;
    private InputMethodManager mInputManager;

    private EditText[] cells;
    private TextView lblTimer;

    private boolean generatingSudoku = false;
    private int unsolvedCells = 81;
    private long startTime = 0;
    private long millis;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
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
        setContentView(R.layout.activity_sudoku_of_the_week);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getApplication());
        mInputManager = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

        lblTimer = (TextView)findViewById(R.id.lblTimer);
        storeCells();
        lblTimer.requestFocus();

        String sudokuOfTheWeek = getIntent().getStringExtra(MainActivity.EXTRA_SUDOKU_OF_THE_WEEK);
        currentSudoku = sudokuOfTheWeek.toCharArray();

        start();
    }

    /** loads a new sudoku into the field */
    private void start() {

        // compute solution
        currentSolution = SudokuActivity.intToChar(
                SudokuActivity.solveSudoku(
                        SudokuActivity.charToInt(currentSudoku)));

        generatingSudoku = true;
        unsolvedCells = 0;
        // fill grid with the numbers
        for (int i = 0; i < cells.length; ++i) {
            if (currentSudoku[i] != '0') {
                cells[i].setText(currentSudoku[i] + "");
                cells[i].setTextColor(Color.BLACK);
                cells[i].setEnabled(false);
                cells[i].setFocusable(false);
            } else {
                cells[i].setText("");
                cells[i].setTextColor(Color.WHITE);
                cells[i].setEnabled(true);
                ++unsolvedCells;
            }
        }
        generatingSudoku = false;

        highlightNumbers("");

        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /** Checks the sudoku for complete solve */
    public void checkSudoku() {

        for (int i = 0; i < cells.length; ++i) {
            if (currentSudoku[i] != '0') { continue; }
            else if (cells[i].getText().toString().equals("")) { return; }
            else if (!cells[i].getText().toString().equals(currentSolution[i] + "")) { return; }
        }

        highlightNumbers("");

        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(getString(R.string.sudoku_of_the_week_solved_flag_key), true);
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), SudokuOfTheWeekSolvedActivity.class);
        intent.putExtra(EXTRA_SUDOKU_OF_THE_WEEK_TIME, millis / 1000);
        startActivity(intent);
        finish();
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
            cell.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    highlightNumbers(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (!generatingSudoku) {
                        // update counter for unsolved cells only when not generating a new sudoku
                        if (s.toString().equals("")) ++unsolvedCells;
                        else --unsolvedCells;
                    }

                    // close keyboard after changing text
                    mInputManager.hideSoftInputFromWindow(cells[0].getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    if (unsolvedCells == 0) { checkSudoku(); }
                }
            });
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
}

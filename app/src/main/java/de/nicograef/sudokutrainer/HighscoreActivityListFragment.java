package de.nicograef.sudokutrainer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;


public class HighscoreActivityListFragment extends ListFragment {

    int level;

    public static HighscoreActivityListFragment newInstance(int level) {
        HighscoreActivityListFragment fragment = new HighscoreActivityListFragment();
        Bundle args = new Bundle();
        args.putInt("level", level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        level = getArguments().getInt("level", 0);
        update();
    }

    //TODO: Give user the option to delete single score on click / or on long click.

    public void update() {
        ArrayList<Score> scores = new ArrayList<>();
        String[] times = new String[5];
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (level == 0) { // easy
            scores.add(new Score(sharedPref.getInt(getString(R.string.easy_key_1), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.easy_key_2), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.easy_key_3), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.easy_key_4), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.easy_key_5), 0)));
        }

        else if (level == 1) { // medium
            scores.add(new Score(sharedPref.getInt(getString(R.string.medium_key_1), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.medium_key_2), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.medium_key_3), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.medium_key_4), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.medium_key_5), 0)));
        }

        else if (level == 2) { // hard
            scores.add(new Score(sharedPref.getInt(getString(R.string.hard_key_1), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.hard_key_2), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.hard_key_3), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.hard_key_4), 0)));
            scores.add(new Score(sharedPref.getInt(getString(R.string.hard_key_5), 0)));
        }

        Collections.sort(scores);

        times[0] = scores.get(0).toString();
        times[1] = scores.get(1).toString();
        times[2] = scores.get(2).toString();
        times[3] = scores.get(3).toString();
        times[4] = scores.get(4).toString();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_highscore_list_item, times);
        setListAdapter(adapter);
    }

    public void reset() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        if (level == 0) {
            editor.putInt(getString(R.string.easy_key_1), 0);
            editor.putInt(getString(R.string.easy_key_2), 0);
            editor.putInt(getString(R.string.easy_key_3), 0);
            editor.putInt(getString(R.string.easy_key_4), 0);
            editor.putInt(getString(R.string.easy_key_5), 0);
            editor.apply(); // or .commit();
        }

        else if (level == 1) {
            editor.putInt(getString(R.string.medium_key_1), 0);
            editor.putInt(getString(R.string.medium_key_2), 0);
            editor.putInt(getString(R.string.medium_key_3), 0);
            editor.putInt(getString(R.string.medium_key_4), 0);
            editor.putInt(getString(R.string.medium_key_5), 0);
            editor.apply(); // or .commit();
        }

        else if (level == 2) {
            editor.putInt(getString(R.string.hard_key_1), 0);
            editor.putInt(getString(R.string.hard_key_2), 0);
            editor.putInt(getString(R.string.hard_key_3), 0);
            editor.putInt(getString(R.string.hard_key_4), 0);
            editor.putInt(getString(R.string.hard_key_5), 0);
            editor.apply(); // or .commit();
        }

        editor.commit();
    }

    // just for debugging and screenshots
    public void fillWithDummyScores() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getString(R.string.easy_key_1), 323);
        editor.putInt(getString(R.string.easy_key_2), 334);
        editor.putInt(getString(R.string.easy_key_3), 445);
        editor.putInt(getString(R.string.easy_key_4), 556);
        editor.putInt(getString(R.string.easy_key_5), 667);
        editor.apply(); // or .commit();

        editor.putInt(getString(R.string.medium_key_1), 435);
        editor.putInt(getString(R.string.medium_key_2), 446);
        editor.putInt(getString(R.string.medium_key_3), 557);
        editor.putInt(getString(R.string.medium_key_4), 668);
        editor.putInt(getString(R.string.medium_key_5), 779);
        editor.apply(); // or .commit();


        editor.putInt(getString(R.string.hard_key_1), 647);
        editor.putInt(getString(R.string.hard_key_2), 758);
        editor.putInt(getString(R.string.hard_key_3), 769);
        editor.putInt(getString(R.string.hard_key_4), 871);
        editor.putInt(getString(R.string.hard_key_5), 982);
        editor.apply(); // or .commit();

        update();
    }
}

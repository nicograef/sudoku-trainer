package de.nicograef.sudokutrainer;

import android.support.v4.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import static de.nicograef.sudokutrainer.MainActivity.EXTRA_LEVEL;

public class HighscoreActivity extends AppCompatActivity implements ResetDialogFragment.ResetDialogListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String level = getIntent().getStringExtra(EXTRA_LEVEL);
        int pageToGo = level.equals("easy") ? 0 : level.equals("medium") ? 1 : 2;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(pageToGo);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_highscore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            showResetDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void resetFromDialog() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        if (mViewPager.getCurrentItem() == 0) {
            editor.putInt(getString(R.string.easy_key_1), 0);
            editor.putInt(getString(R.string.easy_key_2), 0);
            editor.putInt(getString(R.string.easy_key_3), 0);
            editor.putInt(getString(R.string.easy_key_4), 0);
            editor.putInt(getString(R.string.easy_key_5), 0);
            editor.apply(); // or .commit();
        }

        else if (mViewPager.getCurrentItem() == 1) {
            editor.putInt(getString(R.string.medium_key_1), 0);
            editor.putInt(getString(R.string.medium_key_2), 0);
            editor.putInt(getString(R.string.medium_key_3), 0);
            editor.putInt(getString(R.string.medium_key_4), 0);
            editor.putInt(getString(R.string.medium_key_5), 0);
            editor.apply(); // or .commit();
        }

        else if (mViewPager.getCurrentItem() == 2) {
            editor.putInt(getString(R.string.hard_key_1), 0);
            editor.putInt(getString(R.string.hard_key_2), 0);
            editor.putInt(getString(R.string.hard_key_3), 0);
            editor.putInt(getString(R.string.hard_key_4), 0);
            editor.putInt(getString(R.string.hard_key_5), 0);
            editor.apply(); // or .commit();
        }

        editor.commit();

        mViewPager.getAdapter().notifyDataSetChanged(); // calls getItemPosition()
    }

    /**
     * Show a dialog that asks user to either cancel reset or accept to reset the highscore.
     */
    public void showResetDialog() {
        int item = mViewPager.getCurrentItem();
        String level = mViewPager.getAdapter().getPageTitle(item).toString();

        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = ResetDialogFragment.newInstance(level);
        dialog.show(getSupportFragmentManager(), "ResetDialogFragment");
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return HighscoreActivityListFragment.newInstance(position);
        }

        // for updating the fragment view. called when user clicks on "reset".
        @Override
        public int getItemPosition(Object object) {
            HighscoreActivityListFragment listFragment = (HighscoreActivityListFragment) object;
            if (listFragment != null) {
                // listFragment.fillWithDummyScores(); // DEBUG !!
                listFragment.update();
            }
            return super.getItemPosition(object);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.lvlEasy);
                case 1:
                    return getString(R.string.lvlMedium);
                case 2:
                    return getString(R.string.lvlHard);
            }
            return null;
        }
    }
}

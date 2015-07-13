package com.fizix.android.easysudoku.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fizix.android.easysudoku.Board;
import com.fizix.android.easysudoku.R;
import com.fizix.android.easysudoku.data.DbHelper;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String PREFS_NAME = "easy_sudoku_prefs";
    private static final String PREF_DIFFICULTY = "pref_difficulty";

    // The difficulty we are currently playing.
    private int mDifficulty = Board.DIFFICULTY_NONE;

    // Helper we use to get the board data from the database.
    private DbHelper mDbHelper;

    // The board we are currently playing.
    Board mBoard = null;

    // The DrawerLayout for this activity.
    DrawerLayout mDrawerLayout;

    // The DrawerLayout toggle.
    ActionBarDrawerToggle mDrawerToggle;

    // The list of difficulties.
    ListView mDifficultyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Get the difficulty from the preferences.
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
        mDifficulty = prefs.getInt(PREF_DIFFICULTY, Board.DIFFICULTY_EASY);

        // Set up the ListView in the drawer.
        mDifficultyList = (ListView) findViewById(R.id.difficulty_list);
        mDifficultyList.setOnItemClickListener(this);
        mDifficultyList.setSelection(mDifficulty - 1);

        // Create the DB helper.
        mDbHelper = new DbHelper(this);

        switchToBoard(mDifficulty);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            mBoard.createNew();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG, "Drawer item clicked: " + position);

        // Switch to the board with the difficulty the user selected.
        int difficulty = position + 1;
        switchToBoard(difficulty);

        // Store the difficulty in the preferences.
        getSharedPreferences(PREFS_NAME, 0).edit().putInt(PREF_DIFFICULTY, difficulty).commit();

        // Close the drawer.
        mDrawerLayout.closeDrawer(GravityCompat.START);

        // Set the new selected item in the difficulty list.
        mDifficultyList.setSelection(position);
    }

    private void switchToBoard(int difficulty) {
        // If a board already exists, save it first.
        if (mBoard != null) {
            mBoard.saveToDb(mDbHelper);
        }

        // Create the board.
        mBoard = new Board(difficulty);

        // Load the board from the database.
        mBoard.loadFromDb(mDbHelper);

        // Create the fragment that will hold the board.
        PlayFragment playFragment = PlayFragment.newInstance(mBoard);

        // Replace the fragment inside the container.
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.play_fragment_placeholder, playFragment)
                .commit();
    }

}

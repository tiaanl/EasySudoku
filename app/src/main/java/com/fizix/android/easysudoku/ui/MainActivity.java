package com.fizix.android.easysudoku.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.fizix.android.easysudoku.Board;
import com.fizix.android.easysudoku.R;
import com.fizix.android.easysudoku.data.DbHelper;
import com.fizix.android.easysudoku.views.BoardView;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Board mBoard;
    private ButtonsFragment mButtonsFragment;
    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make sure we can access the database.
        mDbHelper = new DbHelper(this);
        mBoard = new Board(0);
        mBoard.loadFromDb(mDbHelper);

        BoardView boardView = (BoardView) findViewById(R.id.board);
        boardView.setBoard(mBoard);

        mButtonsFragment = (ButtonsFragment) getSupportFragmentManager().findFragmentById(R.id.buttonsFragment);
        mButtonsFragment.setBoard(mBoard);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBoard.saveToDb(mDbHelper);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBoard.loadFromDb(mDbHelper);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

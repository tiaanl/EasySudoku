package com.fizix.android.easysudoku;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fizix.android.easysudoku.data.Contract.Boards;
import com.fizix.android.easysudoku.data.Contract.Blocks;
import com.fizix.android.easysudoku.data.DbHelper;

import java.util.HashSet;
import java.util.Set;


public class Board {

    private static final String LOG_TAG = Board.class.getSimpleName();

    // The type of block.
    private int mBoardType;

    // The numbers on the board.
    private Block mBlocks[];

    // Selected block position.
    private int mSelectedBlockX;
    private int mSelectedBlockY;

    // The currently selected number.
    private int mActionNumber;

    public interface Listener {
        void onSelectedBlockChanged(int x, int y, int number);
        void onActionNumberChanged(int actionNumber);
        void onNumbersChanged(int x, int y, int number);
    }

    Set<Listener> mListeners = new HashSet<>();

    public Board(int boardType) {
        mBoardType = boardType;

        mBlocks = new Block[9 * 9];

        for (int i = 0; i < mBlocks.length; ++i) {
            mBlocks[i] = new Block(i % 9, i / 9, 0);
        }

        setNumberAt(7, 4, 8);

        mSelectedBlockX = 1;
        mSelectedBlockY = 1;

        mActionNumber = 1;
    }

    public int getNumberAt(int x, int y) {
        final int index = (y - 1) * 9 + (x - 1);
        assert (index >= 0 && index < 81);
        return mBlocks[index].getNumber();
    }

    public void setNumberAt(int x, int y, int number) {
        final int index = (y - 1) * 9 + (x - 1);
        assert (index >= 0 && index < 81);
        mBlocks[index].setNumber(number);

        for (Listener listener : mListeners) {
            listener.onNumbersChanged(x, y, number);
        }
    }

    public int getSelectedBlockX() {
        return mSelectedBlockX;
    }

    public int getSelectedBlockY() {
        return mSelectedBlockY;
    }

    public boolean hasSelectedBlock() {
        return (mSelectedBlockX >= 1 && mSelectedBlockX <= 9) && (mSelectedBlockY >= 1 && mSelectedBlockY <= 9);
    }

    public void setSelectedBlock(int x, int y) {
        Log.d(LOG_TAG, String.format("setSelectedBlock(%d, %d)", x, y));
        assert ((x >= 1 && x <= 9) && (y >= 1 && y <= 9));
        mSelectedBlockX = x;
        mSelectedBlockY = y;

        // When the selected block changed and it is filled in, then make it the selected number,
        // otherwise fill in the number.
        int selectedNumber = getNumberAt(mSelectedBlockX, mSelectedBlockY);
        if (selectedNumber != 0) {
            if (mActionNumber == 0) {
                setNumberAt(mSelectedBlockX, mSelectedBlockY, 0);
            } else {
                setActionNumber(selectedNumber);
            }
        } else {
            setNumberAt(mSelectedBlockX, mSelectedBlockY, mActionNumber);
        }

        for (Listener listener : mListeners) {
            listener.onSelectedBlockChanged(mSelectedBlockX, mSelectedBlockY, getNumberAt(mSelectedBlockX, mSelectedBlockY));
        }
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    public void setActionNumber(int actionNumber) {
        mActionNumber = actionNumber;

        for (Listener listener : mListeners) {
            listener.onActionNumberChanged(actionNumber);
        }
    }

    public int getActionNumber() {
        return mActionNumber;
    }

    public void saveToDb(DbHelper dbHelper) {
        Log.d(LOG_TAG, "Saving board to database.");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Get the board record for this board.
        ContentValues boardValues = new ContentValues();
        boardValues.put(Boards.COL_BOARD_TYPE, mBoardType);
        boardValues.put(Boards.COL_SEL_BLOCK_X, mSelectedBlockX);
        boardValues.put(Boards.COL_SEL_BLOCK_Y, mSelectedBlockY);

        long boardId = db.insert(Boards.TABLE_NAME, null, boardValues);
        if (boardId == -1) {
            return;
        }

        ContentValues BlockValues = new ContentValues();
        BlockValues.put(Blocks.COL_BOARD_ID, boardId);

        try {
            db.beginTransaction();

            boolean hasErrors = false;
            for (int i = 0; i < mBlocks.length; i++) {
                final int number = mBlocks[i].getNumber();
                if (number == 0)
                    continue;

                BlockValues.put(Blocks.COL_INDEX, i);
                BlockValues.put(Blocks.COL_NUMBER, number);
                if (db.insert(Blocks.TABLE_NAME, null, BlockValues) == -1) {
                    hasErrors = true;
                    Log.e(LOG_TAG, "Could not insert block row.");
                    break;
                }
            }

            if (!hasErrors) {
                db.setTransactionSuccessful();
            }

        } finally {
            db.endTransaction();
        }
    }

    public void loadFromDb(DbHelper dbHelper) {
        Log.d(LOG_TAG, "Loading board from database.");

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String[] columns = {Blocks.COL_INDEX, Blocks.COL_NUMBER};
        //final String selection = Blocks.COL_BOARD_TYPE + "=" + mBoardType;
        final String selection = null;

        Cursor cursor = db.query(Blocks.TABLE_NAME, columns, selection, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Log.d(LOG_TAG, String.format("block: %d, %d", cursor.getInt(0), cursor.getInt(1)));

                int index = cursor.getInt(0);
                mBlocks[index].setNumber(cursor.getInt(1));
            } while(cursor.moveToNext());
        }

        for (Listener listener : mListeners) {
            listener.onNumbersChanged(0, 0, 0);
        }
    }

}

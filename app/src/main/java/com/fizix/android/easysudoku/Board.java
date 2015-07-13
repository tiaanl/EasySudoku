package com.fizix.android.easysudoku;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fizix.android.easysudoku.data.Contract.Boards;
import com.fizix.android.easysudoku.data.Contract.Blocks;
import com.fizix.android.easysudoku.data.DbHelper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Board {

    private static final String LOG_TAG = Board.class.getSimpleName();

    // Board difficulty levels.
    public static final int DIFFICULTY_NONE = 0;
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;

    // The difficulty of this board.
    private int mDifficulty = DIFFICULTY_NONE;

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

    public Board(int difficulty) {
        // Set the difficulty of this board.
        mDifficulty = difficulty;

        // Create the blocks.
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

    public void fillBoard() {
        // Fill out the board with sequential numbers.
        int start = 1;
        for (int y = 0; y < 9; y++) {
            int current = start + (y % 3 * 3);
            for (int x = 0; x < 9; x++) {
                final int index = y * 9 + x;
                mBlocks[index].setNumber(current);
                current = current + 1;
                if (current > 9) {
                    current = 1;
                }
            }
            if (y == 2 || y == 5) {
                start += 1;
            }
        }
    }

    private void swapRows(int row1, int row2) {
        assert(row1 >= 0 && row1 < 9);
        assert(row2 >= 0 && row2 < 9);

        Log.d(LOG_TAG, "Swapping rows " + row1 + " and " + row2 + ".");

        for (int i = 0; i < 9; i++) {
            final int index1 = row1 * 9 + i;
            final int index2 = row2 * 9 + i;

            int temp = mBlocks[index1].getNumber();
            mBlocks[index1].setNumber(mBlocks[index2].getNumber());
            mBlocks[index2].setNumber(temp);
        }
    }

    private void swapColumns(int col1, int col2) {
        assert(col1 >= 0 && col1 < 9);
        assert(col2 >= 0 && col2 < 9);

        Log.d(LOG_TAG, "Swapping columns " + col1 + " and " + col2 + ".");

        for (int i = 0; i < 9; i++) {
            final int index1 = i * 9 + col1;
            final int index2 = i * 9 + col2;

            final int temp = mBlocks[index1].getNumber();
            mBlocks[index1].setNumber(mBlocks[index2].getNumber());
            mBlocks[index2].setNumber(temp);
        }
    }

    public void createNew() {
        fillBoard();

        Random r = new Random();

        // Swap a bunch of random rows and columns.
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 3; j++) {
                int row1 = j * 3 + r.nextInt(3);
                int row2 = j * 3 + r.nextInt(3);
                swapRows(row1, row2);

                int col1 = j * 3 + r.nextInt(3);
                int col2 = j * 3 + r.nextInt(3);
                swapColumns(col1, col2);
            }
        }

        // The amount of blocks we want to remove according to the difficulty.
        int blocksToRemove = 81;
        switch (mDifficulty) {
            case DIFFICULTY_EASY:
                blocksToRemove = 50;
                break;

            case DIFFICULTY_MEDIUM:
                blocksToRemove = 60;
                break;

            case DIFFICULTY_HARD:
                blocksToRemove = 70;
                break;
        }

        Log.d(LOG_TAG, "Blocks to remove for difficulty = " + blocksToRemove);

        int blocksRemoved = 0;
        while (blocksRemoved < blocksToRemove) {
            int index = r.nextInt(9 * 9);
            if (mBlocks[index].getNumber() == 0) {
                continue;
            }

            blocksRemoved += 1;
            mBlocks[index].setNumber(0);
        }

        for (Listener listener : mListeners) {
            listener.onNumbersChanged(0, 0, 0);
        }
    }

    public boolean saveToDb(DbHelper dbHelper) {
        Log.d(LOG_TAG, "Saving board to database.");

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        boolean hasErrors = false;

        try {
            // Get the board record for this board.
            ContentValues boardValues = new ContentValues();
            boardValues.put(Boards.COL_DIFFICULTY, mDifficulty);
            boardValues.put(Boards.COL_SEL_BLOCK_X, mSelectedBlockX);
            boardValues.put(Boards.COL_SEL_BLOCK_Y, mSelectedBlockY);
            boardValues.put(Boards.COL_SEL_NUMBER, mActionNumber);

            long boardId = db.insert(Boards.TABLE_NAME, null, boardValues);
            if (boardId == -1) {
                return false;
            }

            ContentValues blockValues = new ContentValues();
            blockValues.put(Blocks.COL_BOARD_ID, boardId);

            try {
                db.beginTransaction();

                for (int i = 0; i < mBlocks.length; i++) {
                    final int number = mBlocks[i].getNumber();
                    if (number == 0)
                        continue;

                    blockValues.put(Blocks.COL_INDEX, i);
                    blockValues.put(Blocks.COL_NUMBER, number);
                    if (db.insert(Blocks.TABLE_NAME, null, blockValues) == -1) {
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
        } finally {
            db.close();
        }

        return hasErrors;
    }

    public boolean loadFromDb(DbHelper dbHelper) {
        Log.d(LOG_TAG, "Loading board from database.");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {

            // The id of the board in the DB.
            long boardId = 0;

            // Get the board row first.
            {
                final String[] columns = {
                        Boards._ID,
                        Boards.COL_DIFFICULTY,
                        Boards.COL_SEL_BLOCK_X,
                        Boards.COL_SEL_BLOCK_Y,
                        Boards.COL_SEL_NUMBER
                };
                final String selection = Boards.COL_DIFFICULTY + "=?";
                final String selectionArgs[] = {String.valueOf(mDifficulty)};

                Cursor cursor = db.query(Boards.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
                if (cursor.moveToFirst()) {
                    boardId = cursor.getLong(cursor.getColumnIndex(Boards._ID));
                    mDifficulty = cursor.getInt(cursor.getColumnIndex(Boards.COL_DIFFICULTY));
                    mSelectedBlockX = cursor.getInt(cursor.getColumnIndex(Boards.COL_SEL_BLOCK_X));
                    mSelectedBlockX = cursor.getInt(cursor.getColumnIndex(Boards.COL_SEL_BLOCK_Y));
                    mActionNumber = cursor.getInt(cursor.getColumnIndex(Boards.COL_SEL_NUMBER));
                } else {
                    return false;
                }
            }

            if (boardId == 0) {
                return false;
            }

            {
                final String[] columns = {Blocks.COL_INDEX, Blocks.COL_NUMBER};
                final String selection = Blocks.COL_BOARD_ID + "=?";
                final String[] selectionArgs = {String.valueOf(boardId)};

                Cursor cursor = db.query(Blocks.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        Log.d(LOG_TAG, String.format("block: %d, %d", cursor.getInt(0), cursor.getInt(1)));

                        int index = cursor.getInt(cursor.getColumnIndex(Blocks.COL_INDEX));
                        mBlocks[index].setNumber(cursor.getInt(cursor.getColumnIndex(Blocks.COL_NUMBER)));
                    } while (cursor.moveToNext());
                }

                for (Listener listener : mListeners) {
                    listener.onNumbersChanged(0, 0, 0);
                }
            }
        } finally {
            db.close();
        }

        return true;
    }

}

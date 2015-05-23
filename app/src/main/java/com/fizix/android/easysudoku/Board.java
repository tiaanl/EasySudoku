package com.fizix.android.easysudoku;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class Board {

    private static final String LOG_TAG = Board.class.getSimpleName();

    // The numbers on the board.
    private int mNumbers[];

    // Selected block position.
    private int mSelectedBlockX;
    private int mSelectedBlockY;

    public interface Listener {
        void onBoardChanged();
    }

    Set<Listener> mListeners = new HashSet<>();

    public Board() {
        mNumbers = new int[9 * 9];

        for (int i = 0; i < mNumbers.length; ++i) {
            mNumbers[i] = 0;
        }

        mNumbers[4] = 8;

        mSelectedBlockX = 1;
        mSelectedBlockY = 1;
    }

    public int getNumberAt(int x, int y) {
        int index = (y - 1) * 9 + (x - 1);
        assert (index >= 0 && index < 81);
        return mNumbers[index];
    }

    public int getSelectedBlockX() {
        return mSelectedBlockX;
    }

    public int getSelectedBlockY()
    {
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

        boardChanged();
    }

    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    public void boardChanged() {
        for (Listener listener : mListeners) {
            listener.onBoardChanged();
        }
    }

}

package com.fizix.android.easysudoku.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fizix.android.easysudoku.Board;
import com.fizix.android.easysudoku.R;
import com.fizix.android.easysudoku.data.DbHelper;
import com.fizix.android.easysudoku.views.BoardView;
import com.fizix.android.easysudoku.views.NumberButtonView;

public class PlayFragment extends Fragment implements NumberButtonView.SelectListener {

    private static final String LOG_TAG = PlayFragment.class.getSimpleName();

    private static final String sParamDifficulty = "difficulty";

    // The board we are operating on.
    Board mBoard = null;

    // All the buttons.
    private NumberButtonView mButtons[] = new NumberButtonView[10];

    // The selected number.
    private int mSelectedNumber = -1;

    public static PlayFragment newInstance(Board board) {
        PlayFragment fragment = new PlayFragment();
        fragment.mBoard = board;
        return fragment;
    }

    public PlayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_play, container, false);

        BoardView board = (BoardView) view.findViewById(R.id.board);
        board.setBoard(mBoard);

        mButtons[0] = linkButton(view, R.id.button_0, mBoard.getActionNumber() == 0);
        mButtons[1] = linkButton(view, R.id.button_1, mBoard.getActionNumber() == 1);
        mButtons[2] = linkButton(view, R.id.button_2, mBoard.getActionNumber() == 2);
        mButtons[3] = linkButton(view, R.id.button_3, mBoard.getActionNumber() == 3);
        mButtons[4] = linkButton(view, R.id.button_4, mBoard.getActionNumber() == 4);
        mButtons[5] = linkButton(view, R.id.button_5, mBoard.getActionNumber() == 5);
        mButtons[6] = linkButton(view, R.id.button_6, mBoard.getActionNumber() == 6);
        mButtons[7] = linkButton(view, R.id.button_7, mBoard.getActionNumber() == 7);
        mButtons[8] = linkButton(view, R.id.button_8, mBoard.getActionNumber() == 8);
        mButtons[9] = linkButton(view, R.id.button_9, mBoard.getActionNumber() == 9);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(LOG_TAG, "Destroying PlayFragment.");
    }

    private NumberButtonView linkButton(View view, int resourceId, boolean selected) {
        NumberButtonView button = (NumberButtonView) view.findViewById(resourceId);
        button.setSelectListener(this);
        button.setSelected(selected);
        return button;
    }

    @Override
    public void onNumberButtonSelected(int number) {
        Log.d(LOG_TAG, String.format("Button %d pressed.", number));

        // If this is already the selected button, dn't do anything.
        if (mSelectedNumber == number) {
            return;
        }

        // Set the new selected number.
        mSelectedNumber = number;

        // Deselect all the buttons.
        for (int i = 0; i < 10; ++i) {
            mButtons[i].setSelected(number == i);
        }

        // Set the selected number of the board we're connected to.
        if (mBoard != null) {
            mBoard.setActionNumber(number);
        }
    }

}

package com.fizix.android.easysudoku.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fizix.android.easysudoku.Board;
import com.fizix.android.easysudoku.R;
import com.fizix.android.easysudoku.views.NumberButtonView;

public class ButtonsFragment extends Fragment implements Board.Listener, NumberButtonView.SelectListener {

    private static final String LOG_TAG = ButtonsFragment.class.getSimpleName();

    Board mBoard;

    // All the buttons.
    NumberButtonView mButtons[] = new NumberButtonView[10];

    public ButtonsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buttons, container, false);

        mButtons[0] = linkButton(view, R.id.button_0);
        mButtons[1] = linkButton(view, R.id.button_1);
        mButtons[2] = linkButton(view, R.id.button_2);
        mButtons[3] = linkButton(view, R.id.button_3);
        mButtons[4] = linkButton(view, R.id.button_4);
        mButtons[5] = linkButton(view, R.id.button_5);
        mButtons[6] = linkButton(view, R.id.button_6);
        mButtons[7] = linkButton(view, R.id.button_7);
        mButtons[8] = linkButton(view, R.id.button_8);
        mButtons[9] = linkButton(view, R.id.button_9);

        return view;
    }

    private NumberButtonView linkButton(View view, int resourceId) {
        NumberButtonView button = (NumberButtonView) view.findViewById(resourceId);
        button.setSelectListener(this);
        return button;
    }

    public void setBoard(Board board) {
        if (mBoard != null) {
            mBoard.removeListener(this);
        }

        mBoard = board;

        if (mBoard != null) {
            mBoard.addListener(this);
        }
    }

    @Override
    public void onBoardChanged() {
    }

    @Override
    public void onNumberButtonSelected(int number) {
        Log.d(LOG_TAG, String.format("Button %d pressed.", number));

        for (int i = 0; i < 10; ++i) {
            mButtons[i].setSelected(false);
        }
        mButtons[number].setSelected(true);
    }
}

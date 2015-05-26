package com.fizix.android.easysudoku;


public class Block {

    private int mX;
    private int mY;
    private int mNumber;

    public Block(int x, int y, int number) {
        mX = x;
        mY = y;
        mNumber = number;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
    }
}

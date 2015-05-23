package com.fizix.android.easysudoku.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fizix.android.easysudoku.Board;
import com.fizix.android.easysudoku.R;


public class BoardView extends View implements Board.Listener {

    private static final String LOG_TAG = BoardView.class.getSimpleName();

    // The board we are painting.
    private Board mBoard = null;

    private int mBlockWidth;
    private int mBlockHeight;

    // The width of the separator lines.
    private float mLineWidth;

    // The width of the line we use to draw the selected block.
    private float mSelectedLineWidth;

    // The dark and light background paints.
    private Paint mDarkBackgroundPaint;
    private Paint mLightBackgroundPaint;

    // The paint we use to draw the background of the board.
    private Paint mBackgroundPaint;

    // The paint we use to draw the separator lines on the board.
    private Paint mLinePaint;
    private Paint mSelectedLinePaint;

    // The paint used for the numbers on the board.
    private TextPaint mTextPaint;
    private TextPaint mSelectedTextPaint;

    // Temporary Rect used in painting.
    private Rect mBoundsRect;

    public BoardView(Context context) {
        super(context);
        init(null, 0);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setBoard(Board board) {
        // If we were a listener, remove ourselves.
        if (mBoard != null) {
            mBoard.removeListener(this);
        }

        // Set the new board.
        mBoard = board;

        // Set ourselves as a listener.
        if (mBoard != null) {
            mBoard.addListener(this);
        }
    }

    public Board getBoard() {
        return mBoard;
    }

    @Override
    public void onSelectedBlockChanged(int x, int y, int number) {
        invalidate();
    }

    @Override
    public void onActionNumberChanged(int actionNumber) {
        invalidate();
    }

    @Override
    public void onNumbersChanged(int x, int y, int number) {
        invalidate();
    }

    private void init(AttributeSet attrs, int defStyle) {
        mBoundsRect = new Rect();

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BoardView, defStyle, 0);

        // Calculate line widths.
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        mLineWidth = Math.round(displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
        mSelectedLineWidth = Math.round(displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT * 3.0f);

        // Create the paint we'll use.

        mDarkBackgroundPaint = new Paint();
        mDarkBackgroundPaint.setColor(Color.rgb(223, 223, 223));
        mLightBackgroundPaint = new Paint();
        mLightBackgroundPaint.setColor(Color.rgb(255, 255, 255));

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.RED);

        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLACK);

        mSelectedLinePaint = new Paint();
        mSelectedLinePaint.setColor(Color.RED);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mSelectedTextPaint = new TextPaint();
        mSelectedTextPaint.setColor(Color.RED);
        mSelectedTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBlockWidth = w;
        mBlockHeight = h;

        if (mBlockWidth < mBlockHeight) {
            mBlockHeight = mBlockWidth;
        } else {
            mBlockWidth = mBlockHeight;
        }

        float textSize = (float) mBlockWidth / (9.0f + 5.0f);
        mTextPaint.setTextSize(textSize);
        mSelectedTextPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(LOG_TAG, "onDraw");

        Log.d(LOG_TAG, String.format("onDraw: %d -- %d", getWidth(), getHeight()));

        // Draw the background on the entire view.
        // canvas.drawRect(0, 0, mBlockWidth, mBlockHeight, mBackgroundPaint);

        // Draw the darker colored blocks.
        float blockWidth = (float) mBlockWidth / 3.0f;
        float blockHeight = (float) mBlockHeight / 3.0f;
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                boolean isDark = ((y * 3 + x) % 2 == 0);

                canvas.drawRect(
                        (float) x * blockWidth,
                        (float) y * blockHeight,
                        (float) (x + 1) * blockWidth,
                        (float) (y + 1) * blockHeight,
                        isDark ? mDarkBackgroundPaint : mLightBackgroundPaint
                );
            }
        }

        // Calculate the size of the open spaces.
        float openSpace = ((float) mBlockWidth - (10.0f * mLineWidth)) / 9.0f;

        // Draw the horizontal lines.
        float currentX = 0.0f;
        for (int line = 0; line < 10; ++line) {
            canvas.drawRect(
                    currentX,
                    0.0f,
                    currentX + mLineWidth,
                    mBlockHeight,
                    mLinePaint
            );
            currentX += mLineWidth + openSpace;
        }

        // Draw the vertical lines.
        float currentY = 0.0f;
        for (int line = 0; line < 10; ++line) {
            canvas.drawRect(
                    0.0f,
                    currentY,
                    mBlockWidth,
                    currentY + mLineWidth,
                    mLinePaint
            );
            currentY += mLineWidth + openSpace;
        }

        // Draw the selected block.
        if (mBoard != null && mBoard.hasSelectedBlock()) {
            int selectedBlockX = mBoard.getSelectedBlockX() - 1;
            int selectedBlockY = mBoard.getSelectedBlockY() - 1;

            float left = (float) selectedBlockX * ((float) mBlockWidth - mLineWidth) / 9.0f;
            float top = (float) selectedBlockY * ((float) mBlockHeight - mLineWidth) / 9.0f;
            float right = (float) (selectedBlockX + 1) * ((float) mBlockWidth - mLineWidth) / 9.0f + mLineWidth;
            float bottom = (float) (selectedBlockY + 1) * ((float) mBlockHeight - mLineWidth) / 9.0f + mLineWidth;

            canvas.drawRect(left, top, right, top + mSelectedLineWidth, mSelectedLinePaint);
            canvas.drawRect(left, bottom - mSelectedLineWidth, right, bottom, mSelectedLinePaint);
            canvas.drawRect(left, top + mSelectedLineWidth, left + mSelectedLineWidth, bottom - mSelectedLineWidth, mSelectedLinePaint);
            canvas.drawRect(right - mSelectedLineWidth, top + mSelectedLineWidth, right, bottom, mSelectedLinePaint);
        }

        // Draw the numbers.
        if (mBoard != null) {
            int selectedNumber = mBoard.getActionNumber();
            for (int y = 0; y < 9; ++y) {
                for (int x = 0; x < 9; ++x) {
                    int number = mBoard.getNumberAt(x + 1, y + 1);
                    if (number == 0)
                        continue;

                    String str = String.format("%d", number);
                    TextPaint textPaint = (selectedNumber == number) ? mSelectedTextPaint : mTextPaint;
                    textPaint.getTextBounds(str, 0, 1, mBoundsRect);

                    float left = (float) x * ((float) mBlockWidth - mLineWidth) / 9.0f + mLineWidth;
                    float top = (float) y * ((float) mBlockHeight - mLineWidth) / 9.0f + mLineWidth;
                    float right = (float) (x + 1) * ((float) mBlockWidth - mLineWidth) / 9.0f;
                    float bottom = (float) (y + 1) * ((float) mBlockHeight - mLineWidth) / 9.0f;
                    canvas.drawText(
                            str,
                            left + (right - left) / 2.0f - (float) (mBoundsRect.centerX()),
                            top + (bottom - top) / 2.0f - (float) (mBoundsRect.centerY()),
                            textPaint
                    );
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getX() < mBlockWidth && event.getY() < mBlockHeight && mBoard != null) {
            mBoard.setSelectedBlock(
                    (int) Math.round(Math.floor((event.getX() / (float) mBlockWidth * 9.0f)) + 1.0f),
                    (int) Math.round(Math.floor((event.getY() / (float) mBlockHeight * 9.0f)) + 1.0f)
            );
        }

        return super.onTouchEvent(event);
    }
}

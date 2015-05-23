package com.fizix.android.easysudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class BoardView extends View {

    private static final String LOG_TAG = BoardView.class.getSimpleName();

    private int mBlockWidth;
    private int mBlockHeight;

    // The width of the separator lines.
    private float mLineWidth;

    // The dark and light background paints.
    private Paint mDarkBackgroundPaint;
    private Paint mLightBackgroundPaint;

    // The paint we use to draw the background of the board.
    private Paint mBackgroundPaint;

    // The paint we use to draw the separator lines on the board.
    private Paint mSeparatorLinePaint;

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

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BoardView, defStyle, 0);

        Log.d(LOG_TAG, String.format("width: %d, %d", getWidth(), getHeight()));

        // Calculate the line width.
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mLineWidth = Math.round(displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);

        // Create the paint we'll use.

        mDarkBackgroundPaint = new Paint();
        mDarkBackgroundPaint.setColor(Color.rgb(191, 191, 191));
        mLightBackgroundPaint = new Paint();
        mLightBackgroundPaint.setColor(Color.rgb(255, 255, 255));

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.RED);

        mSeparatorLinePaint = new Paint();
        mSeparatorLinePaint.setColor(Color.BLACK);

        /*
        mExampleString = a.getString(
                R.styleable.BoardView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.BoardView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.BoardView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.BoardView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.BoardView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }
        */

        a.recycle();

        // Set up a default TextPaint object
        /*
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        */

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        /*
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
        */
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
                    mSeparatorLinePaint
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
                    mSeparatorLinePaint
            );
            currentY += mLineWidth + openSpace;
        }

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        /*
        // Draw the text.
        canvas.drawText(mExampleString,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2,
                mTextPaint);

        // Draw the example drawable on top of the text.
        if (mExampleDrawable != null) {
            mExampleDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mExampleDrawable.draw(canvas);
        }
        */
    }
}

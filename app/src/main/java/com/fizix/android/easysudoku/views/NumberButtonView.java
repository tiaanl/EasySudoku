package com.fizix.android.easysudoku.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.fizix.android.easysudoku.R;

public class NumberButtonView extends View {
    private static final String LOG_TAG = NumberButtonView.class.getSimpleName();

    // The number we are representing.
    private int mNumber;

    // Paint to draw the outline of the button.
    private Paint mButtonPaint;
    private Paint mButtonPaintSelected;
    private Paint mButtonOutlinePaint;

    // The paint we use for the label.
    private TextPaint mLabelPaint;

    // Temp Rect to calculate label bounds.
    private Rect mLabelBounds;

    // The width of the line we draw around the button.
    private float mLineWidth;

    // The rect we use to draw the outline of the button.
    private RectF mOutlineRect;

    public interface SelectListener {
        void onNumberButtonSelected(int number);
    }

    private SelectListener mSelectListener = null;

    public NumberButtonView(Context context) {
        super(context);
        init(null, 0);
    }

    public NumberButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public NumberButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mLabelBounds = new Rect();
        mOutlineRect = new RectF();

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.NumberButtonView, defStyle, 0);
        mNumber = a.getInteger(R.styleable.NumberButtonView_number, 0);
        a.recycle();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        mLineWidth = Math.round(displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT * 3.0f);

        mButtonPaint = new Paint();
        mButtonPaint.setColor(Color.rgb(223, 223, 223));

        mButtonPaintSelected = new Paint();
        mButtonPaintSelected.setColor(Color.rgb(191, 191, 191));

        mButtonOutlinePaint = new Paint();
        mButtonOutlinePaint.setColor(Color.rgb(127, 127, 127));
        mButtonOutlinePaint.setStrokeWidth(mLineWidth);
        mButtonOutlinePaint.setStyle(Paint.Style.STROKE);

        mLabelPaint = new TextPaint();
        mLabelPaint.setColor(Color.BLACK);
        mLabelPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Update the size of the text.
        mLabelPaint.setTextSize((float) h / 1.5f);

        // Update the rect we use to draw the button outline.
        mOutlineRect.left = mLineWidth / 2.0f;
        mOutlineRect.top = mLineWidth / 2.0f;
        mOutlineRect.right = (float) getWidth() - mLineWidth / 2.0f;
        mOutlineRect.bottom = (float) getHeight() - mLineWidth / 2.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a border around the button.
        canvas.drawRoundRect(mOutlineRect, 50.0f, 50.0f, isSelected() ? mButtonPaintSelected : mButtonPaint);
        canvas.drawRoundRect(mOutlineRect, 50.0f, 50.0f, mButtonOutlinePaint);

        // Draw the label on the button.
        String label = String.valueOf(mNumber);

        mLabelPaint.getTextBounds(label, 0, label.length(), mLabelBounds);

        canvas.drawText(
                label,
                (float) getWidth() / 2.0f - mLabelBounds.centerX(),
                (float) getHeight() / 2.0f - mLabelBounds.centerY(),
                mLabelPaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                setSelected(true);
                invalidate();
                if (mSelectListener != null) {
                    mSelectListener.onNumberButtonSelected(mNumber);
                }
                return true;
        }

        return super.onTouchEvent(event);
    }

    public void setSelectListener(SelectListener selectListener) {
        mSelectListener = selectListener;
    }

}

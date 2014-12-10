package com.android.progress;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ArcProgressBar extends View {
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_UNFINISHED_COLOR = Color.WHITE;
    private static final int DEFAULT_FINISHED_COLOR = Color.BLUE;
    private static final int DEFAULT_START_ANGLE = 125;
    private static final int DEFAULT_END_ANGLE = 285;

    // max value of the progress
    private int mMaxValue;
    //paint for drawing the arc
    private Paint mArcPaint;
    //the progress indicator
    private int mProgress = 10;
    //radius of the arc to calculate the dimensions of the view
    private int mRadius;
    //width of the stroke
    private float mStrokeWidth;

    private int mStartAngle;
    private int mEndAngle;

    private int mFinishedProgressColor;
    private int mUnfinishedProgressColor;

    //rect which represents the progress
    private RectF mShapeRect = new RectF();

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        final Resources res = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar, 0, defStyleAttr);
        mStrokeWidth = a.getDimension(R.styleable.ArcProgressBar_stroke_width, res.getDimension(R.dimen.default_stroke_width));
        mMaxValue = a.getInteger(R.styleable.ArcProgressBar_max, DEFAULT_MAX_VALUE);
        mUnfinishedProgressColor = a.getColor(R.styleable.ArcProgressBar_unfinished_color, DEFAULT_UNFINISHED_COLOR);
        mFinishedProgressColor = a.getColor(R.styleable.ArcProgressBar_finished_color, DEFAULT_FINISHED_COLOR);

        mStartAngle = a.getInteger(R.styleable.ArcProgressBar_start_angle, DEFAULT_START_ANGLE);
        mEndAngle = a.getInteger(R.styleable.ArcProgressBar_end_angle, DEFAULT_END_ANGLE);

        mRadius = Math.round(a.getDimension(R.styleable.ArcProgressBar_radius, res.getDimension(R.dimen.default_radius)));

        a.recycle();

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStrokeWidth(mStrokeWidth);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize, heightSize;

        //Get the width based on the measure specs
        widthSize = getDefaultSize(0, widthMeasureSpec);

        //Get the height based on measure specs
        heightSize = getDefaultSize(0, heightMeasureSpec);

        final int paddingHorizontal = getPaddingRight() + getPaddingStart() + getPaddingLeft() + getPaddingEnd();
        final int paddingVertical = getPaddingTop() + getPaddingBottom();

        final int majorDimension = Math.min(Math.min(widthSize, heightSize), mRadius);

        mShapeRect.set(mStrokeWidth + getPaddingStart() + getPaddingLeft(), mStrokeWidth + getPaddingTop(), majorDimension - mStrokeWidth - getPaddingEnd() - getPaddingRight(), majorDimension - mStrokeWidth - getPaddingBottom());

        //MUST call this to save our own dimensions
        setMeasuredDimension(majorDimension + paddingHorizontal, majorDimension + paddingVertical);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float finishedSweepAngle = mProgress / (float) mMaxValue * mEndAngle;
        mArcPaint.setColor(mFinishedProgressColor);
        //draw the arc that represents the progress bar itself
        canvas.drawArc(mShapeRect, mStartAngle, mEndAngle, false, mArcPaint);
        mArcPaint.setColor(mUnfinishedProgressColor);
        //draw the progress
        canvas.drawArc(mShapeRect, mStartAngle, finishedSweepAngle, false, mArcPaint);
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(final int progress) {
        mProgress = progress;
        if (mProgress > mMaxValue) {
            mProgress %= mMaxValue;
        }
        invalidate();
    }

    public int getMaxValue() {
        return mMaxValue;
    }

    public void setMaxValue(final int maxValue) {
        setMaxValue(maxValue, false);
    }

    public void setMaxValue(final int maxValue, final boolean invalidate) {
        if (maxValue > 0) {
            mMaxValue = maxValue;
            if (invalidate) {
                invalidate();
            }
        }
    }
}

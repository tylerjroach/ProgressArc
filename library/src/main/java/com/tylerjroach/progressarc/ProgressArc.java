package com.tylerjroach.progressarc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.STROKE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ProgressArc extends View {
    private static final int MAX_PROGRESS = 100;

    private Paint paint;
    private RectF arcBounds = new RectF();

    private OnInnerRectangleBoundsChangedListener onInnerRectangleSizeChangedListener;

    private int arcStrokeWidth;
    private int arcBackgroundColor;
    private int arcStrokeColor;
    private int startAngle;
    private int sweepAngle;
    private int progressSweepAngle;
    private int innerRectangleWidth, innerRectangleHeight;

    private float progress;

    public ProgressArc(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressArc(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public ProgressArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressArc);
        arcStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressArc_stroke_width, getResources().getDimensionPixelSize(R.dimen.default_stroke_width));
        arcBackgroundColor = typedArray.getColor(R.styleable.ProgressArc_stroke_inactive_color, getResources().getColor(R.color.progressBarInactive));
        arcStrokeColor = typedArray.getColor(R.styleable.ProgressArc_stroke_active_color, getResources().getColor(R.color.progressBarActive));
        sweepAngle = typedArray.getInteger(R.styleable.ProgressArc_sweep_angle, getResources().getInteger(R.integer.default_sweep_angle));
        typedArray.recycle();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(STROKE);
        paint.setStrokeCap(ROUND);
        paint.setStrokeWidth(arcStrokeWidth);

        updateStartAngle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isWrapContent = getLayoutParams().height == WRAP_CONTENT;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int arcHeight = MeasureSpec.getSize(isWrapContent ? widthMeasureSpec : heightMeasureSpec);
        arcBounds.set(arcStrokeWidth / 2, arcStrokeWidth / 2, width - (arcStrokeWidth / 2), arcHeight - arcStrokeWidth / 2);

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            float radius = arcBounds.width() / 2;
            double layoutHeight = radius * (1 - Math.cos(Math.toRadians(sweepAngle) / 2));
            double strokeArcOffset = arcStrokeWidth - ((sweepAngle - 180) / 180);
            height = (int) Math.ceil(layoutHeight + strokeArcOffset);
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        int innerCircleDiameter = width - (arcStrokeWidth * 2);
        int squareSideLength = (int) (Math.ceil((innerCircleDiameter * Math.sqrt(2)) / 2));
        updateInnerRectangle(squareSideLength, height < squareSideLength ? height : squareSideLength);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(arcBackgroundColor);
        canvas.drawArc(arcBounds, startAngle, sweepAngle, false, paint);
        paint.setColor(arcStrokeColor);
        canvas.drawArc(arcBounds, startAngle, progressSweepAngle, false, paint);
    }

    private void updateInnerRectangle(int width, int height) {
        if (innerRectangleWidth != width || innerRectangleHeight != height) {
            innerRectangleWidth = width;
            innerRectangleHeight = height;
            if (onInnerRectangleSizeChangedListener != null) {
                onInnerRectangleSizeChangedListener.onInnerRectangleSizeChanged(new Rect(0, 0, width, height));
            }
        }
    }

    private void updateStartAngle() {
        startAngle = 90 + ((360 - sweepAngle) / 2);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        progressSweepAngle = (int) Math.ceil((progress / MAX_PROGRESS * sweepAngle));
        invalidate();
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(@IntRange(from = 180, to = 360) int sweepAngle) {
        this.sweepAngle = sweepAngle;
        updateStartAngle();
        setProgress(progress);
        invalidate();
        requestLayout();
    }

    public OnInnerRectangleBoundsChangedListener getOnInnerRectangleSizeChangedListener() {
        return onInnerRectangleSizeChangedListener;
    }

    public void setOnInnerRectangleSizeChangedListener(final OnInnerRectangleBoundsChangedListener onInnerRectangleSizeChangedListener) {
        this.onInnerRectangleSizeChangedListener = onInnerRectangleSizeChangedListener;
        if (innerRectangleWidth != 0 && innerRectangleHeight != 0) {
            onInnerRectangleSizeChangedListener.onInnerRectangleSizeChanged(new Rect(0, 0, innerRectangleWidth, innerRectangleHeight));
        }
    }

    public interface OnInnerRectangleBoundsChangedListener {
        void onInnerRectangleSizeChanged(Rect rect);
    }
}
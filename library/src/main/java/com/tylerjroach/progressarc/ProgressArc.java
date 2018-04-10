package com.tylerjroach.progressarc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.STROKE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ProgressArc extends View {

    private static final int MAX_PROGRESS = 100;

    private RectF arcBounds = new RectF();
    private Paint paint;
    private int strokeWidth;
    private int sweepAngle;
    private int progressSweepAngle;
    private int startAngle;
    private int strokeColor;
    private int backgroundColor;
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
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressArc_stroke_width, getResources().getDimensionPixelSize(R.dimen.default_stroke_width));
        backgroundColor = typedArray.getColor(R.styleable.ProgressArc_stroke_inactive_color, getResources().getColor(R.color.progressBarInactive));
        strokeColor = typedArray.getColor(R.styleable.ProgressArc_stroke_active_color, getResources().getColor(R.color.progressBarActive));
        sweepAngle = typedArray.getInteger(R.styleable.ProgressArc_sweep_angle, getResources().getInteger(R.integer.default_sweep_angle));
        typedArray.recycle();

        startAngle = 90 + ((360 - sweepAngle) / 2);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(STROKE);
        paint.setStrokeCap(ROUND);
        paint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean isWrapContent = getLayoutParams().height == WRAP_CONTENT;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(isWrapContent ? widthMeasureSpec, heightMeasureSpec);
        arcBounds.set(strokeWidth / 2, strokeWidth / 2, width - (strokeWidth / 2), height - strokeWidth / 2);

        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            float radius = arcBounds.width() / 2;
            double layoutHeight = radius * (1 - Math.cos(Math.toRadians(sweepAngle) / 2));
            double strokeArcOffset = strokeWidth - ((sweepAngle - 180) / 180);
            setMeasuredDimension(width, (int) Math.ceil(layoutHeight + strokeArcOffset));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(backgroundColor);
        canvas.drawArc(arcBounds, startAngle, sweepAngle, false, paint);
        paint.setColor(strokeColor);
        canvas.drawArc(arcBounds, startAngle, progressSweepAngle, false, paint);

    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        progressSweepAngle = (int) (progress / MAX_PROGRESS * sweepAngle);
        invalidate();
    }
}
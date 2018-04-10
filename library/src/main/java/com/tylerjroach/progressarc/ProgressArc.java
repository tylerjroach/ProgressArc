package com.tylerjroach.progressarc;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import static android.graphics.Paint.Cap.ROUND;
import static android.graphics.Paint.Style.STROKE;

public class ProgressArc extends View {
    private static final int MAX_PROGRESS = 100;

    private RectF arcBounds = new RectF();
    private Paint arcPaint;
    private TextPaint textPaint;
    private int strokeWidth;
    private int sweepAngle;
    private int progressSweepAngle;
    private int startAngle;
    private int strokeColor;
    private int backgroundColor;
    private float progress;
    private String label;

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
        label = typedArray.getString(R.styleable.ProgressArc_label);
        strokeWidth = typedArray.getDimensionPixelSize(R.styleable.ProgressArc_stroke_width, getResources().getDimensionPixelSize(R.dimen.default_stroke_width));
        backgroundColor = typedArray.getColor(R.styleable.ProgressArc_stroke_inactive_color, getResources().getColor(R.color.progressBarInactive));
        strokeColor = typedArray.getColor(R.styleable.ProgressArc_stroke_active_color, getResources().getColor(R.color.progressBarActive));
        sweepAngle = typedArray.getInteger(R.styleable.ProgressArc_sweep_angle, getResources().getInteger(R.integer.default_sweep_angle));
        int textSizeDimension = typedArray.getDimensionPixelSize(R.styleable.ProgressArc_textSize, getResources().getDimensionPixelSize(R.dimen.default_text_size));
        typedArray.recycle();

        startAngle = 90 + ((360 - sweepAngle) / 2);

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(STROKE);
        arcPaint.setStrokeCap(ROUND);
        arcPaint.setStrokeWidth(strokeWidth);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSizeDimension);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        arcBounds.set(strokeWidth / 2f, strokeWidth / 2f, width - (strokeWidth / 2f), height - (strokeWidth / 2f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        arcPaint.setColor(backgroundColor);
        canvas.drawArc(arcBounds, startAngle, sweepAngle, false, arcPaint);
        arcPaint.setColor(strokeColor);
        canvas.drawArc(arcBounds, startAngle, progressSweepAngle, false, arcPaint);

        if (!TextUtils.isEmpty(label)) {
            canvas.drawText(label, canvas.getWidth() / 2, canvas.getHeight() - strokeWidth * 1.5f, textPaint);
        }

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
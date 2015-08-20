package com.jonkoester.circlemeterwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CircleMeterView extends RelativeLayout {

    private Canvas canvas;
    private RectF frameBounds;
    private Paint circlePaint;
    private Float strokeWidth;
    private TextView centerTextView;
    private TextView smallTextView;
    private String centerText;
    private String smallText;
    private float actualUnits;
    private float totalUnits;
    private Paint progressPaint;
    private Float sweepAngle;
    private Float sweepArc;
    private Float prevAngle;

    private final static float DEFAULT_STROKE_WIDTH = 15f;
    private final static float DEFAULT_START_ANGLE = 90f;

    public CircleMeterView(Context context) {
        super(context);
        init();
    }

    public CircleMeterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        init();
    }

    public CircleMeterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
        init();
    }

    protected void setAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleMeterView);
        try {
            centerText = a.getString(R.styleable.CircleMeterView_centerText);
            smallText = a.getString(R.styleable.CircleMeterView_smallText);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.canvas == null) {
            this.canvas = canvas;
        }

        frameBounds.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);

        centerTextView.setText(centerText);
        smallTextView.setText(smallText);

        canvas.drawOval(frameBounds, circlePaint);
        canvas.drawArc(frameBounds, prevAngle, sweepArc, false, progressPaint);

        if (sweepArc < sweepAngle) {
            sweepArc+=2;
            invalidate();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.circle_meter_view, this);
        if (getBackground() == null) {
            setBackground(new ColorDrawable(Color.TRANSPARENT));
        }

        frameBounds = new RectF();

        circlePaint = new Paint();
        progressPaint = new Paint();

        // Circle paint setup
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLACK);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        // Progress paint setup
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.GRAY);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(getStrokeWidth());

        centerTextView = (TextView) findViewById(R.id.circle_meter_center_text);
        centerTextView.setTextColor(Color.BLACK);
        smallTextView = (TextView) findViewById(R.id.circle_meter_small_text);
        smallTextView.setTextColor(Color.BLACK);

        prevAngle = DEFAULT_START_ANGLE;
        sweepAngle = 0f;
        sweepArc = 0f;
        centerText = centerText != null ? centerText : "0";
        smallText = smallText != null ? smallText : "0";
        actualUnits = Float.valueOf(centerText);

        if (!smallText.equals("%")){
            totalUnits = Float.valueOf(smallText);
        }

        updateMeter();
    }

    private void updateMeter() {
        if (prevAngle != DEFAULT_START_ANGLE) {
            prevAngle = sweepAngle;
        }

        if (totalUnits > 0) {
            sweepAngle = actualUnits / totalUnits * 360.0f;
        } else {
            sweepAngle = actualUnits / 100 * 360.0f;
        }

        if (sweepAngle >= 360f) {
            sweepAngle = 360f;
            progressPaint.setColor(Color.DKGRAY);
        } else {
            progressPaint.setColor(Color.GRAY);
        }
    }

    public Float getStrokeWidth() {
        if (strokeWidth == null) {
            strokeWidth = DEFAULT_STROKE_WIDTH;
        }
        return strokeWidth;
    }

    public void setCenterText(String centerText) {
        actualUnits = Float.valueOf(centerText);
        if ((smallText.equals("%") && actualUnits <= 100f) ||
                actualUnits <= totalUnits) {
            this.centerText = centerText;
        } else if (actualUnits >= totalUnits) {
            if (smallText.equals("%")) {
                actualUnits = 100f;
            } else {
                actualUnits = totalUnits;
            }
            this.centerText = String.format("%.0f", actualUnits);
        }

        updateMeter();
    }

    public void setSmallText(String smallText) {
        totalUnits = smallText.equals("%") ? 0 : Float.valueOf(smallText);
        this.smallText = smallText;
        updateMeter();
    }

    public void setActualUnits(float actualUnits) {
        if (actualUnits <= totalUnits ||
                smallText.equals("%")) {
            setCenterText(String.format("%.0f", actualUnits));
            this.actualUnits = actualUnits;
        } else {
            setCenterText(String.format("%.0f", totalUnits));
            this.actualUnits = totalUnits;
        }
        updateMeter();
    }

    public float getActualUnits() {
        return actualUnits;
    }
}

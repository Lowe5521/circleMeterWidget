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

    private RectF frameBounds;
    private Paint circlePaint;
    private Float strokeWidth;
    private Float actualUnits;
    private Float prevActualUnits;
    private Float totalUnits;
    private Paint progressPaint;
    private Float sweepAngle;
    private Float progressAngle;
    private Float prevAngle;
    private TextView actualUnitsTV;
    private TextView totalUnitsTV;
    private Float angleIncrements;

    // Styleable properties
    private String centerText;
    private String smallText;
    private Integer actualUnitsTextColor;
    private Integer totalUnitsTextColor;
    private Integer totalBackgroundColor;
    private Boolean circleFill;
    private Integer circleFillColor;
    private Integer progressBarColor;

    // Defaults
    private final static float DEFAULT_STROKE_WIDTH = 15f;
    private final static float DEFAULT_START_ANGLE = 90f;
    private final static int DEFAULT_TOTAL_BACKGROUND_COLOR = Color.TRANSPARENT;
    private final static int DEFAULT_CIRCLE_FILL_COLOR = Color.BLACK;
    private final static int DEFAULT_PROGRESS_BAR_COLOR = Color.GRAY;
    private final static int DEFAULT_TEXT_COLOR = Color.BLACK;

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
            centerText = a.getString(R.styleable.CircleMeterView_actualUnitsText);
            smallText = a.getString(R.styleable.CircleMeterView_totalUnitsText);
            totalBackgroundColor = a.getInteger(R.styleable.CircleMeterView_totalBackgroundColor, DEFAULT_TOTAL_BACKGROUND_COLOR);
            circleFill = a.getBoolean(R.styleable.CircleMeterView_circleFill, false);
            circleFillColor = a.getInteger(R.styleable.CircleMeterView_circleFillColor, DEFAULT_CIRCLE_FILL_COLOR);
            progressBarColor = a.getInteger(R.styleable.CircleMeterView_progressBarColor, DEFAULT_PROGRESS_BAR_COLOR);
            actualUnitsTextColor = a.getInteger(R.styleable.CircleMeterView_actualUnitsTextColor, DEFAULT_TEXT_COLOR);
            totalUnitsTextColor = a.getInteger(R.styleable.CircleMeterView_totalUnitsTextColor, DEFAULT_TEXT_COLOR);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (frameBounds == null) {
            frameBounds = new RectF(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);
            canvas.drawOval(frameBounds, circlePaint);
        }

        animateProgress(canvas);
    }

    private void animateProgress(Canvas canvas) {
        canvas.drawOval(frameBounds, circlePaint);
        canvas.drawArc(frameBounds, prevAngle, progressAngle, false, progressPaint);
        if (progressAngle < sweepAngle) {
            progressAngle += angleIncrements;
            actualUnitsTV.setText(String.format("%.0f", prevActualUnits++));
            invalidate();
        } else {
            actualUnitsTV.setText(centerText);
        }
    }

    private void init() {
        inflate(getContext(), R.layout.circle_meter_view, this);
        setBackground(new ColorDrawable(totalBackgroundColor));

        circlePaint = new Paint();
        progressPaint = new Paint();
        strokeWidth = DEFAULT_STROKE_WIDTH;

        // Circle paint setup
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(circleFillColor);
        if (circleFill) {
            circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            circlePaint.setStyle(Paint.Style.STROKE);
        }
        circlePaint.setStrokeWidth(strokeWidth);

        // Progress paint setup
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressBarColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);

        // Text views setup
        actualUnitsTV = (TextView) findViewById(R.id.circle_meter_center_text);
        actualUnitsTV.setTextColor(actualUnitsTextColor);
        totalUnitsTV = (TextView) findViewById(R.id.circle_meter_small_text);
        totalUnitsTV.setTextColor(totalUnitsTextColor);
        centerText = centerText != null ? centerText : "0";
        smallText = smallText != null ? smallText : "%";
        actualUnitsTV.setText(centerText);
        totalUnitsTV.setText(smallText);

        // Angles setup
        prevAngle = DEFAULT_START_ANGLE;
        sweepAngle = 0f;
        progressAngle = 0f;
        angleIncrements = getAngleIncrements();
        actualUnits = Float.valueOf(centerText);
        prevActualUnits = 0f;

        if (!smallText.equals("%")) {
            totalUnits = Float.valueOf(smallText);
        }

        updateMeter();
    }

    private void updateMeter() {
        if (prevAngle != DEFAULT_START_ANGLE) {
            prevAngle = sweepAngle;
        }

        if (isPercentage()) {
            sweepAngle = actualUnits / 100 * 360.0f;
        } else {
            sweepAngle = actualUnits / totalUnits * 360.0f;
        }

        if (sweepAngle >= 360f) {
            sweepAngle = 360f;
            progressPaint.setColor(Color.WHITE);
        } else {
            progressPaint.setColor(progressBarColor);
        }

        invalidate();
    }

    public void setActualUnitsText(String centerText) {
        this.centerText = centerText;
        updateMeter();
    }

    public void setActualUnits(Float actualUnits) {
        prevActualUnits = this.actualUnits;

        if (isPercentage()) {
            actualUnits = actualUnits <= 100 ? actualUnits : 100;
        } else {
            actualUnits = actualUnits <= totalUnits ? actualUnits : totalUnits;
        }

        this.actualUnits = actualUnits;
        setActualUnitsText(String.format("%.0f", actualUnits));
    }

    public void setTotalText(String totalText) {
        totalUnitsTV.setText(totalText);
    }

    public void setTotalUnits(Float totalUnits) {
        this.totalUnits = totalUnits;
        setTotalText(String.format("%.0f", totalUnits));
    }

    public float getActualUnits() {
        return actualUnits;
    }

    private float getAngleIncrements() {
        if (isPercentage()) {
            return 360f / 100f;
        } else {
            return 360f / totalUnits;
        }
    }

    private boolean isPercentage() {
        return totalUnits == null;
    }
}

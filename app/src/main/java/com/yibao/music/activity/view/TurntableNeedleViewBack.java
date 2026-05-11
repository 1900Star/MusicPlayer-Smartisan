package com.yibao.music.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class TurntableNeedleViewBack extends View {
    private Paint paint;
    private Path needlePath;
    private float rotationAngle = 0;
    private float startX, startY;
    private float startAngle;

    public TurntableNeedleViewBack(Context context) {
        super(context);
        init();
    }

    public TurntableNeedleViewBack(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TurntableNeedleViewBack(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(android.graphics.Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        needlePath = new Path();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        canvas.save();
        canvas.rotate(rotationAngle, centerX, centerY);

        needlePath.reset();
        needlePath.moveTo(centerX, centerY);
        needlePath.lineTo(centerX + 100, centerY - 200);
        needlePath.lineTo(centerX + 120, centerY - 220);
        needlePath.lineTo(centerX + 120, centerY - 240);

        canvas.drawPath(needlePath, paint);
        canvas.restore();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = x;
                startY = y;
                startAngle = rotationAngle;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = x - centerX;
                float dy = y - centerY;
                float newAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
                float deltaAngle = newAngle - (float) Math.toDegrees(Math.atan2(startY - centerY, startX - centerX));
                rotationAngle = startAngle + deltaAngle;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}

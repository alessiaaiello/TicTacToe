package com.example.tictactoe;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ConfettiView extends View {

    private Paint[] paints;
    private float[] confettiX;
    private float[] confettiY;
    private float[] confettiVelocityY;
    private float[] confettiRotation;
    private float[] confettiRotationVelocity;
    private boolean isAnimating = false;
    private ValueAnimator animator;

    public ConfettiView(Context context) {
        super(context);
        init();
    }

    public ConfettiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private float[] confettiVelocityX;

    private void init() {
        int particleCount = 50;
        confettiX = new float[particleCount];
        confettiY = new float[particleCount];
        confettiVelocityY = new float[particleCount];
        confettiVelocityX = new float[particleCount];
        confettiRotation = new float[particleCount];
        confettiRotationVelocity = new float[particleCount];
        paints = new Paint[2];

        paints[0] = new Paint();
        paints[0].setColor(Color.parseColor("#006994")); // Ocean blue

        paints[1] = new Paint();
        paints[1].setColor(Color.parseColor("#FF8C42")); // Orange

        resetConfetti();
    }

    private void resetConfetti() {
        for (int i = 0; i < confettiX.length; i++) {
            boolean isLeftCannon = Math.random() < 0.5;
            
            if (isLeftCannon) {
                // Start from bottom left, wider spread
                confettiX[i] = (float) Math.random() * (getWidth() * 0.3f);
                confettiVelocityX[i] = -8 + (float) Math.random() * 4; // Stronger leftward spread
            } else {
                // Start from bottom right, wider spread
                confettiX[i] = getWidth() - (float) Math.random() * (getWidth() * 0.3f);
                confettiVelocityX[i] = 6 + (float) Math.random() * 6; // Stronger rightward spread
            }
            
            confettiY[i] = getHeight();
            confettiVelocityY[i] = -16 - (float) Math.random() * 8; // Much stronger upward force
            confettiRotation[i] = (float) Math.random() * 360;
            confettiRotationVelocity[i] = -5 + (float) Math.random() * 10;
        }
    }

    public void startConfetti() {
        if (isAnimating) return;
        isAnimating = true;
        resetConfetti();

        animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000); // Longer duration - 5 seconds
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();

            for (int i = 0; i < confettiY.length; i++) {
                confettiY[i] += confettiVelocityY[i];
                confettiX[i] += confettiVelocityX[i];
                confettiVelocityY[i] += 0.08f; // Slightly slower gravity to stay up longer
                confettiRotation[i] += confettiRotationVelocity[i];

                if (confettiY[i] < -50) {
                    confettiY[i] = getHeight();
                    boolean isLeftCannon = Math.random() < 0.5;
                    if (isLeftCannon) {
                        confettiX[i] = (float) Math.random() * (getWidth() * 0.3f);
                        confettiVelocityX[i] = -8 + (float) Math.random() * 4;
                    } else {
                        confettiX[i] = getWidth() - (float) Math.random() * (getWidth() * 0.3f);
                        confettiVelocityX[i] = 6 + (float) Math.random() * 6;
                    }
                    confettiVelocityY[i] = -16 - (float) Math.random() * 8;
                }
            }
            invalidate();
        });
        animator.start();
    }

    public void stopConfetti() {
        isAnimating = false;
        if (animator != null) {
            animator.cancel();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isAnimating) return;

        for (int i = 0; i < confettiX.length; i++) {
            canvas.save();
            canvas.translate(confettiX[i], confettiY[i]);
            canvas.rotate(confettiRotation[i]);

            Paint paint = paints[i % 2];
            canvas.drawCircle(0, 0, 5, paint);

            canvas.restore();
        }
    }
}

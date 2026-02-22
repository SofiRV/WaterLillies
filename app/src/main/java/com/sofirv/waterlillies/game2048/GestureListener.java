package com.sofirv.waterlillies.game2048;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private MainActivity activity;


    public GestureListener(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    moveRight();
                } else {
                    moveLeft();
                }
                return true;
            }
        }else {
            if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    moveDown();
                } else {
                    moveUp();
                }
                return true;
            }
        }
        return false;
    }

    private void moveRight() {
        activity.moveRight();
    }

    private void moveLeft() {
        activity.moveLeft();
    }

    private void moveUp() {
        activity.moveUp();
    }

    private void moveDown() {
        activity.moveDown();
    }
}

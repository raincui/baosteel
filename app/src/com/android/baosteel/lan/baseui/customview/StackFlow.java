package com.android.baosteel.lan.baseui.customview;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

public class StackFlow extends Gallery {

    private Camera mCamera = new Camera();

    public StackFlow(Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
    }

    public StackFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setStaticTransformationsEnabled(true);
    }

    public StackFlow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setStaticTransformationsEnabled(true);
    }

    protected boolean getChildStaticTransformation(View child, Transformation t) {
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        View view = getSelectedView();
        if (child == view) {
            transformImageBitmap(child, t, 0);
        } else {
            transformImageBitmap(child, t, 100);
        }
        return true;
    }

    private void transformImageBitmap(View child, Transformation t,
                                      int rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getLayoutParams().height;
        final int imageWidth = child.getLayoutParams().width;
        mCamera.translate(0.0f, 0.0f, rotationAngle);
        mCamera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
        mCamera.restore();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
package com.boost.leonid.customviewanimation;

import android.support.v4.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class VertexHolder {
    private float mTop, mLeft, mWidth, mHeigth;
    private List<Pair<Float, Float>> mNextVertexPosition;


    public VertexHolder(float mLeft, float mTop, float mWidth, float mHeigth) {
        this.mTop = mTop;
        this.mLeft = mLeft;
        this.mWidth = mWidth;
        this.mHeigth = mHeigth;
    }

    public void addVertexPosition(Pair<Float, Float> ... position){
        mNextVertexPosition = Arrays.asList(position);
    }
    public float getTop() {
        return mTop;
    }

    public void setTop(float top) {
        mTop = top;
    }

    public float getLeft() {
        return mLeft;
    }

    public void setLeft(float left) {
        mLeft = left;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    public float getHeigth() {
        return mHeigth;
    }

    public void setHeigth(float heigth) {
        mHeigth = heigth;
    }

    public float getCenterX(){
        return mLeft + mWidth / 2;
    }
    public float getCenterY(){
        return mTop + mHeigth / 2;
    }
    public float getRight(){
        return mLeft + mWidth;
    }
    public float getBottom(){
        return mTop + mHeigth;
    }
}

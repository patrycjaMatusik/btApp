package com.example.patrycja.btappvol2;

import android.view.View;

/**
 * Created by Patrycja on 2018-04-17.
 */


public class Scale {

    private static Scale instance;
    private float xCalibrated;
    private float yCalibrated;
    private float xViewWidthSecond;
    private float yViewHeightSecond;
    private float xViewWidthMine;
    private float yViewHeightMine;

    private Scale() {

    }

    public static Scale getInstance() {
        if (instance == null) {
            synchronized (Scale.class) {
                if (instance == null) {
                    instance = new Scale();
                }
            }
        }
        return instance;
    }

    public void setxViewWidthSecond(float xViewWidthSecond) {
        this.xViewWidthSecond = xViewWidthSecond;
    }

    public void setxViewWidthMine(float xViewWidthMine) {
        this.xViewWidthMine = xViewWidthMine;
    }

    public void setyViewHeightMine(float yViewHeightMine) {
        this.yViewHeightMine = yViewHeightMine;
    }

    public void setyViewHeightSecond(float yViewHeightSecond) {
        this.yViewHeightSecond = yViewHeightSecond;
    }

    public float getxViewWidthSecond() {
        return xViewWidthSecond;
    }

    public float getxViewWidthMine() {
        return xViewWidthMine;
    }

    public float getyViewHeightMine() {
        return yViewHeightMine;
    }

    public float getyViewHeightSecond() {
        return yViewHeightSecond;
    }

    public float[] getValues(float x, float y) {
        xCalibrated = ((x*xViewWidthMine) / xViewWidthSecond);
        yCalibrated = ((y*yViewHeightMine) / yViewHeightSecond);
        float[] out = {instance.xCalibrated, instance.yCalibrated};
        return out;
    }


}


package com.example.patrycja.btappvol2;

import android.view.View;

/**
 * Created by Patrycja on 2018-04-17.
 */


public class Scale {

    private static Scale instance;
    private double xCalibrated;
    private double yCalibrated;
    private double xViewWidthSecond;
    private double yViewHeigthSecond;
    private double xViewWidthMine;
    private double yViewHeightMine;

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

    public void setxViewWidthSecond(double xViewWidthSecond) {
        this.xViewWidthSecond = xViewWidthSecond;
    }

    public void setyViewHeigthSecond(double yViewHeigthSecond) {
        this.yViewHeigthSecond = yViewHeigthSecond;
    }

    public void setxViewWidthMine(double xViewWidthMine) {
        this.xViewWidthMine = xViewWidthMine;
    }

    public void setyViewHeightMine(double yViewHeightMine) {
        this.yViewHeightMine = yViewHeightMine;
    }


    public double[] getValues(double x, double y) {
        xCalibrated = (x / xViewWidthMine) * xViewWidthSecond;
        yCalibrated = (y / yViewHeightMine) * yViewHeigthSecond;
        double[] out = {instance.xCalibrated, instance.yCalibrated};
        return out;
    }


}


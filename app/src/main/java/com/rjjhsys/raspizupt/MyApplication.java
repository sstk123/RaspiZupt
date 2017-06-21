package com.rjjhsys.raspizupt;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;

public class MyApplication extends Application {
    private static Context context;
    public static double[] position = new double[2];
    public static ArrayList<Double> positionX = new ArrayList<Double>();
    public static ArrayList<Double> positionY = new ArrayList<Double>();
    public static int mode = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        positionX.add(Double.parseDouble("0"));
        positionY.add(Double.parseDouble("0"));
    }
    public static Context getContext() {
        return context;
    }
    public static void setPoint(double[] b) {
        position[0] = b[0];
        position[1] = b[1];
    }
}
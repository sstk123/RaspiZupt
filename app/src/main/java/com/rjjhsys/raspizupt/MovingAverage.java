package com.rjjhsys.raspizupt;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by foamf on 2016/5/1.
 */
public class MovingAverage {
    private final Queue<Double> window = new LinkedList<Double>();
    private int period;
    private double sum;
    public void newNum(double num) {
        sum += num;
        window.add(num);
        if (window.size() > period) {
            sum -= window.remove();
        }
    }
    public double getAvg() {
        if (window.isEmpty()) return 0; // technically the average is undefined
        return sum / window.size();
    }
// @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    public double[] moving_average(double[] data,int movingSize){//调用函数
        double[] newData = new double[data.length];
        MovingAverage ma = new MovingAverage();
        ma.period = movingSize;
        for(int i =0;i<data.length;i++){
            ma.newNum(data[i]);
            newData[i] = ma.getAvg();
        }
        return newData;
    }
}


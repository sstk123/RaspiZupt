package com.rjjhsys.raspizupt;

/**
 * Created by 刘武 on 2016/9/3.
 */
public class ReverseArray {
    public static double[] reverseArray(double[] data){//将数组的数据进行翻转
        int number = data.length;
        double[] newData = new double[data.length];
        int num = 0;
        for(int i = number-1;i>=0;i--){
            newData[num] = data[i];
            num++;
        }
        return newData;
    }
}

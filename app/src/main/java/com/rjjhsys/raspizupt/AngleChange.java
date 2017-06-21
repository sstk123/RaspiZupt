package com.rjjhsys.raspizupt;


public class AngleChange {
    public double[] pointChange(double[] iniPoint,double length,double angle){
        //inipoint为基础坐标，length表示为长度，angle表示旋转的角度
        double[] newPoint = new double[2];//创建新的坐标
        newPoint[0] = iniPoint[0]+length* Math.cos(angle);
        newPoint[1] = iniPoint[1]+length* Math.sin(angle);//坐标转化
       return newPoint;//返回新的坐标
    }
}

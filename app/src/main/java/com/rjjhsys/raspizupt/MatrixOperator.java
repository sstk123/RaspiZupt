package com.rjjhsys.raspizupt;

import Jama.Matrix;

/**
 * Created by 刘武 on 2017/4/14.
 */
//此函数用于初始化矩阵，通过pitch,roll,yaw
public class MatrixOperator {
//    C = [cos(pitch)*cos(yaw) (sin(roll)*sin(pitch)*cos(yaw))-(cos(roll)*sin(yaw)) (cos(roll)*sin(pitch)*cos(yaw))+(sin(roll)*sin(yaw));
//    cos(pitch)*sin(yaw)  (sin(roll)*sin(pitch)*sin(yaw))+(cos(roll)*cos(yaw))  (cos(roll)*sin(pitch)*sin(yaw))-(sin(roll)*cos(yaw));
//    -sin(pitch) sin(roll)*cos(pitch) cos(roll)*cos(pitch)];
    public  Matrix getOrientationMatrix(double pitch,double roll,double yaw){
        Matrix matrix = new Matrix(3,3);
        matrix.set(0,0, Math.cos(pitch)* Math.cos(yaw));
        matrix.set(0,1,(Math.sin(roll)* Math.sin(pitch)* Math.cos(yaw))-(Math.cos(roll)* Math.sin(yaw)));
        matrix.set(0,2,(Math.cos(roll)* Math.sin(pitch)* Math.cos(yaw))+(Math.sin(roll)* Math.sin(yaw)));
        matrix.set(1,0, Math.cos(pitch)* Math.sin(yaw));
        matrix.set(1,1,(Math.sin(roll)* Math.sin(pitch)* Math.sin(yaw))+(Math.cos(roll)* Math.cos(yaw)));
        matrix.set(1,2,(Math.cos(roll)* Math.sin(pitch)* Math.sin(yaw))-(Math.sin(roll)* Math.cos(yaw)));
        matrix.set(2,0,-Math.sin(pitch));
        matrix.set(2,1, Math.sin(roll)* Math.cos(pitch));
        matrix.set(2,2, Math.cos(roll)* Math.cos(pitch));
        return matrix;
    }
    public static Matrix getAngrRateMatrix(double a1,double a2,double a3){
        Matrix matrix = new Matrix(3,3);
        matrix.set(0,0,0);
        matrix.set(0,1,-a3);
        matrix.set(0,2,a2);
        matrix.set(1,0,a3);
        matrix.set(1,2,-a1);
        matrix.set(2,0,-a2);
        matrix.set(2,1,a1);
        return matrix;
    }
}

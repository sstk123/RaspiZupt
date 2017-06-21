package com.rjjhsys.raspizupt;

/**
 * Created by gongyan on 2017/4/13.
 修改1123
 */

public class HDR {
    private static double pi = 3.141592653;
    //5456
    public static boolean setHDR(double oldData,double newData){
        if (Math.abs(newData - oldData)>((pi/180.0)*2)){
            return false;
        }else {
        return true;
        }
    }
}

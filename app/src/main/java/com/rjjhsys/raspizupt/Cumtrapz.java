package com.rjjhsys.raspizupt;

/**
 * Created by 刘武 on 2016/7/24.
 */
public class Cumtrapz {
    public double Cumtrapz(double[] Z_gy,double[] time,int start,int end){//根据时间进行积分,数据和时间是一一对应的,进行一次积分（加入了数组开始的起始点和终止点）
        double result = 0;
        for(int i=start+1;i<=end;i++){
            result = result + (Z_gy[i-1]+Z_gy[i])/2*(time[i]-time[i-1]);
        }
        return result;
    }
}

package com.rjjhsys.raspizupt;

import java.util.ArrayList;


public class StandardDeviation {
    public double getAverage(ArrayList<Double> arrayList){
        double sum = 0;
        for(int i = 0;i < arrayList.size();i++){
            sum += arrayList.get(i);
        }
        return (double)(sum / arrayList.size());
    }

    //标准差
    public double getStandardDevition(ArrayList<Double> arrayList){
        double sum = 0;
        for(int i = 0;i < arrayList.size();i++){
            sum += Math.sqrt((arrayList.get(i) -getAverage(arrayList)) * (arrayList.get(i) -getAverage(arrayList)));
        }
        return (sum / (arrayList.size() - 1));
    }
    public double standardDeviation(ArrayList<Double> arrayList, double gap){
        //主函数
        //程序的主要功能是输入一个链表，然后通过设定标准差，删除里面不正确的数据，返回平均值
        //gap的意思就是真实值和平均值当处于什么范围之外，就进行数据的剔除
        double sta = getAverage(arrayList);//得到标准差，通过每个数和标准差进行比较，然后进行数据的删除，求平均
        double sum = 0;
        int number = 0;
        for(int i=0;i<arrayList.size();i++){
            if(Math.abs(arrayList.get(i))-sta<gap){
                sum = sum + arrayList.get(i);
                number++;
            }
        }
        return (sum/number);
    }
}

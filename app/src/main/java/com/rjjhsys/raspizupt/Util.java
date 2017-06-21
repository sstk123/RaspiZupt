package com.rjjhsys.raspizupt;

import android.util.Log;

/**
 * Created by gongyan on 2016/5/12.
 */
public class Util {

    public static double tureLocalToCoordinateX(double localX,double width){
        //实际一米等于40像素
//        if (MyApplication.mode==0){
//            localX = localX*20+width/2;
//
//        }else {
//            localX = localX * 10 + width / 2;
//        }
        localX = localX*(width/((1+MyApplication.mode)*20))+width/2;
//        if (Math.abs(localX-0.5*width)>350&&MyApplication.mode<4){
//            MyApplication.mode = MyApplication.mode+1;
//        }
//        Log.e("utils", String.valueOf(10*(4- MyApplication.mode)));
        return localX;
    }
    public static double tureLocalToCoordinateY(double localY,double width){
        //实际一米等于40像素
//        if (MyApplication.mode==0){
//            localY = localY*20+400;
//        }else{
//            localY = localY*10+400;
//        }
        localY = localY*(width/((1+MyApplication.mode)*20))+width/2+100;
//        if (Math.abs(localY-400)>350&&MyApplication.mode<4){
//            MyApplication.mode = MyApplication.mode+1;
//        }
//       Log.e("utils", width+"/////"+ String.valueOf(localY));
        return localY;
    }
    public static double[] parseData(StringBuilder show){
        //将得到的StringBuilder数据进行处理，得到double数组，空格
        String[] result = show.toString().split(" ");
        double[] data = new double[result.length];
        for(int i = 0;i<result.length;i++){
            data[i] = Double.parseDouble(result[i]);
        }
        return data;
    }
}

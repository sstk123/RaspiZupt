package com.rjjhsys.raspizupt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public  class DrawView extends View {
    private int count = 6;                //数据个数

    private float radius;                   //网格最大半径
    private int centerX;                  //中心X
    private int centerY;                  //中心Y
    private String[] titles = {"a","b","c","d","e","f"};
    private double[] data = {100,60,60,60,100,50,10,20}; //各维度分值
    private float maxValue = 100;             //数据最大值
    private Paint mainPaint;                //雷达区画笔
    private Paint valuePaint;               //数据区画笔
    private Paint textPaint;                //文本画笔
    private  double[] fixed = new double[6];//用以记录三个测试路由器的坐标
    private double xPoint=0;
    private double yPoint=0;
    Canvas canvas1;
    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    int width = wm.getDefaultDisplay().getWidth();

    int height = wm.getDefaultDisplay().getHeight();
    private Paint xyChartPaint, chartLinePaint,linePaint,dashedPaint;//第二种方法
    private  double circleX,circleY;

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context) {
        super(context);
        init();
    }

    public DrawView(Context context, double circleX, double circleY, double[] fixed) {
        super(context);
    }


    //初始化  
    private void init() {
        count = Math.min(data.length,titles.length);

        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setColor(Color.GRAY);
        mainPaint.setStyle(Paint.Style.STROKE);


        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setColor(Color.RED);
        valuePaint.setStrokeWidth(5);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h, w)/2*0.9f;
        centerX = w/2;
        centerY = h/2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas1=canvas;

        //由于在转换坐标时可能会改变mode ，但应该是下次调用时产生作用

        drawAxis(canvas);//绘制直线
        drawPoint(canvas);//绘制点



//        drawRegion2(canvas);//绘制定位点
    }

    /**
     * 绘制点
     * @param canvas
     */

    private void drawPoint(Canvas canvas){
        Path path = new Path();
        valuePaint.setAlpha(255);
        //绘制中心点
        canvas.drawCircle(width / 2, (float) (0.5*width+100), 5, valuePaint);
        float[] d=new float[4];
        d[0]=100;d[1]=200;d[2]=300;d[3]=400;

        for (int i=1;i< MyApplication.positionX.size();i++){
            //必须得先
            d[0]=(float) Util.tureLocalToCoordinateX(MyApplication.positionX.get(i - 1), (double) width);
            d[1]=(float) Util.tureLocalToCoordinateY(MyApplication.positionY.get(i - 1), (double) width);
            d[2]=(float) Util.tureLocalToCoordinateX(MyApplication.positionX.get(i), (double) width);
            d[3]= (float) Util.tureLocalToCoordinateY(MyApplication.positionY.get(i), (double) width);
            canvas.drawLines( d,valuePaint);
//            Log.e("draw", String.valueOf(MyApplication.positionX.get(i))+"---"+MyApplication.positionY.get(i));

        }

        if ((Math.abs(d[2]-0.5*width)>(0.8*0.5*width)&&MyApplication.mode<6)||(Math.abs(d[3]-(0.5*width+100))>(0.8*0.5*width)&&MyApplication.mode<6)){
            MyApplication.mode++;
            Log.e("draw", "222222222222222222222");

        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        //绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }
    /**
     * 绘制坐标轴
     */
    private void drawAxis(Canvas canvas){

        Path path = new Path();
        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(5);

        //画Y轴
        canvas.drawLine(width/2, (float) (100), width/2, (float) (0.5*width+0.5*width+100), linePaint);
//        canvas.drawLine(width/2,800, width/2-20, 800-20, linePaint);
//        canvas.drawLine(width/2,800, width/2+20, 800-20, linePaint);
        //画X轴
        canvas.drawLine(0, (float) (0.5*width+100), width, (float) (0.5*width+100), linePaint);
//        canvas.drawLine(width-10,400, width-40-20, 380, linePaint);
//        canvas.drawLine(width-10,400, width-40-20,420, linePaint);
        linePaint.setTextSize(40);
        final int[] location = new int[2];
        this.getLocationOnScreen(location);
        //绘制中心数
        canvas.drawText("0",width/2+10,width/2+100,linePaint);        //画背景虚线
        dashedPaint = new Paint();
        PathEffect effects = new DashPathEffect(new float[]{5,5,5,5},1);
        dashedPaint.setPathEffect(effects);

        int num = 10*(1+MyApplication.mode);
        //水平的虚线
        for(int i=0;i<num+1;i++){
            canvas.drawLine(0, (float) (0.5*width+100-0.5*width/(num)*i), width,(float) (0.5*width+100-0.5*width/(num)*i),dashedPaint);
            canvas.drawLine(0, (float) (0.5*width+100+0.5*width/(num)*i), width,(float) (0.5*width+100+0.5*width/(num)*i),dashedPaint);
        }
        //垂直的
        for(int i=0;i<num+1;i++){
            canvas.drawLine((float) (0.5*width-0.5*width/(num)*i), (float) (100), (float) (0.5*width-0.5*width/(num)*i),(float) (0.5*width+100+0.5*width),dashedPaint);
            canvas.drawLine((float) (0.5*width+0.5*width/(num)*i), (float) (100), (float) (0.5*width+0.5*width/(num)*i),(float) (0.5*width+100+0.5*width),dashedPaint);

        }

//        if (MyApplication.mode==0){
//        //水平的虚线
//        for(int i=0;i<19;i++){
//            canvas.drawLine(width/2-380, 400+20*i, width/2+380,400+20*i,dashedPaint);
//            canvas.drawLine(width/2-380, 400-20*i, width/2+380,400-20*i,dashedPaint);
//        }
//
//        //垂直的虚线
//        for(int i=0;i<20;i++){
//            canvas.drawLine(width/2+20*i, 38, width/2+20*i, 762, dashedPaint);
//            canvas.drawLine(width/2-20*i, 38, width/2-20*i, 762, dashedPaint);
//        }}else if (MyApplication.mode==1){
//            //水平的虚线
//            for (int i = 0; i < 39; i++) {
//                canvas.drawLine(width / 2 - 420, 400 + 10 * i, width / 2 + 420, 400 + 10 * i, dashedPaint);
//                canvas.drawLine(width / 2 - 420, 400 - 10 * i, width / 2 + 420, 400 - 10 * i, dashedPaint);
//            }
//
//            //垂直的虚线
//            for (int i = 0; i < 43; i++) {
//                canvas.drawLine(width / 2 + 10 * i, 18, width / 2 + 10 * i, 782, dashedPaint);
//                canvas.drawLine(width / 2 - 10 * i, 18, width / 2 - 10 * i, 782, dashedPaint);
//            }
//        }
    }
    public void setXY(double x,double y){
        xPoint=x;
        yPoint=y;
        MyApplication.positionX.add(x);
        MyApplication.positionY.add(y);
//        System.out.println("坐标："+x+"///"+y);
        invalidate();

    }
}  
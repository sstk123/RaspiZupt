package com.rjjhsys.raspizupt;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import Jama.Matrix;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends Activity
{
    StringBuilder show = new StringBuilder();
    double[] getPoint = new double[3];
    boolean flag = true;
    double g = 9.8;
    double pitch;
    double roll;
    double yaw;
    double kalmanAngle;
    double preTime=0;
    double dt;
    private DrawView drawView1;
    private Handler handler;
    Matrix C = new Matrix(3,3);
    Matrix gyroMatr = new Matrix(3,1);
    Matrix accMatr = new Matrix(3,1);
    Matrix accN = new Matrix(3,1);//将得到的数据转化为状态坐标系下
    Matrix accS = new Matrix(3,1);//原始加速度数据矩阵
    Matrix velN = new Matrix(3,1);//进行速度数据的保存
    Matrix posN = new Matrix(3,1);//进行位置的数据保存
    MatrixOperator matrixOperator = new MatrixOperator();
    MyKalmanFilter myKalmanFilter = new MyKalmanFilter();
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawView1 = (DrawView) this.findViewById(R.id.myView);
        handler = new Handler(){

            public void handleMessage(Message msg){
                drawView1.setXY(myKalmanFilter.newPos.get(0,0),myKalmanFilter.newPos.get(1,0));
            }
        };
        new Thread() {//创建线程
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(5678);
                    while (true) {
                        Socket client = serverSocket.accept();
                        show.delete(0,show.length());//将数据进行清除
                        DataInputStream input = new DataInputStream(client.getInputStream()); // 用DataInputStream存放接收到的数据
                        byte[] buf = new byte[1024];
                        int readnum;
                        readnum = input.read(buf);
                        System.out.print("接受到的是：");
//                        ArrayList arrayList = new ArrayList();
                        int i = 0;
                        while (buf[i] != 0) {
//                            System.out.print((char) buf[i]);
                            show.append((char)buf[i]);
                            i++;
                        }
//                        System.out.println("");
                        double[] data=Util.parseData(show);//获得的数据，前面是时间，加速度三轴，陀螺仪
                        for (double r:data){
//                            System.out.print(r);
                        }
//                        System.out.println("");
                        if(flag){
                            //第一组数据进行矩阵的更新，数据的初始化
                            init(data);
                            flag = false;
                        }
                        else {
                            //进行数据的上传和进行图像的处理
                           updateAngDist(data);

                        }
                    }
                }
                catch (Exception e){
                    System.out.println("服务器异常："+e.getMessage());
                 e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateAngDist(double[] data) {
        dt = (preTime - data[0])/1000000;//时间戳是负的
        System.out.println("时间戳是"+dt);
        preTime = data[0];
        //加速度矩阵赋值
        accMatr.set(0, 0, data[1]);
        accMatr.set(1, 0, data[2]);
        accMatr.set(2, 0, data[3]);
        //陀螺仪矩阵赋值
        gyroMatr.set(0, 0, data[4]);
        gyroMatr.set(1, 0, data[5]);
        gyroMatr.set(2, 0, data[6]);
        //直接得到角度值而不是微分值，应该改为把加速度计算出
        //通过上边应该得到某一时刻的距离以及该时刻对应的角度
        getPoint = myKalmanFilter.update(dt,accMatr,gyroMatr);
//        System.out.println("getPoint"+myKalmanFilter.newPos.get(0,0)+"****"+myKalmanFilter.newPos.get(1,0));
        Message message = new Message();

        handler.sendMessage(message);        //通过得到的坐标值进行绘画
        //-----------------------------------------------------------
        //接下来进行图像的更新
    }

    public void init(double[] data){
        preTime = data[0];
        //得到初始方向矩阵以及初始化yaw角
        pitch = -Math.asin(data[1]/g);
        roll = Math.atan(data[2]/data[3]);
        yaw = 0;
        kalmanAngle = yaw;
        MyKalmanFilter.C = matrixOperator.getOrientationMatrix(pitch,roll,yaw);
        MyKalmanFilter.C_prev = MyKalmanFilter.C;
        myKalmanFilter.preAng = yaw;
        myKalmanFilter.preAcc.set(0,0,data[1]);
        myKalmanFilter.preAcc.set(1,0,data[2]);
        myKalmanFilter.preAcc.set(2,0,data[3]);
        accS.set(0,0,data[1]);
        accS.set(1,0,data[2]);
        accS.set(2,0,data[3]);
        accN = C.times(accS);//得到导航坐标下的坐标
        velN.set(0,0,0);
        velN.set(1,0,0);
        velN.set(2,0,0);
        posN.set(0,0,0);
        posN.set(1,0,0);
        posN.set(2,0,0);
    }
    public Matrix getZero(Matrix a){
        int row = a.getRowDimension();
        int co = a.getColumnDimension();
        for(int i =0;i<row;i++){
            for(int j = 0;j<co;j++){
                a.set(i,j,0);
            }
        }
        return a;
    }
}
package com.rjjhsys.raspizupt;
import Jama.Matrix;
/**
 * Created by gongyan &liuwu(foamflare) on 2017/4/13.
 */

public class MyKalmanFilter {
    double[] point = new double[3];//进行坐标的返回数据
    MatrixOperator matrixOperator = new MatrixOperator();
    Matrix deltaX;
    Matrix P;
    Matrix Q;
    Matrix H;
    Matrix F;
    Matrix R;
    Matrix K;
    Matrix S;
    //误差矩阵的存储
    Matrix attitudeError;
    Matrix posError;
    Matrix velError;
    //传感器误差的处理
    double sigmaOmega = 1e-2, sigmaA = 1e-2, sigma_v = 1e-2;
    //陀螺仪阈值的处理，其中进行ZUPT的判断
    double gyroThreshold = 0.6;
    //    double prePro,preVel = 0,prePos;//保存的yaw角
    double preAng = 0;
    //进行原始数据的保存和进行数据更新的变量申请
    Matrix preVel;
    Matrix newVel;
    Matrix prePos;
    Matrix newPos;
    Matrix gMatri;
    Matrix preAcc;
    Matrix dealtaX;
    static Matrix C = new Matrix(3, 3);
    static Matrix C_prev = new Matrix(3, 3);
    Matrix gyro_bias;
    Matrix angMatrix;

    public MyKalmanFilter() {
        double[][] b = {{-0.00156, -0.00101, -0.00010}};
        gyro_bias = new Matrix(b).transpose();
        double[][] a = {{0}, {0}, {0}, {0}, {0}, {0}};
        deltaX = new Matrix(a);// 先验值
        P = new Matrix(9, 9);
        Q = new Matrix(9, 9);// 状态转移协方差矩阵，进行协方差矩阵进行赋值
        H = new Matrix(3, 9);
        H.set(0, 6, 1);
        H.set(1, 7, 1);
        H.set(2, 8, 1);
        R = new Matrix(3, 3);
        R.set(0, 0, sigma_v * sigma_v);
        R.set(1, 1, sigma_v * sigma_v);
        R.set(2, 2, sigma_v * sigma_v);
        F = new Matrix(9, 9);
        gMatri = new Matrix(3,1);
        gMatri.set(2, 0, 9.8);
        S = new Matrix(3, 3);
        K = new Matrix(9, 3);
        attitudeError = new Matrix(3, 1);
        posError = new Matrix(3, 1);
        velError = new Matrix(3, 1);
        preVel = new Matrix(3, 1);
        newVel = new Matrix(3, 1);
        prePos = new Matrix(3, 1);
        newPos = new Matrix(3, 1);
        gMatri = new Matrix(3, 1);
        preAcc = new Matrix(3, 1);
        dealtaX = new Matrix(9, 1);
        angMatrix = new Matrix(3, 3);
    }

    public double[] update(double dt, Matrix acc, Matrix gyro) {
        //先进行积分运算，得到速度、位移以及角度,角度由于旋转矩阵的作用可以得到累积值，而速度和距离并不能依赖于此，
        // 所以必须得声明变量对速度进行记录，距离由于只是短时间的距离，通过上一时刻的速度以及这一时刻的加速度就可以得到
        //将加速度数据转换到导航坐标系
        //acc_n(:,t) = 0.5*(C + C_prev)*acc_s(:,t);
        //vel_n(:,t) = vel_n(:,t-1) + ((acc_n(:,t) - [0; 0; g] )+(acc_n(:,t-1) - [0; 0; g]))*dt/2;% v=v0+((at+a(t-1))/2)*t
        //pos_n(:,t) = pos_n(:,t-1) + (vel_n(:,t) + vel_n(:,t-1))*dt/2;
        System.out.println("rawAcc:"+acc.get(2,0));

        acc = C.plus(C_prev).times(acc).times(0.5);
        newVel = preVel.plus((acc.minus(gMatri).plus(preAcc.minus(gMatri))).times(dt / 2.0));
        System.out.println("加速度值是："+acc.get(2,0)+"时间间隔是"+dt+"newVel是"+newVel.get(0,0));
        newPos = prePos.plus((newVel.plus(preVel)).times(dt / 2.0));
        //计算角度部分
        gyro = gyro.minus(gyro_bias);
        Matrix angRateMa = matrixOperator.getAngrRateMatrix(gyro.get(0, 0), gyro.get(1, 0), gyro.get(2, 0));
        C = C_prev.times((MyKalmanFilter.setEye(3).times(2)).plus(angRateMa.times(dt))).times((MyKalmanFilter.setEye(3).times(2).minus(angRateMa.times(dt))).inverse());
        C_prev = C;
        double pitch2 = -Math.asin(C.get(2, 0));
        double yaw2 = Math.atan2(C.get(1, 0) / Math.cos(pitch2), C.get(0, 0) / Math.cos(pitch2));
        //数据保存
        preVel = newVel;
        prePos = newPos;
        preAcc = acc;
        //Skew-symmetric cross-product operator matrix formed from the n-frame accelerations.
        S.set(0, 1, -acc.get(2, 0));
        S.set(0, 2, acc.get(1, 0));
        S.set(1, 0, acc.get(2, 0));
        S.set(1, 2, -acc.get(0, 0));
        S.set(2, 0, -acc.get(1, 0));
        S.set(2, 1, acc.get(0, 0));
        // 状态传递矩阵
        F.set(0, 0, 1);
        F.set(1, 1, 1);
        F.set(2, 2, 1);
        F.set(3, 3, 1);
        F.set(4, 4, 1);
        F.set(5, 5, 1);
        F.set(3, 6, dt);
        F.set(4, 7, dt);
        F.set(5, 8, dt);
        F.set(6, 0, -dt * S.get(0, 0));
        F.set(6, 1, -dt * S.get(0, 1));
        F.set(6, 2, -dt * S.get(0, 2));
        F.set(7, 0, -dt * S.get(1, 0));
        F.set(7, 1, -dt * S.get(1, 1));
        F.set(7, 2, -dt * S.get(1, 2));
        F.set(8, 0, -dt * S.get(2, 0));
        F.set(8, 1, -dt * S.get(2, 1));
        F.set(8, 2, -dt * S.get(2, 2));
        F.set(6, 6, 1);
        F.set(7, 7, 1);
        F.set(8, 8, 1);
        // 状态转移协方差矩阵，直接进行数值的赋值
        Q.set(0, 0, (sigmaOmega * dt) * (sigmaOmega * dt));
        Q.set(1, 1, (sigmaOmega * dt) * (sigmaOmega * dt));
        Q.set(2, 2, (sigmaOmega * dt) * (sigmaOmega * dt));
        Q.set(6, 6, (sigmaA * dt) * (sigmaA * dt));
        Q.set(7, 7, (sigmaA * dt) * (sigmaA * dt));
        Q.set(8, 8, (sigmaA * dt) * (sigmaA * dt));
        //不确定性，根据上一时刻的不确定性，估计这一时刻的不确定性
        //P = F*P*F' + Q;
        P = F.times(P).times(F.transpose()).plus(Q);
        // End INS，结束INS系统。下面进行zupt的运算
        if (norm(gyro) < gyroThreshold) {
            //上面条件进行zupt的判断，符合条件就是进行卡尔曼滤波器的处理
            //卡尔曼增益的计算  K = (P*(H)')/((H)*P*(H)' + R);
            K = (P.times(H.transpose())).times((H.times(P)).times(H.transpose()).plus(R).inverse());
            //update the filter state
            dealtaX = K.times(newVel);
            P = setEye(9).minus(K.times(H)).times(P);
            //误差的计算
            attitudeError.set(0, 0, dealtaX.get(0, 0));
            attitudeError.set(1, 0, dealtaX.get(1, 0));
            attitudeError.set(2, 0, dealtaX.get(2, 0));
            posError.set(0, 0, dealtaX.get(3, 0));
            posError.set(1, 0, dealtaX.get(4, 0));
            posError.set(2, 0, dealtaX.get(5, 0));
            velError.set(0, 0, dealtaX.get(6, 0));
            velError.set(1, 0, dealtaX.get(7, 0));
            velError.set(2, 0, dealtaX.get(8, 0));
            //skew-symmetric matrix for small angles to correct orientation
            //进行ang_matrix的赋值，注意matlab前面的负号
            angMatrix.set(0, 0, 0);
            angMatrix.set(0, 1, attitudeError.get(2, 0));
            angMatrix.set(0, 2, -attitudeError.get(1, 0));
            angMatrix.set(1, 0, -attitudeError.get(2, 0));
            angMatrix.set(1, 1, 0);
            angMatrix.set(1, 2, attitudeError.get(0, 0));
            angMatrix.set(2, 0, attitudeError.get(1, 0));
            angMatrix.set(2, 1, -attitudeError.get(0, 0));
            angMatrix.set(2, 2, 0);
            //进行方向矩阵的更新
            C = (setEye(3).times(2).plus(angMatrix)).times(setEye(3).times(2).minus(angMatrix).inverse()).times(C_prev);
            C_prev = C;
            //下面进行速度和位置的更新,并且进行数据的保存
            newVel = newVel.minus(velError);
            newPos = newPos.minus(posError);
            preVel = newVel;
            prePos = newPos;
            point[0] = newPos.get(0, 0);
            point[1] = newPos.get(1, 0);
            point[2] = newPos.get(2, 0);
            return point;
        }
        point[0] = newPos.get(0, 0);
        point[1] = newPos.get(1, 0);
        point[2] = newPos.get(2, 0);
        return point;
    }
    public static Matrix setEye(int length) {
        Matrix matrix = new Matrix(length,length);
        for (int i = 0; i < length; i++) {
            matrix.set(i, i, 1);
        }
        return matrix;
    }
    public double norm(Matrix data){
        //sqrt(sum(x.^2)) %各元素的平方和开平方
        double a  = data.get(0,0)*data.get(0,0);
        double b = data.get(1,0)*data.get(1,0);
        double c = data.get(2,0)*data.get(2,0);
        double result  = Math.sqrt(a+b+c);
        return result;
    }
}

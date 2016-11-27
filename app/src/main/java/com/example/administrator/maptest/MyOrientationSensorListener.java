package com.example.administrator.maptest;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Administrator on 2016/11/26 0026.
 */

public class MyOrientationSensorListener implements SensorEventListener {

    Context mContext;
    SensorManager mSensorManager;
    Sensor mSensor;
    private  float lastX; //x轴上最后的坐标。

    public MyOrientationSensorListener(Context context){
        this.mContext = context;
        //this.mSensorManager = sensorManager;
    }

    public void start(){
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE); //获取系统传感服务。从而得到传感器管理器。
        if(mSensorManager != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); //获取方向传感器
        }
        if(mSensor != null){
            mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_UI); //注册监听
        }
    }

    public void stop(){
        mSensorManager.unregisterListener(this); //定位结束时，停止传感器监听
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){ //传感器时间类型为方向，即为方向传感器。
            float x = sensorEvent.values[SensorManager.DATA_X]; //取x放向上的变化值。有三个方向的值。
            if(Math.abs(x - lastX) > 1.0){ //x轴方向上的度数变化大于1度时才进行赋值操作，避免频繁操作。
                if(onOrientationListener != null){
                    onOrientationListener.onOrientationChanged(x);
                }
            }
            lastX = x;
        }
    }

    public  interface OnOrientationListener{
        void onOrientationChanged(float x);
    }

    private OnOrientationListener onOrientationListener;

    public void setOnOrientationListener(OnOrientationListener onOrientationListener){
        this.onOrientationListener = onOrientationListener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

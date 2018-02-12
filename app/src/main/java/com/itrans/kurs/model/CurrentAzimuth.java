package com.itrans.kurs.model;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.itrans.kurs.OnAzimuthChangedListener;

public class CurrentAzimuth implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int azimuthFrom;
    private int azimuthTo;
    private OnAzimuthChangedListener onAzimuthChangedListener;
    Context mContext;

    public CurrentAzimuth(OnAzimuthChangedListener onAzimuthChangedListener, Context mContext) {
        this.onAzimuthChangedListener = onAzimuthChangedListener;
        this.mContext = mContext;

    }

    public void start(){
        azimuthFrom = 0;
        azimuthTo = 0;
        sensorManager = (SensorManager) mContext.getSystemService(mContext.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_UI);
    }
    public void stop(){
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        azimuthFrom = azimuthTo;
        float[] orientation = new float[3];
        float[] rMat = new float[9];
        SensorManager.getRotationMatrixFromVector(rMat,event.values);
        azimuthTo = (int) (Math.toDegrees(SensorManager.getOrientation(rMat,orientation)[0])+360)%360;

        onAzimuthChangedListener.onAzimuthChanged(azimuthFrom,azimuthTo);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

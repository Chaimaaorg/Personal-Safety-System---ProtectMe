package com.example.personalsafetysystem;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class StepCounterHelper implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount = 0;
    private TextView stepCountTextView; // Reference to the TextView in UserProfile

    public StepCounterHelper(Context context, TextView stepCountTextView) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        this.stepCountTextView = stepCountTextView;
    }

    public void start() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public int getStepCount() {
        return stepCount;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];
            // Update the step count in the TextView
            stepCountTextView.setText(String.valueOf(stepCount));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }
}

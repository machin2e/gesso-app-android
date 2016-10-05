package camp.computer.clay.application.spatial;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

public class OrientationInput implements SensorEventListener {

    SensorManager sm;

    public OrientationInput(Context context) {

        sm = (SensorManager) context.getSystemService(SENSOR_SERVICE);

        // Register this class as a listener for the accelerometer sensor
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME); // SensorManager.SENSOR_DELAY_NORMAL
        // ...and the orientation sensor
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);
    }

    //...
    // The following code inside a class implementing a SensorEventListener
    // ...

    float[] inR = new float[16];
    float[] I = new float[16];
    float[] gravity = new float[3];
    float[] geomag = new float[3];
    float[] orientVals = new float[3];

    double azimuth = 0;
    double pitch = 0;
    double roll = 0;

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // If the sensor data is unreliable return
        if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;

        // Gets the value of the sensor that has been changed
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                geomag = sensorEvent.values.clone();
                break;
        }

        // If gravity and geomag have values then find rotation matrix
        if (gravity != null && geomag != null) {

            // checks that the rotation matrix is found
            boolean success = SensorManager.getRotationMatrix(inR, I, gravity, geomag);
            if (success) {
                SensorManager.getOrientation(inR, orientVals);

                // values[0]: Azimuth, rotation around the Z axis (0<=azimuth<360). 0 = North, 90 = East, 180 = South, 270 = West
                // values[1]: Pitch, rotation around X axis (-180<=pitch<=180), with positive values when the z-axis moves toward the y-axis.
                // values[2]: Roll, rotation around Y axis (-90<=roll<=90), with positive values when the z-axis moves toward the x-axis.
                //
                // From Android Documentation (https://developer.android.com/reference/android/hardware/SensorListener.html)

                azimuth = Math.toDegrees(orientVals[0]);
                pitch = Math.toDegrees(orientVals[1]);
                roll = Math.toDegrees(orientVals[2]);
            }
        }
    }

    public double getRotationX() {
        return pitch;
    }

    public double getRotationY() {
        return roll;
    }

    public double getRotationZ() {
        return azimuth;
    }
}

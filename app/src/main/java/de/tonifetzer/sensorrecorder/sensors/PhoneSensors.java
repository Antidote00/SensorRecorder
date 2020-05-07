package de.tonifetzer.sensorrecorder.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * all available sensors
 * and what to do within one class
 *
 */
public class PhoneSensors extends MySensor implements SensorEventListener{

	private static final int SENSOR_TYPE_HEARTRATE = 65562;

    private SensorManager sensorManager;
    private Sensor acc;
    private Sensor grav;
   	private Sensor lin_acc;
    private Sensor gyro;
    private Sensor magnet;
    private Sensor press;
	private Sensor ori;
	private Sensor heart;
	private Sensor humidity;
	private Sensor rotationVector;
	private Sensor light;
	private Sensor temperature;

	/** local gravity copy (needed for orientation matrix) */
    private float[] mGravity = new float[3];
	/** local geomagnetic copy (needed for orientation matrix) */
    private float[] mGeomagnetic = new float[3];


	/** ctor */
    public PhoneSensors(final Activity act){

		// fetch the sensor manager from the activity
        sensorManager = (SensorManager) act.getSystemService(Context.SENSOR_SERVICE);

		// try to get each sensor
        acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        grav = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        lin_acc = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        press = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		ori = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		heart = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
		humidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
		rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
		temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

		// dump sensor-vendor info to file
		dumpVendors(act);

	}

	private final char NL = '\n';

	/** Write Vendors to file */
	private void dumpVendors(final Activity act) {

		final DataFolder folder = new DataFolder(act, "sensorOutFiles");
		final File file = new File(folder.getFolder(), "vendors.txt");

		try {

			final FileOutputStream fos = new FileOutputStream(file);
			final StringBuilder sb = new StringBuilder();

			// constructor smartphone details
			sb.append("[Device]").append(NL);
			sb.append("\tModel: ").append(Build.MODEL).append(NL);
			sb.append("\tAndroid: ").append(Build.VERSION.RELEASE).append(NL);
			sb.append(NL);

			// construct sensor details
			dumpSensor(sb, SensorType.ACCELEROMETER, acc);
			dumpSensor(sb, SensorType.GRAVITY, grav);
			dumpSensor(sb, SensorType.LINEAR_ACCELERATION, lin_acc);
			dumpSensor(sb, SensorType.GYROSCOPE, gyro);
			dumpSensor(sb, SensorType.MAGNETIC_FIELD, magnet);
			dumpSensor(sb, SensorType.PRESSURE, press);
			dumpSensor(sb, SensorType.RELATIVE_HUMIDITY, humidity);
			dumpSensor(sb, SensorType.ORIENTATION_OLD, ori);
			dumpSensor(sb, SensorType.LIGHT, light);
			dumpSensor(sb, SensorType.AMBIENT_TEMPERATURE, temperature);
			dumpSensor(sb, SensorType.HEART_RATE, heart);

			// write
			fos.write(sb.toString().getBytes());
			fos.close();

		}catch (final IOException e) {
			throw new RuntimeException(e);
		}

	}

	/** dump all details of the given sensor into the provided stringbuilder */
	private void dumpSensor(final StringBuilder sb, final SensorType type, final Sensor sensor) {
		sb.append("[Sensor]").append(NL);
		sb.append("\tour_id: ").append(type.id()).append(NL);
		sb.append("\ttype: ").append(type).append(NL);

		if (sensor != null) {
			sb.append("\tVendor: ").append(sensor.getVendor()).append(NL);
			sb.append("\tName: ").append(sensor.getName()).append(NL);
			sb.append("\tVersion: ").append(sensor.getVersion()).append(NL);
			sb.append("\tMinDelay: ").append(sensor.getMinDelay()).append(NL);
			//sb.append("\tMaxDelay: ").append(sensor.getMaxDelay()).append(NL);
			sb.append("\tMaxRange: ").append(sensor.getMaximumRange()).append(NL);
			sb.append("\tPower: ").append(sensor.getPower()).append(NL);
			//sb.append("ReportingMode: ").append(sensor.getReportingMode()).append(NL);
			sb.append("\tResolution: ").append(sensor.getResolution()).append(NL);
			sb.append("\tType: ").append(sensor.getType()).append(NL);
		} else {
			sb.append("\tnot available!\n");
		}
		sb.append("\n");
	}

    @Override
    public void onSensorChanged(SensorEvent event) {

		/*
		// to compare with the other orientation
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.ORIENTATION_OLD,
					Float.toString(event.values[0]) + ";" +
					Float.toString(event.values[1]) + ";" +
					Float.toString(event.values[2])
				);
			}

		}
		*/

		if(event.sensor.getType() == Sensor.TYPE_HEART_RATE) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.HEART_RATE,
						Float.toString(event.values[0])
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_LIGHT) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.LIGHT,
						Float.toString(event.values[0])
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.AMBIENT_TEMPERATURE,
						Float.toString(event.values[0])
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.RELATIVE_HUMIDITY,
						Float.toString(event.values[0])
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

			// inform listeners
			if (listener != null){

				if(event.values.length > 3){
					listener.onData(SensorType.ROTATION_VECTOR,
                            event.values[0] + ";" +
                                    event.values[1] + ";" +
                                    event.values[2] + ";" +
                                    event.values[3]
					);
				} else {
					listener.onData(SensorType.ROTATION_VECTOR,
                            event.values[0] + ";" +
                                    event.values[1] + ";" +
                                    event.values[2]
					);
				}

			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.GYROSCOPE,
                        event.values[0] + ";" +
                            event.values[1] + ";" +
                            event.values[2]
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_PRESSURE) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.PRESSURE,
					Float.toString(event.values[0])
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.LINEAR_ACCELERATION,
                        event.values[0] + ";" +
                            event.values[1] + ";" +
                            event.values[2]
				);
			}

		}

		else if(event.sensor.getType() == Sensor.TYPE_GRAVITY) {

			// inform listeners
            if (listener != null){
                listener.onData(SensorType.GRAVITY,
                        event.values[0] + ";" +
                                event.values[1] + ";" +
                                event.values[2]
                );
            }

        }

		else if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			// inform listeners
			if (listener != null){
				listener.onData(SensorType.ACCELEROMETER,
                        event.values[0] + ";" +
                            event.values[1] + ";" +
                            event.values[2]
				);
			}

			// keep a local copy (needed for orientation matrix)
			System.arraycopy(event.values, 0, mGravity, 0, 3);

			// NOTE:
			// @see TYPE_MAGNETIC_FIELD
			//updateOrientation();

		}

        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

			// inform listeners
            if (listener != null){
                listener.onData(SensorType.MAGNETIC_FIELD,
                        event.values[0] + ";" +
                                event.values[1] + ";" +
                                event.values[2]
                );
            }

			// keep a local copy (needed for orientation matrix)
			System.arraycopy(event.values, 0, mGeomagnetic, 0, 3);

			// NOTE
			// @see TYPE_ACCELEROMETER
			// only MAG updates the current orientation as MAG is usually slower than ACC and this reduces the file-footprint
			updateOrientation();

        }

    }

	/** calculate orientation from acc and mag */
	private void updateOrientation() {

		// skip orientation update if either grav or geo is missing
		if (mGravity == null) {return;}
		if (mGeomagnetic == null) {return;}

		// calculate rotationMatrix and orientation
        float[] R = new float[9];
        float[] I = new float[9];

		// derive rotation matrix from grav and geo sensors
		boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
		if (!success) {return;}

		// derive orientation-vector using the rotation matrix
        float[] orientationNew = new float[3];
		SensorManager.getOrientation(R, orientationNew);

		// inform listeners
		if (listener != null) {

			// orientation vector
			listener.onData(SensorType.ORIENTATION_NEW,
                    orientationNew[0] + ";" +
                        orientationNew[1] + ";" +
                        orientationNew[2]
			);

			// rotation matrix
            //Write the whole rotationMatrix R into the Listener.
            StringBugger sb();
	    for (int i = 1; i < 8; i++) {
	    	sb.append(R[i]);
	    	sb.append(';');
	    }
	    sb.append(R[8]);
            listener.onData(SensorType.ROTATION_MATRIX, sb.toString());
		}

	}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing to-do here
    }

    @Override
    public void onResume(final Activity act) {

		// attach as listener to each of the available sensors
        registerIfPresent(acc, SensorManager.SENSOR_DELAY_FASTEST);
      	registerIfPresent(grav, SensorManager.SENSOR_DELAY_FASTEST);
       	registerIfPresent(gyro, SensorManager.SENSOR_DELAY_FASTEST);
        registerIfPresent(lin_acc, SensorManager.SENSOR_DELAY_FASTEST);
        registerIfPresent(magnet, SensorManager.SENSOR_DELAY_FASTEST);
        registerIfPresent(press, SensorManager.SENSOR_DELAY_FASTEST);
		registerIfPresent(ori, SensorManager.SENSOR_DELAY_FASTEST);
		registerIfPresent(heart, SensorManager.SENSOR_DELAY_FASTEST);
		registerIfPresent(humidity, SensorManager.SENSOR_DELAY_FASTEST);
		registerIfPresent(rotationVector, SensorManager.SENSOR_DELAY_FASTEST);
		registerIfPresent(light, SensorManager.SENSOR_DELAY_FASTEST);
		registerIfPresent(temperature, SensorManager.SENSOR_DELAY_FASTEST);

    }

	private void registerIfPresent(final Sensor sens, final int delay) {
		if (sens != null) {
			sensorManager.registerListener(this, sens, delay);
			Log.d("PhoneSensors", "added sensor " + sens.toString());
		} else {
			Log.d("PhoneSensors", "sensor not present. skipping");
		}
	}

    @Override
    public void onPause(final Activity act) {

		// detach from all events
		sensorManager.unregisterListener(this);

    }

}

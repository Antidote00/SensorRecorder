package de.tonifetzer.sensorrecorder.sensors;

import android.app.Activity;

/**
 * base-class for all Sensors
 */
public abstract class MySensor {

	/** listen for sensor events */
	public interface SensorListener {

		public void onData(final String csv);

		/** received data from the given sensor */
        public void onData(final SensorType id, final String csv);

	}

	/** the listener to inform (if any) */
	protected SensorListener listener = null;


	/** start the sensor */
    public abstract void onResume(final Activity act);

	/** stop the sensor */
	public abstract void onPause(final Activity act);

	/** attach the given listener to the sensor */
	public void setListener(final SensorListener listener) {this.listener = listener;}

}

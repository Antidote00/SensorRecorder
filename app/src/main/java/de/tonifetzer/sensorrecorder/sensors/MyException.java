package de.tonifetzer.sensorrecorder.sensors;

import android.widget.Toast;
import de.tonifetzer.sensorrecorder.MainActivity;


/**
 * Throws an exception into a Toast
 */

public class MyException extends RuntimeException {

    MyException(final String err, final Throwable t) {
        super(err, t);
        Toast.makeText(MainActivity.getAppContext(), err, Toast.LENGTH_LONG).show();
    }

    MyException(final String err) {
        super(err);
        Toast.makeText(MainActivity.getAppContext(), err, Toast.LENGTH_LONG).show();
    }

}

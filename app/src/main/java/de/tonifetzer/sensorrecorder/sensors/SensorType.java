package de.tonifetzer.sensorrecorder.sensors;

/**
 * The different sensor types and their id's
 */
public enum SensorType {

    ACCELEROMETER(0),
    GRAVITY(1),
    LINEAR_ACCELERATION(2),
    GYROSCOPE(3),
    MAGNETIC_FIELD(4),
    PRESSURE(5),
    ORIENTATION_NEW(6),
    ROTATION_MATRIX(7),
    WIFI(8),
    BEACON(9),
    RELATIVE_HUMIDITY(10),
    ORIENTATION_OLD(11),
    ROTATION_VECTOR(12),
    LIGHT(13),
    AMBIENT_TEMPERATURE(14),
    HEART_RATE(15),
    ;

    private int id;

    SensorType(final int id) {
        this.id = id;
    }

    public final int id() {return id;}

}

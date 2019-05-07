# SensorRecorder
Simple Sensor Data Recorder for Smartphone and Watch.

# Description
A very simple application which writes all sensor data into a .csv by pressing a button. Works with smartphone and smartwatch. Missing sensors are ignored. The .csv is structured as follows:

*Timestamp in milliseconds since start; Sensor ID; Values*

The file is named after the current System.currentTimeMillis () timestamp and is stored under 

*MainStorage/Android/Data/de.tonifetzer.sensorrecorder/files/documents/sensorOutFiles/...*

However, the path may differ from device to device, depending on whether an SD card is included or not.


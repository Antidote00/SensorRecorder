package de.tonifetzer.sensorrecorder;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYPlot;

import java.util.concurrent.TimeUnit;

import de.tonifetzer.sensorrecorder.sensors.Logger;
import de.tonifetzer.sensorrecorder.sensors.MySensor;
import de.tonifetzer.sensorrecorder.sensors.PhoneSensors;
import de.tonifetzer.sensorrecorder.sensors.SensorType;

public class MainActivity extends AppCompatActivity {

    private static final int HISTORY_SIZE = 1500;

    //ui in main layout
    private View mainView;
    private ToggleButton recButton;
    private Button plotButton;
    private TextView txtViewTimeCounter;
    private TextView txtViewFilename;
    private TextView txtViewFilesize;
    private boolean isMainView = true;

    //ui in plotter layout
    private View plotView;
    private Button backButton;
    private Button accButton;
    private Button linearAccButton;
    private Button gravityButton;
    private Button gyroscopeButton;
    private Button barometerButton;

    private SimpleXYSeries xBuffer;
    private SimpleXYSeries yBuffer;
    private SimpleXYSeries zBuffer;
    private Redrawer redrawer;
    private XYPlot plotClass;
    private SensorType currentSensorToPlot = SensorType.ACCELEROMETER;

    private PhoneSensors phoneSensors;
    private final Logger dataLogger = new Logger(this);

    private int loadCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this is super ugly. normally we would use fragments to switch ui
        mainView = getLayoutInflater().inflate(R.layout.activity_main, null);
        plotView = getLayoutInflater().inflate(R.layout.simple_1d_plot, null);
        setContentView(mainView);

        //this is a small hack to get a static context
        MainActivity.context = getApplicationContext();

        //init the sensors of the phone
        phoneSensors = new PhoneSensors(this);

        //find ui elements in main
        recButton = findViewById(R.id.toggleButton);
        plotButton = findViewById(R.id.buttonPlotter);
        txtViewTimeCounter = findViewById(R.id.textViewTimeCounter);
        txtViewFilename = findViewById(R.id.textViewFilename);
        txtViewFilesize = findViewById(R.id.textViewFilesize);

        //find ui elements in plotter
        backButton = plotView.findViewById(R.id.buttonBack);
        accButton = plotView.findViewById(R.id.buttonAcc);
        linearAccButton = plotView.findViewById(R.id.buttonLinearAcc);
        gravityButton = plotView.findViewById(R.id.buttonGravity);
        gyroscopeButton = plotView.findViewById(R.id.buttonGyro);
        barometerButton = plotView.findViewById(R.id.buttonBarometer);

        //init the plotter data
        xBuffer = new SimpleXYSeries("x");
        xBuffer.useImplicitXVals();
        yBuffer = new SimpleXYSeries("y");
        yBuffer.useImplicitXVals();
        zBuffer = new SimpleXYSeries("z");
        zBuffer.useImplicitXVals();

        //format the plotter window
        plotClass = plotView.findViewById(R.id.plot);
        plotClass.addSeries(xBuffer, new LineAndPointFormatter(
                Color.rgb(100, 100, 200), null, null, null));
        plotClass.addSeries(yBuffer, new LineAndPointFormatter(
                Color.rgb(100, 200, 100), null, null, null));
        plotClass.addSeries(zBuffer, new LineAndPointFormatter(
                Color.rgb(200, 100, 100), null, null, null));
        plotClass.setDomainStepMode(StepMode.INCREMENT_BY_VAL);
        plotClass.setDomainStepValue(HISTORY_SIZE/10);
        plotClass.setLinesPerRangeLabel(3);
        plotClass.setDomainLabel("index");
        plotClass.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);

        //redrawer for dynamic data in plotter
        redrawer = new Redrawer( plotClass, 100, false);

        //set the listener
        phoneSensors.setListener(new MySensor.SensorListener(){
            @Override public void onData(final String csv) {}
            @Override public void onData(final SensorType id, final String csv) {addDataToFile(id, csv); }
        });

        recButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundColor(Color.parseColor("#FFCC0000"));
                    startRecording();
                }
                else{
                    buttonView.setBackgroundColor(Color.parseColor("#FF669900"));
                    stopRecording();
                }
            }
        });

        plotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(plotView);
                isMainView = false;
                redrawer.start();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(mainView);
                isMainView = true;
                redrawer.pause();
            }
        });

        accButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.ACCELEROMETER;
                plotClass.setTitle("Accelerometer Data");
            }
        });

        linearAccButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.LINEAR_ACCELERATION;
                plotClass.setTitle("Linear Accelerometer Data");
            }
        });

        gravityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.GRAVITY;
                plotClass.setTitle("Gravity Data");
            }
        });

        gyroscopeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.GYROSCOPE;
                plotClass.setTitle("Gyroscope Data");
            }
        });

        barometerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanPlotterData();
                currentSensorToPlot = SensorType.PRESSURE;
                plotClass.setTitle("Barometer Data");
            }
        });

    }

    private void startRecording() {
        phoneSensors.onResume(this);
        loadCounter = 0;
        dataLogger.start();
        String path =  dataLogger.getFile().getAbsolutePath();
        txtViewFilename.setText(path.substring(path.length()-17));
    }

    private void stopRecording() {
        phoneSensors.onPause(this);
        dataLogger.stop();
        cleanPlotterData();
    }

    private void addDataToFile(final SensorType id, final String csv) {

        dataLogger.addCSV(id, csv);
        provideDataToPlotter(id, csv);

        runOnUiThread(new Runnable() {
            @Override public void run() {

                // dump buffer stats every x entries
                if (++loadCounter % 250 == 0) {

                    //set filesize and buffer dump
                    final int kbPerMin = (int) (dataLogger.getTotalSize() / 1024 * 1000 * 60 / (System.currentTimeMillis() - dataLogger.getStartTS()));
                    txtViewFilesize.setText( (dataLogger.getCurrentSize() / 1024) + "kb, " + kbPerMin + "kb/min");

                    //set time (of course, this is not perfectly accurate, however for this purpose its okay)
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - dataLogger.getStartTS());
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - dataLogger.getStartTS());
                    txtViewTimeCounter.setText(minutes + ":" + (seconds - (minutes * 60)));
                }

            }
        });

    }


    //This is also part of the hack to get a static context
    private static Context context;

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public void provideDataToPlotter(final SensorType sensorID, final String csv){

        if(currentSensorToPlot == sensorID){

            String[] data = csv.split(";");

            // get rid the oldest sample in history:
            if (xBuffer.size() > HISTORY_SIZE) {
                xBuffer.removeFirst();
                yBuffer.removeFirst();
                zBuffer.removeFirst();
            }

            if(data.length > 0){
                xBuffer.addLast(null, Float.parseFloat(data[0]));
            }

            if(data.length > 1){
                yBuffer.addLast(null, Float.parseFloat(data[1]));
            }

            if(data.length > 2){
                zBuffer.addLast(null, Float.parseFloat(data[2]));
            }
        }
    }

    public void cleanPlotterData(){
        xBuffer.clear();
        yBuffer.clear();
        zBuffer.clear();
    }
}

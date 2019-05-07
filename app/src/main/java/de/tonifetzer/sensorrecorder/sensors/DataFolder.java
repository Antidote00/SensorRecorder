package de.tonifetzer.sensorrecorder.sensors;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * SDK save file class. Is able to open a folder on the device independent of the given
 * device and android skd version.
 */
public class DataFolder {

    private Context context;
    private File folder;

    public DataFolder(Context context, String folderName){
        this.context = context;

        // 1) try external data folder
        folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), folderName);
        if (isOK(folder)) {return;}

        // 2) try sd-card folder
        folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
        if (isOK(folder)) {return;}

        // 3) try internal data folder
        folder = new File(context.getApplicationInfo().dataDir);
        if (isOK(folder)) {return;}

        // all failed
        throw new MyException("failed to create/access storage folder");

    }

    /** ensure the given folder is OK */
    private static final boolean isOK(final File folder) {
        folder.mkdirs();
        final boolean ok =  folder.exists() && folder.isDirectory();
        if (ok) {
            Log.d("dataFolder", "using: " + folder);
        } else {
            Log.d("dataFolder", "not OK: " + folder);
        }
        return ok;
    }

    public File getFolder(){
        return folder;
    }
}

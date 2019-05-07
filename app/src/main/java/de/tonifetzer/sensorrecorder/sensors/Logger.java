package de.tonifetzer.sensorrecorder.sensors;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * log sensor data to file
 */
public final class Logger {

	private static final int FLUSH_LIMIT = 2*1024*1024;

	private StringBuilder sb = new StringBuilder();
	private File file;
	private FileOutputStream fos;
	private Context context;

	private int entries = 0;
	private int sizeCurrent = 0;
	private int sizeTotal = 0;

	/** timestamp of logging start. all entries are relative to this one */
	private long startTS = 0;

	public Logger(Context context) {
		this.context = context;
	}

	/** start logging (into RAM) */
	public final void start() {

		// start empty
		sb.setLength(0);
		entries = 0;
		sizeTotal = 0;
		sizeCurrent = 0;

		// starting timestamp
		startTS = System.currentTimeMillis();

		// open the output-file immeditaly (to get permission errors)
		// but do NOT yet write anything to the file
		final DataFolder folder = new DataFolder(context, "sensorOutFiles");
		file = new File(folder.getFolder(), startTS + ".csv");

		try {
			fos = new FileOutputStream(file);
			Log.d("logger", "will write to: " + file.toString());
		} catch (final Exception e) {
			throw new MyException("error while opening log-file", e);
		}

	}

	/** stop logging and flush RAM-data to the flash-chip */
	public final void stop() {
		synchronized (this) {
			flush(true);
			close();
		}
	}

	public File getFile() {
		return file;
	}

	public int getCurrentSize() {return sizeCurrent;}
	public int getTotalSize() {return sizeTotal;}

	public int getNumEntries() {return entries;}

	/** add a new CSV entry for the given sensor number to the internal buffer */
	public final void addCSV(final SensorType sensorNr, final String csv) {
		synchronized (this) {
			final long relTS = System.currentTimeMillis() - startTS;
			sb.append(relTS);	// relative timestamp (uses less space)
			sb.append(';');
			sb.append(sensorNr.id());
			sb.append(';');
			sb.append(csv);
			sb.append('\n');
			++entries;
			sizeTotal += csv.length() + 10; // approx!
			sizeCurrent = sb.length();
			if (sb.length() > FLUSH_LIMIT) {flush(false);}
		}

		debug();
	}



	/** helper method for exception-less writing. DO NOT CALL DIRECTLY! */
	private final void _write(final byte[] data) {
		try {
			fos.write(data);
			Log.d("logger", "flushed " + data.length + " bytes to disk");
		} catch (final Exception e) {
			throw new RuntimeException("error while writing log-file", e);
		}
	}

	/** helper-class for background writing */
	class FlushAsync extends AsyncTask<byte[], Integer, Integer> {
		@Override
		protected final Integer doInBackground(byte[][] data) {
			_write(data[0]);
			return null;
		}
	};

	/** flush current buffer-contents to disk */
	private final void flush(boolean sync) {

		// fetch current buffer contents to write and hereafter empty the buffer
		// this action MUST be atomic, just like the add-method
		byte[] data = null;
		synchronized (this) {
			data = sb.toString().getBytes();		// fetch data to write
			sb.setLength(0);						// reset the buffer
			sizeCurrent = 0;
		}

		// write
		if (sync) {
			// write to disk using the current thread
			_write(data);
		} else {
			// write to disk using a background-thread
			new FlushAsync().execute(new byte[][] {data});
		}


	}

	private final void close() {
		try {
			fos.close();
		} catch (final Exception e) {
			throw new MyException("error while writing log-file", e);
		}
	}

	public final long getStartTS() {
		return startTS;
	}

	int cnt = 0;
	private final void debug() {
		if (++cnt % 1000 == 0) {
			Log.d("buffer", "size: " + sizeCurrent);
		}
	}

}

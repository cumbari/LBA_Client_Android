package com.shephertz.cumbari.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TCLogger {
	private static final String storagePathForLogFiles = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/com/cumbari/activity/logfiles";
		
	/**
	 * Write to printing log file.
	 *
	 * @param context
	 *            the context
	 * @param source
	 *            location of the log in code
	 * @param message
	 *            message
	 *            
	 */
	public static void writeToCumbariLogFile(Context context, String source, String message) {

		WriteToPrintFileAsyncTask writeToPrintFileAsyncTask = new WriteToPrintFileAsyncTask(context, source, message);
		writeToPrintFileAsyncTask.execute();
	}
		
	private static class WriteToPrintFileAsyncTask extends AsyncTask<Void, Void, Void> {
		private  Context mContext = null;
		private  String mSource = null;
		private  String mMessage = null;
		private String logfile="ScrollLogFile";

		WriteToPrintFileAsyncTask(Context context, String source, String message) {
			mContext = context;
			mSource = source;
			mMessage = message;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try
			{
				String content = "\r\n------------------------------------------------------------------------\r\n";
				content += "LOCATION" + tabPlacer(":", 1) + tabPlacer(mSource, 1);
				content += "\r\n";
				content += "MESSAGE" + tabPlacer(":", 2) + tabPlacer(mMessage, 1);
				content += "\r\n";
				content += "\r\n";						
				
				Date myDate = new Date();
				FileOutputStream fos = mContext.openFileOutput(logfile+ new SimpleDateFormat("MM-dd-yyyy").format(myDate)+ ".txt",Context.MODE_APPEND);
				fos.write(content.getBytes());
				fos.close();
			} catch (Exception e) {

			}
			copyFileToExternalStorage();
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
		
		private String tabPlacer(String value, int tabcount) 
		{
			String content = "";
			for (int i = 0; i < tabcount; i++) 
			{
				content += "\t";
			}
			content += value;
			return content;

		}
		
		private String longToStringName(long capturedDateLong) {

			String sqlDateString = millisecondsToServerTimeStringMMM(capturedDateLong);
			String temp = sqlDateString.substring(0, sqlDateString.length() - 11);

			return temp;

		}
		
		/**
		 * Milliseconds to server time string mmm.
		 * 
		 * @param time
		 *            the time
		 * @return the string
		 */
		private String millisecondsToServerTimeStringMMM(long time) {
			// YYYY-MM-DD HH:MM:SS.SSS
			// Create a DateFormatter object for displaying date information.
			//
			SimpleDateFormat formatter = new SimpleDateFormat(
					"dd/MMM/yyyy hh:mm:ss", Locale.getDefault());

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			int AM_PM = calendar.get(Calendar.AM_PM);

			String str = formatter.format(calendar.getTime());
			String YYYY = str.substring(7, 11);
			String MM = str.substring(3, 6);
			String DD = str.substring(0, 2);

			String hrInt = "";
			hrInt = str.substring(12, 14);

			String AM_PM_String = "AM";

			if (AM_PM == 1) {
				AM_PM_String = "PM";

			}

			String HH = hrInt;
			String Min = str.substring(15, 17);
			String SS = str.substring(18);

			String sqlDateString = MM + " " + DD + " " + YYYY + " " + HH + ":"
					+ Min + ":" + SS + " " + AM_PM_String;

			return sqlDateString;
		}
		
		private void copyFileToExternalStorage() {
			try {
				File sd = new File(storagePathForLogFiles);
				if (!sd.exists()) {
					sd.mkdirs();
				}
				File data = Environment.getDataDirectory();
				Date myDate = new Date();
				String DB_PATH = "data/com.shephertz.cumbari/files/"
						+ logfile
						+ new SimpleDateFormat("MM-dd-yyyy").format(myDate)
						+ ".txt";

				String currentDBPath = DB_PATH;
				String backupDBPath = logfile
						+ longToStringName(System.currentTimeMillis())
						+ ".txt";
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);
				if (!backupDB.exists()) {
					backupDB.createNewFile();
				}

				FileUtils.copyFile(currentDB, backupDB);
			} catch (IOException e) {
				Log.w("Settings Backup", e);
			}
		}
	}
}

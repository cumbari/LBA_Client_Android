package com.shephertz.cumbari.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceUtil {

	private SharedPreferences sharedPreferences;
	private Editor editor;
	private static SharedPreferenceUtil cumbariSharedPreferenceUtil;

	
	private static void initializeCumabriSharedPref(Context context) {
		if (context != null) {
			cumbariSharedPreferenceUtil = new SharedPreferenceUtil();
			cumbariSharedPreferenceUtil.sharedPreferences = context
					.getSharedPreferences(SharedPrefKeys.PREF_NAME, Activity.MODE_PRIVATE
							| Context.MODE_MULTI_PROCESS);
			cumbariSharedPreferenceUtil.editor = cumbariSharedPreferenceUtil.sharedPreferences.edit();
		}
	}

	public static SharedPreferenceUtil getInstance(Context context) {
		if (cumbariSharedPreferenceUtil == null) {
			initializeCumabriSharedPref(context);
		}
		return cumbariSharedPreferenceUtil;
	}

	public SharedPreferenceUtil() {
	}

	public synchronized boolean saveData(String key, String value) {
		editor.putString(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, boolean value) {
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, long value) {
		editor.putLong(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, float value) {
		editor.putFloat(key, value);
		return editor.commit();
	}

	public synchronized boolean saveData(String key, int value) {
		editor.putInt(key, value);
		return editor.commit();
	}

	public synchronized boolean removeData(String key) {
		editor.remove(key);
		return editor.commit();
	}

	public synchronized Boolean getData(String key, boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public synchronized String getData(String key, String defaultValue) {
		return sharedPreferences.getString(key, defaultValue);
	}

	public synchronized float getData(String key, float defaultValue) {

		return sharedPreferences.getFloat(key, defaultValue);
	}

	public synchronized int getData(String key, int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	public synchronized long getData(String key, long defaultValue) {
		return sharedPreferences.getLong(key, defaultValue);
	}

	public synchronized void deleteAllData() {
		cumbariSharedPreferenceUtil = null;
		editor.clear();
		editor.commit();
	}


}

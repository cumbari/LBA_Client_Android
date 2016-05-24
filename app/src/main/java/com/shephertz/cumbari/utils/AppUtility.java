package com.shephertz.cumbari.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.shephertz.cumbari.R;

public class AppUtility {

	private int ButtonMainMenuBackColor;
	private int ButtonMainMenuForeColor;
	private int ButtonMainMenuGradientColor;

	private int ButtonMainMenuSelectedGradientColor;
	private int ButtonMainMenuSelectedBackColor;
	private int ButtonMainMenuSelectedForeColor;

	private Context mActivity;

	public AppUtility(Context activity){
		this.mActivity = activity;
		ButtonMainMenuBackColor = Color.rgb(220,20,60);
		ButtonMainMenuForeColor = Color.rgb(176,23,31);
		ButtonMainMenuGradientColor = Color.rgb(229, 0, 42);

		ButtonMainMenuSelectedBackColor = Color.rgb(231, 228, 226);
		ButtonMainMenuSelectedGradientColor = Color.rgb(175, 166, 161);
		ButtonMainMenuSelectedForeColor = Color.WHITE;
	}


	/**
	 * Splitting utils.
	 * 
	 * @param test_data
	 *            the test_data
	 * @return the array list
	 */
	public ArrayList<String> SplittingUtils(String test_data) {

		ArrayList<String> list = new ArrayList<String>();
		for (String tag : test_data.split("[\\s,-]"))/* [\\s,;] */// using in
		{
			list.add(tag);
		}
		return list;

	}

	public void setUpMainMenuButton(Button btn) {
		selector(btn, new DrawableGradient(new int[] {
					ButtonMainMenuGradientColor, ButtonMainMenuBackColor }, 5,
					"#333333", 2), new DrawableGradient(
							new int[] { ButtonMainMenuBackColor,
									ButtonMainMenuGradientColor }, 5, "#333333", 2),
									ButtonMainMenuForeColor);
	}

	@SuppressWarnings("deprecation")
	public void selector(Button btn, DrawableGradient drawableGradient,
			DrawableGradient drawableGradient2, int textColor) {

		StateListDrawable states = new StateListDrawable();
		states.addState(new int[] { android.R.attr.state_pressed },
				drawableGradient);
		states.addState(new int[] {}, drawableGradient2);

		btn.setTextColor(textColor);
		btn.setBackgroundDrawable(states);
	}

	public void setUpBackGroundColor(View backGround) {
		selectorBackGround(backGround,
					new DrawableGradient(new int[] {
							ButtonMainMenuBackColor, ButtonMainMenuGradientColor }, 0, "#333333", 0));

	}

	public void setUpMainMenuSelectedButton(Button btn) {

			selector(btn, new DrawableGradient(new int[] {
					ButtonMainMenuSelectedGradientColor,
					ButtonMainMenuSelectedBackColor }, 5, "#333333", 2),
					new DrawableGradient(new int[] {
							ButtonMainMenuSelectedBackColor,
							ButtonMainMenuSelectedGradientColor }, 5,
							"#333333", 2), ButtonMainMenuSelectedForeColor);

	}

	@SuppressWarnings("deprecation")
	public void selectorBackGround(View background,
			DrawableGradient drawableGradient) {
		background.setBackgroundDrawable(drawableGradient);
	}

	/**
	 * Milliseconds to server time.
	 * 
	 * @param time
	 *            the time
	 * @return the string
	 */
	public String millisecondsToServerTime(long time) {

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss",
				Locale.getDefault());

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		int AM_PM = calendar.get(Calendar.AM_PM);

		String str = formatter.format(calendar.getTime());
		String YYYY = str.substring(6, 10);
		String MM = str.substring(3, 5);
		String DD = str.substring(0, 2);

		String hrInt = "";
		hrInt = str.substring(11, 13);

		String AM_PM_String = "AM";

		if (AM_PM == 1) {
			AM_PM_String = "PM";

		}

		String HH = hrInt;
		String Min = str.substring(14, 16);		
		String Sec = str.substring(17, 19);

		String sqlDateString = MM + "" + DD + "" + YYYY + "_" + HH + "" + Min+Sec
				+ " " + AM_PM_String;

		return sqlDateString;
	}

	/**
	 * Gets the day of week.
	 * 
	 * @return the day of week
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("deprecation")
	public String getDayOfWeek() {
		String dayName = null;
		Date dt = new Date();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			dayName = DateUtils.getDayOfWeekString(dt.getDay() + 1,
					DateUtils.LENGTH_LONG);
		} else {
			Calendar cal = dateToCalendar(dt);
			cal.setTime(dt);
			dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT,
					Locale.getDefault());
		}
		return dayName;
	}

	/**
	 * Date to calendar.
	 * 
	 * @param d
	 *            the d
	 * @return the calendar
	 */
	public Calendar dateToCalendar(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		return calendar;
	}

	/**
	 * Hex2 byte.
	 * 
	 * @param str
	 *            the str
	 * @return the byte[]
	 */
	public byte[] hex2Byte(String str) {
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2),
					16);
		}
		return bytes;
	}


	public boolean isNetworkAvailable() {
		   ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		   NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		   for (NetworkInfo ni : netInfo) {
		       if (ni.getTypeName().equalsIgnoreCase("WIFI")){
		           if (ni.isConnected()){
		       		return true;
		           }
		       }
		       if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
		           if (ni.isConnected()){
		       		return true;
		           }
		       }
		   }
		   return false;
		}

	public void hideKeyBoard(Activity c)
	{
		try {
			InputMethodManager inputManager = (InputMethodManager)c.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(c.getCurrentFocus().getWindowToken(), 0);
			c.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		} catch (Exception e) {
		}
	}


	public String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public String getTimerCountDownValue(Context c, int counterMin, int counterSec){
		return c.getResources().getString(R.string.use_deal_within)+" "+pad(counterMin)+":"+pad(counterSec)+" "+c.getResources().getString(R.string.min);
	}

	public void setLocale() {
		Resources res = mActivity.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		if (SharedPreferenceUtil.getInstance(mActivity).getData(SharedPrefKeys.LANGUAGE, "ENG").equals("SWE")) {
			conf.locale = new Locale("sv","SE");
		}else if (SharedPreferenceUtil.getInstance(mActivity).getData(SharedPrefKeys.LANGUAGE, "ENG").equals("GER")) {
			conf.locale = Locale.GERMAN;
		}else{
			conf.locale = Locale.ENGLISH;
		}
		res.updateConfiguration(conf, dm);
	}

}

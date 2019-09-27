package com.dec.dstar.utils;

import android.util.Log;

public class DLog {

	public static boolean VERBOSE = true;
	public static boolean DEBUG = true;
	public static boolean INFO = true;
	public static boolean WARN = true;
	public static boolean ERR = true;

	/**
	 * DEBUG
	 * 
	 * @param desc
	 */
	public static void d(String t, String desc) {
		if (true == DEBUG) {
			Log.d(t, desc);
		}
	}

	/**
	 * INFO
	 * 
	 * @param desc
	 */
	public static void i(String t, String desc) {
		if (true == INFO) {
			Log.i(t, desc);
		}
	}

	/**
	 * VERBOSE
	 * 
	 * @param desc
	 */
	public static void v(String t, String desc) {
		if (true == VERBOSE) {
			Log.v(t, desc);
		}
	}

	/**
	 * WARN
	 * 
	 * @param desc
	 */
	public static void w(String t, String desc) {
		if (true == WARN) {
			Log.w(t, desc);
		}
	}

	/**
	 * ERROR
	 * 
	 * @param desc
	 */
	public static void e(String t, String desc) {
		if (true == ERR) {
			Log.e(t, desc);
		}
	}
}

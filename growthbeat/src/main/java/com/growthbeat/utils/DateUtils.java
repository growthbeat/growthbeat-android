package com.growthbeat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

	private static final String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ssZZ";
	private static final String FORMAT_DATE = "yyyy-MM-dd";

	public static String format(Date date, String format) {

		if (date == null)
			return null;

		return new SimpleDateFormat(format, Locale.getDefault()).format(date);

	}

	public static String formatToDateTimeString(Date date) {
		return format(date, FORMAT_DATETIME);
	}

	public static String formatToDateString(Date date) {
		return format(date, FORMAT_DATE);
	}

	public static Date parse(String string, String format) {

		if (string == null)
			return null;

		try {
			return new SimpleDateFormat(format).parse(string);
		} catch (ParseException e) {
			return null;
		}

	}

	public static Date parseFromDateTimeString(String string) {
		return parse(string, FORMAT_DATETIME);
	}

	public static Date parseFromDateTimeStringWithFormat(String string, String format) {
		return parse(string, format);
	}

	public static Date parseFromDateString(String string) {
		return parse(string, FORMAT_DATE);
	}

}

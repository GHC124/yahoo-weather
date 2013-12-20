package weather.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Content functions using to convert data
 * 
 * @author ChungPV1
 * 
 */
public class ConversionsUtil {
	public static final SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat(
			"dd MMM yyyy");

	public static final SimpleDateFormat RFC822_DATE_FORMATS[] = new SimpleDateFormat[] { new SimpleDateFormat(
			"EEE, dd MMM yyyy hh:mm aa", Locale.US) };

	public static String convertDdMmmYyyyToString(Date date) {
		return DD_MMM_YYYY.format(date);
	}

	public static Date convertStringToDdMmmYyyy(String dateString) {
		Date date = null;
		try {
			date = DD_MMM_YYYY.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Parser RFC822 Date to String
	 * 
	 * @param date
	 *            - {@link Date}
	 * @return String
	 */
	public static String convertRfc822DateToString(Date date) {
		String dateString = null;
		for (SimpleDateFormat sdf : RFC822_DATE_FORMATS) {
			dateString = sdf.format(date);
			if (dateString != null) {
				break;
			}
		}
		return dateString;
	}

	/**
	 * Parse an RFC 822 date string.
	 * 
	 * @param dateString
	 *            The date string to parse
	 * @return The date, or null if it could not be parsed.
	 */
	public static Date convertStringToRfc822Date(String dateString) {
		// Try to convert 12 hour to 24 hour

		Date date = null;
		for (SimpleDateFormat sdf : RFC822_DATE_FORMATS) {
			try {
				date = sdf.parse(dateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (date != null) {
				break;
			}
		}
		return date;
	}

	/**
	 * Convert {@link InputStream} to String
	 * 
	 * @param is
	 *            - {@link InputStream}
	 * @return String
	 * @throws IOException
	 */
	public static String convertStreamToString(InputStream is)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private static final String TIME12HOURS_PATTERN = "(1[012]|[1-9]):[0-5][0-9](\\s)?(?i)(am|pm)";
	public static String convert12HourTo24(String date) {
		String newString = "";

		Pattern pattern = Pattern.compile(TIME12HOURS_PATTERN);
		Matcher matcher = pattern.matcher(date);
		if(matcher.matches()){
			
		}

		return newString;
	}
}

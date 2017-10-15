package org.qcri.youtubescraping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	/**
	 * Remove extra white spaces from input string
	 * 
	 * @param strText
	 *            input string containing extra white spaces at the beginning or
	 *            at the end or repeated empty spaces in the middle of the
	 *            string
	 * @return input string after removing extra whitespaces
	 */
	static public String removeExtraWhiteSpaces(String strText) {
		String strNewString = strText;
		strNewString = strNewString.replaceAll(" +", " ");
		strNewString = strNewString.trim();
		return strNewString;
	}

	/**
	 * Remove new line charachters from input string not to cause problems while
	 * saving to output file
	 * 
	 * @param strText
	 *            input string before processing
	 * @return input string after processing, and replacing all new line
	 *         charachters / tabs with spaces
	 */
	static public String removeNewLineCharachters(String strText) {
		String strNewString = strText;
		strNewString = strNewString.replace("\n", " ");
		strNewString = strNewString.replace("\r", " ");
		strNewString = strNewString.replace("\t", " ");
		strNewString = removeExtraWhiteSpaces(strNewString);

		return strNewString;
	}

	/**
	 * Check if input URL is youtube
	 * 
	 * @param strURL
	 *            Input URL
	 * @return true, if the input URL is youtube
	 */
	static public Boolean isYoutubeURL(String strURL) {
		Boolean blnIsYoutubeURL = false;

		if (strURL.contains("youtu.be") || strURL.contains("youtube.com")) {
			blnIsYoutubeURL = true;
		}

		return blnIsYoutubeURL;
	}

	/**
	 * Extract Video ID from Youtube URL
	 * 
	 * @param strYoutubeURL
	 *            Youtube URL
	 * @return the video ID
	 */
	static public String extractVideoIDFromYoutubeURL(String strYoutubeURL) {
		String strVideoID = null;
		if (strYoutubeURL != null && strYoutubeURL.trim().length() > 0) {
			String strMatchExpression = "^.*((youtu.be\\/)|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
			CharSequence objCharSequence = strYoutubeURL;
			Pattern objPattern = Pattern.compile(strMatchExpression,
					Pattern.CASE_INSENSITIVE);
			Matcher objMatcher = objPattern.matcher(objCharSequence);
			if (objMatcher.matches()) {
				String strVideoIDMatcher = objMatcher.group(7);
				if (strVideoIDMatcher != null
						&& strVideoIDMatcher.length() == 11)
					strVideoID = strVideoIDMatcher;
			}
		}
		return strVideoID;
	}

	/**
	 * Extract Video ID from Youtube URL in a naive way
	 * 
	 * @param strYoutubeURL
	 *            Youtube URL
	 * @return the video ID
	 */
	static public String extractVideoIDFromYoutubeURLNaive(String strYoutubeURL) {
		String strVideoID = null;
		String strVideoTag = "v=";
		Integer intIndexOfVideoTag = strYoutubeURL.indexOf(strVideoTag) + 2;

		if (strYoutubeURL.contains(strVideoTag)) {
			strVideoID = strYoutubeURL.substring(intIndexOfVideoTag,
					intIndexOfVideoTag + 11);
		}

		return strVideoID;
	}
}
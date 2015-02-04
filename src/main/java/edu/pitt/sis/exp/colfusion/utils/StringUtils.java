package edu.pitt.sis.exp.colfusion.utils;

import java.util.regex.Pattern;

/**
 * Utility functions to work with strings.
 * @author Evgeny
 *
 */
public final class StringUtils {
	
	public static final String SPACE_REPLACEMENT_CHARACTER = "_";
	public static final int UNIQUE_SHORT_MAX_LENGTH_DEFAULT = 64;
	public static final String NEWLINE = System.lineSeparator();
	
	/**
	 * This class cannot be instantiated
	 */
	private StringUtils() {	}
	
	/**
	 * Check whether given string value is specified.
	 * @param 	value 
	 * 				the {@link String} value to check
	 * @return
	 * 			true if given value is not null and not empty
	 * 			false if given value is either null or empty
	 */
	public static boolean isSpecified(final String value) {
		return !isNullOrEmpty(value);
	}

	/**
	 * Check whether given string value is null or empty
	 * @param 	value
	 * 				the {@link String} value to check
	 * @return	
	 * 			true if given value is null or empty
	 * 			false if given value is not null and not empty
	 */
	public static boolean isNullOrEmpty(final String value) {
		return value == null || value.isEmpty();
	}

	/**
	 * Replaces all occurrences of ' ' (space) character with {@value #SPACE_REPLACEMENT_CHARACTER}
	 * 
	 * Two or more consecutive spaces are replaces with only one {@value #SPACE_REPLACEMENT_CHARACTER}
	 * 
	 * If null value is provided as input, then null will be returned.
	 * 
	 * @param 	value
	 * 				the {@link String} in which to do the replacements
	 * @return
	 * 			{@link String} with all spaces replaced with {@value #SPACE_REPLACEMENT_CHARACTER}
	 * 			null if input was null
	 */
	public static String replaceSpaces(final String value) {
		return value == null ? null : value.replaceAll(" +", SPACE_REPLACEMENT_CHARACTER);
	}

	/**
	 * Creates a short version of the provided string up to the length of {@value #UNIQUE_SHORT_LAX_LENGTH_DEFAULT},
	 * by taking substring from the beginning up to the <code>{@value #UNIQUE_SHORT_LAX_LENGTH_DEFAULT} - length</code> of hash code of the string
	 * and concatenates it with the hash code value.
	 * 
	 * @param 	value
	 * 				the {@link String} for which to create a short version.
	 * @return
	 * 			the short version of the given string.
	 */
	public static String makeShortUnique(final String value) {
		return makeShortUnique(value, UNIQUE_SHORT_MAX_LENGTH_DEFAULT);		
	}
	
	public static String makeShortUnique(final String value, final int maxLength) {
		if (StringUtils.isNullOrEmpty(value)) {
			return "";
		}
		
		String hashValue = String.valueOf(value.hashCode());
		
		int endIndex = (maxLength - hashValue.length() < 0) ? 0 : maxLength - hashValue.length();
		endIndex = value.length() > endIndex ? endIndex : value.length();
		
		String result = value.substring(0, endIndex) + hashValue;
		
		return result;
	}

	/**
	 * Remove given prefix from given string value. If the prefix not found, then the value returned unchanged.
	 * 
	 * @param value
	 * 		string from which to remove the prefix.
	 * @param prefix
	 * 		prefix to remove
	 * @return the value with removed prefix
	 */
	public static String removePrefix(final String value, final String prefix) {
		
		return value.replaceFirst("^" + Pattern.quote(prefix), "");
	}
}

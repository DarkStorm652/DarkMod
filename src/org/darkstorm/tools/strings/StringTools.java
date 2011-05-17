package org.darkstorm.tools.strings;

import java.util.Vector;

public class StringTools {
	private StringTools() {
	}

	public static boolean isInteger(String testString) {
		try {
			Integer.parseInt(testString);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public static boolean isDouble(String testString) {
		try {
			Double.parseDouble(testString);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public static boolean isFloat(String testString) {
		try {
			Float.parseFloat(testString);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public static boolean isLong(String testString) {
		try {
			Long.parseLong(testString);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public static String[] split(String str, String match) {
		String replacer = String.copyValueOf(str.toCharArray());
		Vector<String> strings = new Vector<String>();
		while(replacer.contains(match)) {
			int i = replacer.indexOf(match);
			strings.add(replacer.substring(0, i));
			replacer = replacer.substring(i + match.length());
		}
		strings.add(replacer);
		return strings.toArray(new String[] {});
	}

	public static String[] splitFirst(String str, String match) {
		String first = split(str, match)[0];
		String last = str.substring(first.length() + match.length());
		return new String[] { first, last };
	}

	public static String[] splitLast(String str, String match) {
		String[] split = split(str, match);
		String last = split[split.length - 1];
		String first = str.replace(match + last, "");
		return new String[] { first, last };
	}

	public static String[] toStringCharacterArray(String s) {
		String[] out = new String[s.length()];
		for(int i = 0; i < s.length(); i++) {
			out[i] = String.valueOf(s.charAt(i));
		}
		return out;
	}

	public static String[] combineArrays(String[][] s) {
		String[] combined = new String[s.length];
		for(int i = 0; i < s.length; i++) {
			combined[i] = concatenate(s[i]);
		}
		return combined;
	}

	public static String concatenate(String[] s) {
		String connected = "";
		for(int i = 0; i < s.length; i++) {
			connected += s[i];
		}
		return connected;
	}

	public static String[] reverse(String[] s) {
		String[] reverse = new String[s.length];
		int a = 0;
		for(int b = s.length - 1; b > -1; b--) {
			reverse[a] = s[b];
			a++;
		}
		return reverse;
	}

	public static String reverse(String s) {
		String[] stringChars = toStringCharacterArray(s);
		stringChars = reverse(stringChars);
		return concatenate(stringChars);
	}

	public static boolean endsWith(String source, String... suffixes) {
		for(String suffix : suffixes) {
			if(source.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	public static String replaceLast(String string, String match,
			String replacement) {
		String reversed = reverse(string);
		String reversedMatch = reverse(match);
		String reversedReplacement = reverse(replacement);
		return reverse(replaceFirst(reversed, reversedMatch,
				reversedReplacement));
	}

	public static String replaceFirst(String string, String match,
			String replacement) {
		int index = string.indexOf(match);
		if(index == -1)
			return string;
		return string.substring(0, index) + replacement
				+ string.substring(index + match.length());
	}
}

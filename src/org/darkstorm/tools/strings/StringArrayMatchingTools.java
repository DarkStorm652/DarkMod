package org.darkstorm.tools.strings;

public class StringArrayMatchingTools {
	private StringArrayMatchingTools() {
	}

	public static boolean hasMatch(String[] regex, String... match) {
		return matchAll(regex, match) != null;
	}

	public static boolean hasMatchIgnoreCase(String[] regex, String... match) {
		return matchAllIgnoreCase(regex, match) != null;
	}

	public static String matchFirst(String[] regex, String... match) {
		return matchAll(regex, match)[0];
	}

	public static String matchFirstIgnoreCase(String[] regex, String... match) {
		return matchAllIgnoreCase(regex, match)[0];
	}

	public static String matchLast(String[] regex, String... match) {
		String[] temp = matchAll(regex, match);
		return temp[temp.length];
	}

	public static String matchLastIgnoreCase(String[] regex, String... match) {
		String[] temp = matchAllIgnoreCase(regex, match);
		return temp[temp.length];
	}

	public static String[] matchAll(String[] regex, String... match) {
		String[] matched = new String[] {};
		for(int a = 0; a < regex.length; a++) {
			for(int b = 0; b < match.length; b++) {
				if(regex[a].equals(match[b])) {
					if(matched.length == 0) {
						matched[0] = regex[a];
					} else {
						matched[matched.length] = regex[a];
					}
				}
			}
		}
		return matched;
	}

	public static String[] matchAllIgnoreCase(String[] regex, String... match) {
		String[] matched = new String[] {};
		for(int a = 0; a < regex.length; a++) {
			for(int b = 0; b < match.length; b++) {
				if(regex[a].toLowerCase().equals(match[b].toLowerCase())) {
					if(matched.length == 0) {
						matched[0] = regex[a];
					} else {
						matched[matched.length] = regex[a];
					}
				}
			}
		}
		return matched;
	}
}

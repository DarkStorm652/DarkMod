package org.darkstorm.tools.strings;

public class StringArrayReplacingTools {
	private StringArrayReplacingTools() {
	}

	public static String replace(String in, String regex, String[] replacements) {
		if(in.contains(regex)) {
			for(int i = 0; i < replacements.length; i++) {
				if(in.contains(regex)) {
					in.replace(regex, replacements[i]);
				} else {
					break;
				}
			}
		}
		return in;
	}

	public static String replace(String in, String[] regexes,
			String[] replacements) {
		boolean done = false;
		while(!done) {
			for(int a = 0; a < replacements.length; a++) {
				for(int b = 0; b < regexes.length; b++) {
					if(in.contains(regexes[b])) {
						in.replace(regexes[b], replacements[a]);
						break;
					}
				}
			}
			done = true;
			for(int i = 0; i < regexes.length; i++) {
				if(in.contains(regexes[i])) {
					done = false;
					break;
				}
			}
		}
		return in;
	}

	public static String[][] replaceAll(String[][] in, String[] regexes,
			String[] replacements) {
		for(int i = 0; i < in[0].length; i++) {
			for(int a = 0; a < replacements.length; a++) {
				for(int b = 0; b < regexes.length; b++) {
					if(in[0][i] == null) {
						return null;
					}
					if(regexes[b] == null) {
						return null;
					}
					if(replacements[a] == null) {
						return null;
					}
					if(in[0][i].contains(regexes[b])) {
						in[1][in[1].length - 1] = in[0][i].replaceAll(
								regexes[b], replacements[b]);
					}
				}
			}
		}
		return in;
	}

	public static String replaceAll(String in, String[] regexes,
			String[] replacements) {
		String out = new String(in.toCharArray());
		for(int i = 0; i < regexes.length; i++) {
			out = out.replace(regexes[i], replacements[i]);
		}
		return out;
	}
}

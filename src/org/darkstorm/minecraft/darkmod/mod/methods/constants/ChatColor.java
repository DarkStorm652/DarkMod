package org.darkstorm.minecraft.darkmod.mod.methods.constants;

public enum ChatColor {
	BLACK('0'),
	DARK_BLUE('1'),
	DARK_GREEN('2'),
	DARK_AQUA('3'),
	DARK_RED('4'),
	PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	INDIGO('9'),
	LIME('a'),
	AQUA('b'),
	RED('c'),
	PINK('d'),
	YELLOW('e'),
	WHITE('f');

	private char colorCode;

	private ChatColor(char colorCode) {
		this.colorCode = colorCode;
	}

	public char getColorCode() {
		return colorCode;
	}

	@Override
	public String toString() {
		return "§" + colorCode;
	}

	public static String removeColors(final String input) {
		if(input != null)
			return input.replaceAll("(?i)§[0-F]", "");
		return null;
	}

	public static ChatColor valueOf(char colorCode) {
		for(ChatColor color : values())
			if(colorCode == color.getColorCode())
				return color;
		return null;
	}
}

package org.darkstorm.minecraft.darkmod.mod.methods.constants;

public enum ChatColor {
	/**
	 * Represents black
	 */
	BLACK('0'),
	/**
	 * Represents dark blue
	 */
	DARK_BLUE('1'),
	/**
	 * Represents dark green
	 */
	DARK_GREEN('2'),
	/**
	 * Represents dark blue (aqua)
	 */
	DARK_AQUA('3'),
	RED('4'),
	PURPLE('5'),
	GOLD('6'),
	GRAY('7'),
	DARK_GRAY('8'),
	CYAN('9'),
	LIME('a'),
	AQUA('b'),
	PINK('c'),
	TURQUOISE('d'),
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

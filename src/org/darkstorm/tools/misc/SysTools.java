package org.darkstorm.tools.misc;

public class SysTools {
	private SysTools() {
	}

	public static void exit(String message, int status) {
		if(status == 0)
			System.out.println(message);
		else
			System.err.println(message);
		System.exit(status);
	}
}

package org.darkstorm.tools.loopsystem;

public interface Loopable {
	public static final int STOP = -1;
	public static final int YIELD = -2;

	public int loop() throws InterruptedException;
}

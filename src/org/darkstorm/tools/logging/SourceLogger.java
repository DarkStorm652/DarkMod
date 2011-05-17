package org.darkstorm.tools.logging;

public abstract class SourceLogger extends Logger {

	public abstract void log(Object source, String message);

	public abstract void log(Object source, LogType logType, String message);

}

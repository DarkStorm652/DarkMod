package org.darkstorm.tools.logging;

import java.text.*;
import java.util.Date;

import org.darkstorm.tools.interfaces.Nameable;

public class SystemLogger extends SourceLogger {
	private DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy HH:mm:ss:SSS");
	private Date date = new Date();

	@Override
	public void log(LogType logType, String message) {
		String fullMessage = createThreadMessageStart(logType) + message;
		printMessage(logType, fullMessage);
	}

	private String createThreadMessageStart(LogType logType) {
		Thread currentThread = Thread.currentThread();
		String threadName = currentThread.getName();
		return createDateMessageStart() + logType + " [" + threadName + "] ";
	}

	@Override
	public void log(Object source, String message) {
		log(source, LogType.MESSAGE, message);
	}

	@Override
	public void log(Object source, LogType logType, String message) {
		String fullMessage = createMessageStart(source, logType) + message;
		printMessage(logType, fullMessage);
	}

	private String createMessageStart(Object source, LogType logType) {
		StringBuilder messageStart = new StringBuilder();
		messageStart.append(createDateMessageStart() + logType + " [");
		if(source instanceof Nameable)
			messageStart.append(((Nameable) source).getName());
		else
			messageStart.append(source);
		messageStart.append("] ");
		return messageStart.toString();
	}

	private String createDateMessageStart() {
		String formattedDate = dateFormat.format(date);
		return "[" + formattedDate + "] ";
	}

	private void printMessage(LogType logType, String message) {
		switch(logType.getId()) {
		case LogType.MESSAGE_ID:
		case LogType.WARNING_ID:
		case LogType.DEBUG_ID:
		case LogType.OTHER_ID:
			System.out.println(message);
			break;
		case LogType.ERROR_ID:
			System.err.println(message);
		}
	}

}

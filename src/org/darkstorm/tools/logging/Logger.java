package org.darkstorm.tools.logging;

public abstract class Logger {
	public static class LogType {
		public static final int MESSAGE_ID = 0;
		public static final int WARNING_ID = 1;
		public static final int DEBUG_ID = 4;
		public static final int ERROR_ID = 2;
		public static final int OTHER_ID = 3;

		public static final LogType MESSAGE = new LogType(MESSAGE_ID, "MESSAGE");
		public static final LogType WARNING = new LogType(WARNING_ID, "WARNING");
		public static final LogType DEBUG = new LogType(DEBUG_ID, "DEBUG");
		public static final LogType ERROR = new LogType(ERROR_ID, "ERROR");
		public static final LogType OTHER = new LogType(OTHER_ID, "OTHER");

		private int id;
		private String name;

		private LogType(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public void log(String message) {
		log(LogType.MESSAGE, message);
	}

	public abstract void log(LogType logType, String message);

}

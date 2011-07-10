package org.darkstorm.minecraft.darkmod.tools;

public class StartupStatus {
	public static final int LOGIN = 0;
	public static final int LOCATE_DIR = 1;
	public static final int LOAD_JAR = 2;
	public static final int DOWNLOAD_HOOKS = 3;
	public static final int CACHE_HOOKS = 4;
	public static final int PARSE_HOOKS = 5;
	public static final int INJECT_HOOKS = 6;
	public static final int OUTPUT_JAR = 7;
	public static final int LOAD_NEW_JAR = 8;
	public static final int START_MINECRAFT = 9;

	private int status;
	private int progress;
	private int maxProgress;

	public int getStatus() {
		return status;
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setStatus(int status) {
		this.status = status;
		progress = 0;
		maxProgress = 100;
	}

	public void setProgress(int progress) {
		if(progress <= maxProgress)
			this.progress = progress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
		if(progress > maxProgress)
			progress = maxProgress;
	}
}

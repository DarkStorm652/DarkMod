package org.darkstorm.tools.loopsystem;

import static org.darkstorm.tools.loopsystem.Loopable.*;

import java.lang.Thread.UncaughtExceptionHandler;

public class LoopController implements UncaughtExceptionHandler {
	private LoopControllerFactory factory;
	private Thread loopThread;
	private String name;
	private Loopable loopable;

	private volatile boolean usingGCOnLoop = false;

	private volatile boolean stop = false;
	private volatile boolean pause = false;
	private volatile boolean paused = false;

	private boolean daemon = false;

	public LoopController(LoopControllerFactory factory, Loopable loopable,
			String name) {
		if(factory == null)
			throw new IllegalArgumentException(
					"param 0 (type LoopControllerFactory) is null");
		if(loopable == null)
			throw new IllegalArgumentException("param 1 (type Loop) is null");
		if(name == null)
			throw new IllegalArgumentException("param 2 (type String) is null");
		this.factory = factory;
		this.loopable = loopable;
		this.name = name;
	}

	private void createThread() {
		ThreadGroup loopControllerThreadGroup = factory
				.getLoopControllerThreadGroup();
		Runnable loopThreadRunnable = createLoopThreadRunnable();
		String threadName = "LoopController [ " + name + " ]";
		loopThread = new Thread(loopControllerThreadGroup, loopThreadRunnable,
				threadName);
		setUncaughtExceptionHandler();
		loopThread.setDaemon(daemon);
	}

	private Runnable createLoopThreadRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				loopUntilStop();
			}
		};
	}

	private void loopUntilStop() {
		while(!stop) {
			loopUntilPaused();
			if(stop)
				break;
			paused = true;
			waitUntilInterrupted();
		}
		stop = false;
	}

	private void loopUntilPaused() {
		while(!pause) {
			paused = false;
			loopWithFinal();
			runGarbageCollectionIfEnabled();
			if(stop)
				break;
		}
	}

	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
		if(loopThread != null)
			loopThread.setDaemon(daemon);
	}

	public boolean isDaemon() {
		return daemon;
	}

	private void loopWithFinal() {
		boolean success = false;
		try {
			loop();
			success = true;
		} finally {
			if(!success)
				stop = false;
		}
	}

	private void loop() {
		try {
			int sleepTime = loopable.loop();
			if(sleepTime < 0)
				handleLoopReturnCode(sleepTime);
			else
				Thread.sleep(sleepTime);
		} catch(InterruptedException e) {}
	}

	private void handleLoopReturnCode(int returnCode)
			throws InterruptedException {
		if(returnCode == STOP)
			stop();
		else if(returnCode == YIELD)
			Thread.yield();
	}

	private void runGarbageCollectionIfEnabled() {
		if(usingGCOnLoop)
			System.gc();
	}

	private void waitUntilInterrupted() {
		try {
			wait();
		} catch(InterruptedException e) {}
	}

	private void setUncaughtExceptionHandler() {
		loopThread.setUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable e) {
		if(loopThread != null && thread.equals(loopThread)
				&& !(e instanceof ThreadDeath)) {
			System.err.print("Exception in thread \"" + thread.getName()
					+ "\": ");
			e.printStackTrace();
			System.err.println("The thread will now stop.");
		}
	}

	public void start() {
		if(!isAlive()) {
			stop = false;
			createThread();
			loopThread.start();
		}
	}

	public void stop() {
		stop(true);
	}

	public void stop(boolean join) {
		stop(join, true);
	}

	@SuppressWarnings("deprecation")
	public void stop(final boolean join, final boolean force) {
		if(isAlive()) {
			stop = true;
			loopThread.interrupt();
			if(join) {
				if(force) {
					join(1000);
					if(stop && isAlive())
						loopThread.stop();
				} else
					join();
				loopThread = null;
			} else {
				new Thread() {
					@Override
					public void run() {
						if(force) {
							LoopController.this.join(1000);
							if(stop && isAlive())
								loopThread.stop();
						} else
							LoopController.this.join();
						loopThread = null;
					};
				}.start();
			}
		}
	}

	public boolean join() {
		try {
			while(isAlive())
				Thread.sleep(50);
			loopThread = null;
			return true;
		} catch(InterruptedException exception) {
			return false;
		}
	}

	public boolean join(int timeout) {
		try {
			int time = 0;
			for(; time <= timeout && isAlive(); time += 50)
				Thread.sleep(50);
			return !isAlive();
		} catch(InterruptedException exception) {
			return false;
		}
	}

	public void pause() {
		if(!pause && isAlive()) {
			pause = true;
			loopThread.interrupt();
			new Thread() {
				@Override
				@SuppressWarnings("deprecation")
				public void run() {
					try {
						Thread.sleep(1000);
					} catch(InterruptedException exception) {}
					if(pause && !paused)
						loopThread.suspend();
				};
			}.start();
		}
	}

	@SuppressWarnings("deprecation")
	public void resume() {
		if(pause && isAlive()) {
			pause = false;
			loopThread.interrupt();
			loopThread.resume();
		}
	}

	public boolean isAlive() {
		if(loopThread != null)
			return loopThread.isAlive();
		return false;
	}

	public boolean isActive() {
		return !paused;
	}

	public LoopControllerFactory getFactory() {
		return factory;
	}

	public Loopable getLoopable() {
		return loopable;
	}

	public void setUsingGCOnLoop(boolean usingGCOnLoop) {
		this.usingGCOnLoop = usingGCOnLoop;
	}

	public boolean isUsingGCOnLoop() {
		return usingGCOnLoop;
	}

}

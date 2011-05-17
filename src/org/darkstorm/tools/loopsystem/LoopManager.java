package org.darkstorm.tools.loopsystem;

import java.util.Vector;

public class LoopManager {
	private ThreadGroup threadGroup;
	private Vector<LoopController> loopControllers;
	private LoopControllerFactory loopControllerFactory;
	private boolean started = false;
	private boolean paused = false;

	public LoopManager(ThreadGroup threadGroup) {
		this(threadGroup, true);
	}

	public LoopManager(ThreadGroup threadGroup, boolean autoStart) {
		if(threadGroup == null)
			throw new IllegalArgumentException(
					"param 0 (type ThreadGroup) is null");
		this.threadGroup = threadGroup;
		loopControllers = new Vector<LoopController>();
		loopControllerFactory = new LoopControllerFactory(this);
		started = autoStart;
	}

	public LoopController addLoopable(Loopable loopable) {
		return addLoopable(loopable, Integer.toString(loopControllers.size()));
	}

	public LoopController addLoopable(Loopable loopable, String name) {
		LoopController loopControllerForLoop = loopControllerFactory
				.produceLoopableController(loopable, name);
		loopControllers.add(loopControllerForLoop);
		return loopControllerForLoop;
	}

	public LoopController removeLoopable(Loopable loopable) {
		if(loopable == null)
			throw new IllegalArgumentException("param 0 (type Loop) is null");
		for(LoopController loopController : loopControllers) {
			Loopable loopableControllerLoop = loopController.getLoopable();
			if(loopableControllerLoop.equals(loopable)) {
				loopController.stop();
				loopControllers.remove(loopController);
				return loopController;
			}
		}
		return null;
	}

	public void startAll() {
		for(LoopController loopController : loopControllers)
			loopController.start();
		started = true;
	}

	public void stopAll() {
		for(LoopController loopController : loopControllers)
			loopController.stop();
		started = false;
	}

	public void pauseAll() {
		for(LoopController loopController : loopControllers)
			loopController.pause();
		paused = true;
	}

	public void resumeAll() {
		for(LoopController loopController : loopControllers)
			loopController.pause();
		paused = false;
	}

	public boolean checkForLivingControllers() {
		for(LoopController loopController : loopControllers)
			if(loopController.isAlive())
				return true;
		return false;
	}

	public boolean checkForActiveControllers() {
		for(LoopController loopController : loopControllers)
			if(loopController.isActive())
				return true;
		return false;
	}

	public ThreadGroup getThreadGroup() {
		return threadGroup;
	}

	public LoopController[] getLoopControllers() {
		return loopControllers.toArray(new LoopController[loopControllers
				.size()]);
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isPaused() {
		return paused;
	}
}

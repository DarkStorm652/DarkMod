package org.darkstorm.tools.loopsystem;

public class LoopControllerFactory {
	private LoopManager loopManager;
	private ThreadGroup loopControllerThreadGroup;

	public LoopControllerFactory(LoopManager loopManager) {
		if(loopManager == null)
			throw new IllegalArgumentException(
					"param 0 (type LoopHandler) is null");
		this.loopManager = loopManager;
		loopControllerThreadGroup = new ThreadGroup(loopManager
				.getThreadGroup(), "LoopControllers");
	}

	public LoopController produceLoopableController(Loopable loopable, String name) {
		LoopController loopController = new LoopController(this, loopable, name);
		if(loopManager.hasStarted()) {
			loopController.start();
			if(loopManager.isPaused())
				loopController.pause();
			else
				loopController.resume();
		} else
			loopController.stop();
		return loopController;
	}

	public ThreadGroup getLoopControllerThreadGroup() {
		return loopControllerThreadGroup;
	}

	public LoopManager getLoopManager() {
		return loopManager;
	}

}

package behaviour_trees.core;

public class Tree extends GuardableTask {
	private Task trunk;
	private boolean keepRunning = false;

	public Tree(int id, Task trunk, Guard guard) {
		super(id, guard);
		this.trunk = trunk;
	}

	public void runOnLoop() {
		keepRunning = true;
		run();
	}

	@Override
	public Status run() {
		Status result = trunk.tick();
		if (keepRunning && result != Status.RUNNING) { //all the branches are finished
			reset();
			return Status.RUNNING;
		}
		else return result;
	}

	@Override
	public void terminate() {
		keepRunning = false;
		trunk.terminate();
		setStatus(Status.TERMINATED);
	}

	@Override
	public void cleanup() {
		trunk.cleanup();
	}
}

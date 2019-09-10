package behaviour_trees.core;

public class Tree implements Task {
	private Task trunk;
	private int id;
	private Status status = Status.FRESH;
	private boolean keepRunning = false;
	private String[] args;

	public Tree(int id, Task trunk, String... args) {
		this.id = id;
		this.trunk = trunk;
		this.args = args;
	}

	public void tickAndLoop() {
		keepRunning = true;
		tick();
	}

	@Override
	public Status tick() {
		Status result = trunk.tick();
		if (keepRunning && result != Status.RUNNING) { //all the branches are finished
			reset();
			result = Status.RUNNING;
		}
		status = result;
		return result;
	}

	@Override
	public void terminate() {
		keepRunning = false;
		trunk.terminate();
		status = Status.TERMINATED;
	}

	@Override
	public void cleanup() {
		trunk.cleanup();
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public boolean isGuarded() {
		return false;
	}

	@Override
	public Guard getGuard() {
		return null;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String[] getArgs() {
		return args;
	}

	@Override
	public void reset() {
		terminate();
		status = Status.FRESH;
	}

	@Override
	public String toString() {
		return "Tree{" +
				"trunk=" + trunk +
				", id=" + id +
				'}';
	}
}

package behaviour_trees.core;

public class Tree implements Task {
	private final Task trunk;
	private final int id;
	private Status status = Status.FRESH;
	private final String[] args;

	public Tree(int id, Task trunk, String... args) {
		this.id = id;
		this.trunk = trunk;
		if(trunk == null) throw new IllegalArgumentException("Trunk can't be null");
		System.out.println(trunk.toString()); //todo log
		this.args = args;
	}

	@Override
	public Status tick() {
		return status = trunk.tick();
	}

	@Override
	public void terminate() {
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
				"trunk=" + trunk.toString() +
				", id=" + id +
				'}';
	}
}

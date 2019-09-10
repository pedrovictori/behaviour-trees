package behaviour_trees.core;

public abstract class GuardableTask implements Task {
	private Guard guard;
	private Status status = Status.FRESH;
	private int id;
	private String[] args;

	public GuardableTask(int id, Guard guard, String... args) {
		this.id = id;
		this.guard = guard;
		guard.setGuardedTask(this);
		this.args = args;
	}

	public Status tick(){
		if (isGuarded()) {
			if(getGuard().checkCondition()) setStatus(run());
			else setStatus(Status.FAILURE);
		} else setStatus(run());

		if(getStatus() != Status.RUNNING) cleanup();
		return getStatus();
	}

	public abstract Status run();

	public boolean isGuarded(){
		return guard != null;
	}

	public Guard getGuard() {
		return guard;
	}

	public Status getStatus() {
		return status;
	}

	public int getId() {
		return id;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void reset() {
		terminate();
		setStatus(Status.FRESH);
	}

	@Override
	public String toString() {
		return getClass().toString();
	}
}

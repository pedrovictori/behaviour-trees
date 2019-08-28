package behaviour_trees.core;

public abstract class GuardableTask implements Task {
	private Guard guard;
	private Status status = Status.FRESH;

	public GuardableTask(Guard guard) {
		this.guard = guard;
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

	public void terminate(){
		setStatus(Status.TERMINATED);
		cleanup();
	};

	public boolean isGuarded(){
		return guard != null;
	}

	public Guard getGuard() {
		return guard;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public abstract void cleanup();

	@Override
	public void reset() {
		terminate();
		setStatus(Status.FRESH);
	}
}

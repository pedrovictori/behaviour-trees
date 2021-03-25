package behaviour_trees.core;

import java.util.Arrays;
import java.util.Objects;

public abstract class GuardableTask implements Task {
	private Guard guard;
	private Status status = Status.FRESH;
	private int id;
	private String[] args = new String[0];

	protected GuardableTask(int id, Guard guard, String... args) {
		this.id = id;
		this.guard = guard;
		this.args = args;
	}

	@Override
	public final Status tick(){
		if (isGuarded()) {
			Guard guard = getGuard();
			boolean success = guard.tick() == Status.SUCCESS;
			if(success) setStatus(run());
			else setStatus(Status.FAILURE);
		} else setStatus(run());

		if(getStatus() != Status.RUNNING) cleanup();
		return getStatus();
	}

	public abstract Status run();

	public abstract void cleanup();

	public boolean isGuarded(){
		return guard != null;
	}

	public Guard getGuard() {
		return guard;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String[] getArgs() {
		return args;
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

package behaviour_trees.core;

public abstract class Guard extends GuardableTask {
	private Task guardedTask;

	public Guard(int id, Guard guard, GuardableTask guardedTask) {
		super(id, guard);
		this.guardedTask = guardedTask;
	}

	/**
	 * This method needs to be implemented with the actual condition logic. It returns true if the condition was met, allowing the guarded task to be run.
	 * @return true if condition is met, false otherwise.
	 */
	public abstract boolean checkCondition();

	public Status run(){
		return checkCondition() ? Status.SUCCESS : Status.FAILURE;
	}

	public void terminate() {
		//do nothing, this task can't run for more than one tick, so terminate doesn't make sense here
	}

	@Override
	public void cleanup() {
		//do nothing, this task can't run for more than one tick, so terminate doesn't make sense here
	}

	public void setGuardedTask(Task guardedTask) {
		this.guardedTask = guardedTask;
	}

	public Task getGuardedTask() {
		return guardedTask;
	}

	/**
	 * Useful for a series of nested Guards wrapping a task.
	 * @return a GuardableTask
	 */
	public Task getRootTask(){
		if (getGuardedTask() instanceof Guard) {
			return ((Guard) getGuardedTask()).getRootTask();
		}
		else return getGuardedTask();
	}

}

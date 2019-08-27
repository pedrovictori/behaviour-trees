package behaviour_trees.decorator;

import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

public class AlwaysSucceed implements Decorator {
	Task wrappedTask;

	public AlwaysSucceed(Task wrappedTask) {
		this.wrappedTask = wrappedTask;
	}

	public Task getWrappedTask() {
		return wrappedTask;
	}

	public Status tick() {
		Status result = wrappedTask.tick();
		if(result == Status.WAITING) return Status.WAITING;
		else return Status.SUCCESS;
	}

	public void terminate() {

	}
}

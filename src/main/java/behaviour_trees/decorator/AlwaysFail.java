package behaviour_trees.decorator;

import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

public class AlwaysFail implements Decorator {
	Task wrappedTask;

	public AlwaysFail(Task wrappedTask) {
		this.wrappedTask = wrappedTask;
	}

	public Task getWrappedTask() {
		return wrappedTask;
	}

	public Status tick() {
		Status result = wrappedTask.tick();
		if(result == Status.WAITING) return Status.WAITING;
		else return Status.FAILURE;
	}

	public void terminate() {

	}
}

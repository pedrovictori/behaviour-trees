package behaviour_trees.decorator;

import behaviour_trees.core.Guard;
import behaviour_trees.core.GuardableTask;
import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

public class AlwaysFail extends GuardableTask implements Decorator {
	Task wrappedTask;

	public AlwaysFail(Task wrappedTask, Guard guard) {
		super(guard);
		this.wrappedTask = wrappedTask;
	}

	public Task getWrappedTask() {
		return wrappedTask;
	}

	public Status tick() {
		Status result = wrappedTask.tick();
		if(result == Status.RUNNING) return Status.RUNNING;
		else return Status.FAILURE;
	}

	public void terminate() {

	}
}

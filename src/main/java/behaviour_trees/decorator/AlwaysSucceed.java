package behaviour_trees.decorator;

import behaviour_trees.core.Guard;
import behaviour_trees.core.GuardableTask;
import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

public class AlwaysSucceed extends GuardableTask implements Decorator {
	Task wrappedTask;

	public AlwaysSucceed(int id, Task wrappedTask, Guard guard) {
		super(id, guard);
		this.wrappedTask = wrappedTask;
	}

	public Task getWrappedTask() {
		return wrappedTask;
	}

	public Status run() {
		Status result = wrappedTask.tick();
		if(result == Status.RUNNING) return Status.RUNNING;
		else return Status.SUCCESS;
	}

	public void terminate() {
		wrappedTask.terminate();
	}

	@Override
	public void cleanup() {
		wrappedTask.cleanup();
	}
}

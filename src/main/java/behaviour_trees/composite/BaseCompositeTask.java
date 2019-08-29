package behaviour_trees.composite;

import behaviour_trees.core.Guard;
import behaviour_trees.core.GuardableTask;
import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

import java.util.List;

public abstract class BaseCompositeTask extends GuardableTask implements CompositeTask {
	private List<Task> branches;

	public BaseCompositeTask(int id, List<Task> branches, Guard guard) {
		super(id, guard);
		this.branches = branches;
	}

	@Override
	public List<Task> getBranches() {
		return branches;
	}

	@Override
	public void terminate() {
		for (Task branch : branches) {
			branch.terminate();
		}
		setStatus(Status.TERMINATED);
	}

	@Override
	public void cleanup() {
		for (Task branch : branches) {
			branch.cleanup();
		}
	}

	@Override
	public void reset() {
		for (Task branch : branches) {
			branch.reset();
		}
		super.reset();
	}
}

package behaviour_trees.leaves;

import behaviour_trees.core.Guard;
import behaviour_trees.core.GuardableTask;
import behaviour_trees.core.Status;

public class Success extends GuardableTask {
	public Success(int id, Guard guard) {
		super(id, guard);
	}

	@Override
	public Status run() {
		return Status.SUCCESS;
	}

	@Override
	public void terminate() {
		//do nothing
	}

	@Override
	public void cleanup() {
		//do nothing
	}
}

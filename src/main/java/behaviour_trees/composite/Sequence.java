package behaviour_trees.composite;

import behaviour_trees.core.Guard;
import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

import java.util.List;

public class Sequence extends BaseCompositeTask implements CompositeTask {
	private int pos;

	public Sequence(int id, List<Task> branches, Guard guard, String... args) {
		super(id, branches, guard, args);
	}

	public Status run() {
		Status result = getBranches().get(pos).tick();
		if(result == Status.SUCCESS){
			pos++;
			if(pos == getBranches().size()) return Status.SUCCESS;
			else return Status.RUNNING;
		}
		else if(result == Status.RUNNING) return Status.RUNNING;
		else return Status.FAILURE;
	}
}

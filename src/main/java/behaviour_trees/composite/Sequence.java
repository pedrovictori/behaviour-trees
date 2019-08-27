package behaviour_trees.composite;

import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

import java.util.List;

public class Sequence implements CompositeTask {
	private List<Task> branches;
	private int pos;

	public Sequence(List<Task> branches) {
		this.branches = branches;
	}

	public List<Task> getBranches() {
		return branches;
	}

	public Status tick() {
		Status result = branches.get(pos).tick();
		if(result == Status.SUCCESS){
			pos++;
			if(pos == branches.size()) return Status.SUCCESS;
			else return Status.WAITING;
		}
		else if(result == Status.WAITING) return Status.WAITING;
		else return Status.FAILURE;
	}

	public void terminate() {

	}
}

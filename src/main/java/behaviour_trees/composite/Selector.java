package behaviour_trees.composite;

import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

import java.util.List;

public class Selector implements CompositeTask {
	private List<Task> branches;
	private int pos;

	public Selector(List<Task> branches) {
		this.branches = branches;
	}

	public List<Task> getBranches() {
		return branches;
	}

	public Status tick() {
		Status result = branches.get(pos).tick();
		if(result == Status.FAILURE){
			pos++;
			if(pos == branches.size()) return Status.FAILURE; //no more branches left
			else return Status.WAITING;
		}
		else if(result == Status.SUCCESS) return Status.SUCCESS;
		else return Status.WAITING;

	}

	public void terminate() {
		branches.get(pos).terminate();
	}
}

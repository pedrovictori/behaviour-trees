package behaviour_trees.composite;

import behaviour_trees.core.Task;

import java.util.List;

public interface CompositeTask extends Task {
	/**
	 * Returns a list of the tasks downstream (branches) either other composite tasks or leaf tasks.
	 * @return a list of branches tasks
	 */
	List<Task> getBranches();
}

package behaviour_trees.composite;

import behaviour_trees.core.Guard;
import behaviour_trees.core.GuardableTask;
import behaviour_trees.core.Status;
import behaviour_trees.core.Task;

import java.util.Collections;
import java.util.List;

public class ParallelTask extends BaseCompositeTask implements CompositeTask {
	public enum Policy{
		SELECTOR,
		SEQUENCE;
	}

	private Policy policy;
	private boolean runAlways;

	public ParallelTask(Policy policy, boolean runAlways, List<Task> branches, Guard guard) {
		super(branches, guard);
		this.policy = policy;
		this.runAlways = runAlways;
	}

	/**
	 * If Policy is SEQUENCE, the parallel task fails as soon as one child fails; if all its children succeed, then the parallel task succeeds.
	 * If Policy is SELECTOR, the parallel task succeeds as soon as one child succeeds; if all its children fail, then the parallel task fails.
	 * If runAlways is true, the parallel task starts or resumes all branches every step.
	 * @return
	 */
	public Status run() {
		for (Task branch : getBranches()) {
			boolean toBeTicked = false;
			if(branch.getStatus() == Status.RUNNING || branch.getStatus() == Status.FRESH) toBeTicked = true;
			else if (runAlways) { //if the branch is finished but mode is runAlways, reset and mark for ticking.
				branch.reset();
				toBeTicked = true;
			}

			if (toBeTicked) {
				Status result = branch.tick();
				if(policy == Policy.SELECTOR && result == Status.SUCCESS) return Status.SUCCESS;
				else if(policy == Policy.SEQUENCE && result == Status.FAILURE) return Status.FAILURE;
			}
		}

		//after ticking all running branches and checking for immediate success or failure, check if any branch is still running.
		for (Task branch : getBranches()) {
			if(branch.getStatus() == Status.RUNNING) return Status.RUNNING; //at least one branch is running, so the ParallelTask keeps running
		}

		//this point is only reached if no branch is still running. Check for success depending on policy.
		if(policy == Policy.SELECTOR) return Status.FAILURE;
		else return Status.SUCCESS;
	}

}

package behaviour_trees.core;

public interface Task {

	/**
	 * Ticks the branch and returns the task result: either the task succeeds, fails, or goes on.
	 * @return A Status, either SUCCESS, FAILURE or WAITING
	 */
	Status tick();

	/**
	 * Terminates the task without waiting for a result.
	 */
	void terminate();
}

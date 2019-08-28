package behaviour_trees.core;

public interface Task {

	/**
	 * Ticks the branch and returns the task result: either the task succeeds, fails, or goes on.
	 * @return A Status, either SUCCESS, FAILURE or WAITING
	 */
	Status tick();

	/**
	 * Method to include the specific logic of each task. It is called from tick().
	 * @return the result Status to pass to tick().
	 */
	Status run();

	/**
	 * Returns the current status of the task. This will be the result of the last tick, or TERMINATED if terminate() was called.
	 * @return the current Status of the task.
	 */
	Status getStatus();

	/**
	 * Terminates the task without waiting for a result.
	 */
	void terminate();

	/**
	 * Checks if the task is guarded (only reachable if a condition is met)
	 * @return true if the task is guarded, false otherwise
	 */
	boolean isGuarded();

	/**
	 * Returns the task's guard
	 * @return a Guard, null if the task is unguarded.
	 */
	Guard getGuard();
}

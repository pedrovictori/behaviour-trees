package behaviour_trees.core;

public interface LeafTask extends Task{
	/**
	 * This method is called whenever this leaf is reached by the tree.
	 */
	void reached();

	/**
	 * This method is called after leaf is consumed (either after producing a result or on being terminated).
	 */
	void cleanup();
}

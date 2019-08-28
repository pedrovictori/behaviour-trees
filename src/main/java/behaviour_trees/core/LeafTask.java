package behaviour_trees.core;

public interface LeafTask extends Task{
	/**
	 * This method is called whenever this leaf is reached by the tree.
	 */
	void reached();
}

package behaviour_trees.composite;

import behaviour_trees.core.Guard;
import behaviour_trees.core.Task;

import java.util.Collections;
import java.util.List;

public class RandomSequence extends Sequence {
	public RandomSequence(int id, List<Task> branches, Guard guard) {
		super(id, branches, guard);
		Collections.shuffle(getBranches());
	}
}

package behaviour_trees.composite;

import behaviour_trees.core.Guard;
import behaviour_trees.core.Task;

import java.util.Collections;
import java.util.List;

public class RandomSelector extends Selector {
	public RandomSelector(int id, List<Task> branches, Guard guard, String... args) {
		super(id, branches, guard, args);
		Collections.shuffle(getBranches());
	}
}

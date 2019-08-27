package behaviour_trees.composite;

import behaviour_trees.core.Task;

import java.util.Collections;
import java.util.List;

public class RandomSelector extends Selector {
	public RandomSelector(List<Task> branches) {
		super(branches);
		Collections.shuffle(getBranches());
	}
}

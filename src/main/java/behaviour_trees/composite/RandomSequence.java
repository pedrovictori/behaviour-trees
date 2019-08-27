package behaviour_trees.composite;

import behaviour_trees.core.Task;

import java.util.Collections;
import java.util.List;

public class RandomSequence extends Sequence {
	public RandomSequence(List<Task> branches) {
		super(branches);
		Collections.shuffle(getBranches());
	}
}

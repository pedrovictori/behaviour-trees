package behaviour_trees.decorator;

import behaviour_trees.core.Task;

public interface Decorator extends Task {
	Task getWrappedTask();
}

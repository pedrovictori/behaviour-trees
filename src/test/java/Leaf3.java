import behaviour_trees.core.Guard;
import behaviour_trees.core.GuardableTask;
import behaviour_trees.core.LeafTask;
import behaviour_trees.core.Status;

public class Leaf3 extends GuardableTask implements LeafTask {
	public Leaf3(int id, Guard guard, String... args) {
		super(id, guard, args);
	}

	@Override
	public Status run() {
		return Status.FAILURE;
	}

	@Override
	public void terminate() {

	}

	@Override
	public void cleanup() {

	}
}
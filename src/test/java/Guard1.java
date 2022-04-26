import behaviour_trees.core.Guard;

public class Guard1 extends Guard {
	public Guard1(int id, Guard guard, String... args) {
		super(id, guard, args);
	}

	@Override
	public boolean checkCondition() {
		return true;
	}
}

import behaviour_trees.core.Guard;

public class Guard2 extends Guard {
	public Guard2(int id, Guard guard, String... args) {
		super(id, guard, args);
	}

	@Override
	public boolean checkCondition() {
		return true;
	}
}

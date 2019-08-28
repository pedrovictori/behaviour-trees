package behaviour_trees.core;

public abstract class GuardableTask implements Task {
	private Guard guard;

	public GuardableTask(Guard guard) {
		this.guard = guard;
	}

	public abstract Status tick();

	public abstract void terminate();

	public boolean isGuarded(){
		return guard != null;
	}

	public Guard getGuard() {
		return guard;
	}
}

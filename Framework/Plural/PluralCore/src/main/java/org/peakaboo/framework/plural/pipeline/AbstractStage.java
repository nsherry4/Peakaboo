package org.peakaboo.framework.plural.pipeline;

import java.util.function.Function;

public abstract class AbstractStage<S, T> extends AbstractOperator<S, T> implements Stage<S, T> {

	protected Function<S, T> function;
	private Stage<T, ?> next;
	private String name;

	
	protected AbstractStage(String name, Function<S, T> function) {
		this.name = name;
		this.function = function;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void accept(S input) {
		if (waitUntilOperating()) {
			//Always apply the function, it may have side-effects
			counter++;
			T output = function.apply(input);
			if (next() == null) return;
			next().accept(output);
		} else {
			if (getState() == State.COMPLETED) {
				// Jobs shouldn't be received after a clean shutdown.
				throw new IllegalStateException("Stage '" + getName() + "' is already closed.");
			}
		}
	}
	
	@Override
	public boolean finish() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return false;
		}
		return setState(State.COMPLETED);
	}
	
	@Override
	public boolean abort() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return false;
		}
		return setState(State.ABORTED);
	}
	
	@Override
	public <O> Pipeline<S, O> then(Stage<T, O> next) {
		this.next = next;
		return new Pipeline<>(this, next);
	}
	
	@Override
	public void link(Stage<T, ?> next) {
		this.next = next;
	}

	@Override
	public Stage<T, ?> next() {
		return next;
	}


}

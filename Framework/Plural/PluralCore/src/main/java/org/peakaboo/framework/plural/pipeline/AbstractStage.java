package org.peakaboo.framework.plural.pipeline;

import java.util.function.Function;

public abstract class AbstractStage<S, T> implements Stage<S, T> {

	protected Function<S, T> function;
	private Stage<T, ?> next;
	protected String name;
	private int counter = 0;
	private State state = State.STARTING;
	
	public AbstractStage(String name, Function<S, T> function) {
		this.name = name;
		this.function = function;
	}
	
	protected void setState(State state) {
		
		if (this.state == State.ABORTED || this.state == State.COMPLETED) {
			// Don't allow changing on stop states
			return;
		}
		if (this.state == State.OPERATING && state == State.STARTING) {
			// Don't allow moving back to starting state
			return;
		}
		this.state = state;
	}
	
	@Override
	public State getState() {
		return state;
	}
		
	@Override
	public void accept(S input) {
		if (this.state != State.OPERATING) {
			// Discard jobs when not in an operating state
			throw new IllegalStateException("Pipeline cannot accept jobs while in state " + this.getState());
		}
		//Always apply the function, it may have side-effects
		counter++;
		T output = function.apply(input);
		if (next() == null) return;
		next().accept(output);
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

	@Override
	public int getCount() {
		return counter;
	}
	

}

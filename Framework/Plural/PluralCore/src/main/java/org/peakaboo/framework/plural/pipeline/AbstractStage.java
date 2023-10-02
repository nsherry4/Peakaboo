package org.peakaboo.framework.plural.pipeline;

import java.util.function.Function;

public abstract class AbstractStage<S, T> implements Stage<S, T> {

	protected Function<S, T> function;
	private Stage<T, ?> next;
	protected String name;

	public AbstractStage(String name, Function<S, T> function) {
		this.name = name;
		this.function = function;
	}
	
	@Override
	public void accept(S input) {
		//Always apply the function, it may have side-effects
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

	
	
}

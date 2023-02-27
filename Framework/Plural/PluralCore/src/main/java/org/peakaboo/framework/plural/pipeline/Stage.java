package org.peakaboo.framework.plural.pipeline;

public interface Stage<S, T> extends Operator<S, T> {

	void link(Stage<T, ?> next);
	Stage<T, ?> next();
	
}

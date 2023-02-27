package org.peakaboo.framework.plural.pipeline;

public interface Operator<A, Z> {
	
	/**
	 * Accepts a new input item from the previous stage of the pipeline. Specific
	 * implementations may optionally performs causes a side-effect, transforms the
	 * input, or passes it directly to {@link Stage#next()}
	 */
	public void accept(A item);
	
	
	/**
	 * Connects this operator to a succeeding one. If the operator generates output,
	 * it will be based to this next {@link Stage}
	 */
	public <O> Pipeline<A, O> then(Stage<Z, O> next);
	
	/**
	 * Shuts down this operator in an orderly manner, rejecting any new inputs
	 * offered after shutdown has started. For composite operators, each component
	 * should be shut down in order.
	 */
	void finish();
}

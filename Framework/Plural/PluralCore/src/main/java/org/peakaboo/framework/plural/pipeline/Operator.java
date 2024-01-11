package org.peakaboo.framework.plural.pipeline;

public interface Operator<A, Z> {
	
	public enum State {
		STARTING, // Not accepting input yet.
		OPERATING, // Accepting input and procuding output
		QUIESCING, // No longer accepting input
		COMPLETED, // No longer procuding output
		ABORTED // Abnormal stop. No longer performing any work
	}
	
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
	 * should be shut down in order. Returns true on successful state change, false
	 * otherwise.
	 */
	boolean finish();
	
	/**
	 * Shuts down this operator immediately, discarding any existing jobs. For
	 * composite operators, each component should be aborted in order. Returns true
	 * on successful state change, false otherwise.
	 */
	boolean abort();


	/**
	 * Returns a count of how many items this {@link Operator} has processed.
	 */
	int getCount();
	
	State getState();


	String getName();
}

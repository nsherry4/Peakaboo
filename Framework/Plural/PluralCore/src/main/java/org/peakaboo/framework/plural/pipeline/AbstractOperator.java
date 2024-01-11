package org.peakaboo.framework.plural.pipeline;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.framework.plural.Plural;

public abstract class AbstractOperator<A, Z> implements Operator<A, Z> {

	protected int counter = 0;
	private State state = State.STARTING;
	
	public AbstractOperator() {}
	
	public static final Map<State, List<State>> TRANSITIONS = Map.of(
			State.STARTING, List.of(State.OPERATING),
			State.OPERATING, List.of(State.ABORTED, State.QUIESCING, State.COMPLETED),
			State.QUIESCING, List.of(State.COMPLETED)
	);
	
	protected boolean setState(State state) {
		
		// Allow a state to transition to itself, and with minimal overhead
		if (this.state == state) {
			return true;
		}
		if (! TRANSITIONS.get(this.state).contains(state)) {
			return false;
		}
		
		this.state = state;
		return true;
	}
	
	@Override
	public State getState() {
		return state;
	}
		
	@Override
	public int getCount() {
		return counter;
	}
	
	/**
	 * Returns true if the component is in an operating state or false if it is past
	 * an operating state. If the component is not in an operating state yet, this
	 * method will block until it is.
	 * 
	 * @throws InterruptedException
	 */
	protected boolean waitUntilOperating() {
		
		//Handle most common case without locks
		if (this.state == State.OPERATING) {
			return true;
		}
		
		synchronized(this) {
			while (true) {
				switch (this.state) {
				case ABORTED:
				case COMPLETED:
					return false;
				case STARTING:
					try {
						this.wait();
					} catch (InterruptedException e) {
						Plural.logger().log(Level.SEVERE, "Failed to wait for processing link to become operational", e);
						Thread.currentThread().interrupt();
						return false;
					}
					break;
				case OPERATING:
				case QUIESCING:
					return true;
				}
			}
		}
		
	}
	
	

}

package org.peakaboo.framework.plural.pipeline;

import java.util.function.Consumer;

public class Pipeline<A, Z> implements Operator<A, Z> {

	private Stage<A, ?> first;
	private Stage<?, Z> last;
	private int counter = 0;
	private State state = State.STARTING;
	
	public Pipeline(Stage<A, Z> single) {
		this.first = single;
		this.last = single;
	}
	
	Pipeline(Stage<A, ?> first, Stage<?, Z> last) {
		this.first = first;
		this.last = last;
		this.state = State.OPERATING;
	}
	
	@Override
	public <O> Pipeline<A, O> then(Stage<Z, O> next) {
		last.link(next);
		return new Pipeline<A, O>(first, (Stage<?, O>) next);
	}
	
	@Override
	public void accept(A item) {
		counter++;
		first.accept(item);
	}

	private void visit(Consumer<Stage<?, ?>> visitor) {
		Stage<?, ?> iter = first;
		while (true) {
			visitor.accept(iter);
			if (iter == last) {
				return;
			}
			iter = iter.next();
			if (iter == null) {
				return;
			}
		}
	}
	
	@Override
	public void finish() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return;
		}
		this.state = State.COMPLETED;
		visit(Stage::finish);
	}

	@Override
	public int getCount() {
		return counter;
	}

	@Override
	public void abort() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return;
		}
		this.state = State.ABORTED;
		visit(Stage::abort);
	}

	@Override
	public State getState() {
		return state;
	}
	
	
}

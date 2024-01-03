package org.peakaboo.framework.plural.pipeline;

import java.util.function.Consumer;

public class Pipeline<A, Z> extends AbstractOperator<A, Z> implements Operator<A, Z> {

	private Stage<A, ?> first;
	private Stage<?, Z> last;
	
	public Pipeline(Stage<A, Z> single) {
		this.first = single;
		this.last = single;
	}
	
	Pipeline(Stage<A, ?> first, Stage<?, Z> last) {
		this.first = first;
		this.last = last;
		setState(State.OPERATING);
	}
	
	@Override
	public <O> Pipeline<A, O> then(Stage<Z, O> next) {
		last.link(next);
		return new Pipeline<A, O>(first, (Stage<?, O>) next);
	}
	
	@Override
	public void accept(A item) {
		// The pipeline itself does not act like a stage, checking for state before
		// submitting, that will be handled by the stages themselves
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
	public boolean finish() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return false;
		}
		setState(State.QUIESCING);
		visit(Stage::finish);
		return setState(State.COMPLETED);
	}


	@Override
	public boolean abort() {
		if (this.getState() != State.OPERATING && this.getState() != State.STARTING) {
			// Once we've moved past the operating stage, don't accept any new shutdown requests
			return false;
		}
		boolean success = setState(State.ABORTED);
		visit(Stage::abort);
		return success;
	}

	@Override
	public String getName() {
		return "Pipeline from " + first.getName() + " to " + last.getName();
	}
	
	
}

package org.peakaboo.framework.plural.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This Stage reuses threads from an upstream {@link ThreadedStage} to continue
 * work on the same input.
 */

public class RunToCompletionStage<S, T> extends AbstractStage<S, T> {

	public RunToCompletionStage(String name, Function<S, T> function) {
		super(name, function);
	}

	@Override
	public void finish() {
		// NOOP
	}
	
	public static <S, T> Stage<S, T> of(String name, Function<S, T> function) {
		return new RunToCompletionStage<>(name, function);
	}
	
	public static <S> Stage<S, S> visit(String name, Consumer<S> function) {
		return new RunToCompletionStage<>(name, s -> {
			function.accept(s);
			return s;
		});
	}
	
	public static <S> Stage<S, Void> sink(String name, Consumer<S> function) {
		return new RunToCompletionStage<>(name, s -> {
			function.accept(s);
			return null;
		});
	}

}

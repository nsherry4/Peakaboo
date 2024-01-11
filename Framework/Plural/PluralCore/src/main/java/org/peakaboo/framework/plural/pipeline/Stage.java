package org.peakaboo.framework.plural.pipeline;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Stage<S, T> extends Operator<S, T> {

	void link(Stage<T, ?> next);
	Stage<T, ?> next();
	
	public static <S, T> Stage<S, T> of(String name, Function<S, T> function) {
		return RunToCompletionStage.of(name, function);
	}
	
	public static <S> Stage<S, S> visit(String name, Consumer<S> function) {
		return RunToCompletionStage.visit(name, function);
	}
	
	public static <S> Stage<S, Void> sink(String name, Consumer<S> function) {
		return RunToCompletionStage.sink(name, function);
	}
	
}

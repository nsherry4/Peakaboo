package net.sciencestudio.bolt.scripting.functions;

import java.util.function.Predicate;

import net.sciencestudio.bolt.scripting.languages.Language;

public class BoltCondition<T1> extends BoltMap<T1, Boolean> implements Predicate<T1>
{

	public BoltCondition(Language language, String inputName, String outputName, String script)
	{
		super(language, inputName, outputName, script);
	}

	@Override
	public boolean test(T1 t) {
		return apply(t);
	}

}

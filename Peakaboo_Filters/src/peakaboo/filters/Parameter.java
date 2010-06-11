package peakaboo.filters;


import java.io.Serializable;

/**
 * 
 * This class defines a parameter for a filter.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class Parameter<T1> implements Serializable
{

	public static enum ValueType
	{
		INTEGER, REAL, BOOLEAN, SET_ELEMENT
	}
	
	public ValueType	type;
	public String		name;
	private T1			value;
	
	public boolean		enabled;

	public T1[]			possibleValues;

	public Parameter(ValueType type, String name, T1 value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		this.possibleValues = null;
		this.enabled = true;
	}
	

	public Parameter(ValueType type, String name, T1 value, T1[] possibleValues)
	{
		this(type, name, value);
		this.possibleValues = possibleValues;
	}
	
	public void setValue(T1 value)
	{
		this.value = value;
	}
	
	public T1 getValue()
	{
		return value;
	}

}

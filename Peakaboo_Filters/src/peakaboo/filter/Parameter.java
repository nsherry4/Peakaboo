package peakaboo.filter;


import java.io.Serializable;

/**
 * 
 * This class defines a parameter for a filter.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class Parameter implements Serializable
{

	public static enum ValueType
	{
		INTEGER, REAL, BOOLEAN, SET_ELEMENT, FILTER, SEPARATOR, CODE
	}
	
	public ValueType	type;
	public String		name;
	private Object		value;
	public String		errorMessage;
	
	public boolean		enabled;

	public Object[]		possibleValues;

	public Parameter()
	{
		type = null;
		name = "";
		value = null;
	}
	
	public Parameter(ValueType type, String name, Object value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		this.possibleValues = null;
		this.enabled = true;
	}
	

	public Parameter(ValueType type, String name, Object value, Object[] possibleValues)
	{
		this(type, name, value);
		this.possibleValues = possibleValues;
	}
	
	public void setValue(Object value)
	{
		this.value = value;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	
	public int intValue()
	{
		try{
			return (Integer)value;
		} catch (ClassCastException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	public float realValue()
	{
		try{
			if (value instanceof Double) return ((Double)value).floatValue();
			//if (value instanceof Integer) return ((Integer)value).floatValue();
			return (Float)value;
		} catch (ClassCastException e)
		{
			e.printStackTrace();
			return 0;
		}
	}
	
	public boolean boolValue()
	{
		try{
			return (Boolean)value;
		} catch (ClassCastException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public AbstractFilter filterValue()
	{
		
		try
		{
			return (AbstractFilter)value;
		} 
		catch (ClassCastException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	public String codeValue()
	{
		
		try
		{
			return (String)value;
		} 
		catch (ClassCastException e)
		{
			e.printStackTrace();
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T enumValue()
	{
		try{
			return (T)value;
		} catch (ClassCastException e)
		{
			e.printStackTrace();
			return null;
		}
	}


}

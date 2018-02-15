package bolt.scripting;

public class BoltScriptExecutionException extends RuntimeException {

	public BoltScriptExecutionException(String s) {
		super(s);
	}
	
	public BoltScriptExecutionException(String s, Throwable cause)
	{
		super(s, cause);
	}
	
}

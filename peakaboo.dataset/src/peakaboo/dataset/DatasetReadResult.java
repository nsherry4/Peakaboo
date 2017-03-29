package peakaboo.dataset;

public class DatasetReadResult
{


	public enum ReadStatus
	{
		SUCCESS,
		CANCELLED,
		FAILED;	
	}
	
	public ReadStatus status;
	public String message;
	
	public DatasetReadResult(ReadStatus result)
	{
		this(result, "");
	}
	
	public DatasetReadResult(ReadStatus result, String message)
	{
		this.status = result;
		this.message = message;
	}
	
}

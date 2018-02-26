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
	public Throwable problem;
	
	public DatasetReadResult(ReadStatus result)
	{
		this(result, "");
	}
	
	public DatasetReadResult(ReadStatus result, String message)
	{
		this.status = result;
		this.message = message;
	}
	
	public DatasetReadResult(Throwable problem)
	{
		this(problem, problem.getMessage());
	}
	
	public DatasetReadResult(Throwable problem, String message)
	{
		this.status = ReadStatus.FAILED;
		this.message = message;
		this.problem = problem;
	}
	
}

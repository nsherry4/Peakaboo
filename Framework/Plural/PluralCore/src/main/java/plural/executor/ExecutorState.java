package plural.executor;

/**
 * The various states that an {@link AbstractExecutor} can be in.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */
public enum ExecutorState
{
	COMPLETED, WORKING, STALLED, UNSTARTED, SKIPPED
}
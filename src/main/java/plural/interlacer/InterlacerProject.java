package plural.interlacer;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

import plural.Plural;

/**
 * The InterlacerProject class manages the information for all of the jobs for a specific project.
 * @author Nathaniel Sherry
 *
 * @param <T>
 */

public abstract class InterlacerProject<T>
{

	private static final int stagingSize = 500;
	
	/*
	 * We use a staging area plus a jobs queue in order to prevent locking the
	 * jobs queue every time we want to add a new job. If the staging area gets
	 * full, or the jobs queue runs empty, the staging area will be committed
	 * to the jobs queue.
	 */
	private List<T> staging;
	private Queue<T> jobs;
	
	private boolean closed = false; 
	
	public InterlacerProject()
	{
		staging = new LinkedList<>();
		jobs = new LinkedList<>();
	}
	
	/**
	 * Programmer-defined method for how to process a job 
	 * @param job the job to process
	 * @return true if job ran successfully, false otherwise
	 */
	protected abstract boolean doJob(T job);
	
	/**
	 * Programmer-defined method for how to process a list of jobs. Usually, 
	 * this can just invoke {@link InterlacerProject#doJob(T)} for each job
	 * in the list, but if there are optimizations that can be done when processing
	 * several jobs at once, they can be added here. 
	 * @param jobs the jobs to process
	 * @return true if all jobs ran successfully, false otherwise
	 */
	protected abstract boolean doJobs(List<T> jobs);
	
	/**
	 * Programmer-defined method called when this project is completed. No action is
	 * required, but this method provides a way to clean up open resources or otherwise
	 * perform final actions. 
	 */
	protected abstract void done();
	
	
	/**
	 * Error handler wrapper for {@link InterlacerProject#doJobs(List)}
	 * @param jobs the jobs to process
	 * @return
	 */
	protected boolean runJobs(List<T> jobs)
	{
		if (jobs == null) return false;
		
		try {
			return doJobs(jobs);
		} catch (Exception e) {
			Plural.logger().log(Level.SEVERE, "Jobs " + jobs + " failed", e);
			return false;
		}
	}
	
	/**
	 * Error handler wrapper for {@link InterlacerProject#doJob(T)}
	 * @param job the job to process
	 * @return
	 */
	protected boolean runJob(T job)
	{

		if (job == null) return false;
		
		try {
			return doJob(job);
		} catch (Exception e) {
			Plural.logger().log(Level.SEVERE, "Job " + job + " failed", e);
			return false;
		}
		
	}
	
	
	/**
	 * Adds a job to be processed
	 * @param job the job to add
	 */
	//locks: staging
	protected void addJob(T job)
	{
		if (closed) throw new ClosedProjectException();
		
		//the logic here is the same as returning a job to the queue
		//except we don't allow it after closing the project
		returnJob(job);

	}
	
	/**
	 * Adds jobs to be processed
	 * @param jobs the jobs to add
	 */
	//locks: staging
	protected void addJobs(Collection<T> jobs)
	{
		if (closed) throw new ClosedProjectException();
		
		//the logic here is the same as returning a job to the queue
		//except we don't allow it after closing the project
		returnJobs(jobs);

	}
	
	//locks: none
	protected void addJobs(T[] jobs)
	{	
		addJobs(Arrays.asList(jobs));
	}
	
	protected final void returnJob(T job)
	{
		
		//outer lock
		synchronized (staging)
		{
			staging.add(job);
			
			if (staging.size() > stagingSize) {
				//inner lock
				commitJobs();
			}
			
		}
		
	}
	
	protected final void returnJobs(Collection<T> jobs)
	{
		synchronized (staging)
		{
			for (T job : jobs){
				staging.add(job);
			}
							
			if (staging.size() > stagingSize) {
				//inner lock
				commitJobs();
			}
			
		}
	}
	
	/**
	 * Returns a single job to be processed.
	 * @return a single job of type T, or null if there are no jobs available 
	 */
	//locks: jobs
	protected T getJob()
	{
		T job;
		
		synchronized (jobs)
		{
			job = jobs.poll();
			
			if (job == null) {
				commitJobs();
				job = jobs.poll();				
			}
		}

		return job;
	}
	
	/**
	 * Returns a list of jobs for processing
	 * @param count the maximum number of jobs to get
	 * @return a list containing at most <tt>count</tt> jobs
	 */
	//locks: jobs
	protected List<T> getJobs(int count)
	{
		List<T> joblist = new LinkedList<>();
		T job;
		
		synchronized (jobs)
		{
			for (int i = 0; i < count; i++) {
		
				job = jobs.poll();
				if (job == null) {
					commitJobs();
					job = jobs.poll();
					if (job == null) break;
				}
				
				joblist.add(job);			
				
			}


		}
		
		return joblist;
	}
	
	//locks: staging + jobs
	private void commitJobs()
	{
		synchronized (staging) {	
			synchronized (jobs)	{
				
				for (T job : staging){
					jobs.offer(job);
				}
				
				staging.clear();
			}
		}
	}
	
	/**
	 * Checks to see if this project has jobs which have not yet been processed.
	 * @return true if there are jobs yet to be run, false otherwise
	 */
	//locks: jobs + staging
	public boolean hasJobs()
	{
		synchronized (jobs)	{
			synchronized (staging) {
				if (jobs.size() > 0) return true;
				if (staging.size() > 0) return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * Prevents new jobs from being added to this project. Existing jobs will 
	 * still be processed. To close the project and remove all existing jobs,
	 * see {@link InterlacerProject#terminate()}
	 */
	//locks: jobs + staging
	public void close()
	{
		synchronized (jobs) {
			synchronized (staging) {
				
				commitJobs();
				closed = true;
				
			}
		}
	}
	
	
	/**
	 * Prevents new jobs from being added to this project. Existing jobs will 
	 * be cleared. To close the project without clearing existing jobs, see 
	 * {@link InterlacerProject#close()}
	 */
	//locks: jobs + staging
	public void terminate()
	{
		synchronized (jobs) {
			synchronized (staging) {
			
				close();
				staging.clear();
				jobs.clear();	
				
			}		
		}

	}
	
	/**
	 * Checks to see if this project has been closed
	 * @return true if the project has been closed, false otherwise
	 */
	public boolean isClosed()
	{
		return closed;
	}
	
	
}

class ClosedProjectException extends RuntimeException
{
	
}
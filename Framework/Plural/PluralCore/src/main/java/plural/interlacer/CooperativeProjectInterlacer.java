package plural.interlacer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import plural.Plural;



/**
 * For when direct OS-level threading isn't the most effective for managing many 
 * similar tasks. This class allows the submission of jobs associated with various 
 * projects. A programmer-modifiable number of threads will take a few jobs from 
 * each of the projects, and process them. This allows the number of projects and 
 * the number of threads to be independent, while still ensuring multi-threaded 
 * operation. Each project defines how its own jobs should be executed, and each 
 * project can have a priority which determines the relative rate at which its jobs 
 * are processed. This class assumes that all jobs being submitted will be of roughly 
 * equal running time, and there is no running-time tracking or adjusted time quantum 
 * associated with the priority of a project.
 * <br/><br/>
 * As the name implies, this class implements something similar to cooperative 
 * multithreading with green threads. As such, it cannot preempt a task which takes 
 * too long, or even deadlocks. In many cases, using OS-level multithreading will be 
 * both simpler for the programmer, and more responsive, as the OS scheduler will be 
 * both more sophisticated, and more knowledgeable about blocked processes. 
 * @author Nathaniel Sherry, 2011
 *
 */

public class CooperativeProjectInterlacer<T>
{

	public boolean debugOutput = false;
	
	private static int defaultDefaultPriority = 3;
	private static int defaultIterationSize = 250;
	private static int defaultNumThreads = Runtime.getRuntime().availableProcessors();
	private static int defaultNumBackgroundEntries = 100;
	
	//Maps project name => work for project
	private Map<String, InterlacerEntry<T>> entries;
	
	private int iterationSize = defaultIterationSize;
	private int defaultPriority = defaultDefaultPriority;
	private int threadCount = defaultNumThreads;
	private int numBackgroundEntries = defaultNumBackgroundEntries;
	
	
	private boolean runAtLeastOnce = false;
	private boolean started = false;

	private String DPName = "DP";

	/**
	 * Convenience method for {@link Runtime#availableProcessors()}
	 * @return the number of cores this machine has available.
	 */
	public static int availableCores()
	{
		return Runtime.getRuntime().availableProcessors();
	}
	
	
	public CooperativeProjectInterlacer() {
		this("DP");
	}
	
	public CooperativeProjectInterlacer(String name) {
		this(defaultNumThreads, name);
	}
	
	
	
	
	/**
	 * Creates an Interlacer using a number of threads defined by availableCores()/percentageOfCores
	 * @param percentageOfCores
	 */
	public CooperativeProjectInterlacer(float percentageOfCores) {
		this(percentageOfCores, "DP");
	}

	/**
	 * Creates an Interlacer using a number of threads defined by availableCores()/percentageOfCores
	 * @param percentageOfCores number of threads to create as a percentage of the system's total cores
	 * @param name the name of this Interlacer
	 */
	public CooperativeProjectInterlacer(float percentageOfCores, String name) {
		this(percentageOfCores, defaultIterationSize, name);
	}
	
	/**
	 * Creates an Interlacer using a number of threads defined by availableCores()/percentageOfCores
	 * @param percentageOfCores number of threads to create as a percentage of the system's total cores 
	 * @param iterationJobCount the maximum number of jobs per project that a thread should work on at once
	 * @param name the name of this Interlacer 
	 */
	public CooperativeProjectInterlacer(float percentageOfCores, int iterationJobCount, String name) {
		this(percentageOfCores, iterationJobCount, defaultDefaultPriority, name);
	}
	
	/**
	 * Creates an Interlacer using a number of threads defined by availableCores()/percentageOfCores
	 * @param percentageOfCores number of threads to create as a percentage of the system's total cores 
	 * @param iterationJobCount the maximum number of jobs per project that a thread should work on at once
	 * @param defaultPriority the priority to assign new projects
	 * @param name the name of this Interlacer 
	 */
	public CooperativeProjectInterlacer(float percentageOfCores, int iterationJobCount, int defaultPriority, String name) {
		this(
				Math.max(1, Math.round(  Runtime.getRuntime().availableProcessors() * percentageOfCores  )), 
				iterationJobCount, 
				defaultPriority, 
				name
			);
	}
	
	

	/**
	 * Creates an Interlacer using a set number of threads
	 * @param numThreads the number of threads to create.
	 */
	public CooperativeProjectInterlacer(int numThreads) {
		this(numThreads, "DP");
	}
	
	/**
	 * Creates an Interlacer using a set number of threads
	 * @param numThreads the number of threads to create.
	 * @param name the name of this Interlacer 
	 */
	public CooperativeProjectInterlacer(int numThreads, String name) {
		this(numThreads, defaultIterationSize, name);
	}
	
	/**
	 * Creates an Interlacer using a set number of threads
	 * @param numThreads the number of threads to create.
	 * @param iterationJobCount the maximum number of jobs per project that a thread should work on at once
	 * @param name the name of this Interlacer 
	 */
	public CooperativeProjectInterlacer(int numThreads, int iterationJobCount, String name) {
		this(numThreads, iterationJobCount, defaultDefaultPriority, name);
	}
	
	/**
	 * Creates an Interlacer using a set number of threads
	 * @param numThreads the number of threads to create.
	 * @param iterationJobCount the maximum number of jobs per project that a thread should work on at once
	 * @param defaultPriority the priority to assign new projects
	 * @param name the name of this Interlacer 
	 */
	public CooperativeProjectInterlacer(int numThreads, int iterationJobCount, int defaultPriority, String name)
	{
		entries = Collections.synchronizedMap(new LinkedHashMap<String, InterlacerEntry<T>>());
		
		DPName = name;
		iterationSize = iterationJobCount;
		this.defaultPriority = defaultPriority;
		threadCount = numThreads;
		
	}
	
	/**
	 * Sets this Interlacer to at-least-once mode. Jobs (or collections of jobs) which fail 
	 * will be readded to the queue and tried again an unlimited number of times. This ensures 
	 * that intermittent problems do not cause some jobs not to run, without having  to build 
	 * more complex error-handling. An uncaught exception while executing a job counts as a 
	 * failure.  
	 */
	public void setRunAtLeastOnce()
	{
		runAtLeastOnce = true;
	}
	
	/**
	 * Sets this Interlacer to at-most-once mode. This is the default mode. Jobs (or 
	 * collections of jobs) which fail will be discarded after the first attempt. 
	 */
	public void setRunAtMostOnce()
	{
		runAtLeastOnce = false;
	}
	
	
	/**
	 * Starts the Interlacer's work on processing jobs from the queues. Subsequent calls 
	 * to this method will do nothing 
	 */
	public synchronized void start()
	{
		if (started) return;
		started = true;
		
		for (int i = 0; i < threadCount; i++) {
			DataProcessorThread<T> dpt = new DataProcessorThread<>(this, "Thread " + (i+1));
			dpt.start();
		}
	}
		
	
	/**
	 * Adds the given job to the project with the given name
	 * @param projectName the name of the project to add the job to
	 * @param job the job to add
	 */
	public void addJob(String projectName, T job)
	{
		addJobs(projectName, new ArrayList<>(Collections.singleton(job)));
	}
	
	/**
	 * Adds the given collection of jobs to the project with the given name
	 * @param projectName the name of the project to add the job to
	 * @param jobs the jobs to add
	 */
	public void addJobs(String projectName, Collection<T> jobs)
	{
		if (jobs.size() == 0) return;
		
		InterlacerProject<T> jobset = getProject(projectName);
		if (jobset == null) return;
		
		addJobs(jobset, jobs);

		
	}
	
	private void addJobs(InterlacerProject<T> project, Collection<T> jobs)
	{
		project.addJobs(jobs);

		//wake any sleeping worker threads, since we just added more work
		synchronized (this) {
			notifyAll();	
		}
	}
	
	
	private void returnJobs(InterlacerProject<T> project, Collection<T> jobs)
	{
		project.returnJobs(jobs);

		//wake any sleeping worker threads, since we just added more work
		synchronized (this) {
			notifyAll();	
		}
	}
	
	/**
	 * Adds a new project with the given name to the Interlacer. Attempting to add a project 
	 * which already exists (has the same name) will fail silently.
	 * @param projectName the name of the project to add. Project names must be unique. 
	 * @param project the {@link InterlacerProject} to add
	 */
	public synchronized void addProject(String projectName, InterlacerProject<T> project)
	{
		addProject(projectName, project, defaultPriority);
	}
	
	
	/**
	 * Adds a new project with the given name to the Interlacer. Attempting to add a project 
	 * which already exists (has the same name) will fail silently.
	 * @param projectName the name of the project to add. Project names must be unique. 
	 * @param project the {@link InterlacerProject} to add
	 * @param priority the priority the new project should be assigned
	 */
	public synchronized void addProject(String projectName, InterlacerProject<T> project, int priority)
	{
	
		if (getProject(projectName) != null) return;
		
		InterlacerEntry<T> entry = new InterlacerEntry<>();
		entry.project = project;
		entry.priority = defaultPriority;
		entry.projectName = projectName;
		
		entries.put(projectName, entry);
	}
	
	
	/**
	 * Change the priority for the project with the given name
	 * @param projectName the name of the project to change
	 * @param priority the new priority
	 */
	public synchronized void setProjectPriority(String projectName, int priority)
	{
		if (priority < 0) priority = 0;
		if (priority > 10) priority = 10;
		
		InterlacerEntry<T> entry = entries.get(projectName);
		if (entry == null) 
		{
			throw new ProjectNotFoundException("Could not find project " + projectName);
		}
		entry.priority = priority;
	}
	
	/**
	 * Gets the current priority of the project with the given name
	 * @param projectName the name of the project
	 * @return the priority of the project, or -1 if the project does not exist
	 */
	public synchronized int getProjectPriority(String projectName)
	{
		InterlacerEntry<T> entry = entries.get(projectName);
		if (entry == null) return -1;
		return entry.priority;
	}
	
	/**
	 * Gets the current limit on the number of active background projects
	 * @return the maximum number of active background projects
	 */
	public int getNumBackgroundProjects() {
		return numBackgroundEntries;
	}

	/**
	 * Sets the limit on the number of active background projects. Background 
	 * projects are projects with priority 0, and are only executed when no 
	 * projects with any higher priority ( &gt;= 1 ) are available.
	 * @param numBackgroundProjects the maximum number of active background projects
	 */
	public void setNumBackgroundProjects(int numBackgroundProjects) {
		this.numBackgroundEntries = numBackgroundProjects;
	}
	
	/**
	 * Checks to see if a project with the given name exists
	 * @param projectName the name of the project to check for
	 * @return true if the project exists, false otherwise
	 */
	public synchronized boolean projectExists(String projectName)
	{
		return (entries.get(projectName) != null);
	}
	
	/**
	 * Removes the project immediately, without calling {@link InterlacerProject#done()}. Any 
	 * threads currently running jobs from this project will continue, but no more jobs from this
	 * project will be given to the worker threads. In most cases, calling
	 * {@link CooperativeProjectInterlacer#removeProject(String)} would be preferable to calling 
	 * this method.
	 * @param projectName the name of the project to kill
	 */
	public synchronized void killProject(String projectName)
	{
		if (getProject(projectName) == null) return;
		
		//remove the project from the set of projects. Any threads currently working on
		//data from this project will complete, but never take any more work from it.
		entries.remove(projectName);
	}
	
	/**
	 * Terminates the project and marks it for cleanup. The project will be closed, and all existing 
	 * jobs in the project will be cleared. When all threads which are currently working 
	 * with jobs from this project are done, it will be removed from the set of active projects, and 
	 * {@link InterlacerProject#done()} will be called to allow the programmer to perform their own 
	 * cleanup.
	 * @param projectName the name of the project to remove
	 */
	public synchronized void removeProject(String projectName)
	{
		if (getProject(projectName) == null) return;
		
		//terminate the project (remove all jobs and prevent new ones from being added)
		//then mark it as being done so that it will be removed
		getProject(projectName).terminate();
		closeProject(projectName);
	}

	/**
	 * Marks this project as no longer accepting new jobs. At some point after the current 
	 * jobs in the project have all been taken, the project will be removed, and 
	 * {@link InterlacerProject#done()} will be called.
	 * @param projectName the name of the project to close
	 */
	public synchronized void closeProject(String projectName)
	{
		InterlacerEntry<T> entry = entries.get(projectName);
		if (entry == null) return;
		
		InterlacerProject<T> project = entry.project;
		if (project == null) return;
		
		project.close();
		
		//if there are no active projects, this could sit empty for a while without
		//being cleaned up since all threads are waiting. So we force a cleanup.
		cleanupCompletedProjects();
	}
	
	/**
	 * Looks up the project with the given name from the set of active projects
	 * @param projectName the name of the project to get
	 * @return the project with the given name, or null if such a project does not exist.
	 */
	public synchronized InterlacerProject<T> getProject(String projectName)
	{
		try {
			return entries.get(projectName).project;
		} catch(Exception e) {
			return null;
		}
	}
	
	
	/**
	 * Worker threads call into this method to be put to work processing jobs
	 */
	protected void doWork()
	{
		
		InterlacerEntry<T> entry;
		List<InterlacerEntry<T>> entryList = new ArrayList<>();
		
		List<T> jobList = new ArrayList<>();
		
		
		while(true) {
			
			int totalPriority = 0;
			int workingEntries = 0;
			float blockSize;
			
			try {
				
				entryList.clear();
				
				
				
				////////////////////////////////////////////////////////////
				// PLANNING
				////////////////////////////////////////////////////////////
				
				/* lock against this object to make sure there are no new projects created and no 
				 * modifications made to existing projects while we are deciding what jobs to run
				 */
				synchronized (this)
				{
					while (true) {
						
						//get a list of projects which have data ready to go
						for (String projectName : entries.keySet()){
							entry = entries.get(projectName);
							
							if (entry.project.hasJobs()) {
								
								//add this project to the list of projects this thread will work on
								entryList.add(entry);
								
								//mark this project has being worked on by this thread
								entry.workingThreads.add(Thread.currentThread());
							}
						}
						
						
						if (entryList.size() > 0) break;
						
						if (debugOutput) System.out.println(DPName + " " + Thread.currentThread() + ": Waiting");
						wait();
						if (debugOutput) System.out.println(DPName + " " + Thread.currentThread() + ": Awoken");
					}
					
					
					
					
					/* calculate the size of a single block of work.
					 * if a project has a priority of p, then it will be
					 * allowed to execute p blocks of work. The total
					 * number of blocks of work should be <= iterationSize.
					 * We don't allow the block size to dip below 5 in order to 
					 * make sure that we don't waste too much time on disk seeking
					 * or cache misses or whatnot caused by jumping from project to 
					 * project too quickly
					 */
					totalPriority = 0;
					workingEntries = 0;
					for (InterlacerEntry<T> currentEntry : entryList)
					{
						totalPriority += currentEntry.priority;
						if (currentEntry.priority > 0) workingEntries++;
					}
								
					
					if (workingEntries > 0) {
						//there are foreground projects
						blockSize = Math.max(5, (float)iterationSize / (float)totalPriority);
					} else if(entryList.size() > 0) {
						//there are no foreground projects, but there are background projects
						//so we work on the background projects instead
						workingEntries = Math.min(numBackgroundEntries, entryList.size());
						blockSize = Math.max(1, (float)iterationSize / (float)workingEntries);
						
						//remove any projects above the cap for number of background projects
						while (entryList.size() > numBackgroundEntries) entryList.remove(numBackgroundEntries);
						
					} else {
						blockSize = 5;
					}
					
				}//synchronize
				
				
				
				////////////////////////////////////////////////////////////
				// RUNNING
				////////////////////////////////////////////////////////////
				
				//run jobs on the projects we have in our list. We time them for logging purposes.
				long totalTime = 0;
				long t1, t2;
				int jobCount, jobsExecuted = 0;
				boolean success = false;
				
				for (InterlacerEntry<T> currentEntry : entryList)
				{
					
					if (totalPriority > 0) {
						jobCount = Math.round(blockSize * currentEntry.priority);
					} else {
						jobCount = Math.round(blockSize);
					}
					
					
					t1 = System.currentTimeMillis();
					
					
					jobList = currentEntry.project.getJobs(jobCount);
					jobsExecuted += jobList.size();
					
					success = currentEntry.project.runJobs(jobList);
					
					//if this run failed, but this is configured to make sure every job runs at least once
					//place the jobs back in the project's job list
					if (!success && runAtLeastOnce)
					{
						//this will lock internally
						returnJobs(currentEntry.project, jobList);
					}
					
					t2 = System.currentTimeMillis();
					totalTime += (t2 - t1);
					
				}
	
				if (entryList.size() > 0 && debugOutput) System.out.println(DPName + " " + Thread.currentThread() + ": Processed " + jobsExecuted + " Jobs from " + workingEntries + " projects in " + totalTime/1000f + "s");
				
				
				
				
				////////////////////////////////////////////////////////////
				// CLEAN-UP
				////////////////////////////////////////////////////////////
				
				//all the projects in the set, mark them as us not working on them anymore
				synchronized (this) {
					
					for (InterlacerEntry<T> currentEntry : entries.values())
					{
						currentEntry.workingThreads.remove(Thread.currentThread());						
					}
					
					//clean up any projects which are marked done and are empty
					cleanupCompletedProjects();
					
				}			
	
				
			} catch (Exception e) {
				
				Plural.logger().log(Level.SEVERE, "Interlacer error", e);
								
			}
			
		}//while
			
	}
	
	
	/**
	 * Cleans up projects which meet the following criteria:
	 * <ul>
	 * <li>The project has no more jobs left</li>
	 * <li>The project is closed (no more jobs may be added)</li>
	 * <li>The project has no threads working on its jobs presently</li>
	 * </ul>
	 * Once these criteria are met, the project is removed from the set of active
	 * projects, and {@link InterlacerProject#done()} is called
	 */
	private synchronized void cleanupCompletedProjects()
	{

		List<InterlacerEntry<T>> completedEntries = new ArrayList<>();
		
		//all the projects in the set, clean up finished ones
				
		for (InterlacerEntry<T> currentEntry : entries.values())
		{

			//if there are no more jobs left, and it is marked as complete, AND there 
			//are no threads marked as working on it anymore, add it to the list of projects
			//to remove
			if (  
					!currentEntry.project.hasJobs() && 
					currentEntry.project.isClosed() &&
					currentEntry.workingThreads.size() == 0
			) 
			{
				completedEntries.add(currentEntry);
			}
			
		}
		
		
		for (InterlacerEntry<T> currentEntry : completedEntries)
		{
			
			//allow the project to clean up before being removed
			currentEntry.project.done();
			entries.remove(currentEntry.projectName);					
			
		}
		
			
	
	}
	
	
}

class InterlacerEntry<T>
{
	
	public String projectName;
	public int priority;
	public InterlacerProject<T> project;
	public Set<Thread> workingThreads = new HashSet<>();
	
}

class DataProcessorThread<T> extends Thread
{
	
	CooperativeProjectInterlacer<T> dp;
	String title;
	
	public DataProcessorThread(CooperativeProjectInterlacer<T> dp, String title) {
		this.dp = dp;
		this.title = title;
		setDaemon(true);
	}
	
	public void run()
	{
		dp.doWork();
	}
	
	public String toString()
	{
		return title;
	}
	
}

class ProjectNotFoundException extends RuntimeException
{
	public ProjectNotFoundException() {
		super();
	}
	
	public ProjectNotFoundException(String message)	{
		super(message);
	}
}
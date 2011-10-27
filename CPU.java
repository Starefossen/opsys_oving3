/**
 * This class implements functionality associated with the CPU device of the
 * simulated system.
 */
public class CPU implements Constants {
	/** The queue of processes waiting for CPU time */
	private Queue cpuQueue;

	/** A reference to the statistics collector */
	private Statistics statistics;

	/** The amount of max CPU time for any process */
	private long maxCpuTime;

	/** Current process in CPU */
	private Process cp;

	/** How long the process has been current process */
	private long cpTime;

	/** Class name used for debug messages */
	private final static String CLASS_NAME = "CPU";

	/**
	 * Creates a new memory device with the given parameters.
	 * 
	 * @param cpuQueue
	 *            The CPU queue to be used.
	 * @param cpuSize
	 *            Max CPU Time for any process.
	 * @param statistics
	 *            A reference to the statistics collector.
	 */
	public CPU(Queue cpuQueue, long maxCpuTime, Statistics statistics) {
		this.cpuQueue = cpuQueue;
		this.maxCpuTime = maxCpuTime;
		this.statistics = statistics;

		// Set current process to null
		this.cp = null;
		this.cpTime = 0;
	}

	/**
	 * Adds a process to the CPU queue.
	 * 
	 * @param p
	 *            The process to be added.
	 */
	public void insertProcess(Process p) {
		Debug.print(CLASS_NAME, "insertProcess", "PID '"+p.getProcessId()+"'");

		this.cpuQueue.insert(p);
	}

	/**
	 * Load next process in queue into CPU
	 * 
	 * @param timePassed
	 *            Amount of time passed since last update
	 */
	public void nextProcess(long timePassed) {
		Debug.print(CLASS_NAME, "nextProcess", ""+timePassed);

		// There is a processes waiting in the queue
		if (!this.cpuQueue.isEmpty()) {
			
			this.cp = (Process) this.cpuQueue.removeNext();
			this.cpTime = 0;

			Debug.print(CLASS_NAME, "nextProcess", "PID '"+this.cp.getProcessId()+"'");
			
			// Update the process with time passed
			if (timePassed > 0) {
				this.updateCurrentProcess(timePassed);
			}

		// No process waiting in queue
		} else {
			
			// Debug
			Debug.print(CLASS_NAME, "nextProcess", "No found. CPU idle time = '"+timePassed+"'");

			// Log CPU Idle Time
			this.incrementCPUIdleTime(timePassed);

			this.cp = null;
			this.cpTime = 0;
		}
	}

	/**
	 * Update time in CPU for Current Process
	 * 
	 * @param timePassed
	 *            Amount of time passed since last update
	 */
	public void updateCurrentProcess(long timePassed) {
		Debug.print(CLASS_NAME, "updateCurrentProcess", Long.toString(timePassed));
				
		// There is a process currently in the CPU
		if (this.cp != null) {
			
			// Debug
			Debug.print(CLASS_NAME, "updateCurrentProcess", "PID '"+this.cp.getProcessId()+"' remaining time '"+this.cp.getRemainingCPUTime()+"'");
			
			boolean nextProcess = false;
			boolean isFinished = true;
			
			long maxTime = this.min(timePassed, (this.maxCpuTime-this.cpTime), this.cp.getRemainingCPUTime());
			long restTime = timePassed - maxTime;
			
			// Increment max time for process
			this.cp.incrementCPUTime(maxTime);
			this.cpTime += maxTime;
			
			// Max CPU time reached
			if (maxTime == this.maxCpuTime) {
				nextProcess = true;
			}
			
			// Process still not finished
			if (this.cp.getRemainingCPUTime() > 0) {
				isFinished = false;
			}
			
			if (this.cp.getRemainingCPUTime() == 0) {
				nextProcess = true;
				isFinished = true;
			}
			
			if (nextProcess) {
				// Debug
				Debug.print(CLASS_NAME, "updateCurrentProcess", "NEXT PROCESS");
				
				if (!isFinished) {
					// Debug
					Debug.print(CLASS_NAME, "updateCurrentProcess", "PID '"+this.cp.getProcessId()+"' !NOT FIN");
					
					this.insertProcess(this.cp);
				} else {
					// Debug
					Debug.print(CLASS_NAME, "updateCurrentProcess", "PID '"+this.cp.getProcessId()+"' finished");
				}
				
				this.nextProcess(restTime);
			}
		} else {
			nextProcess(timePassed);
		}
	}

	/**
	 * Get the minimum of three values
	 * 
	 * @param time1
	 *            {@code long} 1st time
	 * @param time2
	 *            {@code long} 2nd time
	 * @param time3
	 *            {@code long} 3rd time
	 * @return Returns the minimum {@code long} value
	 */
	private long min(long time1, long time2, long time3) {
		return Math.min(time1, Math.min(time2, time3));
	}

	/**
	 * Update CPU Idle Time
	 */
	public void incrementCPUIdleTime(long time) {
		this.statistics.totalIdleCPUTime += time;
	}

	/**
	 * This method is called when a discrete amount of time has passed.
	 * 
	 * @param timePassed
	 *            The amount of time that has passed since the last call to this
	 *            method.
	 */
	public void timePassed(long timePassed) {
		System.out.println("\n\n-----------------------------------------");
		Debug.print(CLASS_NAME, "timePassed", Long.toString(timePassed));

		// Update some statistics
		this.statistics.cpuQueueLengthTime += this.cpuQueue.getQueueLength() * timePassed;
		if (this.cpuQueue.getQueueLength() > this.statistics.memoryQueueLargestLength) {
			this.statistics.cpuQueueLargestLength = this.cpuQueue.getQueueLength();
		}

		// Get next process if there is no current
		if (this.cp == null) {
			this.incrementCPUIdleTime(timePassed);
			this.nextProcess(0);
			
		// Update current process
		} else {
			this.updateCurrentProcess(timePassed);
		}
		System.out.println("-----------------------------------------\n\n");
	}

	/**
	 * This method is called when a process is exiting the system. The memory
	 * allocated to this process is freed.
	 * 
	 * @param p
	 *            The process that is leaving the system.
	 */
	public void processCompleted(Process p) {
		p.updateStatistics(this.statistics);
	}
}

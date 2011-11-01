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
	private long cpCpuTime;

	/** Refference to the GUI */
	private Gui gui;

	/** Class name used for debug messages */
	private final static String CLASS_NAME = "CPU";

	/**
	 * Creates a new memory device with the given parameters.
	 * 
	 * @param cpuQueue The CPU queue to be used.
	 * @param cpuSize Max CPU Time for any process.
	 * @param statistics A reference to the statistics collector.
	 */
	public CPU(Queue cpuQueue, long maxCpuTime, Statistics statistics, Gui gui,
			Memory memory) {
		this.cpuQueue = cpuQueue;
		this.maxCpuTime = maxCpuTime;
		this.statistics = statistics;
		this.gui = gui;

		// Set current process to null
		this.cp = null;
		this.cpCpuTime = 0;
	}

	/**
	 * Adds a process to the CPU queue.
	 * 
	 * @param p The process to be added.
	 */
	public void insertProcess(Process p) {
		Debug.print(CLASS_NAME, "insertProcess", "PID '" + p.getProcessId()
				+ "'");

		this.cpuQueue.insert(p);
	}

	/**
	 * Load next process in queue into CPU
	 * 
	 * @param timePassed Amount of time passed since last update
	 */
	public void nextProcess(long timePassed) {
		Debug.print(CLASS_NAME, "nextProcess", "" + timePassed);

		// There is a processes waiting in the queue
		if (!this.cpuQueue.isEmpty()) {

			this.cp = (Process) this.cpuQueue.removeNext();
			this.gui.setCpuActive(this.cp);
			this.cpCpuTime = 0;

			Debug.print(CLASS_NAME, "nextProcess",
					"PID '" + this.cp.getProcessId() + "'");

			// Update the process with time passed
			if (timePassed > 0) {
				this.updateCurrentProcess(timePassed);
			}

			// No process waiting in queue
		} else {

			// Debug
			Debug.print(CLASS_NAME, "nextProcess",
					"No found. CPU idle time = '" + timePassed + "'");

			// Log CPU Idle Time
			this.incrementCPUIdleTime(timePassed);
			this.gui.setCpuActive(null);

			this.cp = null;
			this.cpCpuTime = 0;
		}
	}

	/**
	 * Update time in CPU for Current Process
	 * 
	 * @param timePassed Amount of time passed since last update
	 */
	public void updateCurrentProcess(long timePassed) {
		Debug.print(CLASS_NAME, "updateCurrentProcess",
				Long.toString(timePassed));

		// There is a process currently in the CPU
		if (this.cp != null) {

			// Debug
			Debug.print(CLASS_NAME, "updateCurrentProcess",
					"PID '" + this.cp.getProcessId() + "' remaining time '"
							+ this.cp.getRemainingCPUTime() + "'");

			boolean nextProcess = false;
			boolean isFinished = true;

			long maxTime = this.min(timePassed,
					(this.maxCpuTime - this.cpCpuTime),
					this.cp.getRemainingCPUTime());
			long timeRemaining = timePassed - maxTime;

			// Increment max time for process
			this.cp.incrementCPUTime(maxTime);
			this.cpCpuTime += maxTime;

			// Max CPU time reached
			if (this.cpCpuTime == this.maxCpuTime) {
				nextProcess = true;
			}

			// Process still not finished
			if (this.cp.getRemainingCPUTime() > 0) {
				isFinished = false;

				// Process is finished
			} else if (this.cp.getRemainingCPUTime() == 0) {
				nextProcess = true;
				isFinished = true;
			}

			if (nextProcess) {
				// Debug
				Debug.print(CLASS_NAME, "updateCurrentProcess", "NEXT PROCESS");

				if (!isFinished) {
					// Update force process changes count
					this.statistics.nofForcedProcessChanges++;

					// Debug
					Debug.print(CLASS_NAME, "updateCurrentProcess", "PID '"
							+ this.cp.getProcessId() + "' !NOT FIN");

					// Re insert the process at the end of the queue
					this.insertProcess(this.cp);
				} else {
					// Update finished processes count
					this.statistics.nofCompletedProcesses++;

					Debug.print(CLASS_NAME, "updateCurrentProcess", "PID '"
							+ this.cp.getProcessId() + "' finished");
				}

				this.nextProcess(timeRemaining);
			}
		} else {
			this.nextProcess(timePassed);
		}
	}

	/**
	 * Get the minimum of three values
	 * 
	 * @param time1 {@code long} 1st time
	 * @param time2 {@code long} 2nd time
	 * @param time3 {@code long} 3rd time
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
	 * @param timePassed The amount of time that has passed since the last call
	 *            to this method.
	 */
	public void timePassed(long timePassed) {
		System.out.println("\n\n-----------------------------------------");
		Debug.print(CLASS_NAME, "timePassed", Long.toString(timePassed));

		// Update some statistics
		this.statistics.cpuQueueLengthTime += this.cpuQueue.getQueueLength()
				* timePassed;
		if (this.cpuQueue.getQueueLength() > this.statistics.memoryQueueLargestLength) {
			this.statistics.cpuQueueLargestLength = this.cpuQueue
					.getQueueLength();
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
	 * @param p The process that is leaving the system.
	 */
	public void processCompleted(Process p) {
		p.updateStatistics(this.statistics);
	}
}

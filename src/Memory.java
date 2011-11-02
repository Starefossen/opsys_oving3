/**
 * This class implements functionality associated with the memory device of the
 * simulated system.
 */
public class Memory {
	/** The queue of processes waiting for free memory */
	private Queue queue;
	/** The amount of memory in the memory device */
	private long memorySize;
	/** The amount of free memory in the memory device */
	private long freeMemory;

	/**
	 * Creates a new memory device with the given parameters.
	 * 
	 * @param memoryQueue The memory queue to be used.
	 * @param memorySize The amount of memory in the memory device.
	 * @param statistics A reference to the statistics collector.
	 */
	public Memory(Queue memoryQueue, long memorySize) {
		this.queue = memoryQueue;
		this.memorySize = memorySize;
		this.freeMemory = memorySize;
	}

	/**
	 * Returns the amount of memory in the memory device.
	 * 
	 * @return The size of the memory device.
	 */
	public long getMemorySize() {
		return this.memorySize;
	}

	/**
	 * Returns the amount of free memeory
	 * 
	 * @return The size of the free memory.
	 */
	public long getFreeMemorySize() {
		return this.freeMemory;
	}

	/**
	 * Adds a process to the memory queue.
	 * 
	 * @param p The process to be added.
	 */
	public void insertProcess(Process p) {
		queue.insert(p);
	}

	/**
	 * Checks whether or not there is enough free memory to let the first
	 * process in the memory queue proceed to the cpu queue. If there is, the
	 * process that was granted memory is returned, otherwise null is returned.
	 * 
	 * @return Returns next process in memory queue; {@code null} if the queue is 
	 * 			empty.
	 */
	public Process getNextProcess() {
		System.out.println("Memory.getNextProcess()");
		System.out.println("Free memory: "+this.getFreeMemorySize());
		Process p = null;
		
		if (!queue.isEmpty()) {
			p = (Process) queue.getNext();
			if (p.getMemoryNeeded() <= freeMemory) {
				queue.removeNext();
				this.freeMemory -= p.getMemoryNeeded();
				p.leaveMemoryQueue();
			} else {
				p = null;
				System.out.println("Memory out of memory");
			}
		} else {
			System.out.println("No waiting in queue.");
		}
		
		return p;
	}

	/**
	 * This method is called when a discrete amount of time has passed.
	 * 
	 * @param timePassed The amount of time that has passed since the last call
	 *            to this method.
	 */
	public void timePassed(long timePassed) {
		Statistics.memoryQueueLengthTime(queue.getQueueLength(), timePassed);
		Statistics.memoryQueueLenght(queue.getQueueLength());
	}

	/**
	 * This method is called when a process is exiting the system. The memory
	 * allocated to this process is freed.
	 * 
	 * @param p The process that is leaving the system.
	 */
	public void releaseMemory(Process p) {
		freeMemory += p.getMemoryNeeded();
	}
}

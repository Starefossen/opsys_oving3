/**
 * This class implements functionality associated with the CPU device of the
 * simulated system.
 */
public class CPU implements Constants {
	/** */
	private Queue queue;
	/** */
	private Process cp;
	/** */
	private Gui gui;

	/**
	 * 
	 * @param cpuQueue
	 * @param gui
	 */
	public CPU(Queue cpuQueue, Gui gui) {

		this.queue = cpuQueue;
		this.gui = gui;
	}

	/**
	 * 
	 * @return
	 */
	public Process startNextProcess() {

		this.cp = getNextProcess();
		this.gui.setCpuActive(cp); // gui

		return this.cp;
	}

	/**
	 * 
	 * @return
	 */
	public Process stopCurrentProcess() {

		this.gui.setCpuActive(null); // gui

		Process tmp = this.cp;
		this.cp = null;

		return tmp;
	}

	/**
	 * 
	 * @param p
	 */
	public void insertProcess(Process p) {
		queue.insert(p);
	}

	/**
	 * 
	 * @return
	 */
	public Process getNextProcess() {
		if (queue.isEmpty()) {
			return null;
		}

		return (Process) queue.removeNext();
	}

	/**
	 * Check if CPU is idle
	 * 
	 * @return {@code true} ... {@code false} otherwise.
	 */
	public boolean isIdle() {
		return (this.cp == null);
	}

	/**
	 * 
	 * @param timePassed
	 */
	public void timePassed(long timePassed) {
		// register CPU idle time
		if (cp == null) {

			Statistics.cpuIdleTime(timePassed);
		}

		Statistics.cpuQueueLengthTime(queue.getQueueLength(), timePassed);
		Statistics.cpuQueueLength(queue.getQueueLength());
	}

}
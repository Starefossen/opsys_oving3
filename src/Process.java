import java.awt.*;
import java.util.*;

/**
 * This class contains data associated with processes, and methods for
 * manipulating this data as well as methods for displaying a process in the
 * GUI.
 * 
 * You will probably want to add more methods to this class.
 */
public class Process implements Constants {
	/** The ID of the next process to be created */
	private static long nextProcessId = 1;

	/** The font used by all processes */
	private static Font font = new Font("Arial", Font.PLAIN, 10);

	/** The ID of this process */
	private long processId;

	/** The color of this process */
	private Color color;

	/** The amount of memory needed by this process */
	private long memoryNeeded;

	/** The amount of CPU time still needed by this process */
	private long cpuTimeNeeded;

	/** The average time between the need for I/O operations for this process */
	private long avgIoInterval;

	/** The time left until the next time this process needs I/O */
	private long timeToNextIoOperation = 0;

	/** The time that this process has spent waiting in the memory queue */
	private long timeSpentWaitingForMemory = 0;

	/** The time that this process has spent waiting in the CPU queue */
	private long timeSpentInReadyQueue = 0;

	/** The time that this process has spent processing */
	private long timeSpentInCpu = 0;

	/** The time that this process has spent waiting in the I/O queue */
	private long timeSpentWaitingForIo = 0;

	/** The time that this process has spent performing I/O */
	private long timeSpentInIo = 0;

	/** The number of times that this process has been placed in the CPU queue */
	private long nofTimesInReadyQueue = 0;

	/** The number of times that this process has been placed in the I/O queue */
	private long nofTimesInIoQueue = 0;

	/** The global time of the last event involving this process */
	private long timeOfLastEvent;

	/**
	 * Creates a new process with given parameters. Other parameters are
	 * randomly determined.
	 * 
	 * @param memorySize The size of the memory unit.
	 * @param creationTime The global time when this process is created.
	 */
	public Process(long memorySize, long creationTime) {
		// Memory need varies from 100 kB to 25% of memory size
		memoryNeeded = 100 + (long) (Math.random() * (memorySize / 4 - 100));

		// CPU time needed varies from 100 to 10000 milliseconds
		cpuTimeNeeded = 100 + (long) (Math.random() * 9900);

		// Average interval between I/O requests varies from 1% to 25% of CPU
		// time needed
		avgIoInterval = (1 + (long) (Math.random() * 25)) * cpuTimeNeeded / 100;

		// The first and latest event involving this process is its creation
		timeOfLastEvent = creationTime;

		// Assign a process ID
		processId = nextProcessId++;

		// Assign a pseudo-random color used by the GUI
		int red = 64 + (int) ((processId * 101) % 128);
		int green = 64 + (int) ((processId * 47) % 128);
		int blue = 64 + (int) ((processId * 53) % 128);
		color = new Color(red, green, blue);
	}

	/**
	 * Process to String for debugging purposes.
	 */
	public String toString() {
		String res = "process id = '" + processId + "' - ";
		res += "RAM needed = '" + memoryNeeded + "' - ";
		res += "CPU time needed = '" + cpuTimeNeeded + "' - ";
		res += "avg IO interval = '" + avgIoInterval + "' - ";
		res += "time of last event = '" + timeOfLastEvent + "'";

		return res;
	}

	/**
	 * Draws this process as a colored box with a process ID inside.
	 * 
	 * @param g The graphics context.
	 * @param x The leftmost x-coordinate of the box.
	 * @param y The topmost y-coordinate of the box.
	 * @param w The width of the box.
	 * @param h The height of the box.
	 */
	public void draw(Graphics g, int x, int y, int w, int h) {
		g.setColor(color);
		g.fillRect(x, y, w, h);
		g.setColor(Color.black);
		g.drawRect(x, y, w, h);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics(font);
		g.drawString("" + processId, x + w / 2 - fm.stringWidth("" + processId)
				/ 2, y + h / 2 + fm.getHeight() / 2);
	}

	/**
	 * Get unique ID for the process
	 * 
	 * @return {@code long} unique identificator
	 */
	public long getProcessId() {
		return this.processId;
	}

	/**
	 * This method is called when the process leaves the memory queue (and
	 * enters the CPU queue).
	 * 
	 * @param clock The time when the process leaves the memory queue.
	 */
	public void leftMemoryQueue(long clock) {
		timeSpentWaitingForMemory += clock - timeOfLastEvent;
		timeOfLastEvent = clock;
	}

	/**
	 * Get amount of memory needed.
	 * 
	 * @return The a {@code long} amount of memory needed by this process.
	 */
	public long getMemoryNeeded() {
		System.out.println("Memory needed: " + memoryNeeded);
		return memoryNeeded;
	}

	/**
	 * Set time spent in CPU
	 * 
	 * @param time Time spent in CPU
	 * 
	 * @return Returns a positive {@code long} if there are remaining time;
	 *         negative {@code long} if there was a rest time where the CPU was
	 *         idle.
	 */
	public long incrementCPUTime(long time) {
		// "Remaining" time
		long tmpTime = this.cpuTimeNeeded - (this.timeSpentInCpu + time);

		if (tmpTime < 0) {
			// Update time spent
			this.timeSpentInCpu += time - Math.abs(tmpTime);

			// Return rest which was idle CPU time after the process finished
			return tmpTime;
		} else {
			// Update time spent
			this.timeSpentInCpu += time;

			return this.getRemainingCPUTime();
		}
	}

	/**
	 * Get remaining time needed for this process
	 * 
	 * @return Returns a positive {@code long} of remaining time needed
	 */
	public long getRemainingCPUTime() {
		return this.cpuTimeNeeded - this.timeSpentInCpu;
	}

	/**
	 * Updates the statistics collected by the given Statistic object, adding
	 * data collected by this process. This method is called when the process
	 * leaves the system.
	 * 
	 * @param statistics The Statistics object to be updated.
	 */
	public void updateStatistics(Statistics statistics) {
		statistics.totalTimeSpentWaitingForMemory += timeSpentWaitingForMemory;
	}

	// Add more methods as needed
}
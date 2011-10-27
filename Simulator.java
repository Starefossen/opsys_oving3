import java.io.*;

/**
 * The main class of the P3 exercise. This class is only partially complete.
 */
public class Simulator implements Constants {
	/** The queue of events to come */
	private EventQueue eventQueue;

	/** Reference to the memory unit */
	private Memory memory;

	/** Reference to the CPU unit */
	private CPU cpu;

	/** Reference to the GUI interface */
	private Gui gui;

	/** Reference to the statistics collector */
	private Statistics statistics;

	/** The global clock */
	private long clock;

	/** The length of the simulation */
	private long simulationLength;

	/** The average length between process arrivals */
	private long avgArrivalInterval;

	/** Class name used for debug messages */
	private final static String CLASS_NAME = "Simulator";

	/**
	 * Constructs a scheduling simulator with the given parameters.
	 * 
	 * @param memoryQueue
	 *            The memory queue to be used.
	 * @param cpuQueue
	 *            The CPU queue to be used.
	 * @param ioQueue
	 *            The I/O queue to be used.
	 * @param memorySize
	 *            The size of the this.memory.
	 * @param maxCpuTime
	 *            The maximum time quant used by the RR algorithm.
	 * @param avgIoTime
	 *            The average length of an I/O operation.
	 * @param simulationLength
	 *            The length of the simulation.
	 * @param avgArrivalInterval
	 *            The average time between process arrivals.
	 * @param gui
	 *            Reference to the GUI interface.
	 */
	public Simulator(Queue memoryQueue, Queue cpuQueue, Queue ioQueue,
			long memorySize, long maxCpuTime, long avgIoTime,
			long simulationLength, long avgArrivalInterval, Gui gui) {

		// System clock
		this.clock = 0;

		// Simulation length
		this.simulationLength = simulationLength;

		// Average Arrival Interval
		this.avgArrivalInterval = avgArrivalInterval;

		// GUI Reference
		this.gui = gui;

		// Statistics
		this.statistics = new Statistics();

		// Event Queue
		this.eventQueue = new EventQueue();

		// RAM
		this.memory = new Memory(memoryQueue, memorySize, this.statistics);

		// CPU
		this.cpu = new CPU(cpuQueue, maxCpuTime, this.statistics);
	}

	/**
	 * Starts the simulation. Contains the main loop, processing events. This
	 * method is called when the "Start simulation" button in the GUI is
	 * clicked.
	 */
	public void simulate() {
		Debug.print(CLASS_NAME, "simulate", "Starting simulation...");

		// Generate the first process arrival event
		eventQueue.insertEvent(new Event(NEW_PROCESS, 0));

		// Process events until the simulation length is exceeded:
		// HERE IS WHERE THE ACTION IS DONE!!!!
		while (clock < simulationLength && !eventQueue.isEmpty()) {
			Debug.print(CLASS_NAME, "simulate", String.valueOf(this.clock));

			// Find the next event
			Event event = eventQueue.getNextEvent();

			// Find out how much time that passed...
			long timeDifference = event.getTime() - this.clock;

			// Debug info
			Debug.print(CLASS_NAME, "simulate", "type = '" + event.getType()
					+ "' - time = '" + event.getTime() + "' - time passed = '"
					+ timeDifference + "'");

			// ...and update the clock.
			clock = event.getTime();

			// Let the RAM know that time has passed
			this.memory.timePassed(timeDifference);

			// Let the GUI know that time has passed
			this.gui.timePassed(timeDifference);

			// Let CPU know that time has passed
			this.cpu.timePassed(timeDifference);

			// Let IO know that time has passed
			// this.io.timePassed(timeDifference);

			// Deal with the event
			if (clock < simulationLength) {
				processEvent(event);
			}

			// Note that the processing of most events should lead to new
			// events being added to the event queue!

			// TODO: You may want to extend this method somewhat.
		}
		System.out.println("..done.");
		// End the simulation by printing out the required statistics
		statistics.printReport(simulationLength);
	}

	/**
	 * Processes an event by inspecting its type and delegating the work to the
	 * appropriate method.
	 * 
	 * @param event
	 *            The event to be processed.
	 */
	private void processEvent(Event event) {
		switch (event.getType()) {
		case NEW_PROCESS:
			createProcess();
			break;
		case SWITCH_PROCESS:
			switchProcess();
			break;
		case END_PROCESS:
			endProcess();
			break;
		case IO_REQUEST:
			processIoRequest();
			break;
		case END_IO:
			endIoOperation();
			break;
		}
	}

	/**
	 * Simulates a process arrival/creation.
	 */
	private void createProcess() {
		Debug.print(CLASS_NAME, "createProcess", "Called");

		// Create a new process
		Process newProcess = new Process(this.memory.getMemorySize(), clock);
		this.memory.insertProcess(newProcess);

		// Transfer process from memory to ready queue
		flushMemoryQueue();

		// Get time for next process arrival
		long nextArrivalTime = getNextTime();

		// Add an event for the next process arrival
		eventQueue.insertEvent(new Event(NEW_PROCESS, nextArrivalTime));

		// Update statistics
		statistics.nofCreatedProcesses++;
	}

	/**
	 * Get time for next event
	 * 
	 * @return random time greater then current time for a new event
	 */
	private long getNextTime() {
		return this.clock + 1 + (long) (2 * Math.random() * this.avgArrivalInterval);
	}

	/**
	 * Transfers processes from the memory queue to the ready queue as long as
	 * there is enough memory for the processes.
	 */
	private void flushMemoryQueue() {
		Debug.print(CLASS_NAME, "flushMemoryQueue", "Called");

		Process p = this.memory.checkMemory(clock);

		// As long as there is enough memory, processes are moved from the
		// memory queue to the CPU queue
		while (p != null) {
			Debug.print(CLASS_NAME, "flushMemoryQueue", "Process found");

			// Add this process to the CPU queue!
			this.cpu.insertProcess(p);

			// Also add new events to the event queue if needed
			// TODO: Do this

			// Since we haven't implemented the CPU and I/O device yet,
			// we let the process leave the system immediately, for now.
			this.cpu.processCompleted(p);
			this.memory.processCompleted(p);

			// Try to use the freed memory:
			flushMemoryQueue();

			// Update statistics
			p.updateStatistics(statistics);

			// Check for more free memory
			p = this.memory.checkMemory(clock);
		}
	}

	/**
	 * Simulates a process switch.
	 */
	private void switchProcess() {
		Debug.print(CLASS_NAME, "switchProcess", "Called");
		// Incomplete
	}

	/**
	 * Ends the active process, and deallocates any resources allocated to it.
	 */
	private void endProcess() {
		Debug.print(CLASS_NAME, "endProcess", "Called");
		// Incomplete
	}

	/**
	 * Processes an event signifying that the active process needs to perform an
	 * I/O operation.
	 */
	private void processIoRequest() {
		Debug.print(CLASS_NAME, "processIoRequest", "Called");
		// Incomplete
	}

	/**
	 * Processes an event signifying that the process currently doing I/O is
	 * done with its I/O operation.
	 */
	private void endIoOperation() {
		Debug.print(CLASS_NAME, "endIoOperation", "Called");
		// Incomplete
	}

	/**
	 * Reads a number from the an input reader.
	 * 
	 * @param reader
	 *            The input reader from which to read a number.
	 * @return The number that was inputed.
	 */
	public static long readLong(BufferedReader reader) {
		try {
			return Long.parseLong(reader.readLine());
		} catch (IOException ioe) {
			return 100;
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	/**
	 * The startup method. Reads relevant parameters from the standard input,
	 * and starts up the GUI. The GUI will then start the simulation when the
	 * user clicks the "Start simulation" button.
	 * 
	 * @param args
	 *            Parameters from the command line, they are ignored.
	 */
	public static void main(String args[]) {
		if (!TESTING_ENABLED) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			System.out.println("Please input system parameters: ");

			System.out.print("Memory size (KB): ");
			long memorySize = readLong(reader);
			while (memorySize < 400) {
				System.out
						.println("Memory size must be at least 400 KB. Specify memory size (KB): ");
				memorySize = readLong(reader);
			}

			System.out
					.print("Maximum uninterrupted cpu time for a process (ms): ");
			long maxCpuTime = readLong(reader);

			System.out.print("Average I/O operation time (ms): ");
			long avgIoTime = readLong(reader);

			System.out.print("Simulation length (ms): ");
			long simulationLength = readLong(reader);
			while (simulationLength < 1) {
				System.out
						.println("Simulation length must be at least 1 ms. Specify simulation length (ms): ");
				simulationLength = readLong(reader);
			}

			System.out.print("Average time between process arrivals (ms): ");
			long avgArrivalInterval = readLong(reader);

			SimulationGui gui = new SimulationGui(memorySize, maxCpuTime,
					avgIoTime, simulationLength, avgArrivalInterval);
		} else {
			SimulationGui gui = new SimulationGui(TESTING_MEMORY_SIZSE,
					TESTING_CPU_TIME, TESTING_IO_TIME,
					TESTING_SIMULATION_LENGTH, TESTING_AVG_ARRIVAL_INTERVAL);
		}
	}
}

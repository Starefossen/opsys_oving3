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
	
	/** Reference to the IO unit */
	private IO io;
	
	/** Reference to the GUI interface */
	private Gui gui;

	/** Max CPU time */
	private long maxCpuTime;

	/** The length of the simulation */
	private long simulationLength;

	/** The average length between process arrivals */
	private long avgProcessArrival;

	/** Class name used for debug messages */
	private final static String CLASS_NAME = "Simulator";
	private IO io;

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

		this.simulationLength = simulationLength;
		this.avgProcessArrival = avgArrivalInterval;
		this.gui = gui;

		this.eventQueue = new EventQueue();
		this.memory = new Memory(memoryQueue, memorySize);
		this.cpu = new CPU(cpuQueue, maxCpuTime, this.gui, this.memory);
		//this.io = new IO(ioQueue);
	}

	/**
	 * Starts the simulation. Contains the main loop, processing events. This
	 * method is called when the "Start simulation" button in the GUI is
	 * clicked.
	 */
	public void simulate() {
		Debug.print(CLASS_NAME, "simulate", "Starting simulation...");

		// Generate the first process arrival event
		this.newEvent(NEW_PROCESS, 0);

		while (SystemClock.getTime() < simulationLength && !eventQueue.isEmpty()) {

			// Get next event in queue
			Event event = eventQueue.getNextEvent();

			// Handle event time
			long eventTime = event.getTime();
			long timePassed = eventTime - SystemClock.getTime();
			SystemClock.setTime(eventTime);

			// Update time passed
			this.memory.timePassed(timePassed);
			//this.io.timePassed(timeDifference);
			this.cpu.timePassed(timePassed);
			this.gui.timePassed(timePassed, this.memory.getFreeMemorySize());

			// Let IO know that time has passed
			//this.io.timePassed(timeDifference);
			// Deal with the event
			if (eventTime < simulationLength) {
				processEvent(event);
			}
		}
		System.out.println("..done.");
		Statistics.printReport(simulationLength);
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
			createNewProcess();
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
	private void createNewProcess() {
		Process newProcess = new Process(this.memory.getMemorySize());
		
		this.memory.insertProcess(newProcess);

		// Transfer process from memory to ready queue
		flushMemoryQueue();

		long arrivalTime = getNextProcessArrivalTime();
		eventQueue.insertEvent(new Event(NEW_PROCESS, arrivalTime));

		// Update statistics
		Statistics.processCreated();
	}

	/**
	 * Create next event for new process
	 * @param p
	 */
	private void createEventForProcess(Process p) {
		long processRemainingTime = p.getRemainingCPUTime();
		long maxCpuTime = this.maxCpuTime;
		long processNextIO = p.getTimeToNextIoOperation();
		
		if (processRemainingTime < maxCpuTime && processRemainingTime < processNextIO) {
			// Process is finished
			this.newEvent(END_PROCESS, processRemainingTime);
		} else if (processRemainingTime > maxCpuTime && processRemainingTime < processNextIO) {
			// Process max time in CPU exceeded
			this.newEvent(SWITCH_PROCESS, maxCpuTime);
		} else {
			// Process needs to perform IO operation
			this.newEvent(IO_REQUEST, processNextIO);
		}
	}
	
	/**
	 * 
	 */
	private void newEvent(int EVENT, long time) {
		long eventTime = SystemClock.getTime()+time;
		eventQueue.insertEvent(new Event(EVENT, eventTime));
	}
	
	/**
	 * Get time for next event
	 * 
	 * @return random time greater then current time for a new event
	 */
	private long getNextProcessArrivalTime() {
		long time = SystemClock.getTime();
		long rand = (long) (2 * Math.random() * this.avgProcessArrival);
		return time + 1 + rand;
	}

	/**
	 * Transfers processes from the memory queue to the ready queue as long as
	 * there is enough memory for the processes.
	 */
	private void flushMemoryQueue() {
		Process p = this.memory.getNextProcess();

		while (p != null) {
			this.cpu.insertProcess(p);
			p.updateStatistics();
			p = this.memory.getNextProcess();
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

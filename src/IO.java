/**
 * This class implements functionality associated with the IO device of the
 * simulated system.
 */
/**
 * This class implements functionality associated with the CPU device of the
 * simulated system.
 */
public class IO implements Constants {
	
	private Queue queue;
	
	private Process cp;

	private Gui gui;
	
	public IO(Queue ioQueue, Gui gui) {
		
		this.queue = ioQueue;
		this.gui = gui;
	}

	public Process startNextProcess() {
		
		cp = getNextProcess();
		gui.setCpuActive(cp); // gui
	
		return cp;		
	}
	
	public Process stopCurrentProcess() {
		
		gui.setIoActive(null); // gui
		
		Process tmp = cp;
		cp = null;
		return tmp;
	}
	
	public void insertProcess(Process p) {
		queue.insert(p);
	}
	
	public Process getNextProcess() {
		
		return (Process) queue.removeNext();
	}
	
	public void timePassed(long timePassed) {
		
		// register IO idle time
		if(cp == null) {
			
			Statistics.ioIdleTime(timePassed);
		}
			
		Statistics.ioQueueLengthTime(queue.getQueueLength(), timePassed);
		Statistics.ioQueueLength(queue.getQueueLength());
	}
	
}
/**
 * This class implements functionality associated with the CPU device of the
 * simulated system.
 */
public class CPU implements Constants {
	
	private Queue queue;
	
	private Process cp;

	private Gui gui;
	
	public CPU(Queue cpuQueue, Gui gui) {
		
		this.queue = cpuQueue;
		this.gui = gui;
	}

	public Process startNextProcess() {
		
		cp = getNextProcess();
		gui.setCpuActive(cp); // gui
	
		return cp;		
	}
	
	public Process stopCurrentProcess() {
		
		gui.setCpuActive(null); // gui
		
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
		
		// register cpu idle time
		if(cp == null) {
			
			Statistics.cpuIdleTime(timePassed);
		}
			
		Statistics.cpuQueueLengthTime(queue.getQueueLength(), timePassed);
		Statistics.cpuQueueLength(queue.getQueueLength());
	}
	
}
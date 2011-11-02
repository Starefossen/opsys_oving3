/**
 * 
 * @author hanskristian
 *
 */
public class SystemClock {
	/**
	 * 
	 */
	private static long time = 0;
	
	/**
	 * 
	 * @param newTime
	 */
	public static void setTime(long newTime) {
		time = newTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public static long getTime() {
		return time;
	}
}

import java.util.Arrays;

/**
 * Debug message class
 * 
 * @author hanskristian
 * 
 */
public class Debug implements Constants {
	public static void print(String className, String method, String msg) {
		String[] accepted = new String[] { "SimulatorX", "Process",
				"CPU", "IO", "Memory", "Statistics" };

		if (TESTING_ENABLED && Arrays.asList(accepted).contains(className)) {
			System.out.println("DEBUG: " + className + "." + method + "() '"
					+ msg + "'");
		}
	}
}

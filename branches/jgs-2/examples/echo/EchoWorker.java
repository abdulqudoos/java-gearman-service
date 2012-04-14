package echo;

import java.io.IOException;

import org.gearman.Gearman;
import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.gearman.GearmanWorker;

public class EchoWorker implements GearmanFunction {
	
	public static final String ECHO_FUNCTION_NAME = "echo";
	public static final String ECHO_HOST = "localhost";
	public static final int ECHO_PORT = 4730;

	public static void main(String... args) throws IOException {
		/** Create a new gearman instance */
		Gearman gearman = Gearman.createGearman();
		
		/** Create a gearman worker */
		GearmanWorker worker = gearman.createGearmanWorker();
		
		/** Tell the worker how to perform the echo function */
		worker.addFunction(EchoWorker.ECHO_FUNCTION_NAME, new EchoWorker());
		
		/** Add a server to communicate with to the worker */
		worker.addServer(gearman.createGearmanServer(EchoWorker.ECHO_HOST, EchoWorker.ECHO_PORT));
	}
	
	
	@Override
	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception {
		
		
		
		return data;
	}

}

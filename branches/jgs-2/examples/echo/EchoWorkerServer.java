package echo;

import java.io.IOException;

import org.gearman.Gearman;
import org.gearman.GearmanWorker;

public class EchoWorkerServer {
	public static void main(String...args) throws IOException {
		Gearman gearman = Gearman.createGearman();
		
		try {
			
			GearmanWorker worker = gearman.createGearmanWorker();
			worker.addFunction(EchoWorker.ECHO_FUNCTION_NAME, new EchoWorker());
			worker.addServer(gearman.createGearmanServer(EchoWorker.ECHO_PORT));
			
		} catch (IOException ioe) {
			gearman.shutdown();
			throw ioe;
		}
	}
}

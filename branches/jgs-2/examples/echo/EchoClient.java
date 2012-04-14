package echo;

import java.nio.charset.Charset;

import org.gearman.Gearman;
import org.gearman.GearmanClient;
import org.gearman.GearmanJobEvent;
import org.gearman.GearmanJobReturn;

public class EchoClient {
	
	public static void main(String...args) throws InterruptedException {
		Gearman gearman = Gearman.createGearman();
		
		GearmanClient client = gearman.createGearmanClient();
		client.addServer(gearman.createGearmanServer(EchoWorker.ECHO_HOST, EchoWorker.ECHO_PORT));
		
		
		GearmanJobReturn jobReturn = client.submitJob(EchoWorker.ECHO_FUNCTION_NAME, "blah".getBytes());
		
		while(!jobReturn.isEOF()) {
			GearmanJobEvent event = jobReturn.poll();
			System.out.println(event.getEventType() + ": " + new String(event.getData(), Charset.forName("UTF-8")));
		}
		
		gearman.shutdown();
	}
}

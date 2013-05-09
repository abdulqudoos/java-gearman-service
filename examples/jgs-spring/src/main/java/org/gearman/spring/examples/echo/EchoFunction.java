package org.gearman.spring.examples.echo;

import org.gearman.GearmanFunction;
import org.gearman.GearmanFunctionCallback;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Illustrates how to setup a worker and function using jgs-spring
 * @author isaiah
 *
 */
public class EchoFunction implements GearmanFunction {

	public static void main(String... args) {
		// the context defines how to setup and teardown gearman (path: src/main/resources/worker.xml)  
		new ClassPathXmlApplicationContext("classpath:worker.xml");
	}
	
	@Override
	public byte[] work(String function, byte[] data, GearmanFunctionCallback callback) throws Exception {
		return data;
	}
}

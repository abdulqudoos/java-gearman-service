package org.gearman.spring.examples.echo;

import org.gearman.GearmanClient;
import org.gearman.GearmanJobReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.gearman.helpers.GearmanClientUtils.*;

public class EchoClient {

	@Autowired
	private GearmanClient client;

	private String functionName;
	private String data;

	public static void main(String... args) throws Exception {
		
		// the context defines how to setup and teardown gearman (path: src/main/resources/client.xml)
		try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:client.xml")) {

			// poll the client bean
			EchoClient client = (EchoClient) context.getBean("echoClient");

			// run the submitting process
			client.submitJob();
		}
	}

	public void submitJob() throws Exception {
		// submit the job
		GearmanJobReturn jobReturn = client.submitJob(functionName, data.getBytes());

		// poll the result
		byte[] value = simpleJobReturnHandler(jobReturn);
		
		// print the result
		System.out.println(new String(value));
	}

	@Required
	public void setFunctionName(String functionName) {
		// set by the spring framework
		this.functionName = functionName;
	}

	@Required
	public void setData(String data) {
		// set by the spring framework
		this.data = data;
	}
}

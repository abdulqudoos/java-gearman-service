package org.gearman.spring;

import org.gearman.GearmanFunction;
import org.springframework.beans.factory.annotation.Required;

/**
 * A bean that defines a function. It contains the function name and function implementation 
 * @author isaiah
 */
public class GearmanFunctionBean {
	
	private String name;
	private GearmanFunction function;
	
	public GearmanFunction getFunction() {
		return function;
	}

	@Required
	public void setFunction(GearmanFunction function) {
		this.function = function;
	}

	public String getName() {
		return name;
	}
	
	@Required
	public void setName(String functionName) {
		this.name = functionName;
	}
}

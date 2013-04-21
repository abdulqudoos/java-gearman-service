package org.gearman.spring;

import org.gearman.GearmanFunction;
import org.springframework.beans.factory.annotation.Required;

/**
 * A function name and {@link GearmanFunction} bean 
 * @author isaiah
 */
public class GearmanFunctionBean {
	
	private String functionName;
	private GearmanFunction function;
	
	public GearmanFunction getFunction() {
		return function;
	}

	@Required
	public void setFunction(GearmanFunction function) {
		this.function = function;
	}

	public String getFunctionName() {
		return functionName;
	}
	
	@Required
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	
}

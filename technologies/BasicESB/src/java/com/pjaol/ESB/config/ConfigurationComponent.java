/*******************************************************************************
 * Copyright 2012 Patrick O'Leary
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.pjaol.ESB.config;

import java.util.List;
import java.util.Map;

public class ConfigurationComponent {
	
	private String name, className, uri;
	private int timeout = -1;
	private Map<String, String> args;
	
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Map<String, String> getArgs() {
		return args;
	}
	public void setArgs(Map<String, String> args) {
		this.args = args;
	}

	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	@Override	
	public String toString(){
		return "{ name:"+getName()
				+", class:"+getClassName()
				+", timeout:"+getTimeout()
				+", args:{"+getArgs()+"} ";
	}
	
}

/**
 * PipeLineComponent used for configuration of pipelines
 * @author pjaol
 *
 */
class PipeLineComponent extends ConfigurationComponent{

	private EvaluatorComponent evaluator;	
	private List<ConfigurationComponent> modules;
	
	public PipeLineComponent(){}
	
	public List<ConfigurationComponent> getModules() {
		return modules;
	}

	public void setModules(List<ConfigurationComponent> modules) {
		this.modules = modules;
	}



	public EvaluatorComponent getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(EvaluatorComponent evaluator) {
		this.evaluator = evaluator;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("PipeLineComponent: {");
		sb.append(super.toString());
		sb.append("}");
		sb.append(getEvaluator()+" ");
		sb.append(getModules());
		return sb.toString();
	}
	
}

/**
 * ControllerComponent
 * used for configuration of the controllers
 * 
 * @author pjaol
 *
 */
class ControllerComponent extends ConfigurationComponent{
	
	private Map<String, List<String>> pipelines;
	private List<String> pipes;
	
	private List<String> limiterPipeLines;
	private String limiterName;
	
	//TODO: is there a better way to get this?
	private String className = "com.pjaol.ESB.core.Controller"; 
	

	public ControllerComponent(){}
	
	public Map<String, List<String>> getPipelines() {
		return pipelines;
	}
	public void setPipelines(Map<String, List<String>> pipelines) {
		this.pipelines = pipelines;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ControllerComponent:{");
		sb.append(super.toString());
		sb.append(getPipelines());
		sb.append("}");
		
		return sb.toString();
	}

	public void setPipes(List<String> pipes) {
		this.pipes = pipes;
	}

	public List<String> getPipes() {
		return pipes;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public void setClassName(String className) {
		if (className != null)
			this.className = className;
	}


	public void setLimiterName(String limiterName) {
		this.limiterName = limiterName;
	}
	
	public String getLimiterName(){
		return limiterName;
	}

	public void setLimiterPipeLines(List<String> limiterPipeLines) {
		this.limiterPipeLines = limiterPipeLines;
	}
	
	public List<String>getLimiterPipeLines(){
		return limiterPipeLines;
	}


}

class EvaluatorComponent extends ConfigurationComponent{
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(", EvaluatorComponent:{");
		sb.append(super.toString());
		sb.append("}");
		return sb.toString();
	}
}

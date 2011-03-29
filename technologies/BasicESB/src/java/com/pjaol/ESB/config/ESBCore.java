package com.pjaol.ESB.config;

import java.util.HashMap;
import java.util.Map;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.core.Controller;
import com.pjaol.ESB.core.PipeLine;


public class ESBCore {
	
	private static ESBCore core;
	
	private Map<String, String> globals = new HashMap<String, String>();
	private Map<String, PipeLine> pipelines;
	private Map<String, Controller> controllers;
	private Map<String, Controller> uris;
	
	private Map<String, PipeLineComponent> pipeLineComponent;
	private Map <String, ControllerComponent> controllerComponent;
	

	private ESBCore(){}
	
	
	public static ESBCore getInstance(){
		if (core == null)
			core = new ESBCore();
		
		return core;
	}
	
	
	protected void setPipelines(Map<String, PipeLine> pipelines){
		this.pipelines = pipelines;
	}
	
	/**
	 * Get a named pipeline, names should come from controllers
	 * Has the potential of being called from any part of the system, maybe good or bad
	 * will need to see
	 * {@link PipeLine}
	 * @param name
	 * @return
	 * @throws ModuleRunException 
	 */
	public PipeLine getPipeLineByName(String name) throws ModuleRunException{
		
		PipeLine result =pipelines.get(name);
		if (result == null)
			throw new ModuleRunException("Invalid pipeline name: "+ name);
		
		
		return result;
	}


	public Map<String, PipeLineComponent> getPipeLineComponent() {
		return pipeLineComponent;
	}


	public void setPipeLineComponent(
			Map<String, PipeLineComponent> pipeLineComponent) {
		this.pipeLineComponent = pipeLineComponent;
	}


	public Map<String, ControllerComponent> getControllerComponent() {
		return controllerComponent;
	}


	public void setControllerComponent(
			Map<String, ControllerComponent> controllerComponent) {
		this.controllerComponent = controllerComponent;
	}


	public void setControllers(Map<String, Controller> controllers) {
		this.controllers = controllers;
	}


	public void setControllerUris(Map<String, Controller> uris) {
		this.uris = uris;
	}


	public Map<String, String> getGlobals() {
		return globals;
	}


	public void setGlobals(Map<String, String> globals) {
		this.globals = globals;
	}
	
	
	public Controller getControllerByUri(String uri){
		return uris.get(uri);
	}
	
	public Controller getControllerByName(String name){
		return controllers.get(name);
	}
}

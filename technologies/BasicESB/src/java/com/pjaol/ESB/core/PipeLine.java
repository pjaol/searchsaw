package com.pjaol.ESB.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.monitor.CounterMonitor;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;

public class PipeLine extends Module{
	
	List<Module> modules = new ArrayList<Module>();
	private Evaluator evaluator;
	private int timeout;
	
	public List<Module> getModules() {
		return modules;
	}


	public void setModules(List<Module> modules) {
		this.modules = modules;
	}


	public Evaluator getEvaluator() {
		return evaluator;
	}


	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}


	@Override
	public NamedList process(NamedList input) throws ModuleRunException {
		
		NamedList result = input;
		
		for(Module module: modules){
			NamedList moduleResult = module.process(input);
			result.add(module.getName(), moduleResult);
		}
		
			
		return result;
	}


	@Override
	public void init(Map args) {
		
		
	}


	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	public int getTimeout() {
		return timeout;
	}

}

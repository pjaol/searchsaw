package com.pjaol.ESB.core;

import java.util.Map;

import org.apache.solr.common.util.NamedList;


/**
 * Evaluators enable modules to be run
 * The evaluate and init methods should be overwritten 
 * @author pjaol
 *
 */
public class Evaluator {
	
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * default returns true
	 * @param input
	 * @return
	 */
	public boolean evaluate(@SuppressWarnings("rawtypes") NamedList input){
		return true;
	}

	
	
	public void init(Map<String, String> args) {
		
	}
	
	 
}

package com.pjaol.ESB.core;

import java.util.Map;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;

public abstract class Module {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public abstract NamedList process(NamedList input) throws ModuleRunException;
	public abstract void init(Map args);
	
}

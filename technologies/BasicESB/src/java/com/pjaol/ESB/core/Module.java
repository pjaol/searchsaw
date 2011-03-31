package com.pjaol.ESB.core;

import java.util.Map;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.monitor.MonitorBean;

public abstract class Module {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Hook into the monitoring beans of {@link MonitorBean}
	 * A good example lies in {@link Controller}
	 */
	public abstract void initializeMonitor();
	
	@SuppressWarnings("rawtypes")
	public abstract NamedList process(NamedList input) throws Exception;
	public abstract void init(Map<String, String> args);
	
}

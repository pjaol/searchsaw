package com.pjaol.ESB.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleNonCriticalException;
import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.monitor.Monitor;
import com.pjaol.ESB.monitor.MonitorBean;
import com.pjaol.ESB.monitor.TYPE;

public class PipeLine extends Module{
	
	List<Module> modules = new ArrayList<Module>();
	private Evaluator evaluator;
	private int timeout;
	private Map<String, MonitorBean> prefBeans = new HashMap<String, MonitorBean>();
	private Map<String, MonitorBean> errorBeans = new HashMap<String, MonitorBean>();
	private Monitor monit = Monitor.getInstance();
	
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


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public NamedList process(NamedList input) throws Exception {
		
		NamedList result = input;
		long start = System.currentTimeMillis();
		
		
		for(Module module: modules){
			NamedList moduleResult = null;
			MonitorBean pbean = prefBeans.get(module.getName());
			MonitorBean ebean = errorBeans.get(module.getName());
			
			try {
				moduleResult = module.process(input);
			}catch (ModuleNonCriticalException e){
				// non-critical exception
				ebean.inc(1);
				e.printStackTrace();
			} catch (Exception e){
				ebean.inc(1);
				throw e; // everything else should bubble up
			}finally{
				long now = System.currentTimeMillis();
				pbean.inc(new Long(now -start).intValue());
				start= now; // ok a couple of ticks will slip, but saves a double call to clock
			}
			

			result.add(module.getName(), moduleResult);
		}
		
			
		return result;
	}


	@Override
	public void init(Map<String, String> args) {
		
		
	}


	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}


	public int getTimeout() {
		return timeout;
	}


	@Override
	public void initializeMonitor() {

		for(Module m: modules){
			String n = "pipe-"+getName()+"-mod-"+m.getName();
			MonitorBean pbean = new MonitorBean("pref-"+n, TYPE.MODULE, "performance");
			
			MonitorBean ebean = new MonitorBean("error-"+n, TYPE.ERROR, "error");
			prefBeans.put(m.getName(), pbean);
			errorBeans.put(m.getName(), ebean);
			
			try {
				monit.setBean("pref-"+n, pbean);
				monit.setBean("error-"+n,ebean);
			} catch (MalformedObjectNameException e) {
				e.printStackTrace();
			} catch (InstanceAlreadyExistsException e) {
				e.printStackTrace();
			} catch (MBeanRegistrationException e) {
				e.printStackTrace();
			} catch (NotCompliantMBeanException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

}

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
package com.pjaol.ESB.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.apache.log4j.Logger;
import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleNonCriticalException;
import com.pjaol.ESB.monitor.Monitor;
import com.pjaol.ESB.monitor.MonitorBean;
import com.pjaol.ESB.monitor.TYPE;
import com.pjaol.oji.utils.TimerThread;

public class PipeLine extends Module{
	
	List<Module> modules = new ArrayList<Module>();
	private Evaluator evaluator;
	private int timeout;
	private Map<String, MonitorBean> prefBeans = new HashMap<String, MonitorBean>();
	private Map<String, MonitorBean> errorBeans = new HashMap<String, MonitorBean>();
	private Monitor monit = Monitor.getInstance();
	private Logger _logger = Logger.getLogger(getClass());
	
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
		
		if (!evaluator.evaluate(input)){
			return null;
		}
			
		TimerThread timer = new TimerThread(getTimeout(), getName(), "pipeline"){
			@Override
			public void timeout() {
				
				super.timeout();
				throw new RuntimeException("Timed out pipeline: "+getName());
			}
		};
		
		
		NamedList inputResult = input; // Input & Output combined
		NamedList result = new NamedList(); // Result returned from pipeline
		
		
		long start = System.currentTimeMillis();
		
		
		for(Module module: modules){
			NamedList moduleResult = null;
			MonitorBean pbean = prefBeans.get(module.getName());
			MonitorBean ebean = errorBeans.get(module.getName());
			
			try {
				moduleResult = module.process(input);
				//System.out.println("****:pipeline:"+getName()+"::"+module.getName()+"::"+moduleResult);
			}catch (ModuleNonCriticalException e){
				// non-critical exception
				ebean.inc(1);
				_logger.error(e);
			} catch (Exception e){
				ebean.inc(1);
				throw e; // everything else should bubble up
			}finally{
				long now = System.currentTimeMillis();
				pbean.inc(Long.valueOf(now -start).intValue());
				start= now; // ok a couple of ticks will slip, but saves a double call to clock
			}
			

			inputResult.add(module.getName(), moduleResult); // Copy the result of a module to the input
															 // of the next module
			
			result.add(module.getName(), moduleResult); // Only store the result of modules for returning
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
				_logger.error(e);
			} catch (InstanceAlreadyExistsException e) {
				_logger.error(e);
			} catch (MBeanRegistrationException e) {
				_logger.error(e);
			} catch (NotCompliantMBeanException e) {
				_logger.error(e);
			} catch (NullPointerException e) {
				_logger.error(e);
			}
			
		}
		
		
	}

}

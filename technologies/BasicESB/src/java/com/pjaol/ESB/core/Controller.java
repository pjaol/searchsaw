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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.config.ESBCore;
import com.pjaol.ESB.monitor.Monitor;
import com.pjaol.ESB.monitor.MonitorBean;
import com.pjaol.ESB.monitor.TYPE;
import com.pjaol.oji.utils.TimerThread;

public class Controller extends Module{

	private Map<String, List<String>> pipelines;
	private List<String>pipes;
	private List<String> limiterPipeLines;
	private String limiterName;
	private String uri;
	private int timeout;
	private ESBCore core = ESBCore.getInstance();
	private ExecutorService executorService ;
	private Logger _logger = Logger.getLogger(getClass());
	
	// what to measure
	private Monitor monit = Monitor.getInstance();
	private MonitorBean performanceBean;
	private MonitorBean errorBean;
	private MonitorBean timeoutCountBean;
	
	/**
	 * Controllers are stored in the ESBCore
	 * 
	 */
	public Controller() { }
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public NamedList process(NamedList input) throws ModuleRunException {
		
		
		CountDownLatch start = new CountDownLatch(1);
		
		CountDownLatch stop = new CountDownLatch(pipes.size()){
			@Override
			public boolean await(long timeout, TimeUnit unit)
					throws InterruptedException {
				
				return super.await(timeout, unit);
			}
		};
		
		TimerThread timer = new TimerThread(getTimeout(), getName(), "controller"){
			@Override
			public void timeout() {
				timeoutCountBean.inc(1);
				super.timeout();
				throw new RuntimeException("Timed out controller: "+getName());
			}
		};
		
		
		NamedList allOutput = new NamedList();
		List<ModuleRunner> moduleRunners = new ArrayList<ModuleRunner>();
		
		// Pipes have little to no value
		// TODO: should pipes be converted to run in parallel?
		
		for(String p: pipes){
			
			// run each pipe in parallel			
			List<String> pipeLinesToRun = pipelines.get(p);
			
			for(String pipename: pipeLinesToRun){
				PipeLine pipeline = core.getPipeLineByName(pipename);
				
				// all pipelines should have a clean version of the input
				NamedList pipeLineInput = input.clone();
				
				ModuleRunner runner = new ModuleRunner(start, stop, pipeline, pipeLineInput, errorBean, timeoutCountBean);
				executorService.execute(runner);
				moduleRunners.add(runner);
				
			}
			
			// The output of a pipeline should be added to 
			// all output
			//allOutput.addAll(pipeLineOutput);
			
		}
		
		//
		//input.addAll(allOutput);
		
		if(_logger.getLevel() == Level.DEBUG)
			_logger.debug("******* Starting *******");
		
		
		long startT = System.currentTimeMillis();
		start.countDown();
		timer.start();
		
		try {
			executorService.shutdown();
			executorService.awaitTermination(getTimeout(), TimeUnit.MILLISECONDS);
			stop.await(getTimeout(), TimeUnit.MILLISECONDS);
			
		} catch (InterruptedException e) {
			// should really be thrown from the ModuleRunner method
			errorBean.inc(1);
			timeoutCountBean.inc(1);
			throw new ModuleRunException(e.getMessage());
			
		}finally {
			executorService.shutdownNow();
		}
		
		
		for(ModuleRunner runner: moduleRunners){
			allOutput.addAll(runner.getOutput());
		}
		
		//System.out.println("Shutdown called with "+ allOutput+"::");
		
		long endT = System.currentTimeMillis();
		
		
		
		
		if(_logger.getLevel() == Level.DEBUG)
			_logger.debug("******* Shutting down ******* taken: "+ (endT - startT) +" ms" );
		
		
		// run limiters in serial
		if (limiterName != null){
			//Input should be cloned and output added from previous pipelines
			NamedList limiterOutput = new NamedList();
			input.addAll(allOutput);
			NamedList limiterInput = input.clone();
			
			for(String pipeLine : limiterPipeLines){
				
				PipeLine p = core.getPipeLineByName(pipeLine);
				try {
					//TODO: do i want to set this exclusively ?
					limiterOutput.addAll(p.process(limiterInput));
				} catch (Exception e) {
					errorBean.inc(1);
					throw new ModuleRunException(e.getMessage());
				}
			}
			
			allOutput = limiterOutput;
		}
		
		long timeTaken = endT - startT;
		
		timer.halt();
		allOutput.add("QTime", timeTaken);
		performanceBean.inc(Long.valueOf(endT - startT).intValue()); // log the time
		errorBean.incCardinal(); // allow averages be calculated against all requests 
		timeoutCountBean.incCardinal();
		
		return allOutput;
	}

	
	@Override
	public void init(Map<String, String> args) {
		
		
		
	}
	
	
	public void initializeMonitor(){
		performanceBean = new MonitorBean(getName(), TYPE.CONTROLLER, "performance");
		errorBean = new MonitorBean(getName(), TYPE.ERROR, "errors");
		timeoutCountBean = new MonitorBean(getName(), TYPE.ERROR, "timeouts");
		
		try {
			monit.setBean("pref-cont-"+getName(), performanceBean);
			monit.setBean("error-cont-"+getName(), errorBean);
			monit.setBean("timeout-cont-"+getName(), timeoutCountBean);
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

	public void setPipelines(Map<String, List<String>> pipelines) {
		this.pipelines = pipelines;
	}

	public Map<String, List<String>> getPipelines() {
		return pipelines;
	}


	public void setPipes(List<String> pipes) {
		this.pipes = pipes;
	}

	public List<String> getPipes() {
		return pipes;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
	public List<String> getLimitorPipeLines() {
		return limiterPipeLines;
	}

	public void setLimitorPipeLines(List<String> limitorPipeLines) {
		this.limiterPipeLines = limitorPipeLines;
	}

	public String getLimitorName() {
		return limiterName;
	}

	public void setLimitorName(String limitorName) {
		this.limiterName = limitorName;
	}
	
	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	
	public void setExecutorService(ExecutorService executorService){
		this.executorService = executorService;
	}
}

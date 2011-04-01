package com.pjaol.ESB.core;

import java.util.List;
import java.util.Map;
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
	private List<String> limitorPipeLines;
	private String limitorName;
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
		
		TimerThread timer = new TimerThread(getTimeout()){
			@Override
			public void timeout() {
				
				super.timeout();
				timeoutCountBean.inc(1);
				throw new RuntimeException("Timed out: "+getName());
			}
		};
		
		for(String p: pipes){
			// run each pipe in parallel
			// pipelines.get(p)
			List<String> pipeLinesToRun = pipelines.get(p);
			
			for(String pipename: pipeLinesToRun){
				PipeLine module = core.getPipeLineByName(pipename);
				
				// this needs to go into a TimerThreadRunner
				//pline.process(input);
				executorService.execute(new ModuleRunner(start, stop, module, input, errorBean, timeoutCountBean));
			}
		}
		
		if(_logger.getLevel() == Level.DEBUG)
			_logger.debug("******* Starting *******");
		
		
		long startT = System.currentTimeMillis();
		start.countDown();
		timer.start();
		
		try {
			executorService.awaitTermination(getTimeout(), TimeUnit.MILLISECONDS);
			stop.await(getTimeout(), TimeUnit.MILLISECONDS);
			timer.halt();
		} catch (InterruptedException e) {
			// should really be thrown from the ModuleRunner method
			errorBean.inc(1);
			timeoutCountBean.inc(1);
			throw new ModuleRunException(e.getMessage());
			
		}finally {
			executorService.shutdownNow();
		}
		
		long endT = System.currentTimeMillis();
		
		if(_logger.getLevel() == Level.DEBUG)
			_logger.debug("******* Shutting down ******* taken: "+ (endT - startT) +" ms" );
		
		performanceBean.inc(Long.valueOf(endT - startT).intValue()); // log the time
		
		return input;
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
		return limitorPipeLines;
	}

	public void setLimitorPipeLines(List<String> limitorPipeLines) {
		this.limitorPipeLines = limitorPipeLines;
	}

	public String getLimitorName() {
		return limitorName;
	}

	public void setLimitorName(String limitorName) {
		this.limitorName = limitorName;
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

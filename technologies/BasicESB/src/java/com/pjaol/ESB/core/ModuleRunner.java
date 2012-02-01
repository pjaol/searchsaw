package com.pjaol.ESB.core;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.monitor.MonitorBean;

public class ModuleRunner implements Runnable{
	
	private CountDownLatch start;
	private CountDownLatch stop;
	private Module module;
	@SuppressWarnings("rawtypes")
	private NamedList input;
	private NamedList output = new NamedList();
	private MonitorBean errorBean, timeoutCountBean;
	Logger _logger = Logger.getLogger(getClass());
	
	public ModuleRunner(CountDownLatch start, CountDownLatch stop, Module module, @SuppressWarnings("rawtypes") NamedList input,  MonitorBean errorBean, MonitorBean timeoutCountBean) {
		this.start = start;
		this.stop = stop;
		this.module = module;
		this.input = input;
		
		this.errorBean = errorBean;
		this.timeoutCountBean = timeoutCountBean;
		
	}

	@Override
	public void run() {
		
		try {
			start.await();
			if(!Thread.interrupted()){
				output = module.process(input);
				//System.out.println("****:"+module.getName()+":::"+output);
				// result of previous modules can be input to next module
				//input.addAll(output);
				//input.add("should not appear", "why am i here");
				
				 
			}
		} catch (InterruptedException e) {
			timeoutCountBean.inc(1);
			throw new RuntimeException(e);
		} catch (Exception e) {
			errorBean.inc(1); // a timeout might not be an error?
			_logger.error(e);
			e.printStackTrace();
		} finally {
			stop.countDown();
		}
		
		
	}

	
	public NamedList getOutput(){
		return this.output;
	}
}

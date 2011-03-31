package com.pjaol.ESB.core;

import java.util.concurrent.CountDownLatch;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.monitor.MonitorBean;

public class ModuleRunner implements Runnable{
	
	private CountDownLatch start;
	private CountDownLatch stop;
	private Module module;
	@SuppressWarnings("rawtypes")
	private NamedList input;
	private MonitorBean errorBean, timeoutCountBean;
	
	public ModuleRunner(CountDownLatch start, CountDownLatch stop, Module module, @SuppressWarnings("rawtypes") NamedList input, MonitorBean errorBean, MonitorBean timeoutCountBean) {
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
				module.process(input);
			}
		} catch (InterruptedException e) {
			timeoutCountBean.inc(1);
			throw new RuntimeException(e);
		} catch (Exception e) {
			errorBean.inc(1); // a timeout might not be an error?
			e.printStackTrace();
		} finally {
			stop.countDown();
		}
	}

}

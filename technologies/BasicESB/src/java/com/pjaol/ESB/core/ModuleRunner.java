package com.pjaol.ESB.core;

import java.util.concurrent.CountDownLatch;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;

public class ModuleRunner implements Runnable{
	
	private CountDownLatch start;
	private CountDownLatch stop;
	private Module module;
	private NamedList input;
	
	public ModuleRunner(CountDownLatch start, CountDownLatch stop, Module module, NamedList input) {
		this.start = start;
		this.stop = stop;
		this.module = module;
		this.input = input;
		
	}

	@Override
	public void run() {
		
		try {
			start.await();
			if(!Thread.interrupted()){
				module.process(input);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ModuleRunException e) {
			throw new RuntimeException(e);
		}
		stop.countDown();
	}

}

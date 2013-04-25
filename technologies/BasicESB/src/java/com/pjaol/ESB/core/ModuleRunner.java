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

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	Logger _logger = LoggerFactory.getLogger(getClass());
	
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
				
			}
		} catch (InterruptedException e) {
			timeoutCountBean.inc(1);
			throw new RuntimeException(e);
		} catch (Exception e) {
			errorBean.inc(1); // a timeout might not be an error?
			_logger.error("Run Error",e);
			e.printStackTrace();
		} finally {
			stop.countDown();
		}
		
		
	}

	
	public NamedList getOutput(){
		return this.output;
	}
}

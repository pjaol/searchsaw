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
package com.pjaol.ESB.Mock;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.config.ConfigurationException;
import com.pjaol.ESB.config.ESBCore;
import com.pjaol.ESB.config.Initializer;
import com.pjaol.ESB.config.XMLConfiguration;
import com.pjaol.ESB.core.Controller;
import com.pjaol.ESB.monitor.Monitor;
import com.pjaol.ESB.monitor.MonitorBean;

public class MockRunner {

	
	private ESBCore core = ESBCore.getInstance();
	private Monitor monit = Monitor.getInstance();
	
	public static void main(String[] args) {
		
		String fileName = "technologies/BasicESB/example/conf/example.xml";
		int until =100;
		
		if (args.length > 0){
			fileName = args[0];
		}
		
		XMLConfiguration config = new XMLConfiguration();
		Initializer initializer = new Initializer();

		try {
			config.parseFile(fileName);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		try {
			initializer.startup();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		MockRunner mr = new MockRunner();
		
		if (args.length >= 2){
			if (args[1].equals("forever")){
				mr.runForever();
			} else {
				until = new Integer(args[1]).intValue();
				mr.runUntil(until);
			}
		} else {
			mr.runUntil(until);
		}
		
		mr.dumpStats();
	}
	
	private void runForever(){
		while(true){
			doRun();
		}
	}
	
	private void runUntil(int until){
		for (int i=0; i< until; i++){
			doRun();
		}
	}
	
	
	private void doRun(){
		NamedList<String> input = new NamedList<String>();
		input.add("foo", "bar");
		
		Controller controller = core.getControllerByUri("/");
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		controller.setExecutorService(executorService);
		
		try {
			controller.process(input);
		} catch (ModuleRunException e) {
			e.printStackTrace();
		}
		
	}

	
	private void dumpStats(){
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("============== Stats ==============");
		Map<String, MonitorBean> mbeans = monit.getBeans();
		for(Entry<String, MonitorBean> k: mbeans.entrySet()){
			System.out.println(k.getKey());
			System.out.println("\t"+k.getValue());
		}
	}
	
}

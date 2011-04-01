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
		for(int i =0; i < 100; i++){
			mr.doRun();
		}
		mr.dumpStats();
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

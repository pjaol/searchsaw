package com.pjaol.ESB.Mock;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.xpath.XPathExpressionException;

import org.apache.solr.common.util.NamedList;
import org.xml.sax.SAXException;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.config.ConfigurationException;
import com.pjaol.ESB.config.ESBCore;
import com.pjaol.ESB.config.Initializer;
import com.pjaol.ESB.config.XMLConfiguration;
import com.pjaol.ESB.core.Controller;

public class MockRunner {

	
	private ESBCore core = ESBCore.getInstance();
	
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
		mr.doRun();
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

	
}

package com.pjaol.ESB.web;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.config.BasicESBVariables;
import com.pjaol.ESB.config.ConfigurationException;
import com.pjaol.ESB.config.ESBCore;
import com.pjaol.ESB.config.Initializer;
import com.pjaol.ESB.config.XMLConfiguration;
import com.pjaol.ESB.core.Controller;
import com.pjaol.ESB.formatters.Formatter;
import com.pjaol.ESB.formatters.JSONFormatter;
import com.pjaol.ESB.formatters.XMLFormatter;
import com.pjaol.ESB.monitor.Monitor;

public class HTTPAPI extends HttpServlet{
	
	private static ESBCore core = ESBCore.getInstance();
	private static Monitor monit = Monitor.getInstance();
	
	String fileName = "/conf/esbconfig.xml";
	
	Formatter jsonFormatter = new JSONFormatter();
	Formatter xmlFormatter = new XMLFormatter();
	
	/**
	 * 
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		
		XMLConfiguration xmlconfig = new XMLConfiguration();
		Initializer initializer = new Initializer();

		// finding the config file
		// 1. look for basicesb.home system property + conf/esbconfig.xml
		// 2. look in current working directory + basicesb/conf/esbconfig.xml

		String homeDir = System.getProperty(BasicESBVariables.basicESBHomeProperty);
		
		if (homeDir == null){
			homeDir = "basicesb";
		}
		
		try {
			xmlconfig.parseFile(homeDir+fileName);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		try {
			initializer.startup();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		
		super.init(config);
	}
	
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		NamedList<String> input = new NamedList<String>();
		String pathInfo=  req.getPathInfo();
		
		input.addAll(HTTPParamsParser.paramsToNamedList(req));
		
		Controller controller = core.getControllerByUri(pathInfo);
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		controller.setExecutorService(executorService);
		NamedList results = null;
		try {
			results = controller.process(input);
		} catch (ModuleRunException e) {
			throw new ServletException(e);
		}
		
		
		
		String format = req.getParameter("format");
		
		Writer writer = resp.getWriter();
		
		if (format == null || format.equals("xml")){
			writer.write(xmlFormatter.toOutput(results));
		} else if (format.equals("json")){
			
			String jsonpCallback = req.getParameter("jsoncallback");
			if (jsonpCallback != null){
				writer.write(jsonpCallback+"(");
			}
			
			writer.write(jsonFormatter.toOutput(results));
			
			if (jsonpCallback != null){
				writer.write(");");
			}
		}
		
		writer.flush();
		writer.close();
	}
	
	
	
	

}

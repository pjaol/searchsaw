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
package com.pjaol.ESB.web;

import java.io.IOException;
import java.io.Writer;
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

public class HTTPAPI extends HttpServlet {

	private static ESBCore core = ESBCore.getInstance();
	private static Monitor monit = Monitor.getInstance();

	String fileName = "/conf/esbconfig.xml";

	Formatter jsonFormatter = new JSONFormatter();
	Formatter xmlFormatter = new XMLFormatter();

	HTTPParamsParser httpParamsParser;

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

		String homeDir = System
				.getProperty(BasicESBVariables.basicESBHomeProperty);

		if (homeDir == null) {
			homeDir = "basicesb";
		}

		try {
			xmlconfig.parseFile(homeDir + fileName);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		try {
			initializer.startup();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		String paramsParser = core.getGlobals().get("HTTPParamsParser");
		
		if (paramsParser != null){
			try {
				httpParamsParser =(HTTPParamsParser)Class.forName(paramsParser).newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		} else {
			httpParamsParser = new HTTPParamsParser();
		}
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		NamedList<String> input = new NamedList<String>();
		String pathInfo = httpParamsParser.getPath(req);

		try {
			input.addAll(httpParamsParser.paramsToNamedList(req));
		} catch (Exception e) {
			throw new ServletException(e);
		}

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

		if (format == null || format.equals("xml")) {
			writer.write(xmlFormatter.toOutput(results));
		} else if (format.equals("json")) {

			String jsonpCallback = req.getParameter("jsoncallback");
			if (jsonpCallback != null) {
				writer.write(jsonpCallback + "(");
			}

			writer.write(jsonFormatter.toOutput(results));

			if (jsonpCallback != null) {
				writer.write(");");
			}
		}

		writer.flush();
		writer.close();
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doGet(req,resp);
	}

}

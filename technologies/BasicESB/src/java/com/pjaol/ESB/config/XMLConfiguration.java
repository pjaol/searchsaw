package com.pjaol.ESB.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class XMLConfiguration {

	//StringBuilder buffer = new StringBuilder();
	//boolean shouldBuffer;

	private static DocumentBuilder builder;

	Map <String, PipeLineComponent> pipelines = new HashMap <String, PipeLineComponent>();
	Map <String, ControllerComponent> controllers = new HashMap<String, ControllerComponent>();
	Map<String, String> globals = new HashMap<String, String>();
	
	ESBCore core = ESBCore.getInstance();
	
	Logger _logger = Logger.getLogger(getClass());

	static {
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true);
		try {
			builder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}



	public void parseFile(String fileName) throws ConfigurationException {

		_logger.info("parsing file: " + fileName);

		Document doc;
		try {
			doc = builder.parse(fileName);
		} catch (SAXException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (IOException e) {
			throw new ConfigurationException(e.getMessage());
		}
		File f = new File(fileName);
		String parentDir = f.getParent();

		// set up globals, in case modules need them for init()
		
		try {
			getGlobals(doc, fileName);
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(e.getMessage());
		}
		core.setGlobals(globals);
		
		try {
			includes(doc, parentDir);
		} catch (XPathExpressionException e) {
			
			throw new ConfigurationException(e.getMessage());
		} catch (SAXException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (IOException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (InstantiationException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		try {
			pipelines(doc, fileName);
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (InstantiationException e) {
			throw new ConfigurationException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(e.getMessage());
		}
		core.setPipeLineComponent(pipelines);
		
		try {
			controllers(doc, fileName);
		} catch (XPathExpressionException e) {
			throw new ConfigurationException(e.getMessage());
		}
		
		core.setControllerComponent(controllers);
		
	}

	/**
	 * allows top level include files {@link XMLConfigPaths} which can allow you
	 * maintain global / reusable components e.g.:
	 * 
	 * <pre>
	 * &lt;includeFile name="admin-controller.xml"/&gt;
	 * </pre>
	 * 
	 * @param doc
	 * @param parentDir
	 * @throws XPathExpressionException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ConfigurationException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	private void includes(Document doc, String parentDir)
			throws XPathExpressionException, SAXException, IOException, ConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		Object resultIncludes = XMLConfigPaths.INCLUDES.evaluate(doc,
				XPathConstants.NODESET);
		if (resultIncludes != null) {
			NodeList nl = (NodeList) resultIncludes;
			int sz = nl.getLength();
			for (int i = 0; i < sz; i++) {
				String fileName = nl.item(i).getNodeValue();

				if (!fileName.startsWith(File.separator)) {
					fileName = parentDir + File.separator + fileName;
				}

				_logger.info("loading includeFile: " + fileName);
				parseFile(fileName);
			}
		}

	}

	/**
	 * Generate pipelines, modules and evaluators from an xml structure like
	 * <pre>
	 * &lt;BasicESB&gt;
	 * ....
	 *  &lt;pipeline name=&quot;testPipeline1&quot; timeout=&quot;200&quot;&gt;
	 *	 &lt;evaluator name=&quot;com.pjaol.ESB.Mock.MockEvaluator&quot;&gt;
	 *		&lt;arg name=&quot;arg1&quot;&gt;lalala&lt;/arg&gt;
	 *	 &lt;/evaluator&gt;
	 *	 &lt;module name=&quot;mockModule1&quot; class=&quot;com.pjaol.ESB.Mock.MockModule&quot;/&gt;
	 *  &lt;/pipeline&gt;
	 * ....
	 * &lt;/BasicESB&gt;
	 * </pre>
	 * @param doc
	 * @param fileName
	 * @throws XPathExpressionException
	 * @throws ConfigurationException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void pipelines(Document doc, String fileName) throws XPathExpressionException, ConfigurationException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		Object resultPipelines = XMLConfigPaths.PIPELINE.evaluate(doc,
				XPathConstants.NODESET);

		if (resultPipelines != null) {

			NodeList nl = (NodeList) resultPipelines;
			int sz = nl.getLength();
			for (int i = 0; i < sz; i++) {
				Node n = nl.item(i);
				
				// Create a pipeline
				PipeLineComponent pl = new PipeLineComponent();
				
				getComponent(n,pl );
				_logger.info(pl);
				
				String name =pl.getName();
				
				// check some required attributes
				if ( name == null){
					throw new ConfigurationException("Pipeline #"+i+" in "+fileName+": is missing a name");
				}
				
				if (pl.getTimeout() == -1){	
					throw new ConfigurationException("Pipeline #"+i+" in "+fileName+": "+ name+" is missing a timeout");
				}
				
				
				// Add and evaluator
				Node e = (Node) XMLConfigPaths.EVALUATOR.evaluate(n, XPathConstants.NODE);
				EvaluatorComponent evaluator = new EvaluatorComponent();
				getComponent(e, evaluator);
				
				_logger.info("\t"+evaluator);
				if (evaluator.getName() == null){
					throw new ConfigurationException("Pipeline: "+ name+" evaluator is missing a name");
				}

				if (evaluator.getClassName() == null){
					throw new ConfigurationException("Pipeline: "+ name+":"+ evaluator.getName()+" evaluator is missing a name");
				}
				
				pl.setEvaluator(evaluator);
				
				
				// add modules
				NodeList mod = (NodeList) XMLConfigPaths.MODULES.evaluate(n, XPathConstants.NODESET);
				int mSz = mod.getLength();
				
				List<ConfigurationComponent> mList = new ArrayList<ConfigurationComponent>();
				
				for(int y =0; y< mSz; y++){
					Node m = mod.item(y);
					ConfigurationComponent module = new ConfigurationComponent();
					getComponent(m, module);
					
					if (module.getName() == null){
						throw new ConfigurationException(fileName+"::"+name+"::module #"+y+" is missing a name");
					}
					if (module.getClassName() == null){
						throw new ConfigurationException(fileName+"::"+name+"::"+module.getName()+" is missing a class");
					}
					_logger.info("\t"+module);
					mList.add(module);
				}
				
				pl.setModules(mList);
				
				if(pipelines.containsKey(pl.getName())){
					throw new ConfigurationException("pipeline name=\""+pl.getName()+"\" already exists");
				}
				pipelines.put(pl.getName(), pl);
				
			}
		}
	}
	
	/**
	 * controllers contain pipelines, which point to more pipelines... fun :-D
	 * 
	 * <pre>
	 * &lt;controller uri=&quot;/&quot; name=&quot;default&quot; timeout=&quot;100&quot;&gt;
	 *	&lt;pipes name=&quot;batch1&quot;&gt;
	 *		&lt;pipeline name=&quot;testPipeline1&quot;/&gt;
	 *		&lt;pipeline name=&quot;testPipeline2&quot;/&gt;
	 *		&lt;pipeline name=&quot;testPipeline3&quot;/&gt;
	 *	&lt;/pipes&gt;
	 * &lt;/controller&gt;
	 * </pre>
	 * @param doc
	 * @param fileName
	 * @throws XPathExpressionException
	 */
	
	private void controllers(Document doc, String fileName) throws XPathExpressionException{
		NodeList controllerList = (NodeList)XMLConfigPaths.CONTROLLER.evaluate(doc, XPathConstants.NODESET);
		int sz = controllerList.getLength();
		
		for(int i=0; i< sz; i++){
			Node c = controllerList.item(i);
			
			ControllerComponent controller = new ControllerComponent();
			getComponent(c, controller);
			
			
			NodeList subPipes = (NodeList) XMLConfigPaths.PIPES.evaluate(c, XPathConstants.NODESET);
			int spSz = subPipes.getLength();
			
			
			// Maintain pipe order, 
			// not necessary but helps with debugging
			List<String> pipes = new ArrayList<String>();
			
			// Could also be a map of String, String[]
			Map<String, List<String>> pipelines = new HashMap<String, List<String>>();
			
			List<String> limitor = new ArrayList<String>();
			
			for (int y = 0; y< spSz; y++){
							
				Node sp = subPipes.item(y);
				String name = getAttribute(sp, "name");
				pipes.add(name);
				
				List<String> subs= new ArrayList<String>();
				
				/*
				 * BasicESB / controller / pipes / pipeline
				 */
				NodeList subPipesSecondLevel = (NodeList) XMLConfigPaths.SUBPIPES.evaluate(sp, XPathConstants.NODESET);
				int subSz = subPipesSecondLevel.getLength();

				for (int z =0; z< subSz; z++){
					String subName = getAttribute(subPipesSecondLevel.item(z), "name");
					subs.add(subName);
				}
				
				pipelines.put(name, subs);

			}
			
			Node limit = (Node)XMLConfigPaths.LIMITER.evaluate(c, XPathConstants.NODE);
			String limitorName = getAttribute(limit, "name");
			
			NodeList limitSubPipes = (NodeList)XMLConfigPaths.SUBPIPES.evaluate(limit, XPathConstants.NODESET);
			int lspSz = limitSubPipes.getLength();
			
			for (int y = 0; y < lspSz; y++){
				Node n = limitSubPipes.item(y);
				String limitSubName = getAttribute(n, "name");
				limitor.add(limitSubName);
			}
			
			controller.setLimiterName(limitorName);
			controller.setLimiterPipeLines(limitor);
			
			controller.setPipes(pipes);
			controller.setPipelines(pipelines);
			controllers.put(controller.getName(), controller);
		}
	
	}

	/**
	 * Get global variables
	 * available within the ESBCore
	 * 
	 * <pre>
	 * &lt;BasicESB&gt;
	 *   &lt;globals&gt;
	 *     &lt;arg name=&quot;fu&quot;&gt;bar&lt;/arg&gt;
	 *   &lt;/globals&gt;
	 *   ......
	 * &lt;/BasicESB&gt;
	 * </pre>
	 * @param doc
	 * @param fileName
	 * @throws XPathExpressionException
	 */
	private void getGlobals(Document doc, String fileName) throws XPathExpressionException{
		Node g = (Node)XMLConfigPaths.GLOBALS.evaluate(doc, XPathConstants.NODE);
		if(g != null){
			_logger.info("Getting globals in :"+fileName);
			NodeList args = (NodeList) XMLConfigPaths.ARGS.evaluate(g, XPathConstants.NODESET);
			globals.putAll( getArgs(args));
		}
	}

	/** 
	 * get basic a component structure
	 * sub-classed Component is passed in
	 * {@link ConfigurationComponent}
	 */ 
	private void getComponent( Node node, ConfigurationComponent component) throws XPathExpressionException{
		
		NodeList args = (NodeList) XMLConfigPaths.ARGS.evaluate(node,XPathConstants.NODESET);
		
		Map<String, String> a = getArgs(args);
		String name = getAttribute(node, "name");
		String className = getAttribute(node, "class");
		String to = getAttribute(node, "timeout");
		String uri = getAttribute(node, "uri");
		
		component.setName(name);
		if (className != null)
			component.setClassName(className);
		
		if (a != null)
			component.setArgs(a);
		
		if (to != null)
			component.setTimeout(new Integer(to).intValue());
		
		if (uri != null)
			component.setUri(uri);
		
	
	}
	
	
	/**
	 * take an xml argument list and convert it to a map used for initialize
	 * e.g.:
	 * <pre>
	 * &lt;arg name="foo"&gt;bar&lt;/arg&gt;
	 * &lt;arg name="picnic"&gt;basket&lt;/arg&gt;
	 * 
	 * Becomes
	 *  'foo' => 'bar',
	 *  'picnic' => 'basket'
	 * </pre>
	 * @param args
	 * @return
	 */
	private Map<String, String> getArgs(NodeList args) {
		Map<String, String> resultArgs = null;

		int sz = args.getLength();
		if (sz > 0)
			resultArgs = new HashMap<String, String>();
		
		for (int i = 0; i < sz; i++) {
			Node n = args.item(i);
			String name = getAttribute(n, "name");
			String value = n.getTextContent();
			resultArgs.put(name, value);
		}

		return resultArgs;
	}

	private String getAttribute(Node node, String attributeName) {

		NamedNodeMap nnm = node.getAttributes();
		String result = null;

		if (nnm != null) {
			Node n = nnm.getNamedItem(attributeName);
			if (n != null)
				result = n.getNodeValue();
		}

		return result;
	}

}

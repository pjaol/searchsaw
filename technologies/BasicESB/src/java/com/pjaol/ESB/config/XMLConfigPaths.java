package com.pjaol.ESB.config;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * 
 *
 * @author pjaol
 *
 */
public class XMLConfigPaths {
	
	static XPath xpath = XPathFactory.newInstance().newXPath();
    // XPath Query for showing all nodes value
	static XPathExpression INCLUDES, PIPELINE, CONTROLLER, EVALUATOR, ARGS, MODULES, PIPES, SUBPIPES, GLOBALS, LIMITER ;
	
	static {
		try {
			INCLUDES = xpath.compile("//BasicESB/includeFile/@name");
			PIPELINE = xpath.compile("//BasicESB/pipeline");
			EVALUATOR = xpath.compile("./evaluator");
			ARGS = xpath.compile("./arg");
			MODULES = xpath.compile("./module");
			CONTROLLER = xpath.compile("//BasicESB/controller");
			PIPES = xpath.compile("./pipes");
			SUBPIPES = xpath.compile("./pipeline");
			GLOBALS = xpath.compile("//BasicESB/globals");
			LIMITER = xpath.compile("./limiter");
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

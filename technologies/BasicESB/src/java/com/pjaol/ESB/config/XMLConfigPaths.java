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

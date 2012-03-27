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

import java.util.Map;

import org.apache.solr.common.util.NamedList;


/**
 * Evaluators enable modules to be run
 * The evaluate and init methods should be overwritten 
 * @author pjaol
 *
 */
public class Evaluator {
	
	String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * default returns true
	 * @param input
	 * @return
	 */
	public boolean evaluate(@SuppressWarnings("rawtypes") NamedList input){
		return true;
	}

	
	
	public void init(Map<String, String> args) {
		
	}
	
	 
}

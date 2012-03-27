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

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.core.Module;

public class MockModuleEchoInput extends Module{

	@Override
	public void initializeMonitor() {
	
		
	}

	@Override
	public NamedList process(NamedList input) throws Exception {
		NamedList result = new NamedList();
		int sz = input.size();
		
		for (int i = 0; i <sz ; i++){
			String k = input.getName(i);
			result.add("echo'd-"+k, input.get(k));
		}
		
		return result;
	}

	@Override
	public void init(Map<String, String> args) {
		
	}

}

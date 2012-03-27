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
package com.pjaol.ESB.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.solr.common.util.NamedList;

import com.pjaol.ESB.Exceptions.ModuleRunException;
import com.pjaol.ESB.core.Module;

public class ESBMonitor extends Module {

	private Monitor monit = Monitor.getInstance();
	public final String useJMX = "useJMX"; 
	

	@Override
	public void init(Map<String, String> args) {
		
		if (args.get(useJMX).equals("true")){
			monit.startupJMX();
		}
		
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public NamedList process(NamedList input) throws ModuleRunException {

		Map<String, MonitorBean> beans = monit.getBeans();
		List<MonitorBean> controllers = new ArrayList<MonitorBean>();
		List<MonitorBean> pipelines = new ArrayList<MonitorBean>();
		List<MonitorBean> modules = new ArrayList<MonitorBean>();
		List<MonitorBean> errors = new ArrayList<MonitorBean>();
		List<MonitorBean> others = new ArrayList<MonitorBean>();
		
		for(Entry<String, MonitorBean> k: beans.entrySet()){
		
			MonitorBean bean = k.getValue();
			if (bean.getType().equals(TYPE.CONTROLLER)){
				controllers.add(bean);
			} else if (bean.getType().equals(TYPE.PIPELINE)){
				pipelines.add(bean);
			} else if (bean.getType().equals(TYPE.MODULE)){
				modules.add(bean);
			} else if (bean.getType().equals(TYPE.ERROR)){
				errors.add(bean);
			} else {
				others.add(bean);
			}
			
		}
		
		NamedList<List> results = new NamedList<List>();
		results.add(TYPE.CONTROLLER, controllers);
		results.add(TYPE.PIPELINE, pipelines);
		results.add(TYPE.MODULE, modules);
		results.add(TYPE.ERROR, errors);
		results.add("OTHER", others);
		
		
		return results;
	}

	@Override
	public void initializeMonitor() {
		// TODO Auto-generated method stub
		
	}
	
	

}

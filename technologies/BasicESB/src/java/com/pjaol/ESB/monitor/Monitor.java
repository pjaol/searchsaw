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

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class Monitor {

	private static Monitor monit;
	private Map<String, MonitorBean> beans = new HashMap<String, MonitorBean>();
	private MBeanServer mbs = null;
	private boolean isMonitoring = false;
	
	private Monitor(){}
	
	public static Monitor getInstance(){
		if (monit == null)
			monit = new Monitor();
		
		return monit;
	}
	
	public ESBMXBean getBean(String name){
		return beans.get(name);
	}
	
	public void setBean(String name, MonitorBean bean) throws MalformedObjectNameException, NullPointerException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException{
		if(beans.containsKey(name)){
			throw new RuntimeException("Monitor bean already exists: "+ name);
		}
		
		beans.put(name, bean);
		
		if (isMonitoring){
			ObjectName obn = new ObjectName("BasicESB:name="+name);
			mbs.registerMBean(bean, obn);
		}
	}
	
	protected void startupJMX(){
		mbs = ManagementFactory.getPlatformMBeanServer();
		isMonitoring = true;
	}
	
	
	// Fetch beans for internal reporting without JMX
	public Map<String, MonitorBean> getBeans(){
		return beans;
	}
}



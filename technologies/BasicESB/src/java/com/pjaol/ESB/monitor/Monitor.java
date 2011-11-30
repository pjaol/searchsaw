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



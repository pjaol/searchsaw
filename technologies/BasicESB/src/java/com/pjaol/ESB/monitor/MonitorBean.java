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

import java.util.concurrent.atomic.AtomicInteger;

public class MonitorBean implements ESBMXBean {

	private String name;
	private String type;
	private String metric;
	private AtomicInteger max = new AtomicInteger();
	private AtomicInteger total = new AtomicInteger();
	private AtomicInteger cardinal = new AtomicInteger();
	
	public MonitorBean(String name, String type, String metric){
		this.name = name;
		this.type = type;
		this.metric = metric;
	}
	
	
	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getName()
	 */
	@Override
	public String getName() {
		return name;
	}


	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getType()
	 */
	@Override
	public String getType() {
		return type;
	}

	// TODO: should not be exposed to JMX?
	// 
	public synchronized void inc(int i){
		total.addAndGet( i);
		if(i > max.get())
			max.set(i);
		cardinal.incrementAndGet();
	}
	
	public synchronized void incCardinal(){
		cardinal.incrementAndGet();
	}
	
	// should these be synced on total & cardinal?
	// deciding to not block setting for reporting
	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getAverage()
	 */
	@Override
	public double getAverage(){
		int c = cardinal.get();
		int t = total.get();
		
		if (c == 0)
			return 0;
		
		return ((double) t / c);
	}
	
	
	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getTotal()
	 */
	@Override
	public int getTotal(){
		return total.get();
	}
	
	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getCardinal()
	 */
	@Override
	public int getCardinal(){
		return cardinal.get();
	}
	
	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getMax()
	 */
	@Override
	public int getMax(){
		return max.get();
	}


	/* (non-Javadoc)
	 * @see com.pjaol.ESB.monitor.ESBMXBean#getMetric()
	 */
	@Override
	public String getMetric() {
		return metric;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()+"-"+ getType()+"-"+getMetric()+" Average: "+getAverage()+" Total: "+ getTotal()+" Max: "+getMax());
		
		return sb.toString();
	}
}
